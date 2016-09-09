package com.fr.bi.cal.analyze.report.report.widget.chart.newstyle;

import com.fr.bi.stable.constant.BIChartSettingConstant;
import com.fr.bi.stable.constant.BIReportConstant;
import com.fr.general.Inter;
import com.fr.json.JSONArray;
import com.fr.json.JSONException;
import com.fr.json.JSONObject;
import com.fr.stable.StringUtils;

/**
 * Created by User on 2016/8/31.
 */
public abstract class BIAbstractChartSetting implements BIChartSetting {

    @Override
    public JSONObject formatItems(JSONArray data, JSONArray types) throws JSONException{
        JSONArray result = new JSONArray();
        int yAxisIndex = 0;
        for(int i = 0; i < data.length(); i++){
            JSONArray belongAxisItems = data.getJSONArray(i);
            JSONArray combineItems = combineChildItems(types.getJSONArray(i), belongAxisItems);
            for(int j = 0; j < combineItems.length(); j++){
                JSONObject axisItems = combineItems.getJSONObject(j);
                result.put(axisItems.put("yAxis", yAxisIndex));
            }
            if(combineItems.length() > 0){
                yAxisIndex ++;
            }
        }
        JSONObject config = combineConfig();
        return new JSONObject().put("result", result).put("config", config);
    }

    private JSONArray combineChildItems(JSONArray items, JSONArray types) throws JSONException{
        JSONArray result = new JSONArray();
        for(int i = 0; i < items.length(); i++){
            JSONArray res = new JSONArray();
            JSONObject item = items.getJSONObject(i);
            res.put(formatChildItem(types.getInt(i), item));
        }
        return result;
    }

    private JSONObject formatChildItem(int type, JSONObject items) throws JSONException{
        switch (type) {
            case BIReportConstant.WIDGET.BAR:
            case BIReportConstant.WIDGET.ACCUMULATE_BAR:
            case BIReportConstant.WIDGET.COMPARE_BAR:
                items.put("type", "bar");
                break;
            case BIReportConstant.WIDGET.BUBBLE:
            case BIReportConstant.WIDGET.FORCE_BUBBLE:
                items.put("type",  "bubble");
                break;
            case BIReportConstant.WIDGET.SCATTER:
                items.put("type", "scatter");
                break;
            case BIReportConstant.WIDGET.AXIS:
            case BIReportConstant.WIDGET.ACCUMULATE_AXIS:
            case BIReportConstant.WIDGET.PERCENT_ACCUMULATE_AXIS:
            case BIReportConstant.WIDGET.COMPARE_AXIS:
            case BIReportConstant.WIDGET.FALL_AXIS:
                items.put("type", "column");
                break;
            case BIReportConstant.WIDGET.LINE:
                items.put("type", "line");
                break;
            case BIReportConstant.WIDGET.AREA:
            case BIReportConstant.WIDGET.ACCUMULATE_AREA:
            case BIReportConstant.WIDGET.COMPARE_AREA:
            case BIReportConstant.WIDGET.RANGE_AREA:
            case BIReportConstant.WIDGET.PERCENT_ACCUMULATE_AREA:
                items.put("type", "area");
                break;
            case BIReportConstant.WIDGET.DONUT:
                items.put("type", "pie");
                break;
            case BIReportConstant.WIDGET.RADAR:
            case BIReportConstant.WIDGET.ACCUMULATE_RADAR:
                items.put("type", "radar");
                break;
            case BIReportConstant.WIDGET.PIE:
                items.put("type", "pie");
                break;
            case BIReportConstant.WIDGET.DASHBOARD:
                items.put("type", "gauge");
                break;
            case BIReportConstant.WIDGET.MAP:
                items.put("type", "areaMap");
                break;
            case BIReportConstant.WIDGET.GIS_MAP:
                items.put("type", "pointMap");
                break;
            default:
                items.put("type", "column");
                break;
        }
        return items;
    }

