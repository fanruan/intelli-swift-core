package com.fr.bi.cal.analyze.report.report.widget.chart.newstyle;

import com.fr.bi.stable.constant.BIChartSettingConstant;
import com.fr.json.JSONArray;
import com.fr.json.JSONException;
import com.fr.json.JSONObject;

/**
 * Created by User on 2016/8/31.
 */
public class DashboardChartSetting extends BIAbstractChartSetting {

    private JSONObject config;
    private JSONArray gaugeAxis;

    public DashboardChartSetting() throws JSONException{
        this.gaugeAxis = new JSONArray().put(new JSONObject().put("minorTickColor", "rgb(226,226,226)")
        .put("tickColor", "rgb(186,186,186)").put("labelStyle", this.getFontStyle())
        .put("step", 0).put("showLabel", true));
    }

    @Override
    public JSONObject formatConfig(JSONObject options, JSONArray data) throws JSONException {
        formatChartDashboardStyle(options, data);
        this.config.put("chartType", "dashboard");
        this.config.remove("xAxis");
        this.config.remove("yAxis");
        return this.config.put("series", data);
    }

    @Override
    public JSONObject getConvertedDataAndSettings(JSONArray data, JSONArray types, JSONObject options) throws JSONException {
        JSONObject configAndData = this.formatItems(data, types, options);
        this.config = configAndData.getJSONObject("config");
        JSONArray items = configAndData.getJSONArray("result");
        return this.formatConfig(options, items);
    }

    private JSONArray getBandsStyles(JSONArray items, JSONArray styles, int change) throws JSONException{
        double min = 0;
        String color = "";
        double max = Double.NaN;
        double conditionMax = Double.NaN;
        JSONArray bands = new JSONArray();
        for(int i = 0; i < items.length(); i++){
            JSONObject data = items.getJSONObject(i).getJSONArray("data").getJSONObject(0);
            if(max == Double.NaN || data.getDouble("y") > max){
                max = data.getDouble("y");
            }
        }
        switch (change) {
            case BIChartSettingConstant.SCALE_SETTING.AUTO:
                return null;
            case BIChartSettingConstant.SCALE_SETTING.CUSTOM:
                if (styles.length() == 0) {
                    return bands;
                } else {
                    for(int i = 0; i < styles.length(); i++){
                        JSONObject style = styles.getJSONObject(i);
                        bands.put(new JSONObject().put("color", style.getString("color"))
                                .put("from", style.getJSONObject("range").getDouble("min"))
                                .put("to", style.getJSONObject("range").getDouble("max")));
                        conditionMax = style.getJSONObject("range").getDouble("max");
                        color = style.getString("color");
                    }
                    min = styles.getJSONObject(0).getJSONObject("range").getDouble("min");
                    bands.put(new JSONObject().put("color", "#808080").put("from", 0).put("to", min));

                    double maxScale = this.calculateValueNiceDomain(0, max)[1];

                    bands.put(new JSONObject().put("color", color).put("from", conditionMax).put("to", maxScale));
                    return bands;
                }
            default:
                return null;
        }

    }

    private void setPlotOptions(String style, JSONArray bands, JSONObject valueLabel, JSONObject percentageLabel, String thermometerLayout, String layout) throws JSONException{
        JSONObject plotOptions = config.getJSONObject("plotOptions");
        plotOptions.put("style", style);
        plotOptions.put("bands", bands);
        plotOptions.put("valueLabel", valueLabel);
        plotOptions.put("percentageLabel", percentageLabel);
        plotOptions.put("thermometerLayout", thermometerLayout);
        plotOptions.put("layout", layout);
    }

    private void changeMaxMinScale(JSONObject options) throws JSONException{
        this.gaugeAxis.getJSONObject(0).put("min", options.optDouble("min_scale"));
        this.gaugeAxis.getJSONObject(0).put("max", options.optDouble("max_scale"));
    }

