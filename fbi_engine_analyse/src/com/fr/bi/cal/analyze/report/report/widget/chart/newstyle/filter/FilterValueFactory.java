package com.fr.bi.cal.analyze.report.report.widget.chart.newstyle.filter;

import com.fr.bi.cal.analyze.report.report.widget.chart.newstyle.filter.string.StringEndWithFilter;

import com.fr.bi.cal.analyze.report.report.widget.chart.newstyle.filter.number.*;
import com.fr.bi.cal.analyze.report.report.widget.chart.newstyle.filter.string.*;
import com.fr.bi.stable.constant.BIReportConstant;
import com.fr.json.JSONException;
import com.fr.json.JSONObject;

/**
 * Created by AstronautOO7 on 2016/9/19.
 */
public class FilterValueFactory {

    public static IFilter parseFilter(JSONObject filter) throws JSONException {
        IFilter filterValue = null;
        if (filter.has("filter_type")) {
            int type = filter.getInt("filter_type");
            switch (type) {
                case BIReportConstant.DIMENSION_FILTER_NUMBER.BELONG_VALUE:
                    filterValue = new NumberInRangeFilter(filter.getJSONObject("filter_value"));
                    break;
                case BIReportConstant.DIMENSION_FILTER_NUMBER.NOT_BELONG_VALUE:
                    filterValue = new NumberNotInRangeFilter(filter.getJSONObject("filter_value"));
                    break;
                case BIReportConstant.TARGET_FILTER_NUMBER.LARGE_OR_EQUAL_CAL_LINE:
                    filterValue = new NumberLargeThanOrEqualFilter();
                    break;
                case BIReportConstant.TARGET_FILTER_NUMBER.SMALL_THAN_CAL_LINE:
                    filterValue = new NumberLessThanFilter();
                    break;
                case BIReportConstant.DIMENSION_FILTER_NUMBER.IS_NULL:
                    filterValue = new NumberNullFilter();
                    break;
                case BIReportConstant.DIMENSION_FILTER_NUMBER.NOT_NULL:
                    filterValue = new NumberNotNullFilter();
                    break;
                case BIReportConstant.DIMENSION_FILTER_NUMBER.TOP_N:
                    filterValue = new NumberKthFilter(filter.optInt("filter_value", 0));
                    break;
                case BIReportConstant.TARGET_FILTER_NUMBER.EQUAL_TO:
                    filterValue = new NumberEqualFilter(filter.optDouble("filter_value", 0));
                    break;
                case BIReportConstant.TARGET_FILTER_NUMBER.NOT_EQUAL_TO:
                    filterValue = new NumberNotEqualFilter(filter.optDouble("filter_value", 0));
                    break;
                case BIReportConstant.DIMENSION_FILTER_STRING.BELONG_VALUE:
                    filterValue = new StringInFilter(filter.optJSONObject("filter_value"));
                    break;
                case BIReportConstant.DIMENSION_FILTER_STRING.NOT_BELONG_VALUE:
                    filterValue = new StringNotInFilter(filter.optJSONObject("filter_value"));
                    break;
                case BIReportConstant.DIMENSION_FILTER_STRING.CONTAIN:
                    filterValue = new StringContainFilter(filter.optString("filter_value"));
                    break;
                case BIReportConstant.DIMENSION_FILTER_STRING.NOT_CONTAIN:
                    filterValue = new StringNotContainFilter(filter.optString("filter_value"));
                    break;
                case BIReportConstant.DIMENSION_FILTER_STRING.IS_NULL:
                    filterValue = new StringNullFilter();
                    break;
                case BIReportConstant.DIMENSION_FILTER_STRING.NOT_NULL:
                    filterValue = new StringNotNullFilter();
                    break;
                case BIReportConstant.DIMENSION_FILTER_STRING.BEGIN_WITH:
                    filterValue = new StringStartWithFilter(filter.optString("filter_value"));
                    break;
                case BIReportConstant.DIMENSION_FILTER_STRING.END_WITH:
                    filterValue = new StringEndWithFilter(filter.optString("filter_value"));
                    break;
                default:
                    break;
            }
        }
        return filterValue;
    }
}
