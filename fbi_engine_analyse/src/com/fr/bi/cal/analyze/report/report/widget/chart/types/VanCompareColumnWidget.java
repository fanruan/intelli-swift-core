package com.fr.bi.cal.analyze.report.report.widget.chart.types;

import com.fr.json.JSONArray;
import com.fr.json.JSONException;
import com.fr.json.JSONObject;
import com.fr.script.Calculator;

/**
 * Created by eason on 2017/2/27.
 */
public class VanCompareColumnWidget extends VanColumnWidget{

    protected JSONObject getDetailChartSetting() throws JSONException {
        JSONObject settings = super.getDetailChartSetting();

        dealYAxisDiffDefaultSettings(settings);

        return settings;
    }

    //make yaxis maxValue Double
    protected void dealYAxisDiffDefaultSettings(JSONObject settings) throws JSONException{
        StringBuilder right = new StringBuilder("max("), left = new StringBuilder("max(");
        boolean hasRight = false, hasLeft = false;

        String[] ids = this.getUsedTargetID();
        for(String id : ids){
            int yAxis = this.yAxisIndex(id);
            if(yAxis == 0){
                hasLeft = true;
                left.append("$").append(id).append("0,");
            } else {
                hasRight = true;
                right.append("$").append(id).append("0,");
            }
        }

        if(hasLeft){
            left.deleteCharAt(left.length() - 1);
        }
        if(hasRight){
            right.deleteCharAt(right.length() - 1);
        }

        left.append(")");right.append(")");

        settings
                .put("rightYReverse", true)
                .put("rightYShowCustomScale", true)
                .put("rightYCustomScale", JSONObject.create().put("maxScale", JSONObject.create().put("formula", String.format("=2 * %s", right.toString()))))
                .put("leftYShowCustomScale", true)
                .put("leftYCustomScale", JSONObject.create().put("maxScale", JSONObject.create().put("formula", String.format("=2 * %s", left.toString()))))

        ;
    }

    private JSONObject createEmptyCategoryAxis(JSONObject settings) throws JSONException{
        return JSONObject.create()
                .put("type", "category")
                .put("position", "top")
                .put("showLabel", false)
                .put("lineWidth", 0)
                .put("enableTick", false)
                .put("labelStyle", settings.optJSONObject("catLabelStyle"))
                ;
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
