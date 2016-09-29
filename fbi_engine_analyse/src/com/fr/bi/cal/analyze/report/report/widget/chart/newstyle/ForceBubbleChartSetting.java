package com.fr.bi.cal.analyze.report.report.widget.chart.newstyle;

import com.fr.bi.stable.constant.BIChartSettingConstant;
import com.fr.json.JSONArray;
import com.fr.json.JSONException;
import com.fr.json.JSONObject;

/**
 * Created by User on 2016/8/31.
 */
public class ForceBubbleChartSetting extends BIAbstractChartSetting {

    private JSONObject config;

    public ForceBubbleChartSetting() {
        this.config = new JSONObject();
    }

    @Override
    public JSONObject formatConfig(JSONObject options, JSONArray data) throws JSONException {
        JSONObject plotOptions = this.config.getJSONObject("plotOptions");
        this.config.put("color", options.getJSONArray("chart_color"));

        plotOptions.put("force", true);
        plotOptions.put("shadow", options.getInt("bubble_style") != BIChartSettingConstant.NO_PROJECT);
        plotOptions.getJSONObject("dataLabels").put("enabled", true).put("align", "inside").getJSONObject("formatter").put("identifier", "${CATEGORY}${VALUE}");

        this.formatChartLegend(this.config, options.optInt("chart_legend"));
        plotOptions.optJSONObject("dataLabels").put("enabled", options.optBoolean("show_data_label"));
        this.config.put("chartType", "bubble");
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