    private JSONObject combineConfig() throws JSONException{
        return new JSONObject("{\"plotOptions\":{\"rotatable\":false,\"startAngle\":0,\"borderRadius\":0,\"endAngle\":360,\"innerRadius\":\"0.0%\",\"layout\":\"horizontal\",\"hinge\":\"rgb(101,107,109)\",\"dataLabels\":{\"style\":{\"fontFamily\":\"inherit\",\"color\":\"#808080\",\"fontSize\":\"12px\"},\"formatter\":{\"identifier\":\"${VALUE}\",\"valueFormat\": "+ BIChartSettingConstant.DEFAULT_FORMAT_FUNCTIONS.CONTENTFORMAT2DECIMAL +",\"seriesFormat\":"+ BIChartSettingConstant.DEFAULT_FORMAT_FUNCTIONS.CONTENTFORMAT +",\"percentFormat\":"+ BIChartSettingConstant.DEFAULT_FORMAT_FUNCTIONS.CONTENTFORMATPERCENTAGE +",\"categoryFormat\":"+ BIChartSettingConstant.DEFAULT_FORMAT_FUNCTIONS.CONTENTFORMAT + ",\"XFormat\":"+ BIChartSettingConstant.DEFAULT_FORMAT_FUNCTIONS.CONTENTFORMAT2DECIMAL +",\"YFormat\":"+ BIChartSettingConstant.DEFAULT_FORMAT_FUNCTIONS.CONTENTFORMAT2DECIMAL +",\"sizeFormat\":"+ BIChartSettingConstant.DEFAULT_FORMAT_FUNCTIONS.CONTENTFORMAT2DECIMAL +"},\"align\":\"outside\",\"enabled\":false},\"percentageLabel\":{\"formatter\":{\"identifier\":\"${PERCENT}\",\"valueFormat\":"+ BIChartSettingConstant.DEFAULT_FORMAT_FUNCTIONS.CONTENTFORMAT2DECIMAL +",\"seriesFormat\":"+ BIChartSettingConstant.DEFAULT_FORMAT_FUNCTIONS.CONTENTFORMAT +",\"percentFormat\":"+ BIChartSettingConstant.DEFAULT_FORMAT_FUNCTIONS.CONTENTFORMATPERCENTAGE +",\"categoryFormat\":"+ BIChartSettingConstant.DEFAULT_FORMAT_FUNCTIONS.CONTENTFORMAT +"},\"style\":{\"fontFamily\":\"Microsoft YaHei, Hiragino Sans GB W3\",\"color\":\"#808080\",\"fontSize\":\"12px\"},\"align\":\"bottom\",\"enabled\":true},\"valueLabel\":{\"formatter\":{\"identifier\":\"${SERIES}${VALUE}\",\"valueFormat\":"+ BIChartSettingConstant.DEFAULT_FORMAT_FUNCTIONS.CONTENTFORMAT2DECIMAL +",\"seriesFormat\":"+ BIChartSettingConstant.DEFAULT_FORMAT_FUNCTIONS.CONTENTFORMAT +",\"percentFormat\":"+ BIChartSettingConstant.DEFAULT_FORMAT_FUNCTIONS.CONTENTFORMATPERCENTAGE +",\"categoryFormat\":"+ BIChartSettingConstant.DEFAULT_FORMAT_FUNCTIONS.CONTENTFORMAT +"},\"backgroundColor\":\"rgb(255,255,0)\",\"style\":{\"fontFamily\":\"Microsoft YaHei, Hiragino Sans GB W3\",\"color\":\"#808080\",\"fontSize\":\"12px\"},\"align\":\"inside\",\"enabled\":true},\"hingeBackgroundColor\":\"rgb(220,242,249)\",\"seriesLabel\":{\"formatter\":{\"identifier\":\"${CATEGORY}\",\"valueFormat\":"+ BIChartSettingConstant.DEFAULT_FORMAT_FUNCTIONS.CONTENTFORMAT2DECIMAL +",\"seriesFormat\":"+ BIChartSettingConstant.DEFAULT_FORMAT_FUNCTIONS.CONTENTFORMAT +",\"percentFormat\":"+ BIChartSettingConstant.DEFAULT_FORMAT_FUNCTIONS.CONTENTFORMATPERCENTAGE +",\"categoryFormat\":"+ BIChartSettingConstant.DEFAULT_FORMAT_FUNCTIONS.CONTENTFORMAT +"},\"style\":{\"fontFamily\":\"Microsoft YaHei, Hiragino Sans GB W3\",\"color\":\"#808080\",\"fontSize\":\"12px\"},\"align\":\"bottom\",\"enabled\":true},\"style\":\"pointer\",\"paneBackgroundColor\":\"rgb(252,252,252)\",\"needle\":\"rgb(229,113,90)\",\"large\":false,\"connectNulls\":false,\"shadow\":true,\"curve\":false,\"sizeBy\":\"area\",\"tooltip\":{\"formatter\":{\"identifier\":\"${SERIES}${X}${Y}${SIZE}{CATEGORY}${SERIES}${VALUE}\",\"valueFormat\":"+ BIChartSettingConstant.DEFAULT_FORMAT_FUNCTIONS.CONTENTFORMAT +",\"seriesFormat\":"+ BIChartSettingConstant.DEFAULT_FORMAT_FUNCTIONS.CONTENTFORMAT +",\"percentFormat\":"+ BIChartSettingConstant.DEFAULT_FORMAT_FUNCTIONS.CONTENTFORMATPERCENTAGE +",\"categoryFormat\":"+ BIChartSettingConstant.DEFAULT_FORMAT_FUNCTIONS.CONTENTFORMAT +",\"XFormat\":"+ BIChartSettingConstant.DEFAULT_FORMAT_FUNCTIONS.CONTENTFORMAT2DECIMAL +",\"sizeFormat\":"+ BIChartSettingConstant.DEFAULT_FORMAT_FUNCTIONS.CONTENTFORMAT2DECIMAL +",\"YFormat\":"+ BIChartSettingConstant.DEFAULT_FORMAT_FUNCTIONS.CONTENTFORMAT2DECIMAL +"},\"shared\":false,\"padding\":5,\"backgroundColor\":\"rgba(0,0,0,0.4980392156862745)\",\"borderColor\":\"rgb(0,0,0)\",\"shadow\":false,\"borderRadius\":2,\"borderWidth\":0,\"follow\":false,\"enabled\":true,\"animation\":true,\"style\":{\"fontFamily\":\"Microsoft YaHei, Hiragino Sans GB W3\",\"color\":\"#c4c6c6\",\"fontSize\":\"12px\",\"fontWeight\":\"\"}},\"maxSize\":80,\"fillColorOpacity\":1,\"step\":false,\"force\":false,\"minSize\":15,\"displayNegative\":true,\"categoryGap\":\"16.0%\",\"borderColor\":\"rgb(255,255,255)\",\"borderWidth\":1,\"gap\":\"22.0%\",\"animation\":true,\"lineWidth\":2,\"bubble\":{\"large\":false,\"connectNulls\":false,\"shadow\":true,\"curve\":false,\"sizeBy\":\"area\",\"maxSize\":80,\"minSize\":15,\"lineWidth\":0,\"animation\":true,\"fillColorOpacity\":0.699999988079071,\"marker\":{\"symbol\":\"circle\",\"radius\":28.39695010101295,\"enabled\":true}}},\"dTools\":{\"enabled\":false,\"style\":{\"fontFamily\":\"Microsoft YaHei, Hiragino Sans GB W3\",\"color\":\"#1a1a1a\",\"fontSize\":\"12px\"},\"backgroundColor\":\"white\"},\"dataSheet\":{\"enabled\":false,\"borderColor\":\"rgb(0,0,0)\",\"borderWidth\":1,\"formatter\":"+ BIChartSettingConstant.DEFAULT_FORMAT_FUNCTIONS.CONTENTFORMAT2DECIMAL +",\"style\":{\"fontFamily\":\"Microsoft YaHei, Hiragino Sans GB W3\",\"color\":\"#808080\",\"fontSize\":\"12px\"}},\"borderColor\":\"rgb(238,238,238)\",\"shadow\":false,\"legend\":{\"borderColor\":\"rgb(204,204,204)\",\"borderRadius\":0,\"shadow\":false,\"borderWidth\":0,\"visible\":true,\"style\":{\"fontFamily\":\"Microsoft YaHei, Hiragino Sans GB W3\",\"color\":\"#1a1a1a\",\"fontSize\":\"12px\"},\"position\":\"right\",\"enabled\":false},\"rangeLegend\":{\"range\":{\"min\":0,\"color\":[[0,\"rgb(182,226,255)\"],[0.5,\"rgb(109,196,255)\"],[1,\"rgb(36,167,255)\"]],\"max\":266393},\"enabled\":false},\"zoom\":{\"zoomType\":\"xy\",\"zoomTool\":{\"visible\":false,\"resize\":true,\"from\":\"\",\"to\":\"\"}},\"plotBorderColor\":\"rgba(255,255,255,0)\",\"tools\":{\"hidden\":true,\"toImage\":{\"enabled\":true},\"sort\":{\"enabled\":true},\"enabled\":false,\"fullScreen\":{\"enabled\":true}},\"plotBorderWidth\":0,\"colors\":[\"rgb(99,178,238)\",\"rgb(118,218,145)\"],\"borderRadius\":0,\"borderWidth\":0,\"style\":\"normal\",\"plotShadow\":false,\"plotBorderRadius\":0}");
    }

