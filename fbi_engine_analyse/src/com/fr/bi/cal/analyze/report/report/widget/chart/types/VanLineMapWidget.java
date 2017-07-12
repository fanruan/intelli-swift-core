package com.fr.bi.cal.analyze.report.report.widget.chart.types;

import com.fr.json.JSONArray;
import com.fr.json.JSONException;
import com.fr.json.JSONObject;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by shine on 2017/7/10.
 */
public class VanLineMapWidget extends VanGisWidget{

    protected JSONObject populateDefaultSettings() throws JSONException {
        JSONObject settings = super.populateDefaultSettings();

        settings.put("lineWidth", 0);
        settings.put("effect", true);
        settings.put("bubbleSizeFrom", 12);
        settings.put("bubbleSizeTo", 40);

        return settings;
    }

    public JSONObject createPlotOptions(JSONObject globalStyle, JSONObject settings) throws Exception {

        JSONObject plotOptions = super.createPlotOptions(globalStyle, settings);
        JSONObject bubble = JSONObject.create();
        bubble.put("minSize", settings.optInt("bubbleSizeFrom"));
        bubble.put("maxSize", settings.optInt("bubbleSizeTo"));
        plotOptions.put("bubble", bubble);

        plotOptions.put("lineWidth", settings.optInt("lineWidth"));

        if(settings.optBoolean("effect")){
            plotOptions.put("effect", JSONObject.create().put("enabled", true).put("period", 8000));
            bubble.put("effect", JSONObject.create().put("enabled", true).put("period", 8000));
        }

        return plotOptions;
    }

    public JSONArray createSeries(JSONObject originData) throws JSONException {

        JSONArray series = JSONArray.create();

        String[] dimensionIDs = this.getUsedDimensionID();
        String[] targetIDs = this.getUsedTargetID();

        JSONObject top = originData.optJSONObject("t");//to
        JSONObject left = originData.optJSONObject("l");//from&value


        if(top != null && left != null){
            Map<String[], Double> map = new HashMap<String[], Double>();

            JSONArray toChild = top.optJSONArray("c");
            JSONArray fromChild = left.optJSONArray("c");

            JSONArray datas = JSONArray.create();
            for(int i = 0, len = toChild.length(); i < len; i++){
                NameLngLat to = calculateNameLngLat(toChild.getJSONObject(i));
                NameLngLat from = calculateNameLngLat(fromChild.getJSONObject(i));

                JSONObject point = JSONObject.create()
                        .put("from", JSONObject.create().put("lnglat", from.lnglat).put("name", from.name))
                        .put("to", JSONObject.create().put("lnglat", to.lnglat).put("name", to.name))
                        .put("value", from.value);

                datas.put(point);

                double value = Double.parseDouble(from.value);
                Double sum = map.get(to.lnglat);

                sum = sum == null ? value : sum + value;
                map.put(to.lnglat, sum);
            }

            series.put(JSONObject.create().put("data", datas).put("type", "lineMap")
                    .put("targetIDs", JSONArray.create().put(targetIDs[0]))
                    .put("dimensionIDs", dimensionIDs));

            addBubbleSeries(series, map, targetIDs, dimensionIDs);
        }

        return series;
    }

    private void addBubbleSeries(JSONArray series, Map<String[], Double> map, String[] targetIDs, String[] dimensionIDs) throws JSONException {
        JSONArray points = JSONArray.create();
        Iterator iterator = map.entrySet().iterator();
        while(iterator.hasNext()) {
            Map.Entry entry = (Map.Entry) iterator.next();

            points.put(JSONObject.create().put("size", entry.getValue()).put("lnglat", entry.getKey()));
        }

        series.put(JSONObject.create().put("data", points).put("type", "bubble")
                .put("targetIDs", JSONArray.create().put(targetIDs[0]))
                .put("dimensionIDs", dimensionIDs));
    }

    protected String getJSONValue(JSONObject jsonObject) throws JSONException {
        return jsonObject.optJSONObject("s").optJSONArray("s").optString(0);
    }

    @Override
    public String getSeriesType(String dimensionID) {
        return "lineMap";
    }

}
