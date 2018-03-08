package com.fr.swift.adaptor.transformer;

import com.finebi.conf.constant.BICommonConstants;
import com.finebi.conf.constant.BIConfConstants;
import com.finebi.conf.internalimp.bean.filter.AbstractFilterBean;
import com.finebi.conf.internalimp.bean.filter.date.DateAfterFilterBean;
import com.finebi.conf.internalimp.bean.filter.date.DateBeforeFilterBean;
import com.finebi.conf.internalimp.bean.filter.date.DateBelongFilterBean;
import com.finebi.conf.internalimp.bean.filter.number.NumberBelongFilterBean;
import com.finebi.conf.internalimp.bean.filter.number.NumberBottomNFilterBean;
import com.finebi.conf.internalimp.bean.filter.number.NumberEqualFilterBean;
import com.finebi.conf.internalimp.bean.filter.number.NumberLargeFilterBean;
import com.finebi.conf.internalimp.bean.filter.number.NumberLargeOrEqualFilterBean;
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
import com.finebi.conf.internalimp.bean.filtervalue.date.DateSelectedValueBean;
import com.finebi.conf.internalimp.bean.filtervalue.date.DateValueBean;
import com.finebi.conf.internalimp.bean.filtervalue.number.NumberValue;
import com.finebi.conf.structure.bean.filter.DateFilterBean;
import com.finebi.conf.structure.bean.filter.FilterBean;
import com.finebi.conf.structure.filter.FineFilter;
import com.fr.swift.adaptor.transformer.date.DateUtils;
import com.fr.swift.query.filter.SwiftDetailFilterType;
import com.fr.swift.query.filter.info.DetailFilterInfo;
import com.fr.swift.query.filter.info.FilterInfo;
import com.fr.swift.query.filter.info.SwiftDetailFilterValue;
import com.fr.swift.query.filter.info.value.SwiftDateInRangeFilterValue;
import com.fr.swift.query.filter.info.value.SwiftNumberInRangeFilterValue;

