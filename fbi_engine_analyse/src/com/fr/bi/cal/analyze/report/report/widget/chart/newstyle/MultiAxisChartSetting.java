package com.fr.bi.cal.analyze.report.report.widget.chart.newstyle;

import com.fr.general.ComparatorUtils;
import com.fr.json.JSONArray;
import com.fr.json.JSONException;
import com.fr.json.JSONObject;

/**
 * Created by User on 2016/8/31.
 */
public class MultiAxisChartSetting extends BIAbstractAxisChartSetting {
    public MultiAxisChartSetting() throws JSONException {
    }

    @Override
    public String getChartTypeString() {
        return "column";
    }

    @Override
    public JSONObject formatConfig(JSONObject options, JSONArray data) throws JSONException {
        JSONObject config = super.formatConfig(options, data);
        JSONArray yAxis = config.getJSONArray("yAxis");
        JSONArray colors = config.getJSONArray("colors");
        for(int i = 0; i < yAxis.length(); i++){
            String color = colors.getString(i);
            yAxis.getJSONObject(i).getJSONObject("labelStyle").put("color", color).put("lineColor", color).put("tickColor", color);
        }
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
