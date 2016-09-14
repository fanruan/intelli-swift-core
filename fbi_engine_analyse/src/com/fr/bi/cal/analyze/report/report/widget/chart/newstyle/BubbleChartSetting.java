package com.fr.bi.cal.analyze.report.report.widget.chart.newstyle;

import com.fr.json.JSONArray;
import com.fr.json.JSONException;
import com.fr.json.JSONObject;

/**
 * Created by User on 2016/8/31.
 */
public class BubbleChartSetting extends BIAbstractBubbleAndScatterSetting {

    public BubbleChartSetting() throws JSONException {
        super(true);
    }

    @Override
    public String getChartTypeString() {
        return "bubble";
    }

    @Override
    public JSONObject formatItems(JSONArray data, JSONArray types, JSONObject options) throws JSONException {

        for(int i = 0; i < data.length(); i++){
            JSONArray item = data.getJSONArray(i);
            for(int j = 0; j < data.length(); ){
                JSONObject it = item.getJSONObject(j);
                JSONArray da = it.getJSONArray("data");
                for(int k = 0; k < da.length(); k++){
                    JSONObject t = da.getJSONObject(k);
                    t.put("size", t.getString("z"));
                }
            }
        }
        return super.formatItems(data, types, options);
    }
}
