package com.fr.bi.cal.analyze.report.report.widget.chart.newstyle;

import com.fr.json.JSONArray;
import com.fr.json.JSONException;
import com.fr.json.JSONObject;

/**
 * Created by User on 2016/8/31.
 */
public class FallAxisChartSetting extends BIAbstractAxisChartSetting {

    public FallAxisChartSetting() throws JSONException {
    }

    @Override
    public String getChartTypeString() {
        return "column";
    }

    @Override
    public JSONObject formatConfig(JSONObject options, JSONArray data) throws JSONException {
        JSONObject config = super.formatConfig(options, data);
        config.getJSONObject("legend").put("enabled", false);
        return config;
    }

    @Override
    public JSONObject formatItems(JSONArray data, JSONArray types, JSONObject options) throws JSONException {
        JSONArray items;
        if(data.length() == 0){
            items = new JSONArray();
            return super.formatItems(items, types, options);
        }else{
            items = data.getJSONArray(0);
            JSONArray tables = new JSONArray();
            int sum = 0;
            JSONArray colors = options.optJSONArray("chart_color");
            if(colors == null){
                colors = new JSONArray();
                colors.put("rgb(152, 118, 170)").put("rgb(0, 157, 227)");
            }
            for(int i = 0; i < items.length(); i++){
                JSONObject item = items.getJSONObject(i);
                JSONArray da = item.getJSONArray("data");
                for(int j = 0; j < da.length(); j++){
                    JSONObject t = da.getJSONObject(j);
                    if(t.getInt("y") < 0){
                        tables.put(new JSONArray().put(t.getString("x")).put(t.getDouble("y")).put(sum + t.getDouble("y")));
                    }else{
                        tables.put(new JSONArray().put(t.getString("x")).put(t.getDouble("y")).put(sum));
                    }
                    sum += t.getDouble("y");
                }
            }
            JSONArray result = new JSONArray();
            for(int i = 0; i < 2; i++){
                JSONObject obj = new JSONObject();
                JSONArray da = new JSONArray();
                for(int j = 0; j < tables.length(); j++){
                    JSONArray cell = tables.getJSONArray(j);
                    JSONObject axis = new JSONObject();
                    axis.put("x", cell.getString(0)).put("y", Math.abs(cell.getInt(2 - i)));
                    if(i == 1){
                        axis.put("color", cell.getDouble(1) < 0 ? colors.getString(1) : colors.getString(0));

                    }else{
                        axis.put("color", "rgba(0,0,0,0)")
                                .put("borderColor", "rgba(0,0,0,0)")
                                .put("borderWidth", 0)
                                .put("clickColor", "rgba(0,0,0,0)")
                                .put("mouseOverColor", "rgba(0,0,0,0)")
                                .put("tooltip", new JSONObject().put("enable", false));
                    }
                    da.put(axis);
                }
                obj.put("data", da).put("stack", "stackedFall").put("name", "");
                result.put(obj);
            }
            return super.formatItems(new JSONArray().put(result), types, options);
        }
    }
}
