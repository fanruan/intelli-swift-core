package com.fr.bi.cal.analyze.report.report.widget.chart.types;

import com.fr.json.JSONArray;
import com.fr.json.JSONException;
import com.fr.json.JSONObject;
import com.fr.script.Calculator;

/**
 * Created by eason on 2017/2/27.
 */
public class VanCompareColumnWidget extends VanColumnWidget{

    @Override
    protected boolean rightYReverse() {
        return true;
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
    protected JSONArray parseCategoryAxis(JSONObject settings, Calculator calculator) throws JSONException {
        JSONArray array = super.parseCategoryAxis(settings, calculator);
        array.put(createEmptyCategoryAxis(settings));
        return array;
    }

    @Override
    public JSONArray createSeries(JSONObject data) throws Exception {
        JSONArray series = super.createSeries(data);
        return dealSeriesWithEmptyAxis(series);
    }

    private JSONArray dealSeriesWithEmptyAxis(JSONArray series) throws JSONException{
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