import java.util.ArrayList;
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
        return transformFilterBean(beans);
    }

    public static FilterInfo transformFilterBean(List<FilterBean> beans) {
        List<SwiftDetailFilterValue> filterValues = new ArrayList<SwiftDetailFilterValue>();
        for (FilterBean bean : beans) {
            filterValues.add(createFilterValue(bean));
        }
        return new DetailFilterInfo(filterValues);
    }

    private static SwiftDetailFilterValue createFilterValue(FilterBean bean) {
        String fieldName = ((AbstractFilterBean) bean).getFieldName();
        int type = bean.getFilterType();
        switch (type) {
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

            case BICommonConstants.ANALYSIS_FILTER_NUMBER.BELONG_VALUE: {
                NumberValue nv = ((NumberBelongFilterBean) bean).getFilterValue();
                return new SwiftDetailFilterValue<SwiftNumberInRangeFilterValue>(fieldName, createValue(nv),
                        SwiftDetailFilterType.NUMBER_IN_RANGE);
            }
            case BICommonConstants.ANALYSIS_FILTER_NUMBER.NOT_BELONG_VALUE: {
                NumberValue nv = ((NumberBelongFilterBean) bean).getFilterValue();
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
                Double min = ((NumberLargeFilterBean) bean).getFilterValue().getValue();
                SwiftNumberInRangeFilterValue value = new SwiftNumberInRangeFilterValue();
                value.setMin(min);
                return new SwiftDetailFilterValue<SwiftNumberInRangeFilterValue>(fieldName, value,
                        SwiftDetailFilterType.NUMBER_IN_RANGE);
            }
            case BICommonConstants.ANALYSIS_FILTER_NUMBER.SMALL: {
                Double max = ((NumberSmallFilterBean) bean).getFilterValue().getValue();
                SwiftNumberInRangeFilterValue value = new SwiftNumberInRangeFilterValue();
                value.setMax(max);
                return new SwiftDetailFilterValue<SwiftNumberInRangeFilterValue>(fieldName, value,
                        SwiftDetailFilterType.NUMBER_IN_RANGE);
            }
            case BICommonConstants.ANALYSIS_FILTER_NUMBER.LARGE_OR_EQUAL: {
                Double min = ((NumberLargeOrEqualFilterBean) bean).getFilterValue().getValue();
                SwiftNumberInRangeFilterValue value = new SwiftNumberInRangeFilterValue();
                value.setMin(min);
                value.setMinIncluded(true);
                return new SwiftDetailFilterValue<SwiftNumberInRangeFilterValue>(fieldName, value,
                        SwiftDetailFilterType.NUMBER_IN_RANGE);
            }
            case BICommonConstants.ANALYSIS_FILTER_NUMBER.SMALL_OR_EQUAL: {
                Double max = ((NumberSmallOrEqualFilterBean) bean).getFilterValue().getValue();
                SwiftNumberInRangeFilterValue value = new SwiftNumberInRangeFilterValue();
                value.setMax(max);
                value.setMaxIncluded(true);
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

            case BICommonConstants.ANALYSIS_FILTER_DATE.BELONG_VALUE: {
                DateValueBean dateValueBean = ((DateBelongFilterBean) bean).getFilterValue();
                return new SwiftDetailFilterValue<SwiftDateInRangeFilterValue> (fieldName,
                        createValue(dateValueBean), SwiftDetailFilterType.DATE_IN_RANGE);
            }
            case BICommonConstants.ANALYSIS_FILTER_DATE.NOT_BELONG_VALUE: {
                DateValueBean dateValueBean = ((DateBelongFilterBean) bean).getFilterValue();
                return new SwiftDetailFilterValue<SwiftDateInRangeFilterValue> (fieldName,
                        createValue(dateValueBean), SwiftDetailFilterType.DATE_NOT_IN_RANGE);
            }
            case BICommonConstants.ANALYSIS_FILTER_DATE.LESS_THAN: {
                DateFilterBean dateFilterBean = ((DateBeforeFilterBean) bean).getFilterValue();
                SwiftDateInRangeFilterValue filterValue = new SwiftDateInRangeFilterValue();
                filterValue.setEnd(getTime((DateSelectedValueBean) dateFilterBean));
                return new SwiftDetailFilterValue<SwiftDateInRangeFilterValue> (fieldName,
                        filterValue, SwiftDetailFilterType.DATE_IN_RANGE);
            }
            case BICommonConstants.ANALYSIS_FILTER_DATE.MORE_THAN: {
                DateFilterBean dateFilterBean = ((DateAfterFilterBean) bean).getFilterValue();
                SwiftDateInRangeFilterValue filterValue = new SwiftDateInRangeFilterValue();
                filterValue.setStart(getTime((DateSelectedValueBean) dateFilterBean));
                return new SwiftDetailFilterValue<SwiftDateInRangeFilterValue> (fieldName,
                        filterValue, SwiftDetailFilterType.DATE_IN_RANGE);
            }
            case BICommonConstants.ANALYSIS_FILTER_DATE.EQUAL_TO:
            case BICommonConstants.ANALYSIS_FILTER_DATE.NOT_EQUAL_TO:
            case BICommonConstants.ANALYSIS_FILTER_DATE.TOP_N:
            case BICommonConstants.ANALYSIS_FILTER_DATE.BOTTOM_N:
            case BICommonConstants.ANALYSIS_FILTER_DATE.IS_NULL:
            case BICommonConstants.ANALYSIS_FILTER_DATE.NOT_NULL:


            case BICommonConstants.ANALYSIS_FILTER_TYPE.FORMULA:
            case BICommonConstants.ANALYSIS_FILTER_TYPE.AND:
            case BICommonConstants.ANALYSIS_FILTER_TYPE.OR:
            case BICommonConstants.ANALYSIS_FILTER_TYPE.EMPTY_FORMULA:
            case BICommonConstants.ANALYSIS_FILTER_TYPE.EMPTY_CONDITION:
            default:
        }
        return null;
    }

    private static long getTime(DateSelectedValueBean bean) {
        int value = bean.getValue();
        int type = bean.getType();
        return DateUtils.getTime(type, value);
    }

    private static SwiftDateInRangeFilterValue createValue(DateValueBean bean) {
        SwiftDateInRangeFilterValue value = new SwiftDateInRangeFilterValue();
        value.setStart(bean.getStart());
        value.setEnd(bean.getEnd());
        return value;
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
