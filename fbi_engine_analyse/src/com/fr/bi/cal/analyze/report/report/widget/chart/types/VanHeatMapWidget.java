package com.fr.bi.cal.analyze.report.report.widget.chart.types;

import com.fr.json.JSONArray;
import com.fr.json.JSONException;
import com.fr.json.JSONObject;

/**
 * Created by eason on 2017/3/22.
 */
public class VanHeatMapWidget extends VanGisWidget{

    protected JSONObject populateDefaultSettings() throws JSONException {
        JSONObject settings = super.populateDefaultSettings();

        settings.put("blur", 80);
        settings.put("minOpacity", 0);
        settings.put("maxOpacity", 100);
        settings.put("lineWidth", 40);

        return settings;
    }

    public JSONObject createPlotOptions(JSONObject globalStyle, JSONObject settings) throws Exception {

        JSONObject plotOptions = super.createPlotOptions(globalStyle, settings);

        plotOptions.put("blur", settings.optDouble("blur")/100);
        plotOptions.put("minOpacity", settings.optDouble("minOpacity")/100);
        plotOptions.put("maxOpacity", settings.optDouble("maxOpacity")/100);
        plotOptions.put("radius", settings.optDouble("lineWidth"));

        return plotOptions;
    }

    public JSONArray createSeries(JSONObject originData) throws Exception {

        JSONArray series = JSONArray.create();
        String[] targetIDs = this.getUsedTargetID();
        String[] dimensionIDs = this.getUsedDimensionID();

        JSONArray children = originData.getJSONArray("c");

        JSONArray data = JSONArray.create();
        for (int j = 0, count = children.length(); j < count; j++) {
            NameLngLat nameLngLat = calculateNameLngLat(children.getJSONObject(j));

            data.put(JSONObject.create().put("lnglat", nameLngLat.lnglat)
                    .put("name", nameLngLat.name)
                    .put("value", nameLngLat.value));

        }
        JSONObject ser = JSONObject.create().put("data", data)
                .put("targetIDs", JSONArray.create().put(targetIDs[0]))
                .put("dimensionIDs", dimensionIDs);

        series.put(ser);


        return series;
    }

    protected void toLegendJSON(JSONObject options, JSONObject settings) throws JSONException{
        options.put(this.getLegendType(), this.parseLegend(settings));
    }

    public String getSeriesType(String dimensionID){
        return "heatMap";
    }

}
