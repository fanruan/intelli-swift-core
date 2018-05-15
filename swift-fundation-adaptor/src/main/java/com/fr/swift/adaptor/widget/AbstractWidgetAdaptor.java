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
import com.finebi.conf.internalimp.bean.filter.GeneraAndFilterBean;
import com.finebi.conf.internalimp.bean.filter.GeneraOrFilterBean;
import com.finebi.conf.internalimp.bean.filtervalue.date.DateRangeValueBean;
import com.finebi.conf.internalimp.dashboard.widget.table.AbstractTableWidget;
import com.finebi.conf.internalimp.filter.GeneraAndFilter;
import com.finebi.conf.internalimp.filter.GeneraOrFilter;
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
import com.fr.swift.adaptor.transformer.FilterInfoFactory;
import com.fr.swift.adaptor.transformer.date.DateUtils;
import com.fr.swift.cal.info.DetailQueryInfo;
import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.query.adapter.dimension.AllCursor;
import com.fr.swift.query.adapter.dimension.DetailDimension;
import com.fr.swift.query.adapter.dimension.Dimension;
import com.fr.swift.query.filter.info.FilterInfo;
import com.fr.swift.query.sort.AscSort;
import com.fr.swift.query.sort.DescSort;
import com.fr.swift.result.DetailResultSet;
import com.fr.swift.segment.column.ColumnKey;
import com.fr.swift.service.QueryRunnerProvider;
import com.fr.swift.source.SourceKey;
import com.fr.swift.structure.array.IntList;
import com.fr.swift.structure.array.IntListFactory;
import com.fr.swift.utils.BusinessTableUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;

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
        FilterInfo filterInfo = FilterInfoFactory.transformFineFilter(getTableName(fieldId), fineFilters);

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

    static void dealWithWidgetFilter(List<FilterInfo> filterInfoList, AbstractTableWidget widget) throws Exception {
        List<FineFilter> filters = dealWithTargetFilter(widget, widget.getFilters());
        if (filters != null && !filters.isEmpty()) {
            filterInfoList.add(FilterInfoFactory.transformFineFilter(widget.getTableName(), filters));
        }
    }

    static void dealWithDimensionDirectFilter(List<FilterInfo> filterInfoList, List<Dimension> dimensions) {
        // 维度上的直接过滤，提取出来
        for (Dimension dimension : dimensions) {
            FilterInfo filter = dimension.getFilter();
            if (filter != null && !filter.isMatchFilter()) {
                filterInfoList.add(dimension.getFilter());
            }
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
                    String primaryTable = null;
                    try {
                        primaryTable = getTableName(bean.getFieldId());
                    } catch (Exception e) {
                        primaryTable = StringUtils.EMPTY;
                    }
                    if (ComparatorUtils.equals(primaryTable, tableName)) {
                        target.add(filter);
                    } else {
                        try {
                            if (relationPathManager.isRelationPathExist(primaryTable, tableName)) {
                                target.add(filter);
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
            if (subFormula != null){
                formula = formula.replace(toParameter(targetId), subFormula);
            }
        }
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
                long time = DateUtils.dateFilterBean2Long(dateFilterBean, true);
                return formatDate(time);
            }
        }
        return formatDate(new Date().getTime());
    }

    private static String formatDate(long time) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        return "\"" + format.format(new Date(time)) + "\"";
    }

    protected static String getFiledFormula(String fieldId, AbstractTableWidget widget) {
        Map<String, WidgetBean> dateWidgetIdValueMap = widget.getValue().getDateWidgetIdValueMap();
        if (dateWidgetIdValueMap != null && dateWidgetIdValueMap.containsKey(fieldId)){
            return getWidgetBeanValue(dateWidgetIdValueMap.get(fieldId));
        }
        WidgetBeanField field = widget.getFieldByFieldId(fieldId);
        WidgetBeanFieldValue widgetBeanFieldValue = field.getCalculate();
        if (widgetBeanFieldValue!= null){
            FormulaValueBean calculate = (FormulaValueBean) field.getCalculate();
            String formula = calculate.getValue();
            for (String targetId : field.getTargetIds()) {
                String subFormula = getFiledFormula(targetId, widget);
                if (subFormula != null){
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
}
