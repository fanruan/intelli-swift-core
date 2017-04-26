package com.fr.bi.cal.analyze.report.report.widget.chart.types;

import com.fr.bi.cal.analyze.report.report.widget.VanChartWidget;
import com.fr.json.JSONObject;

/**
 * Created by eason on 2017/2/27.
 */
public class VanFunnelWidget extends VanChartWidget{

    private static final int SAME_ANGLE = 1;

    public JSONObject createPlotOptions(JSONObject globalStyle, JSONObject settings) throws Exception {

        JSONObject plotOptions = super.createPlotOptions(globalStyle, settings);

        plotOptions.put("sort", false);

        plotOptions.put("useSameSlantAngle", settings.optInt("slantStyle", SAME_ANGLE) == SAME_ANGLE);

        return plotOptions;
    }

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
