package com.fr.bi.cal.analyze.report.report.widget.chart.filter;

import com.fr.bi.conf.report.widget.field.filtervalue.FilterValue;
import com.fr.bi.field.filtervalue.FilterValueFactory;
import com.fr.bi.stable.constant.BIReportConstant;
import com.fr.json.JSONArray;
import com.fr.json.JSONObject;

/**
 * Created by AstronautOO7 on 2016/9/19.
 */
public class ChartFilterFactory {
    public static FilterValue parseFilterValue(JSONObject filterValue, long userID, JSONArray data) throws Exception {
        FilterValue filter = null;
        if(filterValue.has("filterType")){
            switch (filterValue.getInt("filterType")){
                case BIReportConstant.FILTER_TYPE.AND:

                    filter = new DotChartAndFilter(filterValue, userID, data);

                    break;
                case BIReportConstant.DIMENSION_FILTER_NUMBER.TOP_N:
                    filter = new ChartNumberTopNFilter(filterValue, userID, data);
                    break;

                case BIReportConstant.TARGET_FILTER_NUMBER.LARGE_OR_EQUAL_CAL_LINE:
                    filter = new LargeThanOREqualsAvg(filterValue, userID, data);
                    break;

                case BIReportConstant.TARGET_FILTER_NUMBER.SMALL_THAN_CAL_LINE:
                    filter = new SmallThanAvg(filterValue, userID, data);
                    break;

                default:
                    //默认的一些filter能满足需求，不能满足的重写
                    filter = FilterValueFactory.parseFilterValue(filterValue, userID);
                    break;
            }
        }
        return filter;
    }
}
