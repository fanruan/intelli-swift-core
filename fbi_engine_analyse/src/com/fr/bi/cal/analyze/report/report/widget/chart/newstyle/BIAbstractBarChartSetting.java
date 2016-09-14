package com.fr.bi.cal.analyze.report.report.widget.chart.newstyle;

import com.fr.bi.stable.constant.BIChartSettingConstant;
import com.fr.json.JSONArray;
import com.fr.json.JSONException;
import com.fr.json.JSONObject;

/**
 * Created by User on 2016/9/11.
 */
public abstract class BIAbstractBarChartSetting extends BIAbstractAxisChartSetting{

    public BIAbstractBarChartSetting() throws JSONException {
        this(new JSONArray().put(new JSONObject()
                .put("type", "value")
                .put("title", new JSONObject()
                        .put("style", new JSONObject("{" +
                                "fontFamily:\"" + BIChartSettingConstant.FONT_STYLE.FONTFAMILY + "\"," +
                                "color:\"" + BIChartSettingConstant.FONT_STYLE.COLOR + "\"," +
                                "fontSize:\"" + BIChartSettingConstant.FONT_STYLE.FONTSIZE + "\"}"
                        )))
                .put("labelStyle", new JSONObject("{" +
                        "fontFamily:\"" + BIChartSettingConstant.FONT_STYLE.FONTFAMILY + "\"," +
                        "color:\"" + BIChartSettingConstant.FONT_STYLE.COLOR + "\"," +
                        "fontSize:\"" + BIChartSettingConstant.FONT_STYLE.FONTSIZE + "\"}"
                ))));
    }

    public BIAbstractBarChartSetting(JSONArray xAxis) throws JSONException {
        super(xAxis);
    }

    @Override
    public void formatXYAxis(JSONObject options, JSONObject config, JSONArray items) throws JSONException{
        JSONArray yAxis = config.getJSONArray("yAxis");
        JSONObject axis = yAxis.getJSONObject(0);
        axis.getJSONObject("title").put("text", this.getXYAxisTitle(options.optInt("x_axis_number_level"), BIChartSettingConstant.X_AXIS, options.optBoolean("show_x_axis_title"), options.optString("x_axis_unit"), options.optString("x_axis_title")));
        axis.getJSONObject("title").put("rotation", BIChartSettingConstant.ROTATION);
        axis.put("lineWidth", options.optInt("line_width"))
                .put("enableTick", options.optBoolean("enable_tick"))
                .put("enableMinorTick", options.optBoolean("enable_minor_tick"))
                .put("gridLineWidth", options.optBoolean("show_grid_line") ? 1 : 0);

        this.formatNumberLevelInXaxis(items, options.optInt("left_y_axis_number_level"));
        JSONArray xAxis = config.getJSONArray("xAxis");
        xAxis.getJSONObject(0).getJSONObject("title")
                .put("text", this.getXYAxisTitle(options.optInt("left_y_axis_number_level"), BIChartSettingConstant.X_AXIS, options.optBoolean("show_left_y_axis_title"), options.optString("left_y_axis_unit"), options.optString("left_y_axis_title")))
                .put("align", "center");
        xAxis.getJSONObject(0).put("lineWidth", options.optInt("line_width"))
                .put("showLabel", options.optBoolean("showLabel"))
                .put("enableTick", options.optBoolean("enable_tick"))
                .put("reversed", options.optBoolean("left_y_axis_reversed"))
                .put("enableMinorTick", options.optBoolean("enable_minor_tick"))
                .put("gridLineWidth", options.optBoolean("show_grid_line") ? 1 : 0)
                .put("formatter", this.formatTickInXYaxis(options.optInt("left_y_axis_style"), options.optInt("left_y_axis_number_level"), options.optBoolean("num_separators")));
    }

    @Override
    public JSONObject formatConfig(JSONObject options, JSONArray data) throws JSONException {
        JSONObject config = super.formatConfig(options, data);
        if(options.optBoolean("show_data_label")){
            this.formatPercentForItems(config.getJSONArray("series"), config.getJSONArray("xAxis"), options.optInt("left_y_axis_number_level"), options.optInt("right_y_axis_number_level"), options.optInt("right_y_axis_second_number_level"), options.optBoolean("num_separators"), options.optBoolean("right_num_separators"), options.optBoolean("right2_num_separators"));
        }
        return config;
    }

