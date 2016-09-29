package com.fr.bi.cal.analyze.report.report.widget.chart.newstyle;

import com.fr.bi.stable.constant.BIChartSettingConstant;
import com.fr.json.JSONArray;
import com.fr.json.JSONException;
import com.fr.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by User on 2016/9/11.
 */
public abstract class BIAbstractCompareChart extends BIAbstractAxisChartSetting {

    private List<Double> maxes = new ArrayList<Double>();
    private JSONObject extraXAxis;

    public BIAbstractCompareChart() throws JSONException {
        extraXAxis = new JSONObject()
                .put("type", "category")
                .put("position", "top")
                .put("showLabel", false)
                .put("title", new JSONObject()
                        .put("style", this.getFontStyle()))
                .put("labelStyle", this.getFontStyle());
    }

    @Override
    public JSONObject formatItems(JSONArray data, JSONArray types, JSONObject options) throws JSONException {
        for(int i = 0; i < data.length(); i++){
            JSONArray item = data.getJSONArray(i);
            for(int j = 0; j < item.length(); j++){
                JSONObject it = item.getJSONObject(j);
                if(i > 0){
                    it.put("reversed", true).put("xAxis", 1);
                }else{
                    it.put("reversed", false).put("xAxis", 0);
                }
            }
        }
        return super.formatItems(data, types, options);
    }

    @Override
    public void formatXYAxis(JSONObject options, JSONObject config, JSONArray items) throws JSONException {
        JSONArray yAxis = config.getJSONArray("yAxis");
        for(int i = 0; i < yAxis.length(); i++){
            JSONObject axis = yAxis.getJSONObject(i);
            int axisIndex = axis.optInt("axisIndex");
            switch (axisIndex) {
                case BIChartSettingConstant.LEFT_AXIS:
                    axis.getJSONObject("title").put("text", this.getXYAxisTitle(options.optInt("left_y_axis_number_level"), BIChartSettingConstant.LEFT_AXIS, options.optBoolean("show_left_y_axis_title"), options.optString("left_y_axis_unit"), options.optString("left_y_axis_title")));
                    axis.getJSONObject("title").put("rotation", BIChartSettingConstant.ROTATION);
                    axis.put("lineWidth", options.optInt("line_width"))
                            .put("showLabel", options.optBoolean("show_label"))
                            .put("enableTick", options.optBoolean("enable_tick"))
                            .put("reversed", false)
                            .put("enableMinorTick", options.optBoolean("enable_minor_tick"))
                            .put("gridLineWidth", options.optBoolean("show_grid_line") ? 1 : 0)
                            .put("formatter", this.formatTickInXYaxis(options.optInt("left_y_axis_style"), options.optInt("left_y_axis_number_level"), options.optBoolean("num_separators")));
                    this.formatNumberLevelInYaxis(config, items, options.optInt("left_y_axis_number_level"), i, axis.optString("formatter"));
                    break;
                case BIChartSettingConstant.RIGHT_AXIS:
                    axis.getJSONObject("title").put("text", this.getXYAxisTitle(options.optInt("right_y_axis_number_level"), BIChartSettingConstant.RIGHT_AXIS, options.optBoolean("show_right_y_axis_title"), options.optString("right_y_axis_unit"), options.optString("right_y_axis_title")));
                    axis.getJSONObject("title").put("rotation", BIChartSettingConstant.ROTATION);
                    axis.put("lineWidth", options.optInt("line_width"))
                            .put("showLabel", options.optBoolean("show_label"))
                            .put("enableTick", options.optBoolean("enable_tick"))
                            .put("reversed", true)
                            .put("enableMinorTick", options.optBoolean("enable_minor_tick"))
                            .put("gridLineWidth", options.optBoolean("show_grid_line") ? 1 : 0)
                            .put("formatter", this.formatTickInXYaxis(options.optInt("right_y_axis_style"), options.optInt("right_y_axis_number_level"), options.optBoolean("right_num_separators")));
                    this.formatNumberLevelInYaxis(config, items, options.optInt("right_y_axis_number_level"), i, axis.optString("formatter"));
                    break;
            }
            double[] res = this.calculateValueNiceDomain(0, this.maxes.get(axis.getInt("axisIndex")));
            double max = res[1] * 2, min = res[0] * 2;
            axis.put("max", max);
            axis.put("min", min);
            axis.put("tickInterval", (max - min) / 5);
        }
        JSONArray xAxis = config.getJSONArray("xAxis");
        xAxis.getJSONObject(0).getJSONObject("title")
                .put("text", options.optBoolean("show_x_axis_title") ? options.getString("x_axis_title") : "")
                .put("align", "center");
        xAxis.getJSONObject(0)
                .put("lineWidth", options.optInt("line_width"))
                .put("enableTick", options.optBoolean("enable_tick"))
                .put("enableMinorTick", options.optBoolean("enable_minor_tick"))
                .put("labelRotation", options.optString("text_direction"))
                .put("gridLineWidth", options.optBoolean("show_grid_line") ? 1 : 0);
        xAxis.put(extraXAxis.put("lineWidth", options.optInt("line_width"))
                .put("enableTick", options.optBoolean("enable_tick"))
                .put("enableMinorTick", options.optBoolean("enable_minor_tick")));
    }

    @Override
    public void formatNumberLevelInYaxis(JSONObject config, JSONArray items, int type, int position, String formatter) throws JSONException {
        int magnify = this.calcMagnify(type);
        for(int i = 0; i < items.length(); i++){
            double max = Double.NaN;
            JSONObject item = items.getJSONObject(i);
            JSONArray data = item.getJSONArray("data");
            for(int j = 0; j < data.length(); j++){
                JSONObject da = data.getJSONObject(j);
                if(position == item.optInt("yAxis")){
                    double y = da.optDouble("y", 0);
                    da.put("y", this.getFourFiexedFormat().format(y / magnify));
                    if ((Double.isNaN(max) || y > max)) {
                        max = y;
                    }
                }
            }
            if(position == item.optInt("yAxis")){
                JSONObject tooltip = config.getJSONObject("plotOptions").getJSONObject("tooltip");
                tooltip.getJSONObject("formatter").put("valueFormat", formatter);
                item.put("tooltip", tooltip);
            }
            if (!Double.isNaN(max)) {
                this.maxes.add(max);
            }
        }
    }
}
