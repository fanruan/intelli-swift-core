package com.fr.bi.cal.analyze.report.report.widget.chart.newstyle;

import com.fr.json.JSONArray;
import com.fr.json.JSONException;
import com.fr.json.JSONObject;

import java.util.UUID;

/**
 * Created by User on 2016/8/31.
 */
public class AccumulateBarChartSetting extends BIAbstractBarChartSetting {
    public AccumulateBarChartSetting() throws JSONException {
    }

    @Override
    public JSONObject formatItems(JSONArray data, JSONArray types, JSONObject options) throws JSONException {
        for(int i = 0; i < data.length(); i++){
            JSONArray item = data.getJSONArray(i);
            String stackId = UUID.randomUUID().toString();
            for(int j = 0; j < data.length(); ){
                JSONObject it = item.getJSONObject(j);
                it.put("stack", stackId);
                JSONArray da = it.getJSONArray("data");
                for(int k = 0; k < da.length(); k++){
                    JSONObject t = da.getJSONObject(k);
                    String tmp = t.getString("y");
                    t.put("y", t.getString("x"));
                    t.put("x", tmp);
                }
            }
        }
        return super.formatItems(data, types, options);
    }
}
