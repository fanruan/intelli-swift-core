package com.fr.swift.adaptor.transformer;

import com.finebi.conf.constant.BICommonConstants;
import com.finebi.conf.constant.BIConfConstants;
import com.finebi.conf.internalimp.bean.filter.AbstractFilterBean;
import com.finebi.conf.internalimp.bean.filter.GeneraAndFilterBean;
import com.finebi.conf.internalimp.bean.filter.GeneraOrFilterBean;
import com.finebi.conf.internalimp.bean.filter.date.DateAfterFilterBean;
import com.finebi.conf.internalimp.bean.filter.date.DateBeforeFilterBean;
import com.finebi.conf.internalimp.bean.filter.date.DateBelongFilterBean;
import com.finebi.conf.internalimp.bean.filter.date.DateBottomNFilterBean;
import com.finebi.conf.internalimp.bean.filter.date.DateEqualFilterBean;
import com.finebi.conf.internalimp.bean.filter.date.DateNoEqualFilterBean;
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
import com.finebi.conf.internalimp.bean.filter.string.StringContainFilterBean;
import com.finebi.conf.internalimp.bean.filter.string.StringEndWithFilterBean;
import com.finebi.conf.internalimp.bean.filter.string.StringNoBeginWithFilterBean;
import com.finebi.conf.internalimp.bean.filter.string.StringNoBelongFilterBean;
import com.finebi.conf.internalimp.bean.filter.string.StringNoContainFilterBean;
import com.finebi.conf.internalimp.bean.filter.string.StringNoEndWithFilterBean;
import com.finebi.conf.internalimp.bean.filtervalue.date.DateBoxFilterBean;
import com.finebi.conf.internalimp.bean.filtervalue.date.DateRangeValueBean;
import com.finebi.conf.internalimp.bean.filtervalue.date.DateSelectedValueBean;
import com.finebi.conf.internalimp.bean.filtervalue.date.single.DateDynamicFilterBean;
import com.finebi.conf.internalimp.bean.filtervalue.date.single.DateDynamicFilterBeanValue;
import com.finebi.conf.internalimp.bean.filtervalue.date.single.DateStaticFilterBean;
import com.finebi.conf.internalimp.bean.filtervalue.date.single.DateStaticFilterBeanValue;
import com.finebi.conf.internalimp.bean.filtervalue.number.NumberSelectedFilterValueBean;
import com.finebi.conf.internalimp.bean.filtervalue.number.NumberValue;
import com.finebi.conf.structure.bean.filter.DateFilterBean;
import com.finebi.conf.structure.bean.filter.FilterBean;
import com.finebi.conf.structure.filter.FineFilter;
import com.fr.stable.StringUtils;
import com.fr.swift.adaptor.transformer.cal.AvgUtils;
import com.fr.swift.adaptor.transformer.date.DateUtils;
import com.fr.swift.query.filter.SwiftDetailFilterType;
import com.fr.swift.query.filter.info.DetailFilterInfo;
import com.fr.swift.query.filter.info.FilterInfo;
import com.fr.swift.query.filter.info.SwiftDetailFilterValue;
import com.fr.swift.query.filter.info.value.SwiftDateInRangeFilterValue;
import com.fr.swift.query.filter.info.value.SwiftNumberInRangeFilterValue;
import com.fr.swift.segment.Segment;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by pony on 2017/12/21.
 */
public class FilterInfoFactory {

    public static FilterInfo transformFineFilter(List<FineFilter> filters) {
        List<FilterBean> beans = new ArrayList<FilterBean>();
        for (FineFilter filter : filters) {
            beans.add((FilterBean) filter.getValue());
        }
        return transformFilterBean(beans, new ArrayList<Segment>());
    }

    public static FilterInfo transformFilterBean(List<FilterBean> beans, List<Segment> segments) {
        List<SwiftDetailFilterValue> filterValues = new ArrayList<SwiftDetailFilterValue>();
        for (FilterBean bean : beans) {
            filterValues.add(createFilterValue(bean, segments));
        }
        return new DetailFilterInfo(filterValues);
    }

