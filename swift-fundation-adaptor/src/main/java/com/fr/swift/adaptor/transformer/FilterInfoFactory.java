package com.fr.swift.adaptor.transformer;

import com.finebi.base.constant.FineEngineType;
import com.finebi.base.stable.StableManager;
import com.finebi.base.utils.FineNullValue;
import com.finebi.conf.constant.BICommonConstants;
import com.finebi.conf.exception.FineEngineException;
import com.finebi.conf.internalimp.bean.filter.AbstractFilterBean;
import com.finebi.conf.internalimp.bean.filter.FormulaFilterBean;
import com.finebi.conf.internalimp.bean.filter.GeneraAndFilterBean;
import com.finebi.conf.internalimp.bean.filter.GeneraOrFilterBean;
import com.finebi.conf.internalimp.bean.filter.date.DateAfterFilterBean;
import com.finebi.conf.internalimp.bean.filter.date.DateAfterWidgetFilterBean;
import com.finebi.conf.internalimp.bean.filter.date.DateBeforeFilterBean;
import com.finebi.conf.internalimp.bean.filter.date.DateBeforeWidgetFilterBean;
import com.finebi.conf.internalimp.bean.filter.date.DateBelongFilterBean;
import com.finebi.conf.internalimp.bean.filter.date.DateBelongStringFilterBean;
import com.finebi.conf.internalimp.bean.filter.date.DateBelongWidgetFilterBean;
import com.finebi.conf.internalimp.bean.filter.date.DateBottomNFilterBean;
import com.finebi.conf.internalimp.bean.filter.date.DateEqualFilterBean;
import com.finebi.conf.internalimp.bean.filter.date.DateEqualWidgetFilterBean;
import com.finebi.conf.internalimp.bean.filter.date.DateNoBelongFilterBean;
import com.finebi.conf.internalimp.bean.filter.date.DateNoBelongStringFilterBean;
import com.finebi.conf.internalimp.bean.filter.date.DateNoBelongWidgetFilterBean;
import com.finebi.conf.internalimp.bean.filter.date.DateNoEqualFilterBean;
import com.finebi.conf.internalimp.bean.filter.date.DateNoEqualWidgetFilterBean;
import com.finebi.conf.internalimp.bean.filter.date.DateTopNFilterBean;
import com.finebi.conf.internalimp.bean.filter.number.NumberBelongFilterBean;
import com.finebi.conf.internalimp.bean.filter.number.NumberBottomNFilterBean;
import com.finebi.conf.internalimp.bean.filter.number.NumberEqualFilterBean;
import com.finebi.conf.internalimp.bean.filter.number.NumberLargeFilterBean;
import com.finebi.conf.internalimp.bean.filter.number.NumberLargeOrEqualFilterBean;
import com.finebi.conf.internalimp.bean.filter.number.NumberNoBelongFilterBean;
import com.finebi.conf.internalimp.bean.filter.number.NumberNoEqualFilterBean;
import com.finebi.conf.internalimp.bean.filter.number.NumberSmallFilterBean;
import com.finebi.conf.internalimp.bean.filter.number.NumberSmallOrEqualFilterBean;
import com.finebi.conf.internalimp.bean.filter.number.NumberTopNFilterBean;
import com.finebi.conf.internalimp.bean.filter.string.StringBeginWithFilterBean;
import com.finebi.conf.internalimp.bean.filter.string.StringBelongFilterBean;
import com.finebi.conf.internalimp.bean.filter.string.StringBottomNFilterBean;
import com.finebi.conf.internalimp.bean.filter.string.StringContainFilterBean;
import com.finebi.conf.internalimp.bean.filter.string.StringEndWithFilterBean;
import com.finebi.conf.internalimp.bean.filter.string.StringNoBeginWithFilterBean;
import com.finebi.conf.internalimp.bean.filter.string.StringNoBelongFilterBean;
import com.finebi.conf.internalimp.bean.filter.string.StringNoContainFilterBean;
import com.finebi.conf.internalimp.bean.filter.string.StringNoEndWithFilterBean;
import com.finebi.conf.internalimp.bean.filter.string.StringTopNFilterBean;
import com.finebi.conf.internalimp.bean.filtervalue.date.DateRangeValueBean;
import com.finebi.conf.internalimp.bean.filtervalue.date.DateWidgetBean;
import com.finebi.conf.internalimp.bean.filtervalue.number.NumberSelectedFilterValueBean;
import com.finebi.conf.internalimp.bean.filtervalue.number.NumberValue;
import com.finebi.conf.internalimp.bean.filtervalue.string.StringBelongFilterValueBean;
import com.finebi.conf.internalimp.service.pack.FineConfManageCenter;
import com.finebi.conf.service.engine.relation.EngineRelationPathManager;
import com.finebi.conf.structure.bean.filter.DateFilterBean;
import com.finebi.conf.structure.bean.filter.FilterBean;
import com.finebi.conf.structure.filter.FineFilter;
import com.finebi.conf.structure.path.FineBusinessTableRelationPath;
import com.fr.general.ComparatorUtils;
import com.fr.stable.StringUtils;
import com.fr.swift.adaptor.transformer.cal.AvgUtils;
import com.fr.swift.adaptor.transformer.filter.date.DateRangeValueBeanAdaptor;
import com.fr.swift.adaptor.transformer.filter.date.DateUtils;
import com.fr.swift.adaptor.transformer.filter.date.DateWidgetBeanAdaptor;
import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.query.filter.SwiftDetailFilterType;
import com.fr.swift.query.filter.detail.impl.number.NumberAverageFilter;
import com.fr.swift.query.filter.info.FilterInfo;
import com.fr.swift.query.filter.info.GeneralFilterInfo;
import com.fr.swift.query.filter.info.SwiftDetailFilterInfo;
import com.fr.swift.query.filter.info.value.SwiftDateInRangeFilterValue;
import com.fr.swift.query.filter.info.value.SwiftNumberInRangeFilterValue;
import com.fr.swift.query.filter.match.MatchConverter;
import com.fr.swift.segment.Segment;
import com.fr.swift.segment.column.ColumnKey;
import com.fr.swift.source.RelationSource;
import com.fr.swift.utils.BusinessTableUtils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by pony on 2017/12/21.
 */