    public JSONObject getFontStyle() throws JSONException{
        return new JSONObject("{" +
                "fontFamily:" + BIChartSettingConstant.FONT_STYLE.FONTFAMILY + "," +
                "color:" + BIChartSettingConstant.FONT_STYLE.COLOR + "," +
                "fontSize: " + BIChartSettingConstant.FONT_STYLE.FONTSIZE + "}"
        );
    }

    public void formatNumberLevelInYaxis(JSONObject config, JSONArray items, int type, int position, String formatter) throws JSONException{
        int magnify = this.calcMagnify(type);
        for(int i = 0; i < items.length(); i++){
            JSONObject item = items.getJSONObject(i);
            JSONArray data = item.getJSONArray("data");
            for(int j = 0; j < data.length(); j++){
                JSONObject da = data.getJSONObject(j);
                if(position == item.optInt("yAxis")){
                    double y = da.optDouble("y", 0);
                    da.put("y", BIChartSettingConstant.FOURFIEXEDFORMAT.format(y / magnify));
                }
            }
            if(position == item.optInt("yAxis")){
                JSONObject tooltip = config.getJSONObject("plotOptions").getJSONObject("tooltip");
                tooltip.getJSONObject("formatter").put("valueFormat", formatter);
                item.put("tooltip", tooltip);
            }
        }
    }