    private static SwiftDetailFilterValue createFilterValue(FilterBean bean, List<Segment> segments) {
        String fieldName = ((AbstractFilterBean) bean).getFieldName();
        int type = bean.getFilterType();
        switch (type) {
            // string类过滤
            case BICommonConstants.ANALYSIS_FILTER_STRING.BELONG_VALUE:
                List<String> belongValues = ((StringBelongFilterBean) bean).getFilterValue().getValue();
                return new SwiftDetailFilterValue<Set<String>>(fieldName,
                        new HashSet<String>(belongValues), SwiftDetailFilterType.STRING_IN);
            case BICommonConstants.ANALYSIS_FILTER_STRING.NOT_BELONG_VALUE:
                List<String> notBelongValues = ((StringNoBelongFilterBean) bean).getFilterValue().getValue();
                return new SwiftDetailFilterValue<Set<String>>(fieldName,
                        new HashSet<String>(notBelongValues), SwiftDetailFilterType.STRING_NOT_IN);
            case BICommonConstants.ANALYSIS_FILTER_STRING.CONTAIN:
                String contain = ((StringContainFilterBean) bean).getFilterValue();
                return new SwiftDetailFilterValue<String>(fieldName, contain, SwiftDetailFilterType.STRING_LIKE);
            case BICommonConstants.ANALYSIS_FILTER_STRING.NOT_CONTAIN: {
                String value = ((StringNoContainFilterBean) bean).getFilterValue();
                return new SwiftDetailFilterValue<String> (fieldName, value, SwiftDetailFilterType.STRING_NOT_LIKE);
            }
            case BICommonConstants.ANALYSIS_FILTER_STRING.BEGIN_WITH: {
                String value = ((StringBeginWithFilterBean) bean).getFilterValue();
                return new SwiftDetailFilterValue<String> (fieldName, value, SwiftDetailFilterType.STRING_STARTS_WITH);
            }
            case BICommonConstants.ANALYSIS_FILTER_STRING.NOT_BEGIN_WITH: {
                String value = ((StringNoBeginWithFilterBean) bean).getFilterValue();
                return new SwiftDetailFilterValue<String> (fieldName, value, SwiftDetailFilterType.STRING_NOT_STARTS_WITH);
            }
            case BICommonConstants.ANALYSIS_FILTER_STRING.END_WITH: {
                String value = ((StringEndWithFilterBean) bean).getFilterValue();
                return new SwiftDetailFilterValue<String> (fieldName, value, SwiftDetailFilterType.STRING_ENDS_WITH);
            }
            case BICommonConstants.ANALYSIS_FILTER_STRING.NOT_END_WITH: {
                String value = ((StringNoEndWithFilterBean) bean).getFilterValue();
                return new SwiftDetailFilterValue<String> (fieldName, value, SwiftDetailFilterType.STRING_NOT_ENDS_WITH);
            }
            case BICommonConstants.ANALYSIS_FILTER_STRING.IS_NULL:
                return new SwiftDetailFilterValue<Object> (fieldName, null, SwiftDetailFilterType.NULL);
            case BICommonConstants.ANALYSIS_FILTER_STRING.NOT_NULL:
                return new SwiftDetailFilterValue<Object> (fieldName, null, SwiftDetailFilterType.NOT_NULL);

            // 数值类过滤
            case BICommonConstants.ANALYSIS_FILTER_NUMBER.BELONG_VALUE: {
                NumberValue nv = ((NumberBelongFilterBean) bean).getFilterValue();
                return new SwiftDetailFilterValue<SwiftNumberInRangeFilterValue>(fieldName, createValue(nv),
                        SwiftDetailFilterType.NUMBER_IN_RANGE);
            }
            case BICommonConstants.ANALYSIS_FILTER_NUMBER.NOT_BELONG_VALUE: {
                NumberValue nv = ((NumberNoBelongFilterBean) bean).getFilterValue();
                return new SwiftDetailFilterValue<SwiftNumberInRangeFilterValue>(fieldName, createValue(nv),
                        SwiftDetailFilterType.NUMBER_NOT_IN_RANGE);
            }
            case BICommonConstants.ANALYSIS_FILTER_NUMBER.EQUAL_TO: {
                final Double value = ((NumberEqualFilterBean) bean).getFilterValue();
                return new SwiftDetailFilterValue<Set<Double>>(fieldName, new HashSet<Double>() {{ add(value); }},
                        SwiftDetailFilterType.NUMBER_CONTAIN);
            }
            case BICommonConstants.ANALYSIS_FILTER_NUMBER.NOT_EQUAL_TO: {
                final Double value = ((NumberNoEqualFilterBean) bean).getFilterValue();
                return new SwiftDetailFilterValue<Set<Double>>(fieldName, new HashSet<Double>() {{ add(value); }},
                        SwiftDetailFilterType.NUMBER_NOT_CONTAIN);
            }
            case BICommonConstants.ANALYSIS_FILTER_NUMBER.LARGE: {
                NumberSelectedFilterValueBean numberBean = ((NumberLargeFilterBean) bean).getFilterValue();
                SwiftNumberInRangeFilterValue filterValue = new SwiftNumberInRangeFilterValue();
                filterValue.setMin(createValue(numberBean, segments, fieldName));
                filterValue.setMinIncluded(false);
                return new SwiftDetailFilterValue<SwiftNumberInRangeFilterValue>(fieldName, filterValue,
                        SwiftDetailFilterType.NUMBER_IN_RANGE);
            }
            case BICommonConstants.ANALYSIS_FILTER_NUMBER.SMALL: {
                NumberSelectedFilterValueBean numberBean = ((NumberSmallFilterBean) bean).getFilterValue();
                SwiftNumberInRangeFilterValue value = new SwiftNumberInRangeFilterValue();
                value.setMax(createValue(numberBean, segments, fieldName));
                value.setMaxIncluded(false);
                return new SwiftDetailFilterValue<SwiftNumberInRangeFilterValue>(fieldName, value,
                        SwiftDetailFilterType.NUMBER_IN_RANGE);
            }
            case BICommonConstants.ANALYSIS_FILTER_NUMBER.LARGE_OR_EQUAL: {
                NumberSelectedFilterValueBean numberBean = ((NumberLargeOrEqualFilterBean) bean).getFilterValue();
                SwiftNumberInRangeFilterValue value = new SwiftNumberInRangeFilterValue();
                value.setMin(createValue(numberBean, segments, fieldName));
                return new SwiftDetailFilterValue<SwiftNumberInRangeFilterValue>(fieldName, value,
                        SwiftDetailFilterType.NUMBER_IN_RANGE);
            }
            case BICommonConstants.ANALYSIS_FILTER_NUMBER.SMALL_OR_EQUAL: {
                NumberSelectedFilterValueBean numberBean = ((NumberSmallOrEqualFilterBean) bean).getFilterValue();
                SwiftNumberInRangeFilterValue value = new SwiftNumberInRangeFilterValue();
                value.setMax(createValue(numberBean, segments, fieldName));
                return new SwiftDetailFilterValue<SwiftNumberInRangeFilterValue>(fieldName, value,
                        SwiftDetailFilterType.NUMBER_IN_RANGE);
            }
            case BICommonConstants.ANALYSIS_FILTER_NUMBER.TOP_N: {
                int n = ((NumberTopNFilterBean) bean).getFilterValue().intValue();
                return new SwiftDetailFilterValue<Integer> (fieldName, n, SwiftDetailFilterType.TOP_N);
            }
            case BICommonConstants.ANALYSIS_FILTER_NUMBER.BOTTOM_N: {
                int n = ((NumberBottomNFilterBean) bean).getFilterValue().intValue();
                return new SwiftDetailFilterValue<Integer> (fieldName, n, SwiftDetailFilterType.BOTTOM_N);
            }
            case BICommonConstants.ANALYSIS_FILTER_NUMBER.IS_NULL:
                return new SwiftDetailFilterValue<Object> (fieldName, null, SwiftDetailFilterType.NULL);
            case BICommonConstants.ANALYSIS_FILTER_NUMBER.NOT_NULL:
                return new SwiftDetailFilterValue<Object> (fieldName, null, SwiftDetailFilterType.NOT_NULL);

            // 日期类过滤
            case BICommonConstants.ANALYSIS_FILTER_DATE.BELONG_VALUE: {
                DateRangeValueBean dateValueBean = ((DateBelongFilterBean) bean).getFilterValue();
                return new SwiftDetailFilterValue<SwiftDateInRangeFilterValue> (fieldName,
                        createValue(dateValueBean), SwiftDetailFilterType.DATE_IN_RANGE);
            }
            case BICommonConstants.ANALYSIS_FILTER_DATE.NOT_BELONG_VALUE: {
                DateRangeValueBean dateValueBean = ((DateBelongFilterBean) bean).getFilterValue();
                return new SwiftDetailFilterValue<SwiftDateInRangeFilterValue> (fieldName,
                        createValue(dateValueBean), SwiftDetailFilterType.DATE_NOT_IN_RANGE);
            }
            case BICommonConstants.ANALYSIS_FILTER_DATE.LESS_THAN: {
                DateRangeValueBean dateFilterBean = ((DateBeforeFilterBean) bean).getFilterValue();
//                long value = dateFilterBean2long(dateFilterBean);
                SwiftDateInRangeFilterValue filterValue = new SwiftDateInRangeFilterValue();
                filterValue.setEnd(System.currentTimeMillis());
                return new SwiftDetailFilterValue<SwiftDateInRangeFilterValue> (fieldName,
                        filterValue, SwiftDetailFilterType.DATE_IN_RANGE);
            }
            case BICommonConstants.ANALYSIS_FILTER_DATE.MORE_THAN: {
                DateRangeValueBean dateFilterBean = ((DateAfterFilterBean) bean).getFilterValue();
//                long value = createValueByDateFilterBeanType(dateFilterBean);
                SwiftDateInRangeFilterValue filterValue = new SwiftDateInRangeFilterValue();
                filterValue.setStart(System.currentTimeMillis());
                return new SwiftDetailFilterValue<SwiftDateInRangeFilterValue> (fieldName,
                        filterValue, SwiftDetailFilterType.DATE_IN_RANGE);
            }
            case BICommonConstants.ANALYSIS_FILTER_DATE.EQUAL_TO: {
                DateBoxFilterBean boxFilterBean = (DateBoxFilterBean) ((DateEqualFilterBean) bean).getFilterValue();
                SwiftDateInRangeFilterValue filterValue = dateBoxFilterBean2DateRangeFilterValue(boxFilterBean);
                return new SwiftDetailFilterValue<SwiftDateInRangeFilterValue> (fieldName,
                        filterValue, SwiftDetailFilterType.DATE_IN_RANGE);
            }
            case BICommonConstants.ANALYSIS_FILTER_DATE.NOT_EQUAL_TO: {
                DateBoxFilterBean boxFilterBean = (DateBoxFilterBean) ((DateNoEqualFilterBean) bean).getFilterValue();
                SwiftDateInRangeFilterValue filterValue = dateBoxFilterBean2DateRangeFilterValue(boxFilterBean);
                return new SwiftDetailFilterValue<SwiftDateInRangeFilterValue> (fieldName,
                        filterValue, SwiftDetailFilterType.DATE_NOT_IN_RANGE);
            }
            case BICommonConstants.ANALYSIS_FILTER_DATE.TOP_N: {
                int n = ((DateTopNFilterBean) bean).getFilterValue().intValue();
                return new SwiftDetailFilterValue<Integer> (fieldName, n, SwiftDetailFilterType.TOP_N);
            }
            case BICommonConstants.ANALYSIS_FILTER_DATE.BOTTOM_N: {
                int n = ((DateBottomNFilterBean) bean).getFilterValue().intValue();
                return new SwiftDetailFilterValue<Integer> (fieldName, n, SwiftDetailFilterType.BOTTOM_N);
            }
            case BICommonConstants.ANALYSIS_FILTER_DATE.IS_NULL: {
                return new SwiftDetailFilterValue(fieldName, null, SwiftDetailFilterType.NULL);
            }
            case BICommonConstants.ANALYSIS_FILTER_DATE.NOT_NULL: {
                return new SwiftDetailFilterValue(fieldName, null, SwiftDetailFilterType.NOT_NULL);
            }
            case BICommonConstants.ANALYSIS_FILTER_DATE.BELONG_DATE_WIDGET_VALUE: {

            }
            case BICommonConstants.ANALYSIS_FILTER_DATE.NOT_BELONG_DATE_WIDGET_VALUE: {

            }
            case BICommonConstants.ANALYSIS_FILTER_DATE.EQUAL_TO_DATE_WIDGET_VALUE: {

            }
            case BICommonConstants.ANALYSIS_FILTER_DATE.NOT_EQUAL_TO_DATE_WIDGET_VALUE: {

            }
            case BICommonConstants.ANALYSIS_FILTER_DATE.LESS_THAN_DATE_WIDGET_VALUE: {

            }
            case BICommonConstants.ANALYSIS_FILTER_DATE.MORE_THAN_DATE_WIDGET_VALUE: {

            }

            case BICommonConstants.ANALYSIS_FILTER_TYPE.FORMULA:
            case BICommonConstants.ANALYSIS_FILTER_TYPE.AND: {
                List<FilterBean> beans = ((GeneraAndFilterBean) bean).getFilterValue();
                List<SwiftDetailFilterValue> filterValues = createFilterValueList(beans, segments);
                return new SwiftDetailFilterValue<List<SwiftDetailFilterValue>> (fieldName,
                        filterValues, SwiftDetailFilterType.AND);
            }
            case BICommonConstants.ANALYSIS_FILTER_TYPE.OR: {
                List<FilterBean> beans = ((GeneraOrFilterBean) bean).getFilterValue();
                List<SwiftDetailFilterValue> filterValues = createFilterValueList(beans, segments);
                return new SwiftDetailFilterValue<List<SwiftDetailFilterValue>> (fieldName,
                        filterValues, SwiftDetailFilterType.OR);
            }
            case BICommonConstants.ANALYSIS_FILTER_TYPE.EMPTY_FORMULA:
            case BICommonConstants.ANALYSIS_FILTER_TYPE.EMPTY_CONDITION:
            default:
        }
        return new SwiftDetailFilterValue(fieldName, null, SwiftDetailFilterType.ALL_SHOW);
    }

