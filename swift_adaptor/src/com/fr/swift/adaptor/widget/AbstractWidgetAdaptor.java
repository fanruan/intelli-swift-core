package com.fr.swift.adaptor.widget;

import com.finebi.conf.internalimp.analysis.bean.operator.add.group.custom.number.NumberMaxAndMinValue;
import com.finebi.conf.internalimp.bean.dashboard.widget.dimension.WidgetDimensionBean;
import com.finebi.conf.internalimp.bean.dashboard.widget.field.WidgetBeanField;
import com.finebi.conf.internalimp.bean.dashboard.widget.table.TableWidgetBean;
import com.finebi.conf.internalimp.dashboard.widget.filter.ClickValue;
import com.finebi.conf.internalimp.dashboard.widget.filter.ClickValueItem;
import com.finebi.conf.internalimp.dashboard.widget.filter.WidgetLinkItem;
import com.finebi.conf.structure.bean.dashboard.widget.WidgetBean;
import com.finebi.conf.structure.dashboard.widget.dimension.FineDimension;
import com.finebi.conf.structure.filter.FineFilter;
import com.fr.stable.StringUtils;
import com.fr.swift.adaptor.transformer.FilterInfoFactory;
import com.fr.swift.cal.info.DetailQueryInfo;
import com.fr.swift.query.adapter.dimension.AllCursor;
import com.fr.swift.query.adapter.dimension.DetailDimension;
import com.fr.swift.query.filter.SwiftDetailFilterType;
import com.fr.swift.query.filter.info.FilterInfo;
import com.fr.swift.query.filter.info.SwiftDetailFilterInfo;
import com.fr.swift.query.sort.AscSort;
import com.fr.swift.query.sort.DescSort;
import com.fr.swift.result.DetailResultSet;
import com.fr.swift.segment.column.ColumnKey;
import com.fr.swift.service.QueryRunnerProvider;
import com.fr.swift.source.SourceKey;
import com.fr.swift.structure.array.IntList;
import com.fr.swift.structure.array.IntListFactory;
import com.fr.swift.util.Crasher;
import com.fr.swift.utils.BusinessTableUtils;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author pony
 * @date 2018/4/20
 */
public abstract class AbstractWidgetAdaptor {

    protected static SourceKey getSourceKey(String fieldId) {
        return new SourceKey(BusinessTableUtils.getSourceIdByFieldId(fieldId));
    }

    protected static String getFieldId(FineDimension dimension) {
        String fieldId;
        if (dimension.getWidgetBeanField() != null) {
            WidgetBeanField field = dimension.getWidgetBeanField();
            fieldId = StringUtils.isEmpty(field.getSource()) ? field.getId() : field.getSource();
        } else {
            fieldId = dimension.getFieldId();
        }
        return fieldId;
    }

    protected static String getColumnName(FineDimension dimension) {
        return BusinessTableUtils.getFieldNameByFieldId(getFieldId(dimension));
    }

    protected static String getColumnName(String fieldId) {
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

    static void setMaxMinNumValue(String queryId, String fieldId, List<FineFilter> fineFilters, NumberMaxAndMinValue value) throws Exception {
        SourceKey sourceKey = getSourceKey(fieldId);
        FilterInfo filterInfo = FilterInfoFactory.transformFineFilter(fineFilters);

        //先通过明细表排序查最小
        DetailDimension ascDimension = new DetailDimension(0, sourceKey, new ColumnKey(getColumnName(fieldId)),
                null, new AscSort(0), filterInfo);
        IntList sortIndex = IntListFactory.createHeapIntList(1);
        sortIndex.add(0);
        DetailQueryInfo minQueryInfo = new DetailQueryInfo(new AllCursor(), queryId, new DetailDimension[]{ascDimension}, sourceKey, null, sortIndex, null, null);
        DetailResultSet minResultSet = QueryRunnerProvider.getInstance().executeQuery(minQueryInfo);
        minResultSet.next();
        Number min = minResultSet.getRowData().getValue(0);
        value.setMin(Math.min(value.getMin(), min.doubleValue()));

        //再通过明细表排序查最大
        DetailDimension descDimension = new DetailDimension(0, sourceKey, new ColumnKey(getColumnName(fieldId)),
                null, new DescSort(0), filterInfo);
        DetailQueryInfo maxQueryInfo = new DetailQueryInfo(new AllCursor(), queryId, new DetailDimension[]{descDimension}, sourceKey, null, sortIndex, null, null);
        DetailResultSet maxResultSet = QueryRunnerProvider.getInstance().executeQuery(maxQueryInfo);
        maxResultSet.next();
        Number max = maxResultSet.getRowData().getValue(0);
        value.setMax(Math.max(value.getMax(), max.doubleValue()));
    }

    static NumberMaxAndMinValue getMaxMinNumValue(String fieldId) throws Exception {
        NumberMaxAndMinValue val = new NumberMaxAndMinValue();
        setMaxMinNumValue(fieldId, fieldId, Collections.<FineFilter>emptyList(), val);
        return val;
    }
}