public class FilterInfoFactory {
    private static final FineConfManageCenter fineConfManageCenter = StableManager.getContext().getObject("fineConfManageCenter");
    private static final EngineRelationPathManager relationPathManager = fineConfManageCenter.getRelationPathProvider().get(FineEngineType.Cube);

    /**
     * @param tableName widget自身的表名，没表名直接传null
     * @param filters
     * @return
     */
    public static FilterInfo transformFineFilter(String tableName, List<FineFilter> filters) {
        List<FilterBean> beans = new ArrayList<FilterBean>();
        for (FineFilter filter : filters) {
            if (filter.getValue() != null) {
                beans.add((FilterBean) filter.getValue());
            }
        }
        return transformFilterBean(tableName, beans, new ArrayList<Segment>());
    }

    public static FilterInfo transformFilterBean(String tableName, List<FilterBean> beans, List<Segment> segments) {
        List<FilterInfo> filterInfoList = new ArrayList<FilterInfo>();
        for (FilterBean bean : beans) {
            filterInfoList.add(createFilterInfo(tableName, bean, segments));
        }
        return new GeneralFilterInfo(filterInfoList, GeneralFilterInfo.AND);
    }

    public static String transformFormula(String formula, String tableName) {
        if (StringUtils.isEmpty(tableName)) {
            return formula;
        }
        int len = tableName.length();
        StringBuffer buffer = new StringBuffer(String.format("%04d", len));
        buffer.append(tableName);
        return formula.replaceAll(buffer.toString(), StringUtils.EMPTY);
    }

