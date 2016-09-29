package com.fr.bi.cal.analyze.report.report.widget.chart.newstyle.filter.objectcondition;

import com.fr.bi.cal.analyze.report.report.widget.chart.newstyle.filter.condition.EmptyFilter;
import com.fr.bi.stable.constant.BIReportConstant;
import com.fr.json.JSONException;
import com.fr.json.JSONObject;

/**
 * Created by AstronautOO7 on 2016/9/19.
 */
public class FilterBubbleScatterFactory {

    public static AbstractXYZFilter parseFilter(JSONObject filterValue) throws JSONException {
        AbstractXYZFilter filter = null;
        if(filterValue.has("filter_type")){
            switch (filterValue.getInt("filter_type")){
                case BIReportConstant.FILTER_TYPE.AND:
                    filter = new XYZGeneralAndFilter(filterValue);
                    break;
                case BIReportConstant.FILTER_TYPE.OR:
                    filter = new XYZGeneralOrFilter(filterValue);
                    break;
                case BIReportConstant.FILTER_TYPE.EMPTY_CONDITION:
                    filter = new EmptyFilter();
                    break;
                default:
                    filter = new XYZSingleFilter(filterValue);
                    break;
            }
        }
        return filter;
    }
}
