package com.fr.bi.cal.analyze.report.report.widget.chart.newstyle;

import com.fr.json.JSONArray;
import com.fr.json.JSONException;
import com.fr.json.JSONObject;

import java.util.UUID;

/**
 * Created by windy on 2016/9/9.
 */
public abstract class BIAbstractAccumulateChartSetting extends BIAbstractAxisChartSetting {

    public BIAbstractAccumulateChartSetting() throws JSONException {
        super();
    }

    @Override
    public JSONObject formatItems(JSONArray data, JSONArray types, JSONObject options) throws JSONException {
        JSONArray items = new JSONArray();
        for(int i = 0; i < data.length(); i++){
            String uuid = UUID.randomUUID().toString();
            JSONArray item = data.getJSONArray(i);
            for(int j = 0; j < items.length(); ){
                JSONObject it = item.getJSONObject(j);
                item.put(it.put("stack", uuid));
            }
            items.put(item);
        }
        return super.formatItems(items, types, options);
    }
}