    public void formatNumberLevelInXaxis(JSONArray items, int type) throws JSONException{
        int magnify = this.calcMagnify(type);
        for(int i = 0; i < items.length(); i++){
            JSONObject item = items.getJSONObject(i);
            JSONArray data = item.getJSONArray("data");
            for(int j = 0; j < data.length(); j++){
                JSONObject da = data.getJSONObject(j);
                    double x = da.optDouble("x", 0);
                    da.put("x", BIChartSettingConstant.FOURFIEXEDFORMAT.format(x / magnify));
            }
        }
    }

    public String formatXYDataWithMagnify(double number, int magnify){
        return BIChartSettingConstant.FOURFIEXEDFORMAT.format(number / magnify);
    }

    public void formatChartLegend (JSONObject config, int chart_legend) throws JSONException{
        JSONObject legend = new JSONObject(); //config.getJSONObject();
        switch (chart_legend) {
            case BIChartSettingConstant.CHART_LEGENDS.BOTTOM:
                legend.put("enabled", true)
                        .put("position", "bottom")
                        .put("maxHeight", BIChartSettingConstant.LEGEND_HEIGHT);
                break;
            case BIChartSettingConstant.CHART_LEGENDS.RIGHT:
                legend.put("enabled", true)
                        .put("position", "right")
                        .put("maxHeight", BIChartSettingConstant.LEGEND_WIDTH);
                break;
            case BIChartSettingConstant.CHART_LEGENDS.NOT_SHOW:
            default:
                legend.put("enabled", false);
                break;
        }
    }

    public String formatTickInXYaxis(int type, int number_level, boolean separators){
        String formatter = "#.##";
        switch (type) {
            case BIChartSettingConstant.NORMAL:
                formatter = "#.##";
                break;
            case BIChartSettingConstant.ZERO2POINT:
                formatter = "#0";
                break;
            case BIChartSettingConstant.ONE2POINT:
                formatter = "#0.0";
                break;
            case BIChartSettingConstant.TWO2POINT:
                formatter = "#0.00";
                break;
        }
        if (separators) {
            formatter = "#,##0";
        }
        if (number_level == BIChartSettingConstant.CHART_TARGET_STYLE.NUM_LEVEL.PERCENT) {
            if (separators) {
                formatter = "#,##0%";
            } else if (type == BIChartSettingConstant.NORMAL) {
                formatter = "#0.##%";
            } else {
                formatter += '%';
            }
        }
        formatter += ";-" + formatter;
        return "function () { return BH.contentFormat(arguments[0], " + formatter + ")}";
    }

