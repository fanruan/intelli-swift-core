package com.fr.bi.cal.analyze.report.report.widget.chart.types;

import com.fr.json.JSONArray;
import com.fr.json.JSONException;
import com.fr.json.JSONObject;

/**
 * Created by eason on 2017/2/27.
 */
public class VanBarWidget extends VanColumnWidget{

    public boolean isInverted(){
        return true;
    }

    protected JSONArray parseCategoryAxis(JSONObject settings) throws JSONException {
        JSONArray array = super.parseCategoryAxis(settings);
        array.optJSONObject(0).put("reversed", true);
        return array;
    }

    protected JSONObject parseLeftValueAxis(JSONObject settings) throws JSONException {
        return super.parseLeftValueAxis(settings).put("position", "right");
    }

}
