package com.fr.swift.adaptor.widget;

import com.finebi.conf.internalimp.bean.dashboard.widget.dimension.WidgetDimensionBean;
import com.finebi.conf.internalimp.bean.dashboard.widget.table.TableWidgetBean;
import com.finebi.conf.internalimp.dashboard.widget.filter.ClickValue;
import com.finebi.conf.internalimp.dashboard.widget.filter.ClickValueItem;
import com.finebi.conf.internalimp.dashboard.widget.filter.WidgetLinkItem;
import com.finebi.conf.structure.bean.dashboard.widget.WidgetBean;
import com.fr.swift.query.filter.SwiftDetailFilterType;
import com.fr.swift.query.filter.info.FilterInfo;
import com.fr.swift.query.filter.info.SwiftDetailFilterInfo;
import com.fr.swift.source.SourceKey;
import com.fr.swift.util.Crasher;
import com.fr.swift.utils.BusinessTableUtils;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by pony on 2018/4/20.
 */
public abstract class AbstractWidgetAdaptor {

    protected static SourceKey getSourceKey(String fieldId){
        return new SourceKey(BusinessTableUtils.getSourceIdByFieldId(fieldId));
    }

    protected static String getColumnName(String fieldId){
        return BusinessTableUtils.getFieldNameByFieldId(fieldId);
    }

    /**
     * 计算被联动过滤信息
     *
     * @param filterInfos
     * @return
     */
    protected static TableWidgetBean handleClickItem(WidgetLinkItem widgetLinkItem, List<FilterInfo> filterInfos) {
        WidgetBean widgetBean = widgetLinkItem.getWidget();
        if (null == widgetBean) {
            return null;
        }
        if (!(widgetBean instanceof TableWidgetBean)) {
            Crasher.crash("WidgetBean must instance of " + TableWidgetBean.class.getName() + " but got " + widgetBean.getClass().getName());
        }
        TableWidgetBean fromWidget = (TableWidgetBean) widgetBean;
        ClickValue clickValue = widgetLinkItem.getClicked();
        if (null != clickValue) {
            List<ClickValueItem> clickedList = widgetLinkItem.getClicked().getValue();
            if (null != clickedList) {
                for (ClickValueItem clickValueItem : clickedList) {
                    String value = clickValueItem.getText();
                    Set<String> values = new HashSet<String>();
                    values.add(value);
                    WidgetDimensionBean bean = fromWidget.getDimensions().get(clickValueItem.getdId());
                    filterInfos.add(new SwiftDetailFilterInfo<Set<String>>(getColumnName(bean.getFieldId()), values, SwiftDetailFilterType.STRING_IN));
                }
            }
        }
        return fromWidget;
    }
}