    /**
     * @param tableName 控件的表名，没表名传null或者空字符串
     * @param bean
     * @param segments
     * @return
     */
    public static FilterInfo createFilterInfo(String tableName, FilterBean bean, List<Segment> segments) {
        String fieldId = ((AbstractFilterBean) bean).getFieldId();
        // 分析表这边的bean暂时没有FieldId，所以还是取FieldName
        String fieldName = StringUtils.isEmpty(fieldId) ?
                ((AbstractFilterBean) bean).getFieldName() : BusinessTableUtils.getFieldNameByFieldId(fieldId);
        int type = bean.getFilterType();
        ColumnKey columnKey = new ColumnKey(fieldName);
        String primaryTable = null;
        try {
            primaryTable = BusinessTableUtils.getTableByFieldId(((AbstractFilterBean) bean).getFieldId()).getName();
        } catch (Exception e) {
            primaryTable = StringUtils.EMPTY;
        }
        if (StringUtils.isNotEmpty(tableName) && StringUtils.isNotEmpty(primaryTable) && !ComparatorUtils.equals(primaryTable, tableName)) {
            List<FineBusinessTableRelationPath> paths = new ArrayList<FineBusinessTableRelationPath>();
            try {
                paths.addAll(relationPathManager.getRelationPaths(primaryTable, tableName));
            } catch (FineEngineException ignore) {
                SwiftLoggers.getLogger().info("Cannot find relation! ");
                return new SwiftDetailFilterInfo(columnKey, null, SwiftDetailFilterType.ALL_SHOW);
            }
            if (!paths.isEmpty()) {
                RelationSource relationSource = RelationSourceFactory.transformRelationSourcesFromPath(paths.get(0));
                columnKey.setRelation(relationSource);
            }
        }
        switch (type) {
            // string类过滤
            case BICommonConstants.ANALYSIS_FILTER_STRING.BELONG_VALUE: {
                StringBelongFilterValueBean valueBean = ((StringBelongFilterBean) bean).getFilterValue();
                List<String> belongValues = valueBean.getValue();
                if (belongValues == null || belongValues.isEmpty()) {
                    break;
                }
                int valueType = valueBean.getType();
                return new SwiftDetailFilterInfo<Set<String>>(columnKey, new HashSet<String>(belongValues),
                        // 多选同filterType，否则是反选
                        valueType == BICommonConstants.SELECTION_TYPE.MULTI ? SwiftDetailFilterType.STRING_IN : SwiftDetailFilterType.STRING_NOT_IN);
            }
            case BICommonConstants.ANALYSIS_FILTER_STRING.NOT_BELONG_VALUE: {
                StringBelongFilterValueBean valueBean = ((StringNoBelongFilterBean) bean).getFilterValue();
                List<String> notBelongValues = valueBean.getValue();
                if (notBelongValues == null || notBelongValues.isEmpty()) {
                    return new SwiftDetailFilterInfo(columnKey, null, SwiftDetailFilterType.NOT_SHOW);
                }
                int valueType = valueBean.getType();
                return new SwiftDetailFilterInfo<Set<String>>(columnKey, new HashSet<String>(notBelongValues),
                        // 多选同filterType，否则是反选
                        valueType == BICommonConstants.SELECTION_TYPE.MULTI ? SwiftDetailFilterType.STRING_NOT_IN : SwiftDetailFilterType.STRING_IN);
            }
            case BICommonConstants.ANALYSIS_FILTER_DATE.BELONG_STRING_VALUE: {
                StringBelongFilterValueBean filterValueBean = ((DateBelongStringFilterBean) bean).getFilterValue();
                List<String> dates = filterValueBean.getValue();
                if (dates == null || dates.isEmpty()) {
                    break;
                }
                int valueType = filterValueBean.getType();
                return new SwiftDetailFilterInfo<List<String>>(columnKey, dates,
                        valueType == BICommonConstants.SELECTION_TYPE.MULTI ? SwiftDetailFilterType.STRING_IN : SwiftDetailFilterType.STRING_NOT_IN);
            }
            case BICommonConstants.ANALYSIS_FILTER_DATE.NOT_BELONG_STRING_VALUE: {
                StringBelongFilterValueBean filterValueBean = ((DateNoBelongStringFilterBean) bean).getFilterValue();
                List<String> dates = filterValueBean.getValue();
                if (dates == null || dates.isEmpty()) {
                    return new SwiftDetailFilterInfo(columnKey, null, SwiftDetailFilterType.NOT_SHOW);
                }
                int valueType = filterValueBean.getType();
                return new SwiftDetailFilterInfo<List<String>>(columnKey, dates,
                        valueType == BICommonConstants.SELECTION_TYPE.MULTI ? SwiftDetailFilterType.STRING_IN : SwiftDetailFilterType.STRING_IN);
            }
            case BICommonConstants.ANALYSIS_FILTER_STRING.CONTAIN:
                String contain = ((StringContainFilterBean) bean).getFilterValue();
                if (StringUtils.isBlank(contain)) {
                    break;
                }
                return new SwiftDetailFilterInfo<String>(columnKey, contain, SwiftDetailFilterType.STRING_LIKE);
            case BICommonConstants.ANALYSIS_FILTER_STRING.NOT_CONTAIN: {
                String value = ((StringNoContainFilterBean) bean).getFilterValue();
                if (StringUtils.isBlank(value)) {
                    return new SwiftDetailFilterInfo(columnKey, null, SwiftDetailFilterType.NOT_SHOW);
                }
                return new SwiftDetailFilterInfo<String>(columnKey, value, SwiftDetailFilterType.STRING_NOT_LIKE);
            }
            case BICommonConstants.ANALYSIS_FILTER_STRING.BEGIN_WITH: {
                String value = ((StringBeginWithFilterBean) bean).getFilterValue();
                if (StringUtils.isBlank(value)) {
                    break;
                }
                return new SwiftDetailFilterInfo<String>(columnKey, value, SwiftDetailFilterType.STRING_STARTS_WITH);
            }
            case BICommonConstants.ANALYSIS_FILTER_STRING.NOT_BEGIN_WITH: {
                String value = ((StringNoBeginWithFilterBean) bean).getFilterValue();
                if (StringUtils.isBlank(value)) {
                    return new SwiftDetailFilterInfo(columnKey, null, SwiftDetailFilterType.NOT_SHOW);
                }
                return new SwiftDetailFilterInfo<String>(columnKey, value, SwiftDetailFilterType.STRING_NOT_STARTS_WITH);
            }
            case BICommonConstants.ANALYSIS_FILTER_STRING.END_WITH: {
                String value = ((StringEndWithFilterBean) bean).getFilterValue();
                if (StringUtils.isBlank(value)) {
                    break;
                }
                return new SwiftDetailFilterInfo<String>(columnKey, value, SwiftDetailFilterType.STRING_ENDS_WITH);
            }
            case BICommonConstants.ANALYSIS_FILTER_STRING.NOT_END_WITH: {
                String value = ((StringNoEndWithFilterBean) bean).getFilterValue();
                if (StringUtils.isBlank(value)) {
                    return new SwiftDetailFilterInfo(columnKey, null, SwiftDetailFilterType.NOT_SHOW);
                }
                return new SwiftDetailFilterInfo<String>(columnKey, value, SwiftDetailFilterType.STRING_NOT_ENDS_WITH);
            }
            case BICommonConstants.ANALYSIS_FILTER_STRING.IS_NULL:
                return new SwiftDetailFilterInfo<Object>(columnKey, null, SwiftDetailFilterType.NULL);
            case BICommonConstants.ANALYSIS_FILTER_STRING.NOT_NULL:
                return new SwiftDetailFilterInfo<Object>(columnKey, null, SwiftDetailFilterType.NOT_NULL);
            case BICommonConstants.ANALYSIS_FILTER_STRING.TOP_N: {
                Long filterValue = ((StringTopNFilterBean) bean).getFilterValue();
                if (filterValue == null) {
                    break;
                }
                int n = filterValue.intValue();
                // 功能的前N个对应字典排序中最小的N个
                return new SwiftDetailFilterInfo<Integer>(columnKey, n, SwiftDetailFilterType.BOTTOM_N);
            }
            case BICommonConstants.ANALYSIS_FILTER_STRING.BOTTOM_N: {
                Long filterValue = ((StringBottomNFilterBean) bean).getFilterValue();
                if (filterValue == null) {
                    break;
                }
                int n = filterValue.intValue();
                // 功能的后N个对应字典排序中最大的N个
                return new SwiftDetailFilterInfo<Integer>(columnKey, n, SwiftDetailFilterType.TOP_N);
            }


            // 数值类过滤
            case BICommonConstants.ANALYSIS_FILTER_NUMBER.BELONG_VALUE: {
                NumberValue nv = ((NumberBelongFilterBean) bean).getFilterValue();
                if (nv.getMin() == FineNullValue.DOUBLE && nv.getMax() == FineNullValue.DOUBLE) {
                    break;
                }
                if (nv.getMin() == FineNullValue.DOUBLE) {
                    nv.setMin(Double.NEGATIVE_INFINITY);
                }
                if (nv.getMax() == FineNullValue.DOUBLE) {
                    nv.setMax(Double.POSITIVE_INFINITY);
                }
                return new SwiftDetailFilterInfo<SwiftNumberInRangeFilterValue>(columnKey, createValue(nv),
                        SwiftDetailFilterType.NUMBER_IN_RANGE);
            }
            case BICommonConstants.ANALYSIS_FILTER_NUMBER.NOT_BELONG_VALUE: {
                NumberValue nv = ((NumberNoBelongFilterBean) bean).getFilterValue();
                if (nv.getMin() == FineNullValue.DOUBLE && nv.getMax() == FineNullValue.DOUBLE) {
                    return new SwiftDetailFilterInfo(columnKey, null, SwiftDetailFilterType.NOT_SHOW);
                }
                if (nv.getMin() == FineNullValue.DOUBLE) {
                    nv.setMin(Double.NEGATIVE_INFINITY);
                }
                if (nv.getMax() == FineNullValue.DOUBLE) {
                    nv.setMax(Double.POSITIVE_INFINITY);
                }
                return new SwiftDetailFilterInfo<SwiftNumberInRangeFilterValue>(columnKey, createValue(nv),
                        SwiftDetailFilterType.NUMBER_NOT_IN_RANGE);
            }
            case BICommonConstants.ANALYSIS_FILTER_NUMBER.EQUAL_TO: {
                final Double value = ((NumberEqualFilterBean) bean).getFilterValue();
                return new SwiftDetailFilterInfo<Set<Double>>(columnKey, new HashSet<Double>() {{
                    add(value);
                }},
                        SwiftDetailFilterType.NUMBER_CONTAIN);
            }
            case BICommonConstants.ANALYSIS_FILTER_NUMBER.NOT_EQUAL_TO: {
                final Double value = ((NumberNoEqualFilterBean) bean).getFilterValue();
                return new SwiftDetailFilterInfo<Set<Double>>(columnKey, new HashSet<Double>() {{
                    add(value);
                }},
                        SwiftDetailFilterType.NUMBER_NOT_CONTAIN);
            }
            case BICommonConstants.ANALYSIS_FILTER_NUMBER.LARGE: {
                NumberSelectedFilterValueBean numberBean = ((NumberLargeFilterBean) bean).getFilterValue();
                if (numberBean.getValue() == null) {
                    break;
                }
                SwiftNumberInRangeFilterValue filterValue = new SwiftNumberInRangeFilterValue();
                filterValue.setMin(createValue(numberBean, segments, fieldName, fieldId, tableName));
                return new SwiftDetailFilterInfo<SwiftNumberInRangeFilterValue>(columnKey, filterValue,
                        isAverage(numberBean) ? SwiftDetailFilterType.NUMBER_AVERAGE : SwiftDetailFilterType.NUMBER_IN_RANGE);
            }
            case BICommonConstants.ANALYSIS_FILTER_NUMBER.SMALL: {
                NumberSelectedFilterValueBean numberBean = ((NumberSmallFilterBean) bean).getFilterValue();
                if (numberBean.getValue() == null) {
                    break;
                }
                SwiftNumberInRangeFilterValue value = new SwiftNumberInRangeFilterValue();
                value.setMax(createValue(numberBean, segments, fieldName, fieldId, tableName));
                return new SwiftDetailFilterInfo<SwiftNumberInRangeFilterValue>(columnKey, value,
                        isAverage(numberBean) ? SwiftDetailFilterType.NUMBER_AVERAGE : SwiftDetailFilterType.NUMBER_IN_RANGE);
            }
            case BICommonConstants.ANALYSIS_FILTER_NUMBER.LARGE_OR_EQUAL: {
                NumberSelectedFilterValueBean numberBean = ((NumberLargeOrEqualFilterBean) bean).getFilterValue();
                if (numberBean.getValue() == null) {
                    break;
                }
                SwiftNumberInRangeFilterValue value = new SwiftNumberInRangeFilterValue();
                value.setMin(createValue(numberBean, segments, fieldName, fieldId, tableName));
                value.setMinIncluded(true);
                return new SwiftDetailFilterInfo<SwiftNumberInRangeFilterValue>(columnKey, value,
                        isAverage(numberBean) ? SwiftDetailFilterType.NUMBER_AVERAGE : SwiftDetailFilterType.NUMBER_IN_RANGE);
            }
            case BICommonConstants.ANALYSIS_FILTER_NUMBER.SMALL_OR_EQUAL: {
                NumberSelectedFilterValueBean numberBean = ((NumberSmallOrEqualFilterBean) bean).getFilterValue();
                if (numberBean.getValue() == null) {
                    break;
                }
                SwiftNumberInRangeFilterValue value = new SwiftNumberInRangeFilterValue();
                value.setMax(createValue(numberBean, segments, fieldName, fieldId, tableName));
                value.setMaxIncluded(true);
                return new SwiftDetailFilterInfo<SwiftNumberInRangeFilterValue>(columnKey, value,
                        isAverage(numberBean) ? SwiftDetailFilterType.NUMBER_AVERAGE : SwiftDetailFilterType.NUMBER_IN_RANGE);
            }
            case BICommonConstants.ANALYSIS_FILTER_NUMBER.TOP_N: {
                int n = ((NumberTopNFilterBean) bean).getFilterValue().intValue();
                return new SwiftDetailFilterInfo<Integer>(columnKey, n, SwiftDetailFilterType.TOP_N);
            }
            case BICommonConstants.ANALYSIS_FILTER_NUMBER.BOTTOM_N: {
                int n = ((NumberBottomNFilterBean) bean).getFilterValue().intValue();
                return new SwiftDetailFilterInfo<Integer>(columnKey, n, SwiftDetailFilterType.BOTTOM_N);
            }
            case BICommonConstants.ANALYSIS_FILTER_NUMBER.IS_NULL:
                return new SwiftDetailFilterInfo<Object>(columnKey, null, SwiftDetailFilterType.NULL);
            case BICommonConstants.ANALYSIS_FILTER_NUMBER.NOT_NULL:
                return new SwiftDetailFilterInfo<Object>(columnKey, null, SwiftDetailFilterType.NOT_NULL);

            // 日期类过滤
            case BICommonConstants.ANALYSIS_FILTER_DATE.BELONG_VALUE: {
                DateRangeValueBean dateValueBean = ((DateBelongFilterBean) bean).getFilterValue();
                if (dateValueBean.getStart() == null && dateValueBean.getEnd() == null) {
                    break;
                }
                return new SwiftDetailFilterInfo<SwiftDateInRangeFilterValue>(columnKey,
                        DateRangeValueBeanAdaptor.create(dateValueBean), SwiftDetailFilterType.DATE_IN_RANGE);
            }
            case BICommonConstants.ANALYSIS_FILTER_DATE.NOT_BELONG_VALUE: {
                DateRangeValueBean dateValueBean = ((DateNoBelongFilterBean) bean).getFilterValue();
                if (dateValueBean.getStart() == null && dateValueBean.getEnd() == null) {
                    return new SwiftDetailFilterInfo(columnKey, null, SwiftDetailFilterType.NOT_SHOW);
                }
                return new SwiftDetailFilterInfo<SwiftDateInRangeFilterValue>(columnKey,
                        DateRangeValueBeanAdaptor.create(dateValueBean), SwiftDetailFilterType.DATE_NOT_IN_RANGE);
            }
            case BICommonConstants.ANALYSIS_FILTER_DATE.LESS_THAN: {
                DateFilterBean dateFilterBean = ((DateBeforeFilterBean) bean).getFilterValue();
                return createDateLessThanFilterInfo(columnKey, dateFilterBean);
            }
            case BICommonConstants.ANALYSIS_FILTER_DATE.MORE_THAN: {
                DateFilterBean dateFilterBean = ((DateAfterFilterBean) bean).getFilterValue();
                return createDateMoreThanFilterInfo(columnKey, dateFilterBean);
            }
            case BICommonConstants.ANALYSIS_FILTER_DATE.EQUAL_TO: {
                // 最小单位是天
                DateFilterBean dateFilterBean = ((DateEqualFilterBean) bean).getFilterValue();
                return new SwiftDetailFilterInfo<SwiftDateInRangeFilterValue>(columnKey,
                        DateUtils.create(dateFilterBean), SwiftDetailFilterType.DATE_IN_RANGE);
            }
            case BICommonConstants.ANALYSIS_FILTER_DATE.NOT_EQUAL_TO: {
                // 最小单位是天
                DateFilterBean dateFilterBean = ((DateNoEqualFilterBean) bean).getFilterValue();
                return new SwiftDetailFilterInfo<SwiftDateInRangeFilterValue>(columnKey,
                        DateUtils.create(dateFilterBean), SwiftDetailFilterType.DATE_NOT_IN_RANGE);
            }
            case BICommonConstants.ANALYSIS_FILTER_DATE.TOP_N: {
                int n = ((DateTopNFilterBean) bean).getFilterValue().intValue();
                // 最早的，对应BOTTOM_N
                return new SwiftDetailFilterInfo<Integer>(columnKey, n, SwiftDetailFilterType.BOTTOM_N);
            }
            case BICommonConstants.ANALYSIS_FILTER_DATE.BOTTOM_N: {
                int n = ((DateBottomNFilterBean) bean).getFilterValue().intValue();
                // 最晚的，对应TOP_N
                return new SwiftDetailFilterInfo<Integer>(columnKey, n, SwiftDetailFilterType.TOP_N);
            }
            case BICommonConstants.ANALYSIS_FILTER_DATE.IS_NULL: {
                return new SwiftDetailFilterInfo(columnKey, null, SwiftDetailFilterType.NULL);
            }
            case BICommonConstants.ANALYSIS_FILTER_DATE.NOT_NULL: {
                return new SwiftDetailFilterInfo(columnKey, null, SwiftDetailFilterType.NOT_NULL);
            }
            case BICommonConstants.ANALYSIS_FILTER_DATE.BELONG_DATE_WIDGET_VALUE: {
                DateWidgetBean dateWidgetBean = ((DateBelongWidgetFilterBean) bean).getFilterValue();
                return new SwiftDetailFilterInfo<SwiftDateInRangeFilterValue>(columnKey,
                        DateWidgetBeanAdaptor.create(dateWidgetBean), SwiftDetailFilterType.DATE_IN_RANGE);
            }
            case BICommonConstants.ANALYSIS_FILTER_DATE.NOT_BELONG_DATE_WIDGET_VALUE: {
                DateWidgetBean dateWidgetBean = ((DateNoBelongWidgetFilterBean) bean).getFilterValue();
                return new SwiftDetailFilterInfo<SwiftDateInRangeFilterValue>(columnKey,
                        DateWidgetBeanAdaptor.create(dateWidgetBean), SwiftDetailFilterType.DATE_NOT_IN_RANGE);
            }
            case BICommonConstants.ANALYSIS_FILTER_DATE.EQUAL_TO_DATE_WIDGET_VALUE: {
                DateWidgetBean dateWidgetBean = ((DateEqualWidgetFilterBean) bean).getFilterValue();
                return new SwiftDetailFilterInfo<SwiftDateInRangeFilterValue>(columnKey,
                        DateWidgetBeanAdaptor.create(dateWidgetBean), SwiftDetailFilterType.DATE_IN_RANGE);
            }
            case BICommonConstants.ANALYSIS_FILTER_DATE.NOT_EQUAL_TO_DATE_WIDGET_VALUE: {
                DateWidgetBean dateWidgetBean = ((DateNoEqualWidgetFilterBean) bean).getFilterValue();
                return new SwiftDetailFilterInfo<SwiftDateInRangeFilterValue>(columnKey,
                        DateWidgetBeanAdaptor.create(dateWidgetBean), SwiftDetailFilterType.DATE_NOT_IN_RANGE);
            }
            case BICommonConstants.ANALYSIS_FILTER_DATE.LESS_THAN_DATE_WIDGET_VALUE: {
                DateWidgetBean dateWidgetBean = ((DateBeforeWidgetFilterBean) bean).getFilterValue();
                return new SwiftDetailFilterInfo<SwiftDateInRangeFilterValue>(columnKey,
                        DateWidgetBeanAdaptor.createDateLessThanFilterInfo(dateWidgetBean), SwiftDetailFilterType.DATE_IN_RANGE);
            }
            case BICommonConstants.ANALYSIS_FILTER_DATE.MORE_THAN_DATE_WIDGET_VALUE: {
                DateWidgetBean dateWidgetBean = ((DateAfterWidgetFilterBean) bean).getFilterValue();
                return new SwiftDetailFilterInfo<SwiftDateInRangeFilterValue>(columnKey,
                        DateWidgetBeanAdaptor.createDateMoreThanFilterInfo(dateWidgetBean), SwiftDetailFilterType.DATE_IN_RANGE);
            }

            case BICommonConstants.ANALYSIS_FILTER_TYPE.FORMULA: {
                String expr = transformFormula(((FormulaFilterBean) bean).getFilterValue(), tableName);
                return new SwiftDetailFilterInfo<String>(columnKey, expr, SwiftDetailFilterType.FORMULA);
            }
            case BICommonConstants.ANALYSIS_FILTER_TYPE.AND: {
                List<FilterBean> beans = ((GeneraAndFilterBean) bean).getFilterValue();
                List<FilterInfo> filterValues = createFilterInfoList(tableName, beans, segments);
                return new GeneralFilterInfo(filterValues, GeneralFilterInfo.AND);
            }
            case BICommonConstants.ANALYSIS_FILTER_TYPE.OR: {
                List<FilterBean> beans = ((GeneraOrFilterBean) bean).getFilterValue();
                List<FilterInfo> filterValues = createFilterInfoList(tableName, beans, segments);
                return new GeneralFilterInfo(filterValues, GeneralFilterInfo.OR);
            }
            case BICommonConstants.ANALYSIS_FILTER_TYPE.EMPTY_FORMULA:
            case BICommonConstants.ANALYSIS_FILTER_TYPE.EMPTY_CONDITION:
            default:
        }
        return new SwiftDetailFilterInfo(columnKey, null, SwiftDetailFilterType.ALL_SHOW);
    }


