package com.fr.bi.cal.analyze.report.report.widget.chart.newstyle;

import com.fr.json.JSONArray;
import com.fr.json.JSONException;
import com.fr.json.JSONObject;

/**
 * Created by User on 2016/8/31.
 */
public class ScatterChartSetting extends BIAbstractBubbleAndScatterSetting {

    public ScatterChartSetting() throws JSONException {
        super(false);
    }

    @Override
    public String getChartTypeString() {
        return "scatter";
    }

    @Override
    public JSONObject formatConfig(JSONObject options, JSONArray data) throws JSONException {
        JSONObject config = super.formatConfig(options, data);
        JSONObject plotOptions = config.getJSONObject("plotOptions");
        config.getJSONObject("plotOptions").put("marker", new JSONObject().put("symbol", "circle").put("radius", 4.5).put("enabled", true));
        plotOptions.getJSONObject("dataLabels").getJSONObject("formatter").put("identifier", "${X}${Y}");
        return config;
    }
}
