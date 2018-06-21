package com.fr.swift.adaptor.widget;

import com.finebi.base.constant.FineEngineType;
import com.finebi.base.stable.StableManager;
import com.finebi.conf.constant.BICommonConstants;
import com.finebi.conf.constant.BIDesignConstants;
import com.finebi.conf.exception.FineEngineException;
import com.finebi.conf.internalimp.analysis.bean.operator.add.group.custom.number.NumberMaxAndMinValue;
import com.finebi.conf.internalimp.bean.dashboard.widget.control.time.AbstractTimeControlBean;
import com.finebi.conf.internalimp.bean.dashboard.widget.field.WidgetBeanField;
import com.finebi.conf.internalimp.bean.dashboard.widget.field.value.FormulaValueBean;
import com.finebi.conf.internalimp.bean.filter.AbstractFilterBean;
import com.finebi.conf.internalimp.bean.filter.BelongWidgetValue;
import com.finebi.conf.internalimp.bean.filter.GeneraAndFilterBean;
import com.finebi.conf.internalimp.bean.filter.GeneraOrFilterBean;
import com.finebi.conf.internalimp.bean.filter.number.NumberNotNullFilterBean;
import com.finebi.conf.internalimp.bean.filtervalue.date.DateRangeValueBean;
import com.finebi.conf.internalimp.dashboard.widget.filter.JumpItemBean;
import com.finebi.conf.internalimp.dashboard.widget.filter.JumpSourceTargetFieldBean;
import com.finebi.conf.internalimp.dashboard.widget.filter.WidgetGlobalFilterBean;
import com.finebi.conf.internalimp.dashboard.widget.table.AbstractTableWidget;
import com.finebi.conf.internalimp.filter.GeneraAndFilter;
import com.finebi.conf.internalimp.filter.GeneraOrFilter;
import com.finebi.conf.internalimp.filter.number.NumberNotNullFilter;
import com.finebi.conf.internalimp.service.pack.FineConfManageCenter;
import com.finebi.conf.service.engine.relation.EngineRelationPathManager;
import com.finebi.conf.structure.bean.dashboard.widget.WidgetBean;
import com.finebi.conf.structure.bean.filter.DateFilterBean;
import com.finebi.conf.structure.bean.filter.FilterBean;
import com.finebi.conf.structure.dashboard.widget.dimension.FineDimension;
import com.finebi.conf.structure.dashboard.widget.field.WidgetBeanFieldValue;
import com.finebi.conf.structure.filter.FineFilter;
import com.fr.general.ComparatorUtils;
import com.fr.stable.StringUtils;
import com.fr.swift.adaptor.linkage.LinkageAdaptor;
import com.fr.swift.adaptor.transformer.FilterInfoFactory;
import com.fr.swift.adaptor.transformer.filter.date.DateUtils;
import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.query.aggregator.Aggregator;
import com.fr.swift.query.filter.SwiftDetailFilterType;
import com.fr.swift.query.filter.info.FilterInfo;
import com.fr.swift.query.filter.info.GeneralFilterInfo;
import com.fr.swift.query.filter.info.SwiftDetailFilterInfo;
import com.fr.swift.query.group.info.cursor.AllCursor;
import com.fr.swift.query.info.detail.DetailQueryInfo;
import com.fr.swift.query.info.element.dimension.DetailDimension;
import com.fr.swift.query.info.element.dimension.Dimension;
import com.fr.swift.query.info.element.dimension.DimensionInfoImpl;
import com.fr.swift.query.info.element.dimension.GroupDimension;
import com.fr.swift.query.info.element.metric.Metric;
import com.fr.swift.query.info.element.target.GroupTarget;
import com.fr.swift.query.info.element.target.cal.ResultTarget;
import com.fr.swift.query.info.element.target.cal.TargetInfoImpl;
import com.fr.swift.query.info.group.GroupQueryInfoImpl;
import com.fr.swift.query.query.QueryRunnerProvider;
import com.fr.swift.query.sort.AscSort;
import com.fr.swift.query.sort.DescSort;
import com.fr.swift.result.DetailResultSet;
import com.fr.swift.segment.column.ColumnKey;
import com.fr.swift.source.Row;
import com.fr.swift.source.SourceKey;
import com.fr.swift.source.SwiftResultSet;
import com.fr.swift.structure.array.IntList;
import com.fr.swift.structure.array.IntListFactory;
import com.fr.swift.utils.BusinessTableUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author pony
 * @date 2018/4/20
 */
