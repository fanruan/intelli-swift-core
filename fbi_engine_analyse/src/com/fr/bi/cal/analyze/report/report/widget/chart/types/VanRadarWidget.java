package com.fr.bi.cal.analyze.report.report.widget.chart.types;

import com.fr.json.JSONException;
import com.fr.json.JSONObject;

/**
 * Created by eason on 2017/2/27.
 */
public class VanRadarWidget extends VanCartesianWidget{

    private static final int CIRCLE = 6;         //圆形雷达
    private static final int POLYGON = 7;         //多边形雷达

    protected JSONObject populateDefaultSettings() throws JSONException {

        JSONObject settings = super.populateDefaultSettings();

        settings.put("radarChartType", POLYGON);

        settings.put("nullContinuity", true);

        return settings;
    }

    public JSONObject createPlotOptions(JSONObject globalStyle, JSONObject settings) throws Exception{
        JSONObject plotOptions = super.createPlotOptions(globalStyle, settings);

        plotOptions.put("shape", settings.optInt("radarChartType") == CIRCLE ? "circle" : "polygon");

        plotOptions.put("columnType", false);

        return plotOptions;
    }

    protected String getCoordXKey(){
        return "angleAxis";
    }

    protected String getCoordYKey(){
        return "radiusAxis";
    }

    @Override
    protected boolean supportDataSheet() throws Exception {
        return false;
    }

    public String getSeriesType(String dimensionID){
        return "radar";
    }

    protected boolean checkValid(){
        return this.getDim1Size() > 0 && this.hasTarget();
    }
}
