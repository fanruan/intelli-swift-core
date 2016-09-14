package com.fr.bi.cal.analyze.report.report.widget.chart.newstyle;

import com.fr.json.JSONArray;
import com.fr.json.JSONException;
import com.fr.json.JSONObject;

/**
 * Created by User on 2016/8/31.
 */
public class RangeAreaChartSetting extends BIAbstractAxisChartSetting {

    public RangeAreaChartSetting() throws JSONException {
    }

    @Override
    public String getChartTypeString() {
        return "area";
    }

    @Override
    public JSONObject formatItems(JSONArray data, JSONArray types, JSONObject options) throws JSONException {
        //return super.formatItems(data, types, options);
        JSONArray items = new JSONArray();
        for(int i = 0; i < data.length(); i++){
            items.put(data.getJSONArray(i));
        }
        if (items.length() == 0) {
            return super.formatItems(items, types, options);
        }
        if (items.length() == 1) {
            return super.formatItems(new JSONArray().put(items), types, options);
        }
        JSONArray colors = options.optJSONArray("chart_color");
        if (colors == null) {
            colors = new JSONArray().put("#5caae4");
        }
        JSONArray seriesMinus = new JSONArray();
        JSONArray da = items.getJSONObject(0).getJSONArray("data");
        for(int i = 0; i < da.length(); i++){
            JSONObject item = da.getJSONObject(i);
            JSONObject tmp = items.getJSONObject(1).getJSONArray("data").getJSONObject(i);
            double res = tmp.getDouble("y") - item.getDouble("y");
            seriesMinus.put(new JSONObject().put("x", tmp.getString("x")).put("y", res));
        }
        items.getJSONObject(1).put("data", seriesMinus)
                .put("name", items.getJSONObject(1).getString("name"))
                .put("stack", "stackedArea")
                .put("fillColor", colors.getString(0));
        for(int i = 0; i < items.length(); i++){
            JSONObject item = items.getJSONObject(i);
            if(i == 0){
                item.put("name", items.getJSONObject(0).getString("name"))
                        .put("fillColorOpacity", 0).put("stack", "stackedArea")
                        .put("marker", new JSONObject().put("enabled", false))
                        .put("fillColor", "#000000");
            }
        }
        return super.formatItems(new JSONArray().put(items), types, options);
    }

    @Override
    public JSONObject formatConfig(JSONObject options, JSONArray data) throws JSONException {
        JSONObject config = super.formatConfig(options, data);
        config.getJSONObject("plotOptions").optJSONObject("tooltip").getJSONObject("formatter").put("identifier", "${CATEGORY}${VALUE}");
        return config;
    }
}