public abstract class AbstractWidgetAdaptor {

    private static final FineConfManageCenter fineConfManageCenter = StableManager.getContext().getObject("fineConfManageCenter");
    private static final EngineRelationPathManager relationPathManager = fineConfManageCenter.getRelationPathProvider().get(FineEngineType.Cube);


    protected static SourceKey getSourceKey(String fieldId) {
        return new SourceKey(BusinessTableUtils.getSourceIdByFieldId(fieldId));
    }

    public static String getFieldId(FineDimension dimension) {
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

    protected static String getTableName(String fieldId) {
        try {
            return BusinessTableUtils.getTableByFieldId(fieldId).getName();
        } catch (FineEngineException e) {
            return StringUtils.EMPTY;
        }
    }

    static void setMaxMinNumValue(String queryId, String fieldId, List<FineFilter> fineFilters, NumberMaxAndMinValue value) throws Exception {
        SourceKey sourceKey = getSourceKey(fieldId);
        NumberNotNullFilter filter = new NumberNotNullFilter();
        NumberNotNullFilterBean bean = new NumberNotNullFilterBean();
        bean.setFieldId(fieldId);
        filter.setValue(bean);
        fineFilters.add(filter);
        FilterInfo filterInfo = FilterInfoFactory.transformFineFilter(getTableName(fieldId), fineFilters);

        //先通过明细表排序查最小
        DetailDimension ascDimension = new DetailDimension(0, sourceKey, new ColumnKey(getColumnName(fieldId)),
                null, new AscSort(0), filterInfo);
        IntList sortIndex = IntListFactory.createHeapIntList(1);
        sortIndex.add(0);
//        DetailQueryInfo minQueryInfo = new DetailQueryInfo(new AllCursor(), queryId, new DetailDimension[]{ascDimension}, sourceKey, null, sortIndex, null, null);
        DetailQueryInfo minQueryInfo = null;
        DetailResultSet minResultSet = QueryRunnerProvider.getInstance().executeQuery(minQueryInfo);
        minResultSet.next();
        Number min = minResultSet.getRowData().getValue(0);
        value.setMin(Math.min(value.getMin(), min.doubleValue()));

        //再通过明细表排序查最大
        DetailDimension descDimension = new DetailDimension(0, sourceKey, new ColumnKey(getColumnName(fieldId)),
                null, new DescSort(0), filterInfo);
//        DetailQueryInfo maxQueryInfo = new DetailQueryInfo(new AllCursor(), queryId, new DetailDimension[]{descDimension}, sourceKey, null, sortIndex, null, null);
        DetailQueryInfo maxQueryInfo = null;
        DetailResultSet maxResultSet = QueryRunnerProvider.getInstance().executeQuery(maxQueryInfo);
        maxResultSet.next();
        Number max = maxResultSet.getRowData().getValue(0);
        value.setMax(Math.max(value.getMax(), max.doubleValue()));
    }

    static NumberMaxAndMinValue getMaxMinNumValue(String fieldId) throws Exception {
        NumberMaxAndMinValue val = new NumberMaxAndMinValue();
        setMaxMinNumValue(fieldId, fieldId, new ArrayList<FineFilter>(), val);
        return val;
    }

    static void dealWithWidgetFilter(List<FilterInfo> filterInfoList, AbstractTableWidget widget) throws Exception {
        List<FineFilter> filters = dealWithTargetFilter(widget, widget.getFilters());
        if (filters != null && !filters.isEmpty()) {
            filterInfoList.add(FilterInfoFactory.transformFineFilter(widget.getTableName(), filters));
        }
    }

    static void dealWithDimensionDirectFilter(List<FilterInfo> filterInfoList, List<Dimension> dimensions) {
        // 维度上的直接过滤，提取出来
        for (Dimension dimension : dimensions) {
            // TODO: 2018/5/30
//            FilterInfo filter = dimension.getFilter();
//            if (filter != null && !filter.isMatchFilter()) {
//                filterInfoList.add(dimension.getFilter());
//            }
        }
    }

    protected static List<FineFilter> dealWithTargetFilter(AbstractTableWidget widget, List<FineFilter> fineFilters) {
        if (fineFilters == null) {
            return new ArrayList<FineFilter>();
        }
        List<FineFilter> target = new ArrayList<FineFilter>();
        String tableName = widget.getTableName();
        if (StringUtils.isEmpty(tableName)) {
            return fineFilters;
        }
        for (FineFilter filter : fineFilters) {
            AbstractFilterBean bean = (AbstractFilterBean) filter.getValue();
            switch (filter.getFilterType()) {
                case BICommonConstants.ANALYSIS_FILTER_TYPE.AND:
                    List<FineFilter> andFilters = dealWithTargetFilter(widget, ((GeneraAndFilter) filter).getFilters());
                    if (!andFilters.isEmpty()) {
                        GeneraAndFilter andFilter = new GeneraAndFilter();
                        andFilter.setFilters(andFilters);
                        andFilter.setValue((GeneraAndFilterBean) dealWithFilterBean(bean, andFilters));
                        target.add(andFilter);
                    }
                    break;
                case BICommonConstants.ANALYSIS_FILTER_TYPE.OR:
                    List<FineFilter> orFilters = dealWithTargetFilter(widget, ((GeneraOrFilter) filter).getFilters());
                    if (!orFilters.isEmpty()) {
                        GeneraOrFilter orFilter = new GeneraOrFilter();
                        orFilter.setFilters(orFilters);
                        orFilter.setValue((GeneraOrFilterBean) dealWithFilterBean(bean, orFilters));
                        target.add(orFilter);
                    }
                    break;
                default:
                    String primaryTable;
                    try {
                        primaryTable = getTableName(bean.getFieldId());
                    } catch (Exception e) {
                        primaryTable = StringUtils.EMPTY;
                    }
                    if (ComparatorUtils.equals(primaryTable, tableName)) {
                        createFilterByType(widget, target, filter);
                    } else {
                        try {
                            if (relationPathManager.isRelationPathExist(primaryTable, tableName)) {
                                createFilterByType(widget, target, filter);
                            }
                        } catch (FineEngineException ignore) {
                            SwiftLoggers.getLogger().info("Can not find relation");
                        }
                    }
                    break;
            }

        }
        return target;
    }

    private static void createFilterByType(AbstractTableWidget widget, List<FineFilter> target, FineFilter filter) {
        Map<String, WidgetBean> widgetBeanMap = widget.getDateWidgetIdValueMap();
        switch (filter.getFilterType()) {
            case BICommonConstants.ANALYSIS_FILTER_STRING.BELONG_WIDGET_VALUE:
            case BICommonConstants.ANALYSIS_FILTER_NUMBER.BELONG_WIDGET_VALUE:
            case BICommonConstants.ANALYSIS_FILTER_DATE.BELONG_WIDGET_VALUE:
                AbstractFilterBean bean = (AbstractFilterBean) filter.getValue();
                BelongWidgetValue widgetValue = (BelongWidgetValue) bean.getFilterValue();
                createFilterByWidgetType(widgetValue.getWidgetId(), widgetBeanMap, target);
                break;
            default:
                target.add(filter);
        }

    }

    private static void createFilterByWidgetType(String widgetId, Map<String, WidgetBean> widgetBeanMap, List<FineFilter> target) {
        if (widgetBeanMap != null && widgetBeanMap.containsKey(widgetId)) {
            WidgetBean widgetBean = widgetBeanMap.get(widgetId);
            WidgetFilterVisitor visitor = new WidgetFilterVisitor();
            try {
                List<FineFilter> filters = widgetBean.accept(visitor);
                if (filters != null) {
                    target.addAll(filters);
                }
            } catch (Exception e) {
                SwiftLoggers.getLogger().error("add widget filter failed", e);
            }
        }
    }

    private static AbstractFilterBean dealWithFilterBean(AbstractFilterBean bean, List<FineFilter> filters) {
        List<FilterBean> filterBeans = new ArrayList<FilterBean>();
        for (FineFilter filter : filters) {
            if (null != filter.getValue()) {
                filterBeans.add((AbstractFilterBean) filter.getValue());
            }
        }
        switch (bean.getFilterType()) {
            case BICommonConstants.ANALYSIS_FILTER_TYPE.AND:
                GeneraAndFilterBean andBean = new GeneraAndFilterBean();
                andBean.setFilterValue(filterBeans);
                return andBean;
            case BICommonConstants.ANALYSIS_FILTER_TYPE.OR:
                GeneraOrFilterBean orBean = new GeneraOrFilterBean();
                orBean.setFilterValue(filterBeans);
                return orBean;
            default:
                return bean;
        }
    }

    public static String getFormula(String fieldId, AbstractTableWidget widget) {
        WidgetBeanField field = widget.getFieldByFieldId(fieldId);
        FormulaValueBean calculate = (FormulaValueBean) field.getCalculate();
        String formula = calculate.getValue();
        for (String targetId : field.getTargetIds()) {
            String subFormula = getFiledFormula(targetId, widget);
            if (subFormula != null) {
                formula = formula.replace(toParameter(targetId), subFormula);
            }
        }
        formula = FilterInfoFactory.transformFormula(formula, widget.getTableName());
        return formula;
    }

    private static String getWidgetBeanValue(WidgetBean widgetBean) {
        int type = widgetBean.getType();
        switch (type) {
            case BIDesignConstants.DESIGN.WIDGET.DATE_INTERVAL:
            case BIDesignConstants.DESIGN.WIDGET.YEAR_MONTH_INTERVAL: {
                // TODO: 2018/5/12 这边传过来的数据结构有问题，居然是map结构。。应该转为DateFilterBean的
                DateRangeValueBean dateRangeValueBean = (DateRangeValueBean) ((AbstractTimeControlBean) widgetBean).getValue();
                long start = DateUtils.dateFilterBean2Long(dateRangeValueBean.getStart(), true);
                long end = DateUtils.dateFilterBean2Long(dateRangeValueBean.getEnd(), false);
                return formatDate(start) + ", " + formatDate(end);
            }
            case BIDesignConstants.DESIGN.WIDGET.DATE:
            case BIDesignConstants.DESIGN.WIDGET.DATE_PANE:
            case BIDesignConstants.DESIGN.WIDGET.YEAR:
            case BIDesignConstants.DESIGN.WIDGET.QUARTER:
            case BIDesignConstants.DESIGN.WIDGET.MONTH: {
                DateFilterBean dateFilterBean = (DateFilterBean) ((AbstractTimeControlBean) widgetBean).getValue();
                long time = dateFilterBean == null ? System.currentTimeMillis() : DateUtils.dateFilterBean2Long(dateFilterBean, true);
                return formatDate(time);
            }
            default:
                return formatDate(System.currentTimeMillis());
        }
    }

    private static String formatDate(long time) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        return "\"" + format.format(new Date(time)) + "\"";
    }