    /**
     * @param bean
     * @return
     */
    public static FilterInfo createMatchFilterInfo(FilterBean bean, MatchConverter converter) {
        switch (bean.getFilterType()) {
            // string类过滤
            case BICommonConstants.ANALYSIS_FILTER_STRING.BELONG_VALUE:
            case BICommonConstants.ANALYSIS_FILTER_DATE.BELONG_STRING_VALUE:
            case BICommonConstants.ANALYSIS_FILTER_NUMBER.BELONG_STRING_VALUE: {
                StringBelongFilterValueBean valueBean = (StringBelongFilterValueBean) ((AbstractFilterBean) bean).getFilterValue();
                List<String> belongValues = valueBean.getValue();
                if (belongValues == null || belongValues.isEmpty()) {
                    break;
                }
                if (bean.getFilterType() == BICommonConstants.ANALYSIS_FILTER_DATE.BELONG_STRING_VALUE){
                    List<String> transValues = new ArrayList<String>();
                    for (String value : belongValues){
                        transValues.add(converter.convert(Long.valueOf(value)));
                        belongValues = transValues;
                    }
                }
                int valueType = valueBean.getType();
                return new SwiftDetailFilterInfo<Set<String>>(null, new HashSet<String>(belongValues),
                        // 多选同filterType，否则是反选
                        valueType == BICommonConstants.SELECTION_TYPE.MULTI ? SwiftDetailFilterType.STRING_IN : SwiftDetailFilterType.STRING_NOT_IN);
            }
            case BICommonConstants.ANALYSIS_FILTER_STRING.NOT_BELONG_VALUE:
            case BICommonConstants.ANALYSIS_FILTER_NUMBER.NOT_BELONG_STRING_VALUE:
            case BICommonConstants.ANALYSIS_FILTER_DATE.NOT_BELONG_STRING_VALUE: {
                StringBelongFilterValueBean valueBean = (StringBelongFilterValueBean) ((AbstractFilterBean) bean).getFilterValue();
                List<String> notBelongValues = valueBean.getValue();
                if (notBelongValues == null || notBelongValues.isEmpty()) {
                    return new SwiftDetailFilterInfo(null, null, SwiftDetailFilterType.NOT_SHOW);
                }
                if (bean.getFilterType() == BICommonConstants.ANALYSIS_FILTER_DATE.BELONG_STRING_VALUE){
                    List<String> transValues = new ArrayList<String>();
                    for (String value : notBelongValues){
                        transValues.add(converter.convert(Long.valueOf(value)));
                        notBelongValues = transValues;
                    }
                }
                int valueType = valueBean.getType();
                return new SwiftDetailFilterInfo<Set<String>>(null, new HashSet<String>(notBelongValues),
                        // 多选同filterType，否则是反选
                        valueType == BICommonConstants.SELECTION_TYPE.MULTI ? SwiftDetailFilterType.STRING_NOT_IN : SwiftDetailFilterType.STRING_IN);
            }
            case BICommonConstants.ANALYSIS_FILTER_STRING.CONTAIN:
            case BICommonConstants.ANALYSIS_FILTER_DATE.CONTAIN: {
                String contain = (String) ((AbstractFilterBean) bean).getFilterValue();
                if (StringUtils.isBlank(contain)) {
                    break;
                }
                return new SwiftDetailFilterInfo<String>(null, contain, SwiftDetailFilterType.STRING_LIKE);
            }
            case BICommonConstants.ANALYSIS_FILTER_STRING.NOT_CONTAIN:
            case BICommonConstants.ANALYSIS_FILTER_DATE.NOT_CONTAIN: {
                String value = (String) ((AbstractFilterBean) bean).getFilterValue();
                if (StringUtils.isBlank(value)) {
                    return new SwiftDetailFilterInfo(null, null, SwiftDetailFilterType.NOT_SHOW);
                }
                return new SwiftDetailFilterInfo<String>(null, value, SwiftDetailFilterType.STRING_NOT_LIKE);
            }
            case BICommonConstants.ANALYSIS_FILTER_STRING.BEGIN_WITH:
            case BICommonConstants.ANALYSIS_FILTER_DATE.BEGIN_WITH: {
                String value = (String) ((AbstractFilterBean) bean).getFilterValue();
                if (StringUtils.isBlank(value)) {
                    break;
                }
                return new SwiftDetailFilterInfo<String>(null, value, SwiftDetailFilterType.STRING_STARTS_WITH);
            }
            case BICommonConstants.ANALYSIS_FILTER_STRING.NOT_BEGIN_WITH:
            case BICommonConstants.ANALYSIS_FILTER_DATE.NOT_BEGIN_WITH: {
                String value = (String) ((AbstractFilterBean) bean).getFilterValue();
                if (StringUtils.isBlank(value)) {
                    return new SwiftDetailFilterInfo(null, null, SwiftDetailFilterType.NOT_SHOW);
                }
                return new SwiftDetailFilterInfo<String>(null, value, SwiftDetailFilterType.STRING_NOT_STARTS_WITH);
            }
            case BICommonConstants.ANALYSIS_FILTER_STRING.END_WITH:
            case BICommonConstants.ANALYSIS_FILTER_DATE.END_WITH: {
                String value = (String) ((AbstractFilterBean) bean).getFilterValue();
                if (StringUtils.isBlank(value)) {
                    break;
                }
                return new SwiftDetailFilterInfo<String>(null, value, SwiftDetailFilterType.STRING_ENDS_WITH);
            }
            case BICommonConstants.ANALYSIS_FILTER_STRING.NOT_END_WITH:
            case BICommonConstants.ANALYSIS_FILTER_DATE.NOT_END_WITH: {
                String value = (String) ((AbstractFilterBean) bean).getFilterValue();
                if (StringUtils.isBlank(value)) {
                    return new SwiftDetailFilterInfo(null, null, SwiftDetailFilterType.NOT_SHOW);
                }
                return new SwiftDetailFilterInfo<String>(null, value, SwiftDetailFilterType.STRING_NOT_ENDS_WITH);
            }
            case BICommonConstants.ANALYSIS_FILTER_STRING.IS_NULL:
            case BICommonConstants.ANALYSIS_FILTER_DATE.IS_NULL:
            case BICommonConstants.ANALYSIS_FILTER_NUMBER.IS_NULL:
                return new SwiftDetailFilterInfo<Object>(null, null, SwiftDetailFilterType.NULL);
            case BICommonConstants.ANALYSIS_FILTER_STRING.NOT_NULL:
            case BICommonConstants.ANALYSIS_FILTER_DATE.NOT_NULL:
            case BICommonConstants.ANALYSIS_FILTER_NUMBER.NOT_NULL:
                return new SwiftDetailFilterInfo<Object>(null, null, SwiftDetailFilterType.NOT_NULL);
            case BICommonConstants.ANALYSIS_FILTER_STRING.TOP_N:
            case BICommonConstants.ANALYSIS_FILTER_NUMBER.TOP_N:
            case BICommonConstants.ANALYSIS_FILTER_DATE.TOP_N: {
                Long filterValue = (Long) ((AbstractFilterBean) bean).getFilterValue();
                if (filterValue == null) {
                    break;
                }
                int n = filterValue.intValue();
                // 功能的前N个对应字典排序中最小的N个
                return new SwiftDetailFilterInfo<Integer>(null, n, SwiftDetailFilterType.BOTTOM_N);
            }
            case BICommonConstants.ANALYSIS_FILTER_STRING.BOTTOM_N:
            case BICommonConstants.ANALYSIS_FILTER_NUMBER.BOTTOM_N:
            case BICommonConstants.ANALYSIS_FILTER_DATE.BOTTOM_N: {
                Long filterValue = ((StringBottomNFilterBean) bean).getFilterValue();
                if (filterValue == null) {
                    break;
                }
                int n = filterValue.intValue();
                // 功能的后N个对应字典排序中最大的N个
                return new SwiftDetailFilterInfo<Integer>(null, n, SwiftDetailFilterType.TOP_N);
            }


            //对于指标汇总结果的过滤
            case BICommonConstants.ANALYSIS_FILTER_NUMBER.BELONG_VALUE: {
                NumberValue nv = ((NumberBelongFilterBean) bean).getFilterValue();
                if (nv.getMin() == FineNullValue.DOUBLE && nv.getMax() == FineNullValue.DOUBLE) {
                    break;
                }
                if (nv.getMin() == FineNullValue.DOUBLE) {
                    nv.setMin(Double.NEGATIVE_INFINITY);
                }
                if (nv.getMax() == FineNullValue.DOUBLE) {
                    nv.setMax(Double.POSITIVE_INFINITY);
                }
                return new SwiftDetailFilterInfo<SwiftNumberInRangeFilterValue>(null, createValue(nv),
                        SwiftDetailFilterType.NUMBER_IN_RANGE);
            }
            case BICommonConstants.ANALYSIS_FILTER_NUMBER.NOT_BELONG_VALUE: {
                NumberValue nv = ((NumberNoBelongFilterBean) bean).getFilterValue();
                if (nv.getMin() == FineNullValue.DOUBLE && nv.getMax() == FineNullValue.DOUBLE) {
                    return new SwiftDetailFilterInfo(null, null, SwiftDetailFilterType.NOT_SHOW);
                }
                if (nv.getMin() == FineNullValue.DOUBLE) {
                    nv.setMin(Double.NEGATIVE_INFINITY);
                }
                if (nv.getMax() == FineNullValue.DOUBLE) {
                    nv.setMax(Double.POSITIVE_INFINITY);
                }
                return new SwiftDetailFilterInfo<SwiftNumberInRangeFilterValue>(null, createValue(nv),
                        SwiftDetailFilterType.NUMBER_NOT_IN_RANGE);
            }
            case BICommonConstants.ANALYSIS_FILTER_NUMBER.EQUAL_TO: {
                final Double value = ((NumberEqualFilterBean) bean).getFilterValue();
                return new SwiftDetailFilterInfo<Set<Double>>(null, new HashSet<Double>() {{
                    add(value);
                }},
                        SwiftDetailFilterType.NUMBER_CONTAIN);
            }
            case BICommonConstants.ANALYSIS_FILTER_NUMBER.NOT_EQUAL_TO: {
                final Double value = ((NumberNoEqualFilterBean) bean).getFilterValue();
                return new SwiftDetailFilterInfo<Set<Double>>(null, new HashSet<Double>() {{
                    add(value);
                }},
                        SwiftDetailFilterType.NUMBER_NOT_CONTAIN);
            }
            case BICommonConstants.ANALYSIS_FILTER_NUMBER.LARGE: {
                NumberSelectedFilterValueBean numberBean = ((NumberLargeFilterBean) bean).getFilterValue();
                if (numberBean.getValue() == null) {
                    break;
                }
                SwiftNumberInRangeFilterValue filterValue = new SwiftNumberInRangeFilterValue();
                filterValue.setMin(getDimensionAVGValue(numberBean));
                return new SwiftDetailFilterInfo<SwiftNumberInRangeFilterValue>(null, filterValue,
                        isAverage(numberBean) ? SwiftDetailFilterType.NUMBER_AVERAGE : SwiftDetailFilterType.NUMBER_IN_RANGE);
            }
            case BICommonConstants.ANALYSIS_FILTER_NUMBER.SMALL: {
                NumberSelectedFilterValueBean numberBean = ((NumberSmallFilterBean) bean).getFilterValue();
                if (numberBean.getValue() == null) {
                    break;
                }
                SwiftNumberInRangeFilterValue value = new SwiftNumberInRangeFilterValue();
                value.setMax(getDimensionAVGValue(numberBean));
                return new SwiftDetailFilterInfo<SwiftNumberInRangeFilterValue>(null, value,
                        isAverage(numberBean) ? SwiftDetailFilterType.NUMBER_AVERAGE : SwiftDetailFilterType.NUMBER_IN_RANGE);
            }
            case BICommonConstants.ANALYSIS_FILTER_NUMBER.LARGE_OR_EQUAL: {
                NumberSelectedFilterValueBean numberBean = ((NumberLargeOrEqualFilterBean) bean).getFilterValue();
                if (numberBean.getValue() == null) {
                    break;
                }
                SwiftNumberInRangeFilterValue value = new SwiftNumberInRangeFilterValue();
                value.setMin(getDimensionAVGValue(numberBean));
                value.setMinIncluded(true);
                return new SwiftDetailFilterInfo<SwiftNumberInRangeFilterValue>(null, value,
                        isAverage(numberBean) ? SwiftDetailFilterType.NUMBER_AVERAGE : SwiftDetailFilterType.NUMBER_IN_RANGE);
            }
            case BICommonConstants.ANALYSIS_FILTER_NUMBER.SMALL_OR_EQUAL: {
                NumberSelectedFilterValueBean numberBean = ((NumberSmallOrEqualFilterBean) bean).getFilterValue();
                if (numberBean.getValue() == null) {
                    break;
                }
                SwiftNumberInRangeFilterValue value = new SwiftNumberInRangeFilterValue();
                value.setMax(getDimensionAVGValue(numberBean));
                value.setMaxIncluded(true);
                return new SwiftDetailFilterInfo<SwiftNumberInRangeFilterValue>(null, value,
                        isAverage(numberBean) ? SwiftDetailFilterType.NUMBER_AVERAGE : SwiftDetailFilterType.NUMBER_IN_RANGE);
            }
            case BICommonConstants.ANALYSIS_FILTER_TYPE.EMPTY_FORMULA:
            case BICommonConstants.ANALYSIS_FILTER_TYPE.EMPTY_CONDITION:
            default:
        }
        return new SwiftDetailFilterInfo(null, null, SwiftDetailFilterType.ALL_SHOW);
    }