    private static long dateFilterBean2long(DateFilterBean bean) {
        int type = bean.getType();
        Calendar c = Calendar.getInstance();
        switch (type) {
            case BICommonConstants.DATE_TYPE.STATIC: {
                DateStaticFilterBeanValue value = ((DateStaticFilterBean) bean).getValue();
                c.clear();
                c.set(Calendar.YEAR, value.getYear());
                c.set(Calendar.MONTH, value.getMonth());
                c.set(Calendar.DATE, value.getDay());
                return toDayEnd(c);
            }
            case BICommonConstants.DATE_TYPE.DYNAMIC: {
                DateDynamicFilterBeanValue value = ((DateDynamicFilterBean) bean).getValue();
                c.add(Calendar.YEAR, StringUtils.isNotEmpty(value.getYear()) ? Integer.parseInt(value.getYear().trim()) : 0);
                c.add(Calendar.MONTH, StringUtils.isNotEmpty(value.getMonth()) ? Integer.parseInt(value.getMonth().trim()) : 0);
                c.add(Calendar.DATE, StringUtils.isNotEmpty(value.getWorkDay()) ? Integer.parseInt(value.getWorkDay().trim()) : 0);
                return c.getTimeInMillis();
            }
        }
        return c.getTimeInMillis();
    }