    public int calcMagnify(int type){
        int magnify = 1;
        switch (type) {
            case BIChartSettingConstant.CHART_TARGET_STYLE.NUM_LEVEL.NORMAL:
            case BIChartSettingConstant.CHART_TARGET_STYLE.NUM_LEVEL.PERCENT:
                magnify = 1;
                break;
            case BIChartSettingConstant.CHART_TARGET_STYLE.NUM_LEVEL.TEN_THOUSAND:
                magnify = 10000;
                break;
            case BIChartSettingConstant.CHART_TARGET_STYLE.NUM_LEVEL.MILLION:
                magnify = 1000000;
                break;
            case BIChartSettingConstant.CHART_TARGET_STYLE.NUM_LEVEL.YI:
                magnify = 100000000;
                break;
        }
        return magnify;
    }

    public String formatChartStyle(int style){
        switch (style) {
            case BIChartSettingConstant.CHART_STYLE.STYLE_GRADUAL:
                return "gradual";
            case BIChartSettingConstant.CHART_STYLE.STYLE_NORMAL:
            default:
                return "normal";
        }
    }

    public void formatCordon(JSONArray cordon, JSONArray xAxis, JSONArray yAxis, int leftYAxisNumberLevel, int xAxisNumberLevel, int rightYAxisNumberLevel, int rightYAxisSecondNumberLevel) throws JSONException {
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
                    case BIChartSettingConstant.RIGHT_AXIS:
                        magnify = this.calcMagnify(rightYAxisNumberLevel);
                        break;
                    case BIChartSettingConstant.RIGHT_AXIS_SECOND:
                        magnify = this.calcMagnify(rightYAxisSecondNumberLevel);
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

    public String getXYAxisTitle(int numberLevelType, int position, boolean show, String units, String title){
        String unit = "";
        switch (numberLevelType) {
            case BIChartSettingConstant.CHART_TARGET_STYLE.NUM_LEVEL.NORMAL:
                unit = "";
                break;
            case BIChartSettingConstant.CHART_TARGET_STYLE.NUM_LEVEL.TEN_THOUSAND:
                unit = Inter.getLocText("BI-Wan");
                break;
            case BIChartSettingConstant.CHART_TARGET_STYLE.NUM_LEVEL.MILLION:
                unit = Inter.getLocText("BI-Million");
                break;
            case BIChartSettingConstant.CHART_TARGET_STYLE.NUM_LEVEL.YI:
                unit = Inter.getLocText("BI-Yi");
                break;
        }
        if(position == BIChartSettingConstant.X_AXIS){
            unit = unit + units;
        }
        if(position == BIChartSettingConstant.LEFT_AXIS){
            unit = unit + units;
        }
        if(position == BIChartSettingConstant.RIGHT_AXIS){
            unit = unit + units;
        }
        unit = StringUtils.isEmpty(unit) ? unit : "(" + unit + ")";

        return show ? title + unit : unit;
    }

    public void formatPercentForItems(JSONArray items, JSONArray yAxis, int leftYAxisNumberLevel, int rightYAxisNumberLevel, boolean numSeparator) throws JSONException {
        for (int i = 0; i < items.length(); i++) {
            JSONObject item = items.getJSONObject(i);
            boolean isNeedFormatDataLabel = false;
            switch (yAxis.getJSONObject(item.getInt("yAxis")).getInt("axisIndex")) {
                case BIChartSettingConstant.LEFT_AXIS:
                    if (leftYAxisNumberLevel == BIChartSettingConstant.CHART_TARGET_STYLE.NUM_LEVEL.PERCENT || numSeparator) {
                        isNeedFormatDataLabel = true;
                    }
                    break;
                case BIChartSettingConstant.RIGHT_AXIS:
                    if (rightYAxisNumberLevel == BIChartSettingConstant.CHART_TARGET_STYLE.NUM_LEVEL.PERCENT || numSeparator) {
                        isNeedFormatDataLabel = true;
                    }
                    break;
            }
            if (isNeedFormatDataLabel) {
                item.put("dataLabels", new JSONObject()
                        .put("style", this.getFontStyle())
                        .put("align", "outside")
                        .put("enable", true)
                        .put("formatter", new JSONObject("{identifier:\"${VALUE}\", valueFormat:" +
                                yAxis.getJSONObject(item.getInt("yAxis")).getString("formatter") + "}")));

            }
        }
    }

    public void formatXYAxis(JSONObject options, JSONObject config, JSONArray items) throws JSONException{
        JSONArray yAxis = config.getJSONArray("yAxis");
        for(int i = 0; i < yAxis.length(); i++){
            JSONObject axis = yAxis.getJSONObject(i);
            int axisIndex = axis.optInt("axisIndex");
            switch (axisIndex) {
                case BIChartSettingConstant.LEFT_AXIS:
                    axis.getJSONObject("title").put("text", this.getXYAxisTitle(options.optInt("left_y_axis_number_level"), BIChartSettingConstant.LEFT_AXIS, options.optBoolean("show_left_y_axis_title"), options.optString("left_y_axis_unit"), options.optString("left_y_axis_title")));
                    axis.getJSONObject("title").put("rotation", BIChartSettingConstant.ROTATION);
                    axis.put("lineWidth", options.optInt("line_width"))
                            .put("showLabel", options.optBoolean("showLabel"))
                            .put("enableTick", options.optBoolean("enable_tick"))
                            .put("reversed", options.optBoolean("left_y_axis_reversed"))
                            .put("enableMinorTick", options.optBoolean("enable_minor_tick"))
                            .put("gridLineWidth", options.optBoolean("show_grid_line") ? 1 : 0)
                            .put("formatter", this.formatTickInXYaxis(options.optInt("left_y_axis_style"), options.optInt("left_y_axis_number_level"), options.optBoolean("num_separators")));
                    this.formatNumberLevelInYaxis(config, items, options.optInt("left_y_axis_number_level"), i, axis.optString("formatter"));
                    break;
                case BIChartSettingConstant.RIGHT_AXIS:
                    axis.getJSONObject("title").put("text", this.getXYAxisTitle(options.optInt("right_y_axis_number_level"), BIChartSettingConstant.RIGHT_AXIS, options.optBoolean("show_right_y_axis_title"), options.optString("right_y_axis_unit"), options.optString("right_y_axis_title")));
                    axis.getJSONObject("title").put("rotation", BIChartSettingConstant.ROTATION);
                    axis.put("lineWidth", options.optInt("line_width"))
                            .put("showLabel", options.optBoolean("showLabel"))
                            .put("enableTick", options.optBoolean("enable_tick"))
                            .put("reversed", options.optBoolean("right_y_axis_reversed"))
                            .put("enableMinorTick", options.optBoolean("enable_minor_tick"))
                            .put("gridLineWidth", options.optBoolean("show_grid_line") ? 1 : 0)
                            .put("formatter", this.formatTickInXYaxis(options.optInt("right_y_axis_style"), options.optInt("right_y_axis_number_level"), options.optBoolean("right_num_separators")));
                    this.formatNumberLevelInYaxis(config, items, options.optInt("right_y_axis_number_level"), i, axis.optString("formatter"));
                    break;
                case BIChartSettingConstant.RIGHT_AXIS_SECOND:
                    axis.getJSONObject("title").put("text", this.getXYAxisTitle(options.optInt("right_y_axis_second_number_level"), BIChartSettingConstant.RIGHT_AXIS_SECOND, options.optBoolean("show_right_y_axis_second_title"), options.optString("right_y_axis_second_unit"), options.optString("right_y_axis_second_title")));
                    axis.getJSONObject("title").put("rotation", BIChartSettingConstant.ROTATION);
                    axis.put("lineWidth", options.optInt("line_width"))
                            .put("showLabel", options.optBoolean("show_label"))
                            .put("enableTick", options.optBoolean("enable_tick"))
                            .put("reversed", options.optBoolean("right_y_axis_second_reversed"))
                            .put("enableMinorTick", options.optBoolean("enable_minor_tick"))
                            .put("gridLineWidth", options.optBoolean("show_grid_line") ? 1 : 0)
                            .put("formatter", this.formatTickInXYaxis(options.optInt("right_y_axis_second_style"), options.optInt("right_y_axis_second_number_level"), options.optBoolean("right2_num_separators")));
                    this.formatNumberLevelInYaxis(config, items, options.optInt("right_y_axis_second_number_level"), i, axis.optString("formatter"));
                    break;

            }
        }
    }
}
