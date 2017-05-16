package com.fr.bi.cal.analyze.report.report.widget.chart.types;

import com.finebi.cube.common.log.BILoggerFactory;
import com.fr.base.Formula;
import com.fr.base.ParameterMapNameSpace;
import com.fr.bi.cal.analyze.report.report.widget.VanChartWidget;
import com.fr.bi.field.target.target.BISummaryTarget;
import com.fr.bi.stable.constant.BIReportConstant;
import com.fr.json.JSONArray;
import com.fr.json.JSONException;
import com.fr.json.JSONObject;
import com.fr.script.Calculator;
import com.fr.stable.StringUtils;

import java.util.HashMap;

/**
 * Created by eason on 2017/3/2.
 */
public abstract class VanCartesianWidget extends VanChartWidget {

    //面积图的三种形态
    private static final int NORMAL = 1;
    private static final int STEP = 2;
    private static final int CURVE = 3;

    private static final String FALL_COLUMN = "fallColumn";
    private static final String TRANS = "rgba(0,0,0,0)";

    private static final int VERTICAL = 90;

    protected JSONObject populateDefaultSettings() throws JSONException{
        JSONObject settings = super.populateDefaultSettings();

        //分类轴
        settings.put("catShowTitle", false)
                .put("catTitle", StringUtils.EMPTY)
                .put("catTitleStyle", this.defaultFont())
                .put("catShowLabel", true).put("vShowGridLine", true).put("vGridLineColor", "#dddddd")
                .put("catLabelStyle", JSONObject.create().put("textStyle", this.defaultFont()))
                .put("catLineColor", "#dddddd");

        //左值轴
        settings.put("leftYUnit", StringUtils.EMPTY)
                .put("leftYNumberLevel", BIReportConstant.TARGET_STYLE.NUM_LEVEL.NORMAL)
                .put("leftYShowTitle", false)
                .put("leftYTitle", StringUtils.EMPTY)
                .put("leftYTitleStyle", this.defaultFont())
                .put("leftYReverse", false)
                .put("leftYShowLabel", true).put("hShowGridLine", true).put("hGridLineColor", "#dddddd")
                .put("leftYLabelStyle", JSONObject.create().put("textStyle", this.defaultFont()))
                .put("leftYLineColor", "#dddddd")
                .put("leftYSeparator", true);

        //右值轴
        settings.put("rightYUnit", StringUtils.EMPTY)
                .put("rightYNumberLevel", BIReportConstant.TARGET_STYLE.NUM_LEVEL.NORMAL)
                .put("rightYShowTitle", false)
                .put("rightYTitle", StringUtils.EMPTY)
                .put("rightYTitleStyle", this.defaultFont())
                .put("rightYReverse", false)
                .put("rightYShowLabel", true)
                .put("rightYLabelStyle", JSONObject.create().put("textStyle", this.defaultFont()))
                .put("rightYLineColor", "#dddddd")
                .put("rightYLineColor", true);

        //右二值轴
        settings.put("rightY2Unit", StringUtils.EMPTY)
                .put("rightY2NumberLevel", BIReportConstant.TARGET_STYLE.NUM_LEVEL.NORMAL)
                .put("rightY2ShowTitle", false)
                .put("rightY2Title", StringUtils.EMPTY)
                .put("rightY2TitleStyle", this.defaultFont())
                .put("rightY2Reverse", false)
                .put("rightY2ShowLabel", true)
                .put("rightY2LabelStyle", JSONObject.create().put("textStyle", this.defaultFont()))
                .put("rightY2LineColor", "#dddddd")
                .put("rightYLineColor", true);

        return settings;
    }

    //坐标轴标签的格式由千分符号，和数量级构成
    private String tickFormatter(int yAxis){

        boolean hasSeparator = true;
        try {
            JSONObject settings = this.getDetailChartSetting();

            if(yAxis == 0){
                hasSeparator = settings.optBoolean("leftYSeparator");
            }else if(yAxis == 1){
                hasSeparator = settings.optBoolean("rightYSeparator");
            }else if(yAxis == 2){
                hasSeparator = settings.optBoolean("rightY2Separator");
            }

        }catch (Exception e){
            BILoggerFactory.getLogger().error(e.getMessage(),e);
        }

        return String.format("function(){return BI.contentFormat(%s, \"%s\")}", labelString(yAxis), hasSeparator ? "#,###.##" : "#.##");
    }

    protected String labelString(int yAxis){
        return "arguments[0]";
    }