    private static double getDimensionAVGValue(NumberSelectedFilterValueBean bean) {
        int valueType = bean.getType();
        if (valueType == BICommonConstants.ANALYSIS_FILTER_NUMBER_VALUE.AVG) {
            return NumberAverageFilter.AVG_HOLDER;
        } else {
            return bean.getValue();
        }
    }

    private static SwiftDetailFilterInfo createDateLessThanFilterInfo(ColumnKey columnKey, DateFilterBean bean) {
        // 在某一时刻之前。-1ms，因为DateInRangeFilter的范围是左右包含的(startTimeIncluded, endTimeIncluded)
        long value = DateUtils.dateFilterBean2Long(bean, false) - 1;
        SwiftDateInRangeFilterValue filterValue = new SwiftDateInRangeFilterValue();
        filterValue.setEnd(value);
        return new SwiftDetailFilterInfo<SwiftDateInRangeFilterValue>(columnKey,
                filterValue, SwiftDetailFilterType.DATE_IN_RANGE);
    }

    private static SwiftDetailFilterInfo createDateMoreThanFilterInfo(ColumnKey columnKey, DateFilterBean bean) {
        // 在某一时刻之后。+1ms，因为DateInRangeFilter的范围是左右包含的(startTimeIncluded, endTimeIncluded)
        long value = DateUtils.dateFilterBean2Long(bean, true) + 1;
        SwiftDateInRangeFilterValue filterValue = new SwiftDateInRangeFilterValue();
        filterValue.setStart(value);
        return new SwiftDetailFilterInfo<SwiftDateInRangeFilterValue>(columnKey,
                filterValue, SwiftDetailFilterType.DATE_IN_RANGE);
    }

