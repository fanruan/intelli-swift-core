package com.fr.bi.conf.report.style;

import com.fr.json.JSONArray;
import com.fr.json.JSONObject;
import com.fr.json.JSONParser;

/**
 * Created by User on 2016/9/6.
 */
public class ChartSetting implements JSONParser {

    private JSONArray cordon;
    private JSONObject settings;

    @Override
    public void parseJSON(JSONObject jo) throws Exception {
        if(jo.has("cordon")){
            cordon = jo.getJSONArray("cordon");
        }
        if(jo.has("settings")){
            settings = jo.getJSONObject("settings");
        }
    }

    public JSONObject getSettings(){
        return settings;
    }

    public JSONArray getCordon(){
        return cordon;
    }
}
