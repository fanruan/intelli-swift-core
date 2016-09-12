package com.fr.bi.cal.analyze.report.report.widget.chart.newstyle;

import com.fr.json.JSONArray;
import com.fr.json.JSONException;
import com.fr.json.JSONObject;

/**
 * Created by windy on 2016/9/9.
 * 所有坐标轴图的基类
 */
public abstract class BIAbstractAxisChartSetting extends BIAbstractChartSetting {
    private JSONArray yAxis;
    private JSONArray xAxis;
    private JSONObject config;


    public BIAbstractAxisChartSetting() throws JSONException {
        this.xAxis = new JSONArray();
        this.yAxis = new JSONArray();
        this.config = new JSONObject();
        this.xAxis.put(new JSONObject()
                .put("type", "category")
                .put("title", new JSONObject()
                        .put("style", this.getFontStyle()))
                .put("labelStyle", this.getFontStyle()));
    }

    public BIAbstractAxisChartSetting(JSONArray xAxis) throws JSONException {
        this.xAxis = new JSONArray();
        this.yAxis = new JSONArray();
        this.config = new JSONObject();
        this.xAxis = xAxis;
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
        this.formatCordon(options.getJSONArray("cordon"), this.xAxis, this.yAxis, options.optInt("x_axis_number_level"), options.optInt("left_y_axis_number_level"), options.optInt("right_y_axis_number_level"), options.optInt("right_y_axis_second_number_level"));
        this.formatChartLegend(options.optInt("chart_legend"));
        plotOptions.optJSONObject("dataLabels").put("enabled", options.optBoolean("show_data_label"));
        this.config.optJSONObject("dataSheet").put("enabled", options.optBoolean("show_data_table"));
        this.xAxis.getJSONObject(0).put("showLabel", !options.optBoolean("show_data_table"));
        this.config.getJSONObject("zoom").getJSONObject("zoomTool").put("visible", options.optBoolean("show_zoom"));
        if(options.optBoolean("show_zoom")){
            this.config.remove("dataSheet");
            this.config.optJSONObject("zoom").remove("zoomType");
        }
        this.config.put("xAxis", this.xAxis).put("yAxis", this.yAxis);
        this.formatXYAxis(options, this.config, data);
        this.config.put("chartType", getChartTypeString());
        if(options.optBoolean("show_data_label")){
            this.formatPercentForItems(data, this.yAxis, options.optInt("left_y_axis_number_level"), options.optInt("right_y_axis_number_level"), options.optInt("right_y_axis_second_number_level"), options.optBoolean("num_separators"), options.optBoolean("right_num_separators"), options.optBoolean("right2_num_separators"));
        }
        return this.config.put("series", data);
    }

    public abstract String getChartTypeString();

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
        JSONObject configAndData = this.formatItems(data, types, options);
        this.config = configAndData.getJSONObject("config");
        JSONArray items = configAndData.getJSONArray("result");
        return this.formatConfig(options, items);
    }

    public void setyAxis(JSONArray yAxis) {
        this.yAxis = yAxis;
    }

    public void setxAxis(JSONArray xAxis) {
        this.xAxis = xAxis;
    }

    public void setConfig(JSONObject config) {

        this.config = config;
    }
}
