package com.fr.bi.cal.analyze.report.report.widget.chart.types;

import com.fr.bi.cal.analyze.report.report.widget.VanChartWidget;
import com.fr.json.JSONArray;
import com.fr.json.JSONException;
import com.fr.json.JSONObject;

/**
 * Created by eason on 2017/2/27.
 */
public class VanFunnelWidget extends VanChartWidget{

    protected String categoryKey(){
        return "name";
    }

    protected String valueKey(){
        return "value";
    }

    public String getSeriesType(String dimensionID){
        return "funnel";
    }

    protected String getTooltipIdentifier(){
        return NAME + SERIES + VALUE;
    }
}
