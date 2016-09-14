package com.fr.bi.cal.analyze.report.report.widget.chart.newstyle;

import com.fr.json.JSONArray;
import com.fr.json.JSONException;
import com.fr.json.JSONObject;

/**
 * Created by windy on 2016/8/31.
 */
public class AccumulateAreaChartSetting extends BIAbstractAccumulateChartSetting {

    public AccumulateAreaChartSetting() throws JSONException {
    }

    @Override
    public String getChartTypeString() {
        return "area";
    }

    @Override
    public JSONObject getConvertedDataAndSettings(JSONArray data, JSONArray types, JSONObject options) throws JSONException {
        JSONObject DataAndSetting = super.getConvertedDataAndSettings(data, types, options);
        formatChartLineStyle(DataAndSetting.getJSONObject("config"), options.getInt("chart_line_type"));
        return DataAndSetting;
    }
}
