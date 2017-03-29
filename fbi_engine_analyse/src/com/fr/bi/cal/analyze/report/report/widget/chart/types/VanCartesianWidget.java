package com.fr.bi.cal.analyze.report.report.widget.chart.types;

import com.fr.bi.cal.analyze.report.report.widget.VanChartWidget;
import com.fr.bi.conf.session.BISessionProvider;
import com.fr.json.JSONArray;
import com.fr.json.JSONException;
import com.fr.json.JSONObject;
import com.fr.stable.StringUtils;


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

    protected JSONObject populateDefaultSettings() throws JSONException{
        JSONObject settings = super.populateDefaultSettings();

        //分类轴
        settings.put("catShowTitle", false)
                .put("catTitle", StringUtils.EMPTY)
                .put("catTitleStyle", this.defaultFont())
                .put("catShowLabel", true)
                .put("catLabelStyle", JSONObject.create().put("textStyle", this.defaultFont()))
                .put("catLineColor", "#dddddd");

        //左值轴
        settings.put("leftYUnit", StringUtils.EMPTY)
                .put("leftYShowTitle", false)
                .put("leftYTitle", StringUtils.EMPTY)
                .put("leftYTitleStyle", this.defaultFont())
                .put("leftYReverse", false)
                .put("leftYShowLabel", true)
                .put("leftYLabelStyle", JSONObject.create().put("textStyle", this.defaultFont()))
                .put("leftYLineColor", "#dddddd")
                .put("leftYSeparator", true);

        //右值轴
        settings.put("rightYUnit", StringUtils.EMPTY)
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

    public JSONArray createSeries(JSONObject data) throws JSONException {
        return this.createXYSeries(data);
    }

    protected JSONArray createStackedEmptySeries(JSONObject originData) throws JSONException{
        JSONArray series = this.createSeries(originData);

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

    public JSONObject createPlotOptions(BISessionProvider session) throws Exception{

        JSONObject settings = this.getDetailChartSetting();

        JSONObject plotOptions = super.createPlotOptions(session);

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

    public  JSONObject createOptions(BISessionProvider session) throws Exception{

        JSONObject settings = this.getDetailChartSetting();

        JSONObject options = super.createOptions(session);

        options.put("dataSheet", JSONObject.create().put("enabled", settings.optBoolean("showDataTable")));

        if(options.optBoolean("showZoom")){
            options.put("zoom", JSONObject.create().put("zoomTool", JSONObject.create().put("enabled", true)));
        }

        options.put("xAxis", this.parseCategoryAxis(settings));
        options.put("yAxis", this.parseValueAxis(settings));

        return options;
    }

    protected JSONArray parseCategoryAxis(JSONObject settings) throws JSONException{

        JSONObject category = JSONObject.create();

        category
                .put("type", "category")
                .put("title", JSONObject.create().put("enabled", settings.optJSONObject("catShowTitle")).put("style", settings.optJSONObject("catTitleStyle")).put("text", settings.optString("catTitle")))
                .put("showLabel", settings.optBoolean("catShowLabel"))
                .put("labelStyle", settings.optJSONObject("catLabelStyle"))
                .put("lineColor", settings.optString("catLineColor"));

        return JSONArray.create().put(category);
    }

    protected JSONArray parseValueAxis(JSONObject settings) throws JSONException{

        JSONArray axis = JSONArray.create();
        JSONObject labelStyle = settings.optJSONObject("leftYLabelStyle");

        JSONObject left = JSONObject.create()
                .put("type", "value")
                .put("title", JSONObject.create().put("enabled", settings.optJSONObject("leftYShowTitle")).put("style", settings.optJSONObject("leftYTitleStyle")).put("text", settings.optString("leftYTitle")))
                .put("showLabel", settings.optBoolean("leftYShowLabel"))
                .put("labelStyle", labelStyle.optJSONObject("textStyle"))
                .put("lineColor", settings.optString("leftYLineColor"))
                .put("position", "bottom");

        labelStyle = settings.optJSONObject("rightYLabelStyle");
        JSONObject right = JSONObject.create()
                .put("type", "value")
                .put("title", JSONObject.create().put("enabled", settings.optJSONObject("rightYShowTitle")).put("style", settings.optJSONObject("rightYTitleStyle")).put("text", settings.optString("rightYTitle")))
                .put("showLabel", settings.optBoolean("rightYShowLabel"))
                .put("labelStyle", labelStyle.optJSONObject("textStyle"))
                .put("lineColor", settings.optString("rightYLineColor"))
                .put("position", "left");

        axis.put(left);
        axis.put(right);

        if(settings.has("rightY2LineColor")){
            labelStyle = settings.optJSONObject("rightY2LabelStyle");
            JSONObject right2 = JSONObject.create()
                    .put("type", "value")
                    .put("title", JSONObject.create().put("enabled", settings.optJSONObject("rightY2ShowTitle")).put("style", settings.optJSONObject("rightY2TitleStyle")).put("text", settings.optString("rightY2Title")))
                    .put("showLabel", settings.optBoolean("rightY2ShowLabel"))
                    .put("labelStyle", labelStyle.optJSONObject("textStyle"))
                    .put("lineColor", settings.optString("rightY2LineColor"))
                    .put("position", "right");

            axis.put(right2);
        }

        return axis;
    }

}
