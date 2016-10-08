package com.fr.bi.cal.analyze.report.report.widget.chart.newstyle;

import com.fr.bi.stable.constant.BIChartSettingConstant;
import com.fr.general.Inter;
import com.fr.json.JSONArray;
import com.fr.json.JSONException;
import com.fr.json.JSONObject;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by User on 2016/9/12.
 */
public abstract class BIAbstractBubbleAndScatterSetting extends BIAbstractChartSetting {
    private JSONArray yAxis;
    private JSONArray xAxis;
    private JSONObject config;
    private boolean hasSize;


    public BIAbstractBubbleAndScatterSetting() throws JSONException {
        this.xAxis = new JSONArray();
        this.yAxis = new JSONArray();
        this.config = new JSONObject();
        this.xAxis.put(new JSONObject()
                .put("type", "value")
                .put("title", new JSONObject()
                        .put("style", this.getFontStyle()))
                .put("labelStyle", this.getFontStyle())
                .put("position", "bottom")
                .put("gridLineWidth", 0));
        this.yAxis.put(new JSONObject()
                .put("type", "value")
                .put("title", new JSONObject()
                        .put("style", this.getFontStyle()))
                .put("labelStyle", this.getFontStyle())
                .put("position", "left")
                .put("gridLineWidth", 0));
    }

    public BIAbstractBubbleAndScatterSetting(boolean hasSize) throws JSONException {
        this();
        this.hasSize = hasSize;
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
        this.formatChartLegend(this.config, options.optInt("chart_legend"));

        if (options.optJSONArray("tooltip") != null) {
            JSONArray tooltips = options.getJSONArray("tooltip");
            String y = getFormatFunctionContent(formatTickInXYaxis(options.getInt("left_y_axis_style"), options.getInt("left_y_axis_number_level"), options.getBoolean("num_separators")), "this.y");
            String x = getFormatFunctionContent(formatTickInXYaxis(options.getInt("x_axis_style"), options.getInt("x_axis_number_level"), options.getBoolean("right_num_separators")), "this.x");
            String formatter;
            if(tooltips.length() ==  3){
                formatter = "function(){ return this.seriesName + '<div>(X)" + tooltips.getString(1) + ":' +" + x + "+ '</div><div>(Y)"
                        + tooltips.getString(0) + ":' +" + y + "+ '</div><div>(" + Inter.getLocText("BI-Size") + ")" + tooltips.getString(2)
                        + ":' + this.size + '</div>'}";
            }else{
                formatter = "function(){ return this.seriesName + '<div>(X)" + tooltips.getString(1) + ":' +" + x + "+ '</div><div>(Y)"
                        + tooltips.getString(0) + ":' +" + y + "+ '</div>'}";
            }
            plotOptions.optJSONObject("tooltip").put("formatter", formatter);
        }
        plotOptions.optJSONObject("dataLabels").put("enabled", options.optBoolean("show_data_label")).getJSONObject("formatter").put("identifier", "${X}${Y}${SIZE}");
        plotOptions.put("shadow", options.getInt("bubble_style") != BIChartSettingConstant.NO_PROJECT);

        this.config.put("xAxis", this.xAxis).put("yAxis", this.yAxis);
        this.formatXYAxis(options, this.config, data);
        this.config.put("chartType", getChartTypeString());
        if(options.optBoolean("show_data_label")){
            this.formatPercentForItemsXY(data, this.xAxis, this.yAxis, options.optInt("x_axis_number_level"), options.optInt("left_y_axis_number_level"), options.optBoolean("num_separators"), options.optBoolean("right_num_separators"));
        }
        return this.config.put("series", data);
    }

    public abstract String getChartTypeString();

