package com.fr.bi.conf.report.style;

import com.fr.json.JSONArray;
import com.fr.json.JSONObject;
import com.fr.json.JSONParser;

/**
 * Created by User on 2016/9/6.
 */
public class ChartSetting implements JSONParser {

    private JSONArray cordon = new JSONArray();
    private JSONObject settings = new JSONObject();
    private JSONObject styleOfChart = new JSONObject();

    @Override
    public void parseJSON(JSONObject jo) throws Exception {
        if(jo.has("cordon")){
            cordon = jo.getJSONArray("cordon");
        }
        if(jo.has("settings")){
            settings = jo.getJSONObject("settings");
        }
        if(jo.has("style_of_chart")){
            styleOfChart = jo.getJSONObject("settings");
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
}