    private static String getFiledFormula(String fieldId, AbstractTableWidget widget) {
        Map<String, WidgetBean> dateWidgetIdValueMap = widget.getValue().getDateWidgetIdValueMap();
        if (dateWidgetIdValueMap != null && dateWidgetIdValueMap.containsKey(fieldId)) {
            return getWidgetBeanValue(dateWidgetIdValueMap.get(fieldId));
        }
        WidgetBeanField field = widget.getFieldByFieldId(fieldId);
        WidgetBeanFieldValue widgetBeanFieldValue = field.getCalculate();
        if (widgetBeanFieldValue != null) {
            FormulaValueBean calculate = (FormulaValueBean) field.getCalculate();
            String formula = calculate.getValue();
            for (String targetId : field.getTargetIds()) {
                String subFormula = getFiledFormula(targetId, widget);
                if (subFormula != null) {
                    formula = formula.replace(toParameter(targetId), subFormula);
                }
            }
            return formula;
        } else {
            return null;
        }

    }

    private static String toParameter(String targetId) {
        return "${" + targetId + "}";
    }

    private static void handleCrossTempletCustomLink(String tableName, WidgetGlobalFilterBean globalBean, JumpItemBean jump, List<FilterInfo> filterInfos) throws Exception {
        // todo 提炼公共部分 或者这边widget可以进行实例化的重构，减少参数传来传去
        List<JumpSourceTargetFieldBean> sourceTargetFields = jump.getSourceTargetFields();
        // 自定义设置的维度
        Dimension[] fromColumns = new Dimension[sourceTargetFields.size()];
        // 要过滤的维度
        String[] toColumns = new String[sourceTargetFields.size()];
        for (int i = 0; i < sourceTargetFields.size(); i++) {
            JumpSourceTargetFieldBean bean = sourceTargetFields.get(i);
            String from = bean.getSourceFieldId();
            ColumnKey fromKey = new ColumnKey(getColumnName(from));
            fromColumns[i] = (new GroupDimension(i, getSourceKey(from), fromKey, null, new AscSort(i, fromKey), null));
            toColumns[i] = getColumnName(bean.getTargetFieldId());
        }

        // 根据点击的值，创建过滤条件
        List<FilterInfo> filters = new ArrayList<FilterInfo>();
        LinkageAdaptor.handleCrossTempletClick(tableName, globalBean, filters, fromColumns, toColumns);
        // 分组表查询
        FilterInfo filterInfo = new GeneralFilterInfo(filters, GeneralFilterInfo.AND);
        GroupQueryInfoImpl queryInfo = new GroupQueryInfoImpl(globalBean.getLinkedWidget().getwId(), fromColumns[0].getSourceKey(),
                new DimensionInfoImpl(new AllCursor(), filterInfo, null, fromColumns),
                new TargetInfoImpl(0, new ArrayList<Metric>(0), new ArrayList<GroupTarget>(0), new ArrayList<ResultTarget>(0), new ArrayList<Aggregator>(0)));
        SwiftResultSet resultSet = QueryRunnerProvider.getInstance().executeQuery(queryInfo);
        Set[] results = new HashSet[toColumns.length];
        for (int i = 0; i < results.length; i++) {
            results[i] = new HashSet();
        }
        while (resultSet.next()) {
            Row row = resultSet.getRowData();
            for (int i = 0; i < row.getSize(); i++) {
                results[i].add(row.getValue(i));
            }
        }
        for (int i = 0; i < toColumns.length; i++) {
            filterInfos.add(new SwiftDetailFilterInfo(new ColumnKey(toColumns[i]), results[i], SwiftDetailFilterType.STRING_IN));
        }
    }

    static void handleCrossTempletLink(List<FilterInfo> filterInfos, AbstractTableWidget widget) throws Exception {
        // 跨模板联动
        WidgetGlobalFilterBean globalFilter = widget.getValue().getGlobalFilter();
        if (globalFilter == null) {
            return;
        }
        List<JumpItemBean> jumps = globalFilter.getLinkedWidget().getJump();
        if (jumps == null || jumps.isEmpty()) {
            // 联动过滤
            LinkageAdaptor.handleCrossTempletClick(widget.getTableName(), globalFilter, filterInfos);
            return;
        }
        JumpItemBean jump = jumps.get(0);
        if (jump.isPassValue()) {
            // 值过滤
            handleCrossTempletCustomLink(widget.getTableName(), globalFilter, jump, filterInfos);
        } else {
            LinkageAdaptor.handleCrossTempletClick(widget.getTableName(), globalFilter, filterInfos);
        }
    }
}