    public void formatNumberLevelInDashboardAxis(JSONObject config, JSONObject options, JSONArray items, int position, String formatter) throws JSONException {
        super.formatNumberLevelInYaxis(config, items, options.getInt("dashboard_number_level"), position, formatter);
        JSONObject plotOptions = config.getJSONObject("plotOptions");
        plotOptions.getJSONObject("tooltip").getJSONObject("formatter")
        .put("valueFormat", "function () {" +
            "return " + BIChartSettingConstant.CUSTOM_FORMAT.VALUEFORMAT + getXYAxisTitle(options.getInt("dashboard_number_level"), position, true, options.getString("dashboard_unit"), "") +
        ";}");
        if (options.getBoolean("num_separators")) {
            plotOptions.getJSONObject("tooltip").getJSONObject("formatter")
            .put("valueFormat", "function () {" +
                "return " + BIChartSettingConstant.CUSTOM_FORMAT.NUMSEPARATORS +
            ";}");
        }
        if (options.getInt("dashboard_number_level") == BIChartSettingConstant.CHART_TARGET_STYLE.NUM_LEVEL.PERCENT) {
            plotOptions.getJSONObject("tooltip").getJSONObject("formatter")
                    .put("valueFormat", "function () {" +
                            "return "+ BIChartSettingConstant.CUSTOM_FORMAT.PERCENTVALUEFORMAT +";" +
                            "}");
            if (options.getBoolean("num_separators")) {
                plotOptions.getJSONObject("tooltip").getJSONObject("formatter")
                        .put("valueFormat", "function () {" +
                                "return "+ BIChartSettingConstant.CUSTOM_FORMAT.PERCENTNUMSEPARATORS +";" +
                                "}");
            }
        }
    }

    private void formatChartDashboardStyle(JSONObject options, JSONArray items) throws JSONException{
        JSONObject plotOptions = this.config.getJSONObject("plotOptions");
        JSONArray bands = getBandsStyles(items, options.getJSONArray("bands_styles"), plotOptions.getInt("auto_custom_style"));
        JSONObject percentageLabel = plotOptions.getJSONObject("percentageLabel").put("enabled", options.getInt("show_percentage") == BIChartSettingConstant.PERCENTAGE.SHOW);
        JSONObject slotValueLAbel = new JSONObject();
        int numberLevel = options.getInt("dashboard_number_level");
        String value;
        String formatter;
        if(BIChartSettingConstant.CHART_TARGET_STYLE.NUM_LEVEL.PERCENT == numberLevel){
            if(options.getBoolean("num_separators")){
                value = BIChartSettingConstant.CUSTOM_FORMAT.PERCENTNUMSEPARATORS;
            }else{
                value = BIChartSettingConstant.CUSTOM_FORMAT.PERCENTVALUEFORMAT;
            }
        }else{
            if(options.getBoolean("num_separators")){
                value = BIChartSettingConstant.CUSTOM_FORMAT.NUMSEPARATORS;
            }else{
                value = BIChartSettingConstant.CUSTOM_FORMAT.VALUEFORMAT;
            }
        }
        if(options.getInt("chart_dashboard_type") == BIChartSettingConstant.CHART_SHAPE.VERTICAL_TUBE){
            formatter = "'<div style=\"text-align: center\">' + this.category + '</div>' + '<div style=\"text-align: center\">' + this.seriesName + '</div>' + '<div style=\"text-align: center\">'" + value + "'</div>'";
        }else{
            formatter = "'<div style=\"text-align: center\">' + this.category + '</div>' + '<div style=\"text-align: center\">' + this.seriesName + '</div>' + '<div style=\"text-align: center\">'" + value +
                    getXYAxisTitle(options.getInt("dashboard_number_level"), BIChartSettingConstant.DASHBOARD_AXIS, true, options.getString("dashboard_unit"), "") + "'</div>'";
        }
        slotValueLAbel.put("formatter", "function () { return "+ formatter +" }")
                .put("style", options.getJSONObject("valueLabel").getString("style"))
                .put("useHtml", true);

        switch (options.getInt("chart_dashboard_type")) {
            case BIChartSettingConstant.CHART_SHAPE.HALF_DASHBOARD:
                setPlotOptions("pointer_semi", bands, slotValueLAbel, null, null, null);
                break;
            case BIChartSettingConstant.CHART_SHAPE.PERCENT_DASHBOARD:
                setPlotOptions("ring", bands, slotValueLAbel, percentageLabel, null, null);
                break;
            case BIChartSettingConstant.CHART_SHAPE.PERCENT_SCALE_SLOT:
                setPlotOptions("slot", bands, slotValueLAbel, percentageLabel, null, null);
                break;
            case BIChartSettingConstant.CHART_SHAPE.HORIZONTAL_TUBE:
                slotValueLAbel.put("align", "bottom");
                percentageLabel.put("align", "bottom");
                setPlotOptions("thermometer", bands, slotValueLAbel, percentageLabel, "horizontal", "vertical");
                break;
            case BIChartSettingConstant.CHART_SHAPE.VERTICAL_TUBE:
                slotValueLAbel.put("align", "left");
                percentageLabel.put("align", "left");
                setPlotOptions("thermometer", bands, slotValueLAbel, percentageLabel, "vertical", "horizontal");
                break;
            case BIChartSettingConstant.CHART_SHAPE.NORMAL:
            default:
                setPlotOptions("pointer", bands, slotValueLAbel, null, null, null);
                break;
        }
        changeMaxMinScale(options);
        config.put("gaugeAxis", this.gaugeAxis);
        formatNumberLevelInDashboardAxis(config, options, items, BIChartSettingConstant.LEFT_AXIS, "");
        if (options.getInt("dashboard_number_level") == BIChartSettingConstant.CHART_TARGET_STYLE.NUM_LEVEL.PERCENT) {
            plotOptions.getJSONObject("valueLabel").getJSONObject("formatter")
                    .put("valueFormat", "function () {" +
                            "return "+ BIChartSettingConstant.CUSTOM_FORMAT.THISPERCENTVALUEFORMAT +
                            ";}");
            String scaleString;
            if(options.optBoolean("num_separators")){
                scaleString = BIChartSettingConstant.CUSTOM_FORMAT.THISPERCENTNUMSEPARATORS;
            }else{
                scaleString = BIChartSettingConstant.CUSTOM_FORMAT.THISPERCENTVALUEFORMAT;
            }
            gaugeAxis.getJSONObject(0).put("formatter", "function () {" +
                "return" + scaleString + getXYAxisTitle(options.getInt("dashboard_number_level"), BIChartSettingConstant.DASHBOARD_AXIS, true, options.getString("dashboard_unit"), "") +
            ";}");
        } else {
            String v = "this";
            if(options.getBoolean("num_separators")){
                v = "BI.contentFormat(this, \"#,###\")";
            }
            gaugeAxis.getJSONObject(0).put("formatter", "function () {" +
                    "return" + v + getXYAxisTitle(options.getInt("dashboard_number_level"), BIChartSettingConstant.DASHBOARD_AXIS, true, options.getString("dashboard_unit"), "") +
                    "}");
        }
    }

