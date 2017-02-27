package com.fr.bi.cal.analyze.report.report.widget.chart.style;

import com.fr.bi.stable.constant.BIReportConstant;
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
    public JSONArray formatTypes(JSONArray data, JSONArray types) throws JSONException {
        JSONArray newTypes = new JSONArray();
//        for(int i = 0; i < data.length(); i++){
//            JSONArray type = new JSONArray();
//            JSONArray axisItems = data.getJSONArray(i);
//            for(int j = 0; j < axisItems.length(); j++){
//                type.put(BIReportConstant.WIDGET.COLUMN);
//            }
//            newTypes.put(type);
//        }
        return newTypes;
    }

    @Override
    public JSONObject formatItems(JSONArray data, JSONArray types, JSONObject options) throws JSONException {
        return JSONObject.EMPTY;
    }
}