    //值标签和小数位数，千分富符，数量级和单位构成的后缀
    protected String valueFormat(BISummaryTarget dimension, boolean isTooltip){
        int yAxis = this.yAxisIndex(dimension.getId());

        boolean hasSeparator = true;
        String unit = StringUtils.EMPTY;

        try {
            JSONObject settings = this.getDetailChartSetting();

            if(yAxis == 0){
                hasSeparator = settings.optBoolean("leftYSeparator");
                unit = settings.optString("leftYUnit");
            }else if(yAxis == 1){
                hasSeparator = settings.optBoolean("rightYSeparator");
                unit = settings.optString("rightYUnit");
            }else if(yAxis == 2){
                hasSeparator = settings.optBoolean("rightY2Separator");
                unit = settings.optString("rightY2Unit");
            }

        }catch (Exception e){
            BILoggerFactory.getLogger().error(e.getMessage(),e);
        }

        String scaleUnit = this.scaleUnit(this.numberLevel(dimension.getId()));

        String format = this.decimalFormat(dimension, hasSeparator);
        if(isTooltip){
            format += (scaleUnit + unit);
        }

        return format;
    }

    //todo 坐标轴标题和数量级，单位构成的后缀
    protected String axisTitleUnit(int level, String unit){
        String scaleUnit = this.scaleUnit(level);
        return StringUtils.isBlank(scaleUnit) ? StringUtils.EMPTY : "(" + this.scaleUnit(level) + unit + ")";
    }

    protected String dataLabelValueFormat(BISummaryTarget dimension){
        return this.valueFormatFunc(dimension, false);
    }

    protected int numberLevel(String dimensionID){
        int level = BIReportConstant.TARGET_STYLE.NUM_LEVEL.NORMAL;
        try {
            JSONObject settings = this.getDetailChartSetting();
            int yAxis = this.yAxisIndex(dimensionID);

            String key = "leftYNumberLevel";
            if(yAxis == 1){
                key = "rightYNumberLevel";
            }else if(yAxis == 2){
                key = "rightY2NumberLevel";
            }

            level = settings.optInt(key, level);
        }catch (Exception e){
            BILoggerFactory.getLogger().error(e.getMessage(),e);
        }

        return level;
    }

    protected JSONArray addStackedEmptySeries(JSONArray series) throws JSONException{
        if(series.length() > 0){
            JSONObject ser1 = series.getJSONObject(0);

            ser1.put("stack", FALL_COLUMN);

            JSONObject ser0 = new JSONObject(ser1.toString());
            ser0.put("name", FALL_COLUMN).put("color", TRANS).put("borderColor", TRANS).put("borderWidth", 0)
                    .put("clickColor", TRANS).put("mouseOverColor", TRANS).put("tooltip", JSONObject.create().put("enabled", false))
                    .put("fillColor", TRANS).put("marker", JSONObject.create().put("enabled", false));

            double stackValue = 0;
            JSONArray data = ser0.optJSONArray("data");
            for(int i = 0, len = data.length(); i < len; i++){
                JSONObject dataPoint = data.getJSONObject(i);
                double y = dataPoint.optDouble("y");
                dataPoint.put("y", stackValue);
                stackValue += y;
            }
            series = JSONArray.create().put(ser0).put(ser1);
        }

        return series;
    }

    public JSONObject createPlotOptions(JSONObject globalSetting, JSONObject settings) throws Exception{

        JSONObject plotOptions = super.createPlotOptions(globalSetting, settings);

        plotOptions.put("inverted", this.isInverted());

        int lineType = settings.optInt("lineAreaChartType", NORMAL);
        if(lineType == STEP){
            plotOptions.put("step", true);
        }else if(lineType == CURVE){
            plotOptions.put("curve", true);
        }

        return plotOptions;
    }

    public boolean isInverted(){
        return false;
    }