    @Override
    public JSONObject formatItems(JSONArray data, JSONArray types, JSONObject options) throws JSONException {

        if (data.length() == 0) {
            return super.formatItems(new JSONArray(), types, options);
        }
        if (options.getInt("chart_dashboard_type") == BIChartSettingConstant.NORMAL || options.getInt("chart_dashboard_type") == BIChartSettingConstant.HALF_DASHBOARD) {
            JSONArray result = new JSONArray();
            if (options.getInt("number_of_pointer") == BIChartSettingConstant.ONE_POINTER && data.getJSONArray(0).length() == 1) {//单个系列
                JSONObject series = data.getJSONArray(0).getJSONObject(0);
                for(int i = 0; i < series.getJSONArray("data").length(); i++){
                    JSONObject da = series.getJSONArray("data").getJSONObject(i);
                    result.put(new JSONObject().put("name", da.getString("x")).put("data", new JSONArray().put(da.put("x", series.getString("name")))));
                }
                return super.formatItems(new JSONArray().put(result), types, options);
            }
            if (options.getInt("number_of_pointer") == BIChartSettingConstant.MULTI_POINTER && data.getJSONArray(0).length() > 1) {//多个系列
                for(int i = 0; i < data.length(); i++){
                    JSONArray item = data.getJSONArray(i);
                    for(int j = 0; j < item.length(); j++){
                        JSONObject it = item.getJSONObject(j);
                        JSONObject da = it.getJSONArray("data").getJSONObject(0);
                        da.put("x", it.getString("name"));
                        result.put(da);
                    }
                }
                return super.formatItems(new JSONArray().put(new JSONArray().put(new JSONObject().put("data", result).put("name", ""))), types, options);
            }
        } else {
            JSONArray others = new JSONArray();
            for(int i = 0; i < data.getJSONArray(0).length(); i++){
                JSONObject item = data.getJSONArray(0).getJSONObject(i);
                for(int j = 0; j < item.getJSONArray("data").length(); j++){
                    JSONObject da = item.getJSONArray("data").getJSONObject(j);
                    others.put(new JSONObject().put("name", da.getString("x"))
                            .put("data", new JSONArray().put(da.put("x", item.getString("name")).put("y", da.getDouble("y")))));
                }
            }
            return super.formatItems(new JSONArray().put(others), types, options);
        }
        return super.formatItems(data, types, options);
    }


}
