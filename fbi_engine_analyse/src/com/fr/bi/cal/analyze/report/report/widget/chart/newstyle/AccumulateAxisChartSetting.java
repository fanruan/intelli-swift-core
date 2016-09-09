package com.fr.bi.cal.analyze.report.report.widget.chart.newstyle;

import com.fr.json.JSONArray;
import com.fr.json.JSONException;
import com.fr.json.JSONObject;

import java.util.UUID;

/**
 * Created by User on 2016/8/31.
 */
public class AccumulateAxisChartSetting extends BIAbstractChartSetting {

    private JSONArray yAxis;
    private JSONArray xAxis;
    private JSONObject config;


    public AccumulateAxisChartSetting() throws JSONException{
        this.xAxis = new JSONArray();
        this.yAxis = new JSONArray();
        this.config = new JSONObject();
        this.xAxis.put(new JSONObject()
                .put("type", "category")
                .put("title", new JSONObject()
                        .put("style", this.getFontStyle()))
                .put("labelStyle", this.getFontStyle()));
    }

    @Override
    public JSONObject formatItems(JSONArray data, JSONArray types) throws JSONException {
        JSONArray items = new JSONArray();
        for(int i = 0; i < data.length(); i++){
            String uuid = UUID.randomUUID().toString();
            JSONArray item = data.getJSONArray(i);
            for(int j = 0; j < items.length(); ){
                JSONObject it = item.getJSONObject(j);
                item.put(it.put("stack", uuid));
            }
        }
        return super.formatItems(items, types);
    }

    @Override
    public JSONObject formatConfig(JSONObject options, JSONArray data) throws JSONException{
        this.config.put("color", options.getJSONArray("chart_color"));
        return null;
    }

    @Override
    public JSONObject getConvertedDataAndSettings(JSONArray data, JSONArray types, JSONObject options) throws JSONException{
        for(int i = 0; i < types.length(); i++){
            JSONArray type = types.getJSONArray(i);
            if(type.length() == 0){
                continue;
            }
            JSONObject newYAxis = new JSONObject().put("type", "value")
                    .put("title", new JSONObject().put("style", this.getFontStyle()))
                    .put("labelStyle", this.getFontStyle())
                    .put("position", i > 0 ? "right" : "left")
                    .put("lineWidth", 1)
                    .put("axisIndex", i)
                    .put("gridLineWidth", 0);
            this.yAxis.put(newYAxis);
        }
        JSONObject configAndData = this.formatItems(data, types);
        this.config = configAndData.getJSONObject("config");
        JSONArray items = configAndData.getJSONArray("result");
        return this.formatConfig(options, items);
    }
}