    private static List<SwiftDetailFilterValue> createFilterValueList(List<FilterBean> beans, List<Segment> segments) {
        List<SwiftDetailFilterValue> filterValues = new ArrayList<SwiftDetailFilterValue>();
        for (FilterBean bean : beans) {
            filterValues.add(createFilterValue(bean, segments));
        }
        return filterValues;
    }

    private static long createValueByDateFilterBeanType(DateFilterBean bean) {
        int beanType = bean.getType();
        long value;
        switch (beanType) {
            case BIConfConstants.CONF.DATE_TYPE.MULTI_DATE_CALENDAR:
                value = dateFilterBean2long(bean);
                break;
            default:
                value = getTime((DateSelectedValueBean) bean);
        }
        return value;
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

    private static long getTime(DateSelectedValueBean bean) {
        int value = bean.getValue();
        int type = bean.getType();
        return DateUtils.getTime(type, value);
    }

    private static SwiftDateInRangeFilterValue createValue(DateRangeValueBean bean) {
        SwiftDateInRangeFilterValue value = new SwiftDateInRangeFilterValue();
        value.setStart(dateFilterBean2long(bean.getStart()));
        value.setEnd(dateFilterBean2long(bean.getEnd()));
        return value;
    }

    private static long dateBoxFilterBean2Long(DateBoxFilterBean bean){
        Calendar c = Calendar.getInstance();
        c.set(Calendar.YEAR, bean.getYear());
        c.set(Calendar.MONTH, bean.getMonth());
        c.set(Calendar.DAY_OF_MONTH, bean.getDay());
        return c.getTimeInMillis();
    }

    private static SwiftDateInRangeFilterValue dateBoxFilterBean2DateRangeFilterValue(DateBoxFilterBean bean) {
        Calendar c = Calendar.getInstance();
        c.clear();
        c.set(Calendar.YEAR, bean.getYear());
        c.set(Calendar.MONTH, bean.getMonth());
        c.set(Calendar.DATE, bean.getDay());
        SwiftDateInRangeFilterValue filterValue = new SwiftDateInRangeFilterValue();
        filterValue.setStart(c.getTimeInMillis());
        filterValue.setEnd(toDayEnd(c));
        return filterValue;
    }

    private static long toDayEnd(Calendar c) {
        c.add(Calendar.DATE, 1);
        c.add(Calendar.MILLISECOND, -1);
        return c.getTimeInMillis();
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
