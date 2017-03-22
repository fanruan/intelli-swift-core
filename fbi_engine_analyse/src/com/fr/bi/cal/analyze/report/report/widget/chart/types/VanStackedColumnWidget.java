package com.fr.bi.cal.analyze.report.report.widget.chart.types;


import com.fr.json.JSONArray;
import com.fr.json.JSONException;
import com.fr.json.JSONObject;

/**
 * Created by eason on 2017/2/27.
 */
public class VanStackedColumnWidget extends VanColumnWidget{
    public JSONArray createSeries(JSONObject data) throws JSONException {
        JSONArray series =  createXYSeries(data);

        for(int i = 0, len = series.length(); i < len; i++){
            series.getJSONObject(i).put("stack", true);
        }

        return series;
    }
}