    public  JSONObject createOptions(JSONObject globalStyle, JSONObject data) throws Exception{

        JSONObject settings = this.getDetailChartSetting();

        JSONObject options = super.createOptions(globalStyle, data);

        options.put("dataSheet", JSONObject.create().put("enabled", settings.optBoolean("showDataTable"))
                .put("style", this.defaultFont()).put("borderColor", "#000000").put("borderWidth", 1));

        if(settings.optBoolean("showZoom") && !settings.optBoolean("miniMode")){
            options.put("zoom", JSONObject.create().put("zoomTool", JSONObject.create().put("enabled", true)).put("zoomType", ""));
        }

        Calculator calculator = Calculator.createCalculator();
        String[] ids = this.getUsedTargetID();
        for(int i = 0, len = ids.length; i < len; i++){
            String id = ids[i];
            Double[] values = this.getValuesByID(id);
            if(values.length > 0){
                double min = Double.MAX_VALUE, max = -min, average = 0, count = values.length;
                for(int j = 0; j < count; j++){
                    double value = values[j].doubleValue();
                    min = Math.min(min, value);
                    max = Math.max(max, value);
                    average += value;
                }
                average /= count;

                //放参数
                HashMap map = new HashMap();
                map.put(String.format("%s0",id), max);
                map.put(String.format("%s1",id), min);
                map.put(String.format("%s2",id), average);

                ParameterMapNameSpace space = ParameterMapNameSpace.create(map);
                calculator.pushNameSpace(space);
            }
        }

        JSONArray cateArray = this.parseCategoryAxis(settings, calculator);
        JSONArray valueArray = this.parseValueAxis(settings, calculator);
        if(settings.optBoolean("miniMode", false)){
            checkMIniMode(cateArray, true);
            checkMIniMode(valueArray, false);
        }
        options.put(this.getCoordXKey(), cateArray);
        options.put(this.getCoordYKey(), valueArray);

        return options;
    }

    private void checkMIniMode(JSONArray array, boolean cate) throws JSONException{
        if(array == null){
            return;
        }
        for(int i = 0, len = array.length(); i < len; i++){
            JSONObject axis = array.optJSONObject(i);
            if(axis == null){
                continue;
            }
            axis.put("showLabel", cate).put("lineWidth", 0).put("gridLineWidth", 0).put("enableTick", false).put("title", JSONObject.create().put("enabled", false));
        }
    }

    protected String getCoordXKey(){
        return "xAxis";
    }

    protected String getCoordYKey(){
        return "yAxis";
    }

    protected JSONArray parseCategoryAxis(JSONObject settings, Calculator calculator) throws JSONException{
        JSONObject labelStyle = settings.optJSONObject("catLabelStyle");

        JSONObject category = JSONObject.create();
        boolean enabled = settings.optBoolean("catShowTitle");

        category
                .put("type", "category").put("position", "bottom")
                .put("title", JSONObject.create().put("style", settings.optJSONObject("catTitleStyle")).put("text", enabled ?settings.optString("catTitle") : StringUtils.EMPTY))
                .put("showLabel", settings.optBoolean("catShowLabel") && !settings.optBoolean("showDataTable"))
                .put("labelStyle", labelStyle.optJSONObject("textStyle"))
                .put("labelRotation", labelStyle.optInt("textDirection"))
                .put("lineColor", settings.optString("catLineColor"))
                .put("gridLineWidth", settings.optBoolean("vShowGridLine") ? 1 : 0)
                .put("gridLineColor", settings.optString("vGridLineColor"));

        return JSONArray.create().put(category);
    }

    protected JSONArray parseValueAxis(JSONObject settings, Calculator calculator) throws JSONException{

        JSONArray axis = JSONArray.create();

        axis.put(this.parseLeftValueAxis(settings, calculator));

        axis.put(this.parseRightValueAxis(settings, calculator));

        axis.put(this.parseThirdValueAxis(settings, calculator));

        return axis;
    }

    protected JSONObject parseLeftValueAxis(JSONObject settings, Calculator calculator) throws JSONException{

        JSONObject labelStyle = settings.optJSONObject("leftYLabelStyle");
        String axisTitle = this.axisTitleUnit(settings.optInt("leftYNumberLevel"), settings.optString("leftYUnit"));
        boolean enabled = settings.optBoolean("leftYShowTitle");

        JSONObject left = JSONObject.create()
                .put("type", "value")
                .put("title", JSONObject.create()
                        .put("style", settings.optJSONObject("leftYTitleStyle"))
                        .put("text", enabled ? settings.optString("leftYTitle") + axisTitle : axisTitle)
                        .put("rotation", VERTICAL)
                )
                .put("showLabel", settings.optBoolean("leftYShowLabel"))
                .put("formatter", this.tickFormatter(0))
                .put("labelStyle", labelStyle.optJSONObject("textStyle"))
                .put("labelRotation", labelStyle.optInt("textDirection"))
                .put("lineColor", settings.optString("leftYLineColor")).put("lineWidth", 1)
                .put("position", "left").put("reversed", settings.optBoolean("leftYReverse", false))
                .put("gridLineWidth", settings.optBoolean("hShowGridLine") ? 1 : 0)
                .put("gridLineColor", settings.optString("hGridLineColor"));

        if(settings.optBoolean("leftYShowCustomScale")){
            this.putMinMaxInterval(left, settings.optJSONObject("leftYCustomScale"), calculator);
        }

        return left;
    }

