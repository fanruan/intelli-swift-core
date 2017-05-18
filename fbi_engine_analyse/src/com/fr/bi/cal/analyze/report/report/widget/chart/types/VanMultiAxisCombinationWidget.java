package com.fr.bi.cal.analyze.report.report.widget.chart.types;

import com.fr.json.JSONArray;
import com.fr.json.JSONObject;

/**
 * Created by eason on 2017/2/27.
 */
public class VanMultiAxisCombinationWidget extends VanCombinationWidget{

    public JSONObject createOptions(JSONObject globalStyle, JSONObject data) throws Exception{
        JSONObject options = super.createOptions(globalStyle, data);

        JSONArray colors = options.optJSONArray("colors");

        JSONArray yAxis = options.optJSONArray("yAxis");

        for(int i = 0, len = yAxis.length(); i < len; i++){
            JSONObject config = yAxis.optJSONObject(i);
            config.optJSONObject("labelStyle").put("color", colors.optString(i));
        }

        return options;
    }
}
