package com.fr.bi.cal.analyze.report.report.widget.chart.newstyle;

import com.fr.bi.stable.constant.BIChartSettingConstant;
import com.fr.json.JSONArray;
import com.fr.json.JSONException;
import com.fr.json.JSONObject;

/**
 * Created by User on 2016/9/11.
 */
public abstract class BIAbstractRadarChartSetting extends BIAbstractChartSetting {
    private JSONArray radiusAxis;
    private JSONObject config;


    public BIAbstractRadarChartSetting() throws JSONException {
        this.radiusAxis = new JSONArray();
        this.config = new JSONObject();
        this.radiusAxis.put(new JSONObject()
                .put("type", "value")
                .put("title", new JSONObject()
                        .put("style", this.getFontStyle()))
                .put("labelStyle", this.getFontStyle()));
    }

    @Override
    public JSONObject formatItems(JSONArray data, JSONArray types, JSONObject options) throws JSONException {
        return super.formatItems(data, types, options);
    }

    @Override
    public JSONObject formatConfig(JSONObject options, JSONArray data) throws JSONException{
        JSONObject plotOptions = this.config.getJSONObject("plotOptions");
        this.config.put("color", options.getJSONArray("chart_color"));
        this.config.put("style", this.formatChartStyle(options.getInt("chart_style")));
        this.formatChartLegend(this.config, options.optInt("chart_legend"));
        plotOptions.optJSONObject("dataLabels").put("enabled", options.optBoolean("show_data_label"));
        this.config.put("radiusAxis", this.radiusAxis);

        this.radiusAxis.getJSONObject(0).put("formatter", formatTickInXYaxis(options.optInt("left_y_axis_style"), options.optInt("left_y_axis_number_level"), options.optBoolean("num_separators")));
        formatNumberLevelInYaxis(config, data, options.optInt("left_y_axis_number_level"), 0, this.radiusAxis.getJSONObject(0).optString("formatter"));
        this.radiusAxis.getJSONObject(0).getJSONObject("title").put("text", getXYAxisTitle(options.optInt("left_y_axis_number_level"), BIChartSettingConstant.LEFT_AXIS, options.optBoolean("show_left_y_axis_title"), options.optString("left_y_axis_unit"), options.optString("left_y_axis_title")));
        this.radiusAxis.getJSONObject(0).put("gridLineWidth", options.optBoolean("show_grid_line") ? 1 : 0);
        this.config.put("chartType", getChartTypeString());
        if(options.optBoolean("show_data_label")){
            for (int i = 0; i < data.length(); i++) {
                JSONObject item = data.getJSONObject(i);
                if (options.optInt("left_y_axis_number_level") == BIChartSettingConstant.CHART_TARGET_STYLE.NUM_LEVEL.PERCENT || options.optBoolean("num_separators")) {
                    item.put("dataLabels", new JSONObject()
                            .put("style", this.getFontStyle())
                            .put("align", "outside")
                            .put("enable", true)
                            .put("formatter", new JSONObject("{identifier:\"${VALUE}\", valueFormat:" +
                                    radiusAxis.getJSONObject(0).getString("formatter") + "}")));
                }
            }
        }
        return this.config.put("series", data);
    }

    public String getChartTypeString(){
        return "radar";
    };

    @Override
    public JSONObject getConvertedDataAndSettings(JSONArray data, JSONArray types, JSONObject options) throws JSONException{
        JSONObject configAndData = this.formatItems(data, types, options);
        this.config = configAndData.getJSONObject("config");
        JSONArray items = configAndData.getJSONArray("result");
        formatChartRadarStyle(this.config, options.getInt("chart_line_type"));
        return this.formatConfig(options, items);
    }

    public void formatChartRadarStyle(JSONObject config, int type) throws JSONException{
        JSONObject plotOptions = config.optJSONObject("plotOptions");
        switch (type) {
            case BIChartSettingConstant.CHART_SHAPE.POLYGON:
                plotOptions.put("shape", "polygon");
                break;
            case BIChartSettingConstant.CHART_SHAPE.CIRCLE:
                plotOptions.put("shape", "circle");
                break;
        }
    }
}
