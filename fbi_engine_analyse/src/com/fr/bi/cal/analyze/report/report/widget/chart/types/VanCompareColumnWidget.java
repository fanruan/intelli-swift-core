package com.fr.bi.cal.analyze.report.report.widget.chart.types;

import com.fr.json.JSONArray;
import com.fr.json.JSONException;
import com.fr.json.JSONObject;

/**
 * Created by eason on 2017/2/27.
 */
public class VanCompareColumnWidget extends VanColumnWidget{

    private static final double DEFAULT_MAX = 100;

    protected JSONArray parseValueAxis(JSONObject settings) throws JSONException{

        dealYAxisDiffDefaultSettings(settings);

        return super.parseValueAxis(settings);

    }

    //make yaxis maxValue Double
    protected void dealYAxisDiffDefaultSettings(JSONObject settings) throws JSONException{

        double leftYMax = -Double.MAX_VALUE, rightYMax = -Double.MAX_VALUE;
        String[] ids = this.getUsedTargetID();

        for(String id : ids){

            Double[] values = this.getValuesByID(id);

            int yAxis = this.yAxisIndex(id);
            if(yAxis == 0){
                for (int j = 0, count = values.length; j < count; j++) {
                    leftYMax = Math.max(leftYMax, values[j].doubleValue());
                }
            }else{
                for (int j = 0, count = values.length; j < count; j++) {
                    rightYMax = Math.max(rightYMax, values[j].doubleValue());
                }
            }
        }

        if(leftYMax == -Double.MAX_VALUE){
            leftYMax = DEFAULT_MAX;
        }

        if(rightYMax == -Double.MAX_VALUE){
            leftYMax = DEFAULT_MAX;
        }

        settings.put("rightYReverse", true);

        if(!settings.optBoolean("leftYShowCustomScale")){
            settings
                    .put("leftYShowCustomScale", true)
                    .put("leftYCustomScale", JSONObject.create().put("maxScale", 2 * leftYMax));
        }

        if(!settings.optBoolean("rightYShowCustomScale")){
            settings
                    .put("rightYShowCustomScale", true)
                    .put("rightYCustomScale", JSONObject.create().put("maxScale", 2 * rightYMax));
        }

    }

    private JSONObject createEmptyCategoryAxis(JSONObject settings) throws JSONException{
        return JSONObject.create()
                .put("type", "category")
                .put("position", "top")
                .put("showLabel", false)
                .put("lineWidth", 0)
                .put("enableTick", false)
                .put("labelStyle", settings.optJSONObject("catLabelStyle"));
    }

    @Override
    protected JSONArray parseCategoryAxis(JSONObject settings) throws JSONException {
        JSONArray array = super.parseCategoryAxis(settings);
        array.put(createEmptyCategoryAxis(settings));
        return array;
    }

    @Override
    public JSONArray createSeries(JSONObject data) throws Exception {
        JSONArray series = super.createSeries(data);
        return dealSeriesWithEmptyAxis(series);
    }

    protected JSONArray dealSeriesWithEmptyAxis(JSONArray series) throws JSONException{
        for(int i = 0, len = series.length(); i < len; i++){
            JSONObject ser = series.getJSONObject(i);

            int yAxisIndex = ser.optInt("yAxis");
            if(yAxisIndex == 1){
                ser.put("xAxis", 1);
            }
        }

        return series;
    }

}