    private static List<FilterInfo> createFilterInfoList(String tableName, List<FilterBean> beans, List<Segment> segments) {
        List<FilterInfo> filterInfoList = new ArrayList<FilterInfo>();
        for (FilterBean bean : beans) {
            filterInfoList.add(createFilterInfo(tableName, bean, segments));
        }
        return filterInfoList;
    }

    private static double createValue(NumberSelectedFilterValueBean bean, List<Segment> segments,
                                      String fieldName, String fieldId, String tableName) {
        int valueType = bean.getType();
        Double value;
        if (valueType == BICommonConstants.ANALYSIS_FILTER_NUMBER_VALUE.AVG) {
            value = AvgUtils.average(segments, fieldName);
            // FIXME: 2018/5/18 明细表（包括分析表）中的这类依赖聚合结果的过滤怎么处理比较好呢？
            if (value.equals(NumberAverageFilter.AVG_HOLDER)) {
                value = AvgUtils.average(fieldId, tableName);
            }
        } else {
            value = bean.getValue();
        }
        return value;
    }

    private static boolean isAverage(NumberSelectedFilterValueBean bean) {
        return bean.getType() == BICommonConstants.ANALYSIS_FILTER_NUMBER_VALUE.AVG;
    }

    private static SwiftNumberInRangeFilterValue createValue(NumberValue nv) {
        SwiftNumberInRangeFilterValue value = new SwiftNumberInRangeFilterValue();
        value.setMin(nv.getMin());
        value.setMax(nv.getMax());
        value.setMinIncluded(nv.isClosemin());
        value.setMaxIncluded(nv.isClosemax());
        return value;
    }

}