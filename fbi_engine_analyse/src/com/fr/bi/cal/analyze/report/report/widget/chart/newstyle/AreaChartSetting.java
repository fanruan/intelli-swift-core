package com.fr.bi.cal.analyze.report.report.widget.chart.newstyle;

import com.fr.json.JSONArray;
import com.fr.json.JSONException;
import com.fr.json.JSONObject;

/**
 * Created by User on 2016/8/31.
 */
public class AreaChartSetting extends BIAbstractChartSetting {
    @Override
    public JSONObject formatItems(JSONArray data, JSONArray types) throws JSONException {
        return super.formatItems(data, types);
    }

    @Override
    public JSONObject formatConfig(JSONObject options, JSONArray data) throws JSONException {
        return null;
    }

    @Override
    public JSONObject getConvertedDataAndSettings(JSONArray data, JSONArray types, JSONObject options) throws JSONException {
        return null;
    }
}
