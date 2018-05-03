package com.fr.swift.adaptor.linkage;

import com.finebi.base.constant.FineEngineType;
import com.finebi.base.stable.StableManager;
import com.finebi.conf.exception.FineEngineException;
import com.finebi.conf.internalimp.bean.dashboard.widget.dimension.WidgetDimensionBean;
import com.finebi.conf.internalimp.bean.dashboard.widget.table.TableWidgetBean;
import com.finebi.conf.internalimp.dashboard.widget.filter.ClickValue;
import com.finebi.conf.internalimp.dashboard.widget.filter.ClickValueItem;
import com.finebi.conf.internalimp.dashboard.widget.filter.WidgetLinkItem;
import com.finebi.conf.internalimp.service.pack.FineConfManageCenter;
import com.finebi.conf.service.engine.relation.EngineRelationPathManager;
import com.finebi.conf.structure.bean.dashboard.widget.WidgetBean;
import com.finebi.conf.structure.path.FineBusinessTableRelationPath;
import com.fr.general.ComparatorUtils;
import com.fr.swift.adaptor.transformer.RelationSourceFactory;
import com.fr.swift.query.filter.SwiftDetailFilterType;
import com.fr.swift.query.filter.info.FilterInfo;
import com.fr.swift.query.filter.info.SwiftDetailFilterInfo;
import com.fr.swift.segment.column.ColumnKey;
import com.fr.swift.source.RelationSource;
import com.fr.swift.util.Crasher;
import com.fr.swift.utils.BusinessTableUtils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author yee
 * @date 2018/4/28
 */
public class LinkageAdaptor {
    private static final FineConfManageCenter fineConfManageCenter = StableManager.getContext().getObject("fineConfManageCenter");

    /**
     * 计算被联动过滤信息
     *
     * @param filterInfos
     * @return
     */
    public static TableWidgetBean handleClickItem(String tableName, WidgetLinkItem widgetLinkItem, List<FilterInfo> filterInfos) {
        WidgetBean widgetBean = widgetLinkItem.getWidget();
        if (null == widgetBean) {
            return null;
        }
        if (!(widgetBean instanceof TableWidgetBean)) {
            Crasher.crash("WidgetBean must instance of " + TableWidgetBean.class.getName() + " but got " + widgetBean.getClass().getName());
        }
        TableWidgetBean fromWidget = (TableWidgetBean) widgetBean;
        String fromTableName = fromWidget.getTableName();

        ClickValue clickValue = widgetLinkItem.getClicked();
        if (null != clickValue) {
            List<ClickValueItem> clickedList = clickValue.getValue();
            if (ComparatorUtils.equals(fromTableName, tableName)) {
                handleOneTableFilter(fromWidget, clickedList, filterInfos);
            } else {
                handleRelationFilter(tableName, fromWidget, clickedList, filterInfos);
            }
        }
        return fromWidget;
    }

    private static void handleOneTableFilter(TableWidgetBean fromWidget, List<ClickValueItem> clickedList, List<FilterInfo> filterInfos) {
        if (null != clickedList) {
            for (ClickValueItem clickValueItem : clickedList) {
                String value = clickValueItem.getText();
                Set<String> values = new HashSet<String>();
                values.add(value);
                WidgetDimensionBean bean = fromWidget.getDimensions().get(clickValueItem.getdId());
                ColumnKey columnKey = new ColumnKey(BusinessTableUtils.getFieldNameByFieldId(bean.getFieldId()));
                filterInfos.add(new SwiftDetailFilterInfo<Set<String>>(columnKey, values, SwiftDetailFilterType.STRING_IN));
            }
        }
    }

    private static void handleRelationFilter(String table, TableWidgetBean fromWidget, List<ClickValueItem> clickedList, List<FilterInfo> filterInfos) {
        if (null != clickedList) {
            EngineRelationPathManager manager = fineConfManageCenter.getRelationPathProvider().get(FineEngineType.Cube);
            List<FineBusinessTableRelationPath> relationPaths = new ArrayList<FineBusinessTableRelationPath>();
            try {
                relationPaths.addAll(manager.getRelationPaths(fromWidget.getTableName(), table));
                relationPaths.addAll(manager.getRelationPaths(table, fromWidget.getTableName()));
            } catch (FineEngineException e) {
                Crasher.crash("get relation paths error: ", e);
            }
            if (relationPaths.isEmpty()) {
                Crasher.crash(String.format("can not find relation paths between %s and %s!", table, fromWidget.getTableName()));
            }
            RelationSource relationSource = RelationSourceFactory.transformRelationSourcesFromPath(relationPaths.get(0));
            for (int i = 0; i < clickedList.size(); i++) {
                ClickValueItem clickValueItem = clickedList.get(i);
                String value = clickValueItem.getText();
                Set<String> values = new HashSet<String>();
                values.add(value);
                WidgetDimensionBean bean = fromWidget.getDimensions().get(clickValueItem.getdId());
                ColumnKey columnKey = new ColumnKey(BusinessTableUtils.getFieldNameByFieldId(bean.getFieldId()));
                columnKey.setRelation(relationSource);
                filterInfos.add(new SwiftDetailFilterInfo<Set<String>>(columnKey, values, SwiftDetailFilterType.STRING_IN));
            }
        }
    }
}
