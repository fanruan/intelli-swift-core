package com.fr.bi.cal.analyze.report.report.widget.chart.newstyle;

import com.fr.json.JSONArray;
import com.fr.json.JSONException;
import com.fr.json.JSONObject;

/**
 * Created by User on 2016/8/31.
 */
public class DonutChartSetting extends BIAbstractChartSetting {

    private JSONObject config;

    public DonutChartSetting() {
        this.config = new JSONObject();
    }

    @Override
    public JSONObject formatConfig(JSONObject options, JSONArray data) throws JSONException {
        JSONObject plotOptions = this.config.getJSONObject("plotOptions");
        this.config.put("color", options.getJSONArray("chart_color"));
        this.config.put("style", this.formatChartStyle(options.getInt("chart_style")));
        this.formatChartLegend(options.optInt("chart_legend"));
        plotOptions.optJSONObject("dataLabels").put("enabled", options.optBoolean("show_data_label"));
        plotOptions.put("innerRadius", "50.0%");
        this.config.put("chartType", "pie");
        plotOptions.getJSONObject("dataLabels").put("align", "outside").put("connectorWidth", "outside").getJSONObject("formatter").put("identifier", "${VALUE}${PERCENT}");
        for(int i = 0; i < data.length(); i++){
            JSONArray da = data.getJSONObject(i).getJSONArray("data");
            for(int j = 0; j < da.length(); j++){
                JSONObject num = da.getJSONObject(j);
                num.put("y", this.formatXYDataWithMagnify(num.getDouble("y"), 1));
            }
        }
        return this.config.put("series", data);
    }

    @Override
    public JSONObject getConvertedDataAndSettings(JSONArray data, JSONArray types, JSONObject options) throws JSONException {
        JSONObject configAndData = this.formatItems(data, types, options);
        this.config = configAndData.getJSONObject("config");
        JSONArray items = configAndData.getJSONArray("result");
        return this.formatConfig(options, items);
    }
}
