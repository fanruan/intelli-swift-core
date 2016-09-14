package com.fr.bi.conf.report.style;

import com.fr.json.JSONArray;
import com.fr.json.JSONObject;
import com.fr.json.JSONParser;

/**
 * Created by User on 2016/9/6.
 * 维度指标上的chart setting属性
 */
public class ChartSetting implements JSONParser {

    private JSONArray cordon = new JSONArray();
    private JSONObject settings = new JSONObject();
    private JSONObject styleOfChart = new JSONObject();
    private JSONObject position = new JSONObject();

    @Override
    public void parseJSON(JSONObject jo) throws Exception {
        if(jo.has("cordon")){
            cordon = jo.getJSONArray("cordon");
        }
        if(jo.has("settings")){
            settings = jo.getJSONObject("settings");
        }
        if(jo.has("style_of_chart")){
            styleOfChart = jo.getJSONObject("style_of_chart");
        }
        if(jo.has("position")){
            position = jo.getJSONObject("position");
        }
    }

    public JSONObject getSettings(){
        return settings;
    }

    public JSONArray getCordon(){
        return cordon;
    }

    public JSONObject getStyleOfChart() {
        return styleOfChart;
    }

    public JSONObject getPosition(){
        return position;
    }
}
