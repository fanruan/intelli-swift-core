package com.fr.bi.conf.report.style;

import com.fr.json.JSONObject;
import com.fr.json.JSONParser;

/**
 * Created by User on 2016/9/6.
 * chart get的setting属性
 */
public class DetailChartSetting implements JSONParser {

    private JSONObject settings = new JSONObject();

    @Override
    public void parseJSON(JSONObject jo) throws Exception {
        if(jo.has("settings")){
            settings = jo.getJSONObject("settings");
        }
    }

    public JSONObject getDetailChartSetting() {
        return settings;
    }
}