    protected JSONObject parseRightValueAxis(JSONObject settings, Calculator calculator) throws JSONException{

        String axisTitle = this.axisTitleUnit(settings.optInt("rightYNumberLevel"), settings.optString("rightYUnit"));
        boolean enabled = settings.optBoolean("rightYShowTitle");
        JSONObject labelStyle = settings.optJSONObject("rightYLabelStyle");
        JSONObject right = JSONObject.create()
                .put("type", "value")
                .put("title", JSONObject.create()
                        .put("style", settings.optJSONObject("rightYTitleStyle"))
                        .put("text", enabled ? settings.optString("rightYTitle") + axisTitle : axisTitle)
                        .put("rotation", VERTICAL)
                )
                .put("showLabel", settings.optBoolean("rightYShowLabel"))
                .put("formatter", this.tickFormatter(1))
                .put("labelStyle", labelStyle.optJSONObject("textStyle"))
                .put("labelRotation", labelStyle.optInt("textDirection"))
                .put("lineColor", settings.optString("rightYLineColor")).put("lineWidth", 1)
                .put("position", "right").put("reversed", settings.optBoolean("rightYReverse", false));

        if(settings.optBoolean("rightYShowCustomScale")){
            this.putMinMaxInterval(right, settings.optJSONObject("rightYCustomScale"), calculator);
        }

        return right;
    }

    private JSONObject parseThirdValueAxis(JSONObject settings, Calculator calculator) throws JSONException{
        String axisTitle = this.axisTitleUnit(settings.optInt("rightY2NumberLevel"), settings.optString("rightY2Unit"));
        boolean enabled = settings.optBoolean("rightY2ShowTitle");
        JSONObject labelStyle = settings.optJSONObject("rightY2LabelStyle");
        JSONObject right2 = JSONObject.create()
                .put("type", "value")
                .put("title", JSONObject.create()
                        .put("style", settings.optJSONObject("rightY2TitleStyle"))
                        .put("text", enabled ? settings.optString("rightY2Title") + axisTitle : axisTitle)
                        .put("rotation", VERTICAL)
                )
                .put("showLabel", settings.optBoolean("rightY2ShowLabel"))
                .put("formatter", this.tickFormatter(2))
                .put("labelStyle", labelStyle.optJSONObject("textStyle"))
                .put("labelRotation", labelStyle.optInt("textDirection"))
                .put("lineColor", settings.optString("rightY2LineColor")).put("lineWidth", 1)
                .put("position", "right").put("reversed", settings.optBoolean("rightY2Reverse", false));

        if(settings.optBoolean("rightY2ShowCustomScale")){
            this.putMinMaxInterval(right2, settings.optJSONObject("rightY2CustomScale"), calculator);
        }

        return right2;
    }

    private void putMinMaxInterval(JSONObject axis, JSONObject scale, Calculator calculator) throws JSONException{

        String min = StringUtils.EMPTY, max = StringUtils.EMPTY, interval = StringUtils.EMPTY;
        if(scale.has("minScale")) {
            min = scale.optJSONObject("minScale").optString("formula");
        }
        if(scale.has("maxScale")) {
            max = scale.optJSONObject("maxScale").optString("formula");
        }
        if(scale.has("interval")) {
            interval = scale.optJSONObject("interval").optString("formula");
        }

        if(StringUtils.isNotBlank(min)){
            axis.put("min", this.niceAxisValue(min, calculator));
        }

        if(StringUtils.isNotBlank(max)){
            axis.put("max", this.niceAxisValue(max, calculator));
        }

        if(StringUtils.isNotBlank(interval)){
            axis.put("tickInterval", this.niceAxisValue(interval, calculator));
        }
    }

    //有参数的时候，需要计算出一个相对nice的值
    //没有的时候直接返回计算结果
    private double niceAxisValue(String formula, Calculator calculator){
        double value = 0;
        try {
            formula = formula.replaceAll("[{}]", StringUtils.EMPTY);
            value = Double.parseDouble(calculator.eval(new Formula(formula)).toString());

        }catch (Exception e){
            BILoggerFactory.getLogger().error(e.getMessage(),e);
        }

        return value;
    }

}
