package com.fr.bi.cal.analyze.report.report.widget.chart.types;

import com.fr.bi.stable.constant.BIChartSettingConstant;
import com.fr.json.JSONArray;
import com.fr.json.JSONException;
import com.fr.json.JSONObject;
import com.fr.stable.StableUtils;
import com.fr.stable.StringUtils;

/**
 * Created by eason on 2017/3/20.
 */
public class VanGaugeWidget extends VanCartesianWidget{

    private static final int NORMAL = 1;     //360的仪表盘
    private static final int HALF_DASHBOARD = 9;//180'的仪表盘
    private static final int PERCENT_DASHBOARD = 10;//百分比的仪表盘
    private static final int PERCENT_SCALE_SLOT = 11;//带刻度槽的仪表盘
    private static final int VERTICAL_TUBE = 12;     //竖起来的试管型仪表盘
    private static final int HORIZONTAL_TUBE = 13;//横过来的试管型仪表盘

    private static final int SINGLE_POINTER = 1;
    private static final int MULTI_POINTERS = 2;

    protected JSONObject populateDefaultSettings() throws JSONException{
        JSONObject settings = super.populateDefaultSettings();

        settings.put("leftYShowCustomScale", true);

        settings.put("dashboardChartType", NORMAL);

        settings.put("dashboardPointer", SINGLE_POINTER);

        settings.put("styleRadio", AUTO);
        settings.put("dashboardStyles", JSONArray.create());

        settings.put("minScale", StringUtils.EMPTY);
        settings.put("maxScale", StringUtils.EMPTY);

        settings.put("showDataLabel", true);
        settings.put("showPercentage", BIChartSettingConstant.PERCENTAGE.SHOW);

        return settings;
    }

    protected JSONObject parseLeftValueAxis(JSONObject settings) throws JSONException{
        JSONObject left = super.parseLeftValueAxis(settings);
        String min = StringUtils.EMPTY, max = StringUtils.EMPTY;

        if(settings.has("minScale")) {
            min = settings.optString("minScale");
        }

        if(settings.has("maxScale")) {
            max = settings.optString("maxScale");
        }

        if(StringUtils.isNotBlank(min)){
            left.put("min", StableUtils.string2Number(min).doubleValue());
        }

        if(StringUtils.isNotBlank(max)){
            left.put("max", StableUtils.string2Number(max).doubleValue());
        }

        return left;
    }

    protected String getCoordYKey(){
        return "gaugeAxis";
    }

    //仪表盘的标签样式固定
    public JSONObject createPlotOptions(JSONObject globalSetting, JSONObject settings) throws Exception{
        JSONObject plotOptions = super.createPlotOptions(globalSetting, settings);

        addDataLabel(settings, plotOptions);

        if(settings.optInt("styleRadio", AUTO) != AUTO){
            JSONArray dashboardStyles = settings.optJSONArray("dashboardStyles");
            plotOptions.put("bands", this.mapStyleToRange(dashboardStyles));
        }

        plotOptions.put("paneBackgroundColor", TRANS);
        return plotOptions;
    }

    private void addDataLabel(JSONObject settings, JSONObject plotOptions) throws JSONException{
        JSONObject dataLabelSettings = settings.optJSONObject("dataLabelSetting");
        dataLabelSettings = dataLabelSettings == null ? JSONObject.create().put("showValue", true).put("showCategoryName", true) : dataLabelSettings;

        int gaugeStyle = settings.optInt("dashboardChartType");
        boolean isPointer = gaugeStyle == NORMAL || gaugeStyle == HALF_DASHBOARD;
        boolean hasCategory = getCategoryDimension() != null;
        boolean multi = isPointer && (settings.optInt("dashboardPointer") == MULTI_POINTERS);

        boolean showCate = dataLabelSettings.optBoolean("showCategoryName");
        String key = hasCategory || multi ? SERIES : CATEGORY;

        String valueID = StringUtils.EMPTY, seriesID = StringUtils.EMPTY;
        if(dataLabelSettings.optBoolean("showValue")){
            valueID += VALUE;
        }
        if(isPointer){
            if(dataLabelSettings.optBoolean("showPercentage")){
                valueID += PERCENT;
            }
            if(showCate){
                valueID += key;
            }
        } else {
            if(showCate){
                valueID += key;
            }
        }

        String align = gaugeStyle == HORIZONTAL_TUBE ? "top" : "left";
        JSONObject font = defaultFont();
        font = dataLabelSettings.has("textStyle") ? merge(dataLabelSettings.optJSONObject("textStyle"), font) : font;
        JSONObject valueLabel = JSONObject.create().put("enabled", settings.optBoolean("showDataLabel")).put("backgroundColor", TRANS).put("align", align).put("formatter", JSONObject.create().put("identifier", valueID)).put("style", font);
        JSONObject seriesLabel = JSONObject.create().put("enabled", settings.optBoolean("showDataLabel")).put("formatter", JSONObject.create().put("identifier", seriesID)).put("align", "bottom").put("style", font);
        JSONObject percentageLabel = JSONObject.create().put("enabled", settings.optInt("showPercentage") == BIChartSettingConstant.PERCENTAGE.SHOW).put("align", align).put("formatter",  JSONObject.create().put("identifier", PERCENT)
                .put("percentFormat", "function(){return BI.contentFormat(arguments[0], \"#.##%\")}"));

        if(gaugeStyle == PERCENT_SCALE_SLOT){
            percentageLabel.put("style", JSONObject.create().put("fontSize", "24px"));
        } else if(gaugeStyle == PERCENT_DASHBOARD){
            valueLabel.optJSONObject("style").put("color", DARK);
        } else if(!isPointer){
            percentageLabel.put("style", font);
        }

        plotOptions.put("valueLabel", valueLabel).put("seriesLabel", seriesLabel).put("percentageLabel", percentageLabel);
    }

