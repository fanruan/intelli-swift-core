package com.fr.swift.adaptor.transformer;

import com.finebi.base.constant.FineEngineType;
import com.finebi.base.stable.StableManager;
import com.finebi.conf.constant.BICommonConstants;
import com.finebi.conf.exception.FineEngineException;
import com.finebi.conf.internalimp.bean.dashboard.widget.field.WidgetBeanField;
import com.finebi.conf.internalimp.bean.filter.AbstractFilterBean;
import com.finebi.conf.internalimp.bean.filter.FormulaFilterBean;
import com.finebi.conf.internalimp.bean.filter.GeneraAndFilterBean;
import com.finebi.conf.internalimp.bean.filter.GeneraOrFilterBean;
import com.finebi.conf.internalimp.bean.filter.date.DateAfterFilterBean;
import com.finebi.conf.internalimp.bean.filter.date.DateAfterWidgetFilterBean;
import com.finebi.conf.internalimp.bean.filter.date.DateBeforeFilterBean;
import com.finebi.conf.internalimp.bean.filter.date.DateBeforeWidgetFilterBean;
import com.finebi.conf.internalimp.bean.filter.date.DateBelongFilterBean;
import com.finebi.conf.internalimp.bean.filter.date.DateBelongWidgetFilterBean;
import com.finebi.conf.internalimp.bean.filter.date.DateBottomNFilterBean;
import com.finebi.conf.internalimp.bean.filter.date.DateEqualFilterBean;
import com.finebi.conf.internalimp.bean.filter.date.DateEqualWidgetFilterBean;
import com.finebi.conf.internalimp.bean.filter.date.DateNoBelongFilterBean;
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
import com.finebi.conf.structure.dashboard.widget.dimension.FineDimension;
import com.finebi.conf.structure.dashboard.widget.target.FineTarget;
import com.finebi.conf.structure.filter.FineFilter;
import com.finebi.conf.structure.path.FineBusinessTableRelationPath;
import com.fr.general.ComparatorUtils;
import com.fr.stable.StringUtils;
import com.fr.swift.adaptor.transformer.cal.AvgUtils;
import com.fr.swift.adaptor.transformer.date.DateRangeValueBeanAdaptor;
import com.fr.swift.adaptor.transformer.date.DateUtils;
import com.fr.swift.adaptor.transformer.date.DateWidgetBeanAdaptor;
import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.query.filter.SwiftDetailFilterType;
import com.fr.swift.query.filter.info.FilterInfo;
import com.fr.swift.query.filter.info.GeneralFilterInfo;
import com.fr.swift.query.filter.info.MatchFilterInfo;
import com.fr.swift.query.filter.info.SwiftDetailFilterInfo;
import com.fr.swift.query.filter.info.value.SwiftDateInRangeFilterValue;
import com.fr.swift.query.filter.info.value.SwiftNumberInRangeFilterValue;
import com.fr.swift.segment.Segment;
import com.fr.swift.segment.column.ColumnKey;
import com.fr.swift.source.RelationSource;
import com.fr.swift.util.Crasher;
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

    public static FilterInfo transformDimensionFineFilter(FineDimension dimension) {
        return transformDimensionFineFilter(null, dimension, false, null);
    }


    /**
     * @param tableName 控件的表名，没表名传null或者空字符串
     * @param dimension
     * @param attachTargetFilters 最后一个维度上面要加上指标的过滤
     * @param targets
     * @return
     */
    public static FilterInfo transformDimensionFineFilter(String tableName, FineDimension dimension, boolean attachTargetFilters, List<FineTarget> targets) {
        List<FineFilter> filters = dimension.getFilters();
        String dimId = dimension.getId();
        List<FilterBean> beans = new ArrayList<FilterBean>();
        if (filters != null) {
            for (FineFilter filter : filters) {
                //nice job! foundation 维度过滤没id，要从维度上设置一下。
                if (filter.getValue() != null) {
                    AbstractFilterBean bean = (AbstractFilterBean) filter.getValue();
                    String fieldId;
                    if (dimension.getWidgetBeanField() != null) {
                        WidgetBeanField field = dimension.getWidgetBeanField();
                        fieldId = StringUtils.isEmpty(field.getSource()) ? field.getId() : field.getSource();
                    } else {
                        fieldId = dimension.getFieldId();
                    }
                    // 如果是generalBean还要递归地设置一下，坑爹！
                    deepSettingFieldId(bean, fieldId);
                    beans.add(bean);
                }
            }
        }
        List<FilterInfo> filterInfoList = new ArrayList<FilterInfo>();
        for (FilterBean bean : beans) {
            AbstractFilterBean filterBean = (AbstractFilterBean) bean;
            FilterInfo info = createFilterInfo(tableName, filterBean, new ArrayList<Segment>());
            if (!ComparatorUtils.equals(filterBean.getTargetId(), dimId) && targets != null && targets.size() != 0) {
                filterInfoList.add(new MatchFilterInfo(info, getIndex(filterBean.getTargetId(), targets)));
            } else {
                filterInfoList.add(info);
            }
        }
        if (attachTargetFilters && targets != null) {
            for (int i = 0; i < targets.size(); i++) {
                FineTarget target = targets.get(i);
                List<FineFilter> targetFilters = target.getFilters();
                if (targetFilters != null) {
                    for (FineFilter filter : targetFilters) {
                        if (filter.getFilterType() == BICommonConstants.ANALYSIS_FILTER_TYPE.EMPTY_CONDITION) {
                            continue;
                        }
                        FilterInfo targetFilterInfo = createFilterInfo(tableName, (AbstractFilterBean) filter.getValue(), new ArrayList<Segment>());
                        filterInfoList.add(new MatchFilterInfo(targetFilterInfo, i));
                    }
                }
            }
        }
        return new GeneralFilterInfo(filterInfoList, GeneralFilterInfo.AND);
    }

    private static void deepSettingFieldId(AbstractFilterBean bean, String fieldId) {
        List<FilterBean> filterBeans = null;
        if (bean instanceof GeneraAndFilterBean) {
            filterBeans = ((GeneraAndFilterBean) bean).getFilterValue();
        } else if (bean instanceof GeneraOrFilterBean) {
            filterBeans = ((GeneraOrFilterBean) bean).getFilterValue();
        }
        if (filterBeans != null) {
            for (FilterBean b : filterBeans) {
                deepSettingFieldId((AbstractFilterBean) b, fieldId);
            }
        } else {
            bean.setFieldId(fieldId);
        }
    }

    private static int getIndex(String targetId, List<FineTarget> targets) {
        for (int i = 0; i < targets.size(); i++) {
            if (ComparatorUtils.equals(targetId, targets.get(i).getId())) {
                return i;
            }
        }
        return Crasher.crash("invalid target filter id :" + targetId);
    }

    public static FilterInfo transformFilterBean(String tableName, List<FilterBean> beans, List<Segment> segments) {
        List<FilterInfo> filterInfoList = new ArrayList<FilterInfo>();
        for (FilterBean bean : beans) {
            filterInfoList.add(createFilterInfo(tableName, bean, segments));
        }
        return new GeneralFilterInfo(filterInfoList, GeneralFilterInfo.AND);
    }

    /**
     * @param tableName 控件的表名，没表名传null或者空字符串
     * @param bean
     * @param segments
     * @return
     */
    public static SwiftDetailFilterInfo createFilterInfo(String tableName, FilterBean bean, List<Segment> segments) {
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
                if (belongValues == null) {
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
                if (notBelongValues == null) {
                    break;
                }
                int valueType = valueBean.getType();
                return new SwiftDetailFilterInfo<Set<String>>(columnKey, new HashSet<String>(notBelongValues),
                        // 多选同filterType，否则是反选
                        valueType == BICommonConstants.SELECTION_TYPE.MULTI ? SwiftDetailFilterType.STRING_NOT_IN : SwiftDetailFilterType.STRING_IN);
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
                    break;
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
                    break;
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
                    break;
                }
                return new SwiftDetailFilterInfo<String>(columnKey, value, SwiftDetailFilterType.STRING_NOT_ENDS_WITH);
            }
            case BICommonConstants.ANALYSIS_FILTER_STRING.IS_NULL:
                return new SwiftDetailFilterInfo<Object>(columnKey, null, SwiftDetailFilterType.NULL);
            case BICommonConstants.ANALYSIS_FILTER_STRING.NOT_NULL:
                return new SwiftDetailFilterInfo<Object>(columnKey, null, SwiftDetailFilterType.NOT_NULL);
            case BICommonConstants.ANALYSIS_FILTER_STRING.TOP_N: {
                int n = ((StringTopNFilterBean) bean).getFilterValue().intValue();
                // 功能的前N个对应字典排序中最小的N个
                return new SwiftDetailFilterInfo<Integer>(columnKey, n, SwiftDetailFilterType.BOTTOM_N);
            }
            case BICommonConstants.ANALYSIS_FILTER_STRING.BOTTOM_N: {
                int n = ((StringBottomNFilterBean) bean).getFilterValue().intValue();
                // 功能的后N个对应字典排序中最大的N个
                return new SwiftDetailFilterInfo<Integer>(columnKey, n, SwiftDetailFilterType.TOP_N);
            }


            // 数值类过滤
            case BICommonConstants.ANALYSIS_FILTER_NUMBER.BELONG_VALUE: {
                NumberValue nv = ((NumberBelongFilterBean) bean).getFilterValue();
                return new SwiftDetailFilterInfo<SwiftNumberInRangeFilterValue>(columnKey, createValue(nv),
                        SwiftDetailFilterType.NUMBER_IN_RANGE);
            }
            case BICommonConstants.ANALYSIS_FILTER_NUMBER.NOT_BELONG_VALUE: {
                NumberValue nv = ((NumberNoBelongFilterBean) bean).getFilterValue();
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
                SwiftNumberInRangeFilterValue filterValue = new SwiftNumberInRangeFilterValue();
                filterValue.setMin(createValue(numberBean, segments, fieldName));
                return new SwiftDetailFilterInfo<SwiftNumberInRangeFilterValue>(columnKey, filterValue,
                        SwiftDetailFilterType.NUMBER_IN_RANGE);
            }
            case BICommonConstants.ANALYSIS_FILTER_NUMBER.SMALL: {
                NumberSelectedFilterValueBean numberBean = ((NumberSmallFilterBean) bean).getFilterValue();
                SwiftNumberInRangeFilterValue value = new SwiftNumberInRangeFilterValue();
                value.setMax(createValue(numberBean, segments, fieldName));
                return new SwiftDetailFilterInfo<SwiftNumberInRangeFilterValue>(columnKey, value,
                        SwiftDetailFilterType.NUMBER_IN_RANGE);
            }
            case BICommonConstants.ANALYSIS_FILTER_NUMBER.LARGE_OR_EQUAL: {
                NumberSelectedFilterValueBean numberBean = ((NumberLargeOrEqualFilterBean) bean).getFilterValue();
                SwiftNumberInRangeFilterValue value = new SwiftNumberInRangeFilterValue();
                value.setMin(createValue(numberBean, segments, fieldName));
                value.setMinIncluded(true);
                return new SwiftDetailFilterInfo<SwiftNumberInRangeFilterValue>(columnKey, value,
                        SwiftDetailFilterType.NUMBER_IN_RANGE);
            }
            case BICommonConstants.ANALYSIS_FILTER_NUMBER.SMALL_OR_EQUAL: {
                NumberSelectedFilterValueBean numberBean = ((NumberSmallOrEqualFilterBean) bean).getFilterValue();
                SwiftNumberInRangeFilterValue value = new SwiftNumberInRangeFilterValue();
                value.setMax(createValue(numberBean, segments, fieldName));
                value.setMaxIncluded(true);
                return new SwiftDetailFilterInfo<SwiftNumberInRangeFilterValue>(columnKey, value,
                        SwiftDetailFilterType.NUMBER_IN_RANGE);
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
                return new SwiftDetailFilterInfo<SwiftDateInRangeFilterValue>(columnKey,
                        DateRangeValueBeanAdaptor.create(dateValueBean), SwiftDetailFilterType.DATE_IN_RANGE);
            }
            case BICommonConstants.ANALYSIS_FILTER_DATE.NOT_BELONG_VALUE: {
                DateRangeValueBean dateValueBean = ((DateNoBelongFilterBean) bean).getFilterValue();
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
                String expr = ((FormulaFilterBean) bean).getFilterValue();
                return new SwiftDetailFilterInfo<String>(columnKey, expr, SwiftDetailFilterType.FORMULA);
            }
            case BICommonConstants.ANALYSIS_FILTER_TYPE.AND: {
                List<FilterBean> beans = ((GeneraAndFilterBean) bean).getFilterValue();
                List<SwiftDetailFilterInfo> filterValues = createFilterInfoList(tableName, beans, segments);
                return new SwiftDetailFilterInfo<List<SwiftDetailFilterInfo>>(columnKey,
                        filterValues, SwiftDetailFilterType.AND);
            }
            case BICommonConstants.ANALYSIS_FILTER_TYPE.OR: {
                List<FilterBean> beans = ((GeneraOrFilterBean) bean).getFilterValue();
                List<SwiftDetailFilterInfo> filterValues = createFilterInfoList(tableName, beans, segments);
                return new SwiftDetailFilterInfo<List<SwiftDetailFilterInfo>>(columnKey,
                        filterValues, SwiftDetailFilterType.OR);
            }
            case BICommonConstants.ANALYSIS_FILTER_TYPE.EMPTY_FORMULA:
            case BICommonConstants.ANALYSIS_FILTER_TYPE.EMPTY_CONDITION:
            default:
        }
        return new SwiftDetailFilterInfo(columnKey, null, SwiftDetailFilterType.ALL_SHOW);
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

    private static List<SwiftDetailFilterInfo> createFilterInfoList(String tableName, List<FilterBean> beans, List<Segment> segments) {
        List<SwiftDetailFilterInfo> filterInfoList = new ArrayList<SwiftDetailFilterInfo>();
        for (FilterBean bean : beans) {
            filterInfoList.add(createFilterInfo(tableName, bean, segments));
        }
        return filterInfoList;
    }

    private static double createValue(NumberSelectedFilterValueBean bean, List<Segment> segments, String fieldName) {
        int valueType = bean.getType();
        Double min;
        if (valueType == BICommonConstants.ANALYSIS_FILTER_NUMBER_VALUE.AVG) {
            min = AvgUtils.average(segments, fieldName);
        } else {
            min = bean.getValue();
        }
        return min;
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