    @Override
    public void formatPercentForItems(JSONArray items, JSONArray yAxis, int leftYAxisNumberLevel, int rightYAxisNumberLevel, int rightYAxisSecondNumberLevel, boolean numSeparator, boolean numSeparatorRight, boolean right2NumSeparators) throws JSONException {
        for (int i = 0; i < items.length(); i++) {
            JSONObject item = items.getJSONObject(i);
            if (leftYAxisNumberLevel == BIChartSettingConstant.CHART_TARGET_STYLE.NUM_LEVEL.PERCENT || numSeparator) {
                item.put("dataLabels", new JSONObject()
                        .put("style", this.getFontStyle())
                        .put("align", "outside")
                        .put("enable", true)
                        .put("formatter", new JSONObject("{identifier:\"${VALUE}\", valueFormat:" +
                                yAxis.getJSONObject(0).getString("formatter") + "}")));

            }
        }
    }

    @Override
    public String getChartTypeString() {
        return "bar";
    }

    @Override
    public void formatCordon(JSONArray cordon, JSONArray xAxis, JSONArray yAxis, int xAxisNumberLevel, int leftYAxisNumberLevel, int rightYAxisNumberLevel, int rightYAxisSecondNumberLevel) throws JSONException {
        for (int i = 0; i < cordon.length(); i++) {
            JSONArray cor = cordon.getJSONArray(i);
            if (i == 0 && xAxis.length() > 0) {
                int magnify = this.calcMagnify(leftYAxisNumberLevel);
                JSONArray plotLines = new JSONArray();
                for (int j = 0; j < cor.length(); j++) {
                    JSONObject t = new JSONObject();
                    t.put("value", t.optDouble("value") / magnify)
                            .put("width", 1)
                            .put("label", new JSONObject()
                                    .put("style", this.getFontStyle())
                                    .put("text", t.optString("text"))
                                    .put("align", "top"));
                    plotLines.put(t);
                }
                xAxis.getJSONObject(0).put("plotLines", plotLines);
            }
            if (i > 0 && yAxis.length() >= i) {
                int magnify = 1;
                switch (i - 1) {
                    case BIChartSettingConstant.LEFT_AXIS:
                        magnify = this.calcMagnify(xAxisNumberLevel);
                        break;
                }
                JSONArray plotLines = new JSONArray();
                for (int j = 0; j < cor.length(); j++) {
                    JSONObject t = new JSONObject();
                    t.put("value", t.optDouble("value") / magnify)
                            .put("width", 1)
                            .put("label", new JSONObject()
                                    .put("style", this.getFontStyle())
                                    .put("text", t.optString("text"))
                                    .put("align", "top"));
                    plotLines.put(t);
                }
                yAxis.getJSONObject(i - 1).put("plotLines", plotLines);
            }
        }
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
                    String tmp = t.getString("y");
                    t.put("y", t.getString("x"));
                    t.put("x", tmp);
                }
            }
        }
        return super.formatItems(data, types, options);
    }

    @Override
    public JSONObject getConvertedDataAndSettings(JSONArray data, JSONArray types, JSONObject options) throws JSONException {
        JSONArray yAxis = new JSONArray();
        for(int i = 0; i < types.length(); i++){
            JSONArray type = types.getJSONArray(i);
            if(type.length() == 0){
                continue;
            }
            JSONObject newYAxis = new JSONObject().put("type", "category")
                    .put("title", new JSONObject().put("style", this.getFontStyle()))
                    .put("labelStyle", this.getFontStyle())
                    .put("position", i > 0 ? "right" : "left")
                    .put("lineWidth", 1)
                    .put("axisIndex", i)
                    .put("gridLineWidth", 0);
            yAxis.put(newYAxis);
        }
        this.setyAxis(yAxis);
        JSONObject configAndData = this.formatItems(data, types, options);
        this.setConfig(configAndData.getJSONObject("config"));
        JSONArray items = configAndData.getJSONArray("result");
        return this.formatConfig(options, items);
    }
}
