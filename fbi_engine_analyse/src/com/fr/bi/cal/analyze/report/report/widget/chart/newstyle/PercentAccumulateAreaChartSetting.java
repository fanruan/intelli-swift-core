package com.fr.bi.cal.analyze.report.report.widget.chart.newstyle;

import com.fr.json.JSONArray;
import com.fr.json.JSONException;
import com.fr.json.JSONObject;

import java.util.UUID;

/**
 * Created by User on 2016/8/31.
 */
public class PercentAccumulateAreaChartSetting extends AccumulateAreaChartSetting {

    public PercentAccumulateAreaChartSetting() throws JSONException {
    }

    @Override
    public JSONObject formatConfig(JSONObject options, JSONArray data) throws JSONException {
        JSONObject config = super.formatConfig(options, data);
        config.optJSONObject("plotOptions").optJSONObject("tooltip").optJSONObject("formatter").put("identifier", "${CATEGORY}${SERIES}${PERCENT}");
        return config;
    }

    @Override
    public JSONObject formatItems(JSONArray data, JSONArray types, JSONObject options) throws JSONException {
        JSONArray items = new JSONArray();
        for(int i = 0; i < data.length(); i++){
            String uuid = UUID.randomUUID().toString();
            JSONArray item = data.getJSONArray(i);
            for(int j = 0; j < items.length(); ){
                JSONObject it = item.getJSONObject(j);
                item.put(it.put("stack", uuid))
                    .put(it.put("stackByPercent", true));
            }
            items.put(item);
        }
        return super.formatItems(items, types, options);
    }
}
