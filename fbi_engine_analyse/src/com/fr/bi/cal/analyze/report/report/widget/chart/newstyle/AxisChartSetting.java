package com.fr.bi.cal.analyze.report.report.widget.chart.newstyle;

import com.fr.general.ComparatorUtils;
import com.fr.json.JSONArray;
import com.fr.json.JSONException;
import com.fr.json.JSONObject;

/**
 * Created by windy on 2016/8/31.
 */
public class AxisChartSetting extends BIAbstractAxisChartSetting {

    public AxisChartSetting() throws JSONException {
    }

    @Override
    public String getChartTypeString() {
        return "column";
    }

    @Override
    public JSONObject formatConfig(JSONObject options, JSONArray data) throws JSONException {
        JSONObject config = super.formatConfig(options, data);
        JSONArray lineItem = new JSONArray();
        JSONArray otherItem = new JSONArray();
        for(int i = 0; i < data.length(); i++){
            JSONObject item = data.getJSONObject(i);
            if(ComparatorUtils.equals(item.optString("type"), "line")){
                lineItem.put(item);
            }else{
                otherItem.put(item);
            }
        }
        for(int i = 0; i < lineItem.length(); i++){
            otherItem.put(lineItem.getJSONObject(i));
        }
        config.put("series", otherItem);
        return config;
    }
}