    @Override
    public void formatXYAxis(JSONObject options, JSONObject config, JSONArray items) throws JSONException {
        JSONArray yAxis = config.getJSONArray("yAxis");
        JSONObject axis = yAxis.getJSONObject(0);
        axis.getJSONObject("title").put("text", this.getXYAxisTitle(options.optInt("left_y_axis_number_level"), BIChartSettingConstant.LEFT_AXIS, options.optBoolean("show_left_y_axis_title"), options.optString("left_y_axis_unit"), options.optString("left_y_axis_title")));
        axis.getJSONObject("title").put("rotation", BIChartSettingConstant.ROTATION);
        axis.put("gridLineWidth", options.optBoolean("show_grid_line") ? 1 : 0)
                .put("formatter", this.formatTickInXYaxis(options.optInt("left_y_axis_style"), options.optInt("left_y_axis_number_level"), options.optBoolean("num_separators")));
        this.formatNumberLevelInYaxis(config, items, options.optInt("left_y_axis_number_level"), 0, axis.optString("formatter"));

        JSONArray xAxis = config.getJSONArray("xAxis");
        xAxis.getJSONObject(0).getJSONObject("title")
                .put("text", this.getXYAxisTitle(options.optInt("x_axis_number_level"), BIChartSettingConstant.X_AXIS, options.optBoolean("show_x_axis_title"), options.optString("x_axis_unit"), options.optString("x_axis_title")))
                .put("align", "center");
        xAxis.getJSONObject(0)
                .put("labelRotation", options.optString("text_direction"))
                .put("gridLineWidth", options.optBoolean("show_grid_line") ? 1 : 0)
                .put("formatter", this.formatTickInXYaxis(options.optInt("x_axis_style"), options.optInt("x_axis_number_level"), options.optBoolean("num_separators")));
        this.formatNumberLevelInXaxis(items, options.optInt("left_y_axis_number_level"));
    }

    public void formatPercentForItemsXY(JSONArray items, JSONArray xAxis, JSONArray yAxis, int xAxisNumberLevel, int leftYAxisNumberLevel, boolean numSeparator, boolean numSeparatorRight) throws JSONException {
        for (int i = 0; i < items.length(); i++) {
            JSONObject item = items.getJSONObject(i);
            boolean isNeedFormatDataLabelX = false;
            boolean isNeedFormatDataLabelY = false;
            if (xAxisNumberLevel == BIChartSettingConstant.CHART_TARGET_STYLE.NUM_LEVEL.PERCENT || numSeparatorRight) {
                isNeedFormatDataLabelX = true;
            }
            if (leftYAxisNumberLevel == BIChartSettingConstant.CHART_TARGET_STYLE.NUM_LEVEL.PERCENT || numSeparator) {
                isNeedFormatDataLabelY = true;
            }
            if (isNeedFormatDataLabelX || isNeedFormatDataLabelY) {
                String identifier = "${X}${Y}${SIZE}";
                if(!hasSize){
                    identifier = "${X}${Y}";
                }
                item.put("dataLabels", new JSONObject()
                        .put("style", this.getFontStyle())
                        .put("align", "outside")
                        .put("enable", true)
                        .put("formatter", new JSONObject("{identifier:\""+ identifier +"\", XFormat: "+ BIChartSettingConstant.DEFAULT_FORMAT_FUNCTIONS.CONTENTFORMAT2DECIMAL + "," +
                                "YFormat: "+ BIChartSettingConstant.DEFAULT_FORMAT_FUNCTIONS.CONTENTFORMAT2DECIMAL + ", sizeFormat: "+ BIChartSettingConstant.DEFAULT_FORMAT_FUNCTIONS.CONTENTFORMAT2DECIMAL
                        )));
            }
            if(isNeedFormatDataLabelX){
                item.getJSONObject("dataLabels").getJSONObject("formatter").put("XFormat", xAxis.getJSONObject(0).getString("formatter"));
            }
            if(isNeedFormatDataLabelY){
                item.getJSONObject("dataLabels").getJSONObject("formatter").put("YFormat", yAxis.getJSONObject(0).getString("formatter"));
            }
        }

    }

    private String getFormatFunctionContent(String functionString, String argument){
        Pattern p = Pattern.compile("'.*'");
        Matcher m = p.matcher(functionString);
        if(m.find()){
            return "(window.BH ? BH.contentFormat("+ argument +"," + m.group() + ") : " + argument + ")";
        }
        return "";
    }

    @Override
    public JSONObject getConvertedDataAndSettings(JSONArray data, JSONArray types, JSONObject options) throws JSONException{
        JSONObject configAndData = this.formatItems(data, types, options);
        this.config = configAndData.getJSONObject("config");
        JSONArray items = configAndData.getJSONArray("result");
        return this.formatConfig(options, items);
    }
}
