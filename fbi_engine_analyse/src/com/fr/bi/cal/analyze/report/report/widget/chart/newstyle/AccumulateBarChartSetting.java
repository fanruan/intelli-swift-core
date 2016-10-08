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
            for(int j = 0; j < item.length(); j++){
                JSONObject it = item.getJSONObject(j);
                it.put("stack", stackId);
            }
        }
        return super.formatItems(data, types, options);
    }
}
