package com.fr.bi.cal.analyze.report.report.widget.chart.newstyle;

import com.fr.bi.stable.constant.BIChartSettingConstant;
import com.fr.general.ComparatorUtils;
import com.fr.general.Inter;
import com.fr.json.JSONArray;
import com.fr.json.JSONException;
import com.fr.json.JSONObject;
import com.fr.stable.StringUtils;

/**
 * Created by User on 2016/8/31.
 */
public class MapChartSetting extends BIAbstractChartSetting {
    private JSONObject config;

    public MapChartSetting() {
        this.config = new JSONObject();
    }

    @Override
    public JSONObject formatConfig(JSONObject options, JSONArray data) throws JSONException {
        JSONObject plotOptions = this.config.getJSONObject("plotOptions");
        this.config.put("color", options.getJSONArray("chart_color"));
        this.formatRangeLegend(this.config, data, options.getInt("chart_legend"), options.getJSONArray("map_styles"), options.getInt("auto_custom"), options.getString("theme_color"));
        this.formatChartLegend(options.optInt("chart_legend"));
        this.config.remove("legend");
        plotOptions.optJSONObject("dataLabels").put("enabled", options.optBoolean("show_data_label"));
        this.config.put("chartType", "areaMap");

        plotOptions.getJSONObject("tooltip").put("shared", true);
        JSONArray formatterArray = new JSONArray();
        for(int i = data.length() - 1; i > -1; i--){
            JSONObject item = data.getJSONObject(i);
            if(item.has("settings")){
                JSONObject settings = item.getJSONObject("settings");
                formatterArray.put(formatToolTipAndDataLabel(settings.optInt("format", BIChartSettingConstant.NORMAL), settings.optInt("num_level", BIChartSettingConstant.NORMAL),
                        settings.optString("unit", ""), settings.optBoolean("num_separators", BIChartSettingConstant.NUM_SEPARATORS)));
            }
        }
        plotOptions.getJSONObject("tooltip").put("formatter","function () {" +
            "var tip = this.name;" +
            "for(int i = 0; i < this.points.length; i++){" +
                "var value = point.size || point.y;" +
                "tip += ('<div>' + point.seriesName + ':' + (window.BH ? BH.contentFormat(value, formatterArray[idx]) : value) + '</div>');" +
            "}"+
            "return tip;}");
        plotOptions.getJSONObject("dataLabels").getJSONObject("formatter").put("valueFormat", "function () {" +
            "return (window.BH ? BH.contentFormat(arguments[0], "+ formatterArray.getString(0) + ") : arguments[0]);" +
        "};");

        config.put("geo", options.getJSONObject("geo"));
        JSONObject geo = config.getJSONObject("geo");
        if (options.getBoolean("show_background_layer")&& options.optJSONObject("background_layer_info") != null) {
            JSONObject background_layer_info = options.getJSONObject("background_layer_info");
            if (ComparatorUtils.equals(options.getJSONObject("background_layer_info").getString("type"), BIChartSettingConstant.WMS_SERVER)) {
                geo.put("tileLayer", false)
                        .put("wmsUrl", background_layer_info.getString("url"))
                        .put("wmsLayer", background_layer_info.getString("wmsLayer"));
            } else {
                geo.put("tileLayer", background_layer_info.getString("url"));
            }
        }
        if (options.getJSONArray("initDrillPath").length() > 1) {
            config.put("initDrillPath", options.getJSONArray("initDrillPath"));
        }
        config.getJSONObject("dTools").put("enabled", true);
        config.remove("xAxis");
        config.remove("yAxis");

        boolean find = false;
        for(int i = 0; i < data.length(); i++){
            JSONObject item = data.getJSONObject(i);
            if(item.has("type") && ComparatorUtils.equals(item.get("type"), "areaMap")){
                find = true;
                break;
            }
        }
        if (find) {
            data.put(new JSONObject().put("type", "areaMap").put("data", new JSONArray()));
        }

        return this.config.put("series", data);
    }

