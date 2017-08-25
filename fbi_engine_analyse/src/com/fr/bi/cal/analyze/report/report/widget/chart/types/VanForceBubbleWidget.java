package com.fr.bi.cal.analyze.report.report.widget.chart.types;

import com.fr.json.JSONArray;
import com.fr.json.JSONException;
import com.fr.json.JSONObject;

/**
 * Created by eason on 2017/3/20.
 */
public class VanForceBubbleWidget extends VanDotWidget{
    protected JSONObject populateDefaultSettings() throws JSONException {
        return super.populateDefaultSettings().put("showDataLabel", true);
    }

    protected JSONObject defaultDataLabelSetting() throws JSONException {
        return JSONObject.create().put("showCategoryName", false)
                .put("showSeriesName", false).put("showValue", true).put("showPercentage", false)
                .put("textStyle", defaultFont());
    }

    public JSONArray createSeries(JSONObject data) throws Exception {
        return this.createXYSeries(data);
    }

    protected void formatSeriesTooltipFormat(JSONObject options) throws Exception {
        this.defaultFormatSeriesTooltipFormat(options);
    }

    protected void formatSeriesDataLabelFormat(JSONObject options) throws Exception {
        this.defaultFormatSeriesDataLabelFormat(options);
    }

    public JSONObject createOptions(JSONObject globalStyle, JSONObject data) throws Exception{
        JSONObject options = super.createOptions(globalStyle, data);

        options.put(this.getCoordXKey(), JSONObject.EMPTY);
        options.put(this.getCoordYKey(), JSONObject.EMPTY);

        return options;
    }

    //气泡图不是target3颜色，强制normallegend
    protected boolean dotChartUseNormalLegend() {
        return false;
    }

    protected String getTooltipIdentifier() {
        return CATEGORY + SERIES + VALUE;
    }

    public String getSeriesType(String dimensionID){
        return "forceBubble";
    }

    protected String valueLabelKey() {
        return VALUE;
    }

    protected String categoryLabelKey() {
        return CATEGORY;
    }

    protected String seriesLabelKey() {
        return SERIES;
    }

    protected boolean customFixedStyleRadio(JSONObject settings) throws JSONException {
        return true;
    }

    protected boolean customGradientStyleRadio(JSONObject settings) throws JSONException {
        return true;
    }

    // 使用默认的chartWidget的numberLevel方法
    protected int numberLevel(String dimensionID) {
        return this.numberLevelFromSettings(dimensionID);
    }

    protected boolean checkValid(){
        return this.getDim1Size() > 0 && this.hasTarget();
    }
}