    protected String dataLabelsKey() {
        return "valueLabel";
    }

    //仪表盘的标签和数据点提示都要加单位
    protected boolean showUnit(boolean isTooltip) {
        return true;
    }

    public JSONArray createSeries(JSONObject originData) throws Exception{

        JSONArray series = this.createXYSeries(originData);

        JSONObject settings = this.getDetailChartSetting();
        int gaugeStyle = settings.optInt("dashboardChartType");
        String style = StringUtils.EMPTY, layout = "vertical";
        if(gaugeStyle == NORMAL){
            style = "pointer";
        }else if(gaugeStyle == HALF_DASHBOARD){
            style = "pointer_semi";
        }else if(gaugeStyle == PERCENT_DASHBOARD){
            style = "ring";
        }else if(gaugeStyle == PERCENT_SCALE_SLOT){
            style = "slot";
        }else if(gaugeStyle == VERTICAL_TUBE){
            style = "thermometer";
        }else if(gaugeStyle == HORIZONTAL_TUBE){
            style = "thermometer";
            layout = "horizontal";
        }

        boolean isPointer = gaugeStyle == NORMAL || gaugeStyle == HALF_DASHBOARD;
        boolean multi = isPointer && (settings.optInt("dashboardPointer") == MULTI_POINTERS);

        return dealSeries(series, style, layout, multi);
    }

    private JSONArray dealSeries(JSONArray series, String style, String layout, boolean multi) throws JSONException {
        JSONArray newSeries = JSONArray.create();
        for(int i = 0, len = series.length(); i < len; i++){
            JSONObject ser = series.getJSONObject(i);
            ser.put("style", style).put("thermometerLayout", layout).put("layout", layout.equals("vertical") ? "horizontal" : "vertical");

            if(multi){
                JSONObject combineSer = newSeries.optJSONObject(0);
                if(combineSer == null){
                    combineSer = ser;
                    seriesToCate(ser);
                    newSeries.put(combineSer);
                } else {//将多个指标的点放到一个系列里
                    JSONArray combineData = combineSer.optJSONArray("data");
                    seriesToCate(ser);
                    JSONArray datas = ser.optJSONArray("data");
                    for(int dataIndex = 0, dataCount = datas.length(); dataIndex < dataCount; dataIndex ++) {
                        combineData.put(datas.opt(dataIndex));
                    }
                    combineSer.put("data", combineData);
                }
            } else {//将一个系列的多个点拆成多个系列
                JSONArray datas = ser.optJSONArray("data");
                for(int dataIndex = 0, dataCount = datas.length(); dataIndex < dataCount; dataIndex ++){
                    JSONObject newSer = new JSONObject(ser.toString());
                    newSer.put("data", JSONArray.create().put(datas.optJSONObject(dataIndex)));
                    newSeries.put(newSer);
                }
            }

        }

        return newSeries;
    }

    private void seriesToCate(JSONObject ser) throws JSONException {
        if(getCategoryDimension() != null){
            return;
        }
        String name = ser.optString("name");
        ser.put("name", "");
        JSONArray datas = ser.optJSONArray("data");
        for(int dataIndex = 0, dataCount = datas.length(); dataIndex < dataCount; dataIndex ++) {
            JSONObject point = datas.optJSONObject(dataIndex);
            point.put("x", name);
        }
    }

    protected JSONObject createDataLabels(JSONObject settings) throws JSONException{
        return JSONObject.EMPTY;
    }

    @Override
    protected boolean supportDataSheet() throws Exception {
        return false;
    }

    public String getSeriesType(String dimensionID){
        return "gauge";
    }

    protected boolean checkValid(){
        return this.getTar1Size() > 0;
    }
}