    public String formatToolTipAndDataLabel(int format, int numberLevel, String unit, boolean num_separators) {
        String formatter = "#.##";
        switch (format) {
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

        if (num_separators) {
            formatter = "#,##0";
        }

        switch (numberLevel) {
            case BIChartSettingConstant.CHART_TARGET_STYLE.NUM_LEVEL.NORMAL:
                formatter += "";
                break;
            case BIChartSettingConstant.CHART_TARGET_STYLE.NUM_LEVEL.TEN_THOUSAND:
                formatter += Inter.getLocText("BI-Wan");
                break;
            case BIChartSettingConstant.CHART_TARGET_STYLE.NUM_LEVEL.MILLION:
                formatter += Inter.getLocText("BI-Million");
                break;
            case BIChartSettingConstant.CHART_TARGET_STYLE.NUM_LEVEL.YI:
                formatter += Inter.getLocText("BI-Yi");
                break;
            case BIChartSettingConstant.CHART_TARGET_STYLE.NUM_LEVEL.PERCENT:
                if (format == BIChartSettingConstant.NORMAL) {
                    formatter = "#0%";
                } else {
                    formatter += "%";
                }
                break;
        }

        return formatter + unit;
    }

    @Override
    public JSONObject getConvertedDataAndSettings(JSONArray data, JSONArray types, JSONObject options) throws JSONException {
        JSONObject configAndData = this.formatItems(data, types, options);
        this.config = configAndData.getJSONObject("config");
        JSONArray items = configAndData.getJSONArray("result");
        return this.formatConfig(options, items);
    }

    private void formatRangeStyle(JSONObject rangeLegend, JSONArray items, JSONArray mapStyles, int auto_custom, String theme_color) throws JSONException{
        JSONArray range = new JSONArray();
        String color = null;
        JSONObject defaultStyle = new JSONObject();
        double conditionMax, conditionMin, max, min;
        conditionMax = conditionMin = min = max = Double.NaN;
        for(int i = 0; i < items.length(); i++){
            JSONObject item = items.getJSONObject(i);
            JSONArray data = item.getJSONArray("data");
            for(int j = 0; j < data.length(); j++){
                JSONObject it = data.getJSONObject(j);
                if(Double.isNaN(min) || min > it.getDouble("y")){
                    min = it.getDouble("y");
                }
                if(Double.isNaN(max) || max < it.getDouble("y")){
                    max = it.getDouble("y");
                }
            }
        }
        switch (auto_custom) {
            case BIChartSettingConstant.SCALE_SETTING.AUTO:
                defaultStyle.put("color", theme_color);
                rangeLegend.put("range", defaultStyle);
            case BIChartSettingConstant.SCALE_SETTING.CUSTOM:
                if (mapStyles.length() != 0) {
                    for(int i = 0; i < mapStyles.length(); i++){
                        JSONObject style = mapStyles.getJSONObject(i);
                        range.put(new JSONObject().put("color", style.getString("color"))
                        .put("from", style.getJSONObject("range").getDouble("min"))
                        .put("to", style.getJSONObject("range").getDouble("max")));
                        color = style.getString("color");
                        conditionMax = style.getJSONObject("range").getDouble("max");
                    }
                    conditionMin = mapStyles.getJSONObject(0).getJSONObject("range").getDouble("min");
                    if(conditionMin != 0){
                        range.put(new JSONObject().put("color", "#808080").put("from", 0).put("to", conditionMin));
                    }

                    double maxScale = this.calculateValueNiceDomain(0, max)[1];
                    if (conditionMax < maxScale) {
                        range.put(new JSONObject().put("color", color).put("from", conditionMax).put("to", maxScale));
                    }
                    rangeLegend.put("range", range);
                } else {
                    defaultStyle.put("color", theme_color);
                    rangeLegend.put("range", defaultStyle);
                }
        }
    }

    private void formatRangeLegend(JSONObject config, JSONArray items, int type, JSONArray map_styles, int auto_custom, String theme_color) throws JSONException{
        JSONObject rangeLegend = config.getJSONObject("rangeLegend");
        rangeLegend.put("enabled", true);
        switch (type) {
            case BIChartSettingConstant.CHART_LEGENDS.BOTTOM:
                rangeLegend.put("visible", true).put("position", "bottom");
                break;
            case BIChartSettingConstant.CHART_LEGENDS.RIGHT:
                rangeLegend.put("visible", true).put("position", "right");
                break;
            case BIChartSettingConstant.CHART_LEGENDS.NOT_SHOW:
                rangeLegend.put("visible", false);
                break;
        }
        rangeLegend.put("continuous", false);
        formatRangeStyle(rangeLegend, items, map_styles, auto_custom, theme_color);
        boolean needAddUnitOnLegend = false;
        String initTo = "";
        int numberLevel = BIChartSettingConstant.NORMAL;
        if(items.length() > 0 && items.getJSONObject(0).has("settings")){
            needAddUnitOnLegend = true;
            numberLevel = items.getJSONObject(0).getJSONObject("settings").getInt("num_level");
            if(items.getJSONObject(0).getJSONObject("settings").getBoolean("num_separators")){
                initTo = "(window.BH ? BH.contentFormat(to, \"#,##0\") : to)";
            }
        }
        rangeLegend.put("formatter", "function () {" +
            "var to = this.to;" +
            "if ("+ needAddUnitOnLegend +") {" +
                "if("+ StringUtils.isNotEmpty(initTo) +"){" +
                    "to = "+ initTo +
                "}" +
                "switch ("+ numberLevel +") {" +
                    "case " + BIChartSettingConstant.CHART_TARGET_STYLE.NUM_LEVEL.NORMAL + ":" +
                        "to += '';" +
                        "break;" +
                    "case " + BIChartSettingConstant.CHART_TARGET_STYLE.NUM_LEVEL.TEN_THOUSAND + ":" +
                        "to +=" + Inter.getLocText("BI-Wan") + ";" +
                        "break;" +
                    "case " + BIChartSettingConstant.CHART_TARGET_STYLE.NUM_LEVEL.MILLION + ":" +
                        "to +=" + Inter.getLocText("BI-Million") + ";" +
                        "break;" +
                    "case " + BIChartSettingConstant.CHART_TARGET_STYLE.NUM_LEVEL.YI + ":" +
                        "to +=" + Inter.getLocText("BI-Yi") + ";" +
                        "break;" +
                    "case " + BIChartSettingConstant.CHART_TARGET_STYLE.NUM_LEVEL.PERCENT + ":" +
                        "to = (window.BH ? BH.contentFormat(to, \"#0%\") : to);" +
                        "break;"+
                "}" +
            "}" +
            "return to;" +
        "}");
    }

    private void formatDrillItems(JSONObject items) throws JSONException{
        JSONArray series = items.getJSONArray("series");
        for(int i = 0; i < series.length(); i++){
            JSONObject da = series.getJSONObject(i);
            JSONArray data = da.getJSONArray("data");
            for(int j = 0; j < data.length(); j++){
                JSONObject d = data.getJSONObject(j);
                d.put("y", this.formatXYDataWithMagnify(d.getDouble("y"), 1));
                if(da.has("settings")){
                    JSONObject settings = da.getJSONObject("settings");
                    d.put("y", this.formatNumberLevel(settings.optInt("num_level", BIChartSettingConstant.NORMAL), d.getDouble("y")));
                }
                if(da.has("type") && ComparatorUtils.equals(da.getString("type"), "bubble")){
                    d.put("name", d.getString("x")).put("size", d.getString("y"));
                }else{
                    d.put("name", d.getString("x")).put("value", d.getString("y"));
                }
                if(d.has("drilldown")){
                    this.formatDrillItems(d.getJSONObject("drilldown"));
                }
            }
        }
    }

    private String formatNumberLevel(int num_level, double value){
        return BIChartSettingConstant.TWOFIEXEDFORMAT.format(value / calcMagnify(num_level));
    }

    @Override
    public JSONObject formatItems(JSONArray data, JSONArray types, JSONObject options) throws JSONException {
        double max, min;
        max = min = Double.NaN;
        for(int i = 0; i < data.length(); i++){
            JSONArray item = data.getJSONArray(i);
            for(int j = 0; j < item.length(); j++){
                JSONObject it = item.getJSONObject(j);
                JSONArray d = it.getJSONArray("data");
                for(int k = 0; k < d.length(); k++){
                    JSONObject da = d.getJSONObject(k);
                    da.put("y", this.formatXYDataWithMagnify(da.getDouble("y"), 1));
                    if(it.has("settings")){
                        JSONObject settings = it.getJSONObject("settings");
                        da.put("y", this.formatNumberLevel(settings.optInt("num_level", BIChartSettingConstant.NORMAL), da.getDouble("y")));
                    }
                    if ((Double.isNaN(max) || da.getDouble("y") > max) && j == 0) {
                        max = da.getDouble("y");
                    }
                    if ((Double.isNaN(min) || da.getDouble("y") < min) && j == 0) {
                        min = da.getDouble("y");
                    }
                    if(it.has("type") && ComparatorUtils.equals(it.getString("type"), "bubble")){
                        da.put("name", da.getString("x")).put("size", da.getString("y"));
                    } else {
                        da.put("name", da.getString("x")).put("value", da.getString("y"));
                    }
                    if(da.has("drilldown")){
                        this.formatDrillItems(da.getJSONObject("drilldown"));
                    }
                }
            }
        }
        return super.formatItems(data, types, options);
    }
}
