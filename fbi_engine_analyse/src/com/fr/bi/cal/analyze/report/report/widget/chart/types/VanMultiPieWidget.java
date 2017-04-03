package com.fr.bi.cal.analyze.report.report.widget.chart.types;

import com.fr.json.JSONArray;
import com.fr.json.JSONException;
import com.fr.json.JSONObject;

/**
 * Created by eason on 2017/3/22.
 */
public class VanMultiPieWidget extends VanPieWidget{

    private static final int LIGHTER = 1;
    private static final int DARKER = 2;

    protected JSONObject populateDefaultSettings() throws JSONException {
        JSONObject settings = super.populateDefaultSettings();

        settings.put("gradientType", LIGHTER);

        return settings;
    }

    public JSONObject createPlotOptions(JSONObject globalStyle, JSONObject settings) throws Exception{
        JSONObject plotOptions = super.createPlotOptions(globalStyle, settings);

        plotOptions.put("gradual", settings.optInt("gradientType") == LIGHTER ? "lighter" : "darker");

        plotOptions.put("drilldown", true);

        return plotOptions;
    }

    public JSONArray createSeries(JSONObject data) throws Exception{


        return JSONArray.create();
    }

    public String getSeriesType(String dimensionID){
        return "multiPie";
    }

}
