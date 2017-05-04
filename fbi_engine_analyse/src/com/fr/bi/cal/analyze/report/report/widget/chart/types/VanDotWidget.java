package com.fr.bi.cal.analyze.report.report.widget.chart.types;

import com.finebi.cube.common.log.BILoggerFactory;
import com.fr.bi.conf.report.WidgetType;
import com.fr.general.FRLogger;
import com.fr.json.JSONArray;
import com.fr.json.JSONException;
import com.fr.json.JSONObject;
import com.fr.script.Calculator;
import com.fr.stable.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

/**
 * Created by eason on 2017/2/27.
 */
public class VanDotWidget extends VanCartesianWidget{

    private static final int BUBBLE = 1;
    private static final int SCATTER = 2;

    private static final String TARGET = "50000";

    //显示颜色的规则
    private static final int SERIES_RULE = 1;
    private static final int INTERVAL_RULE = 2;
    private static final int GRADUAL_RULE = 3;

    //气泡图和散点图的指标个数
    private static final int BUBBLE_COUNT = 3;
    private static final int SCATTER_COUNT = 2;

    private static final int NO_SHADOW =  16;  //没有投影的气泡
    private static final int SHADOW =  17;       //有投影的气泡

    //点的样式
    private static final int SQUARE = 1;
    private static final int TRIANGLE = 2;

    //气泡的大小
    private static final int MIN_SIZE = 15;
    private static final int MAX_SIZE = 80;

    //值区间的默认颜色
    private static final String[] INTERVAL_COLORS = new String[]{"#65B3EE", "#95E1AA", "#F8D08E"};
    private static final int INTERVAL = 100;

    private static final int BUBBLE_DIMENSION = 3;

    protected JSONObject populateDefaultSettings() throws JSONException {
        JSONObject settings = super.populateDefaultSettings();

        settings.put("displayRules", SERIES_RULE);
        settings.put("bubbleStyle", NO_SHADOW);
        settings.put("dotStyle", SQUARE);

        settings.put("bubbleSizeFrom", MIN_SIZE);
        settings.put("bubbleSizeTo", MAX_SIZE);

        JSONArray fixedStyle = JSONArray.create();
        for(int i = 0, len = INTERVAL_COLORS.length; i < len; i++){
            JSONObject range = JSONObject.create();
            range.put("min", i * INTERVAL).put("max", (i + 1) * INTERVAL);
            fixedStyle.put(JSONObject.create().put("color", INTERVAL_COLORS[i]).put("range", range));
        }

        JSONObject gradualStyle = JSONObject.create();
        gradualStyle.put("range", JSONObject.create().put("min", 0).put("max", 100));
        gradualStyle.put("color_range", JSONObject.create().put("from_color", "#65B3EE").put("to_color", "#95E1AA"));

        settings.put("fixedStyle", fixedStyle);
        settings.put("gradientStyle", JSONArray.create().put(gradualStyle));

        return settings;
    }

    public JSONObject createPlotOptions(JSONObject globalStyle, JSONObject settings) throws Exception{
        JSONObject plotOptions = super.createPlotOptions(globalStyle, settings);

        plotOptions.put("sizeBy", "width");
        plotOptions.put("minSize", settings.optInt("bubbleSizeFrom"));
        plotOptions.put("maxSize", settings.optInt("bubbleSizeTo"));

        plotOptions.put("shadow", settings.optInt("bubbleStyle") == SHADOW);

        return plotOptions;
    }

    protected JSONObject parseLegend(JSONObject settings) throws JSONException{

        JSONObject legend = super.parseLegend(settings);

        int rule = settings.optInt("displayRules");
        if(rule == INTERVAL_RULE){
            legend.put("continuous", false);
            legend.put("range", this.mapStyleToRange(settings.optJSONArray("fixedStyle")));
        }else if(rule == GRADUAL_RULE){
            legend.put("continuous", true);
            legend.put("range", this.gradualStyleToRange(settings.optJSONArray("gradientStyle")));
        }

        return legend;
    }


    private JSONObject gradualStyleToRange(JSONArray style) throws JSONException{
        JSONArray colors = JSONArray.create();

        int count = style.length();
        double max = style.getJSONObject(count - 1).optJSONObject("range").optDouble("max");

        for(int i = 0, len = style.length(); i < len; i++){
            JSONObject config = style.getJSONObject(i);
            JSONObject range = config.optJSONObject("range"), colorRange = config.optJSONObject("color_range");
            double from = range.optDouble("min") / max;
            colors.put(JSONArray.create().put(from).put(colorRange.optString("from_color")));
        }

        return JSONObject.create().put("color", colors);
    }

    protected String valueLabelKey() {
        return "{SIZE}";
    }

    protected String getLegendType(){

        String legend = "legend";

        try {
            JSONObject settings = this.getDetailChartSetting();
            int rule = settings.optInt("displayRules");

            if(rule != SERIES_RULE){
                legend = "rangeLegend";
            }

        }catch (JSONException e){
            BILoggerFactory.getLogger().error(e.getMessage(), e);
        }


        return legend;
    }

    public JSONArray createSeries(JSONObject originData) throws Exception{
        WidgetType chartType = this.getChartType();
        if(chartType == WidgetType.BUBBLE || chartType == WidgetType.SCATTER || this.getSeriesDimension() == null){
            return this.createBubbleScatterSeries(originData);
        }
        JSONArray series = JSONArray.create();
        String[] ids = this.getUsedTargetID();
        if(ids.length < SCATTER_COUNT){
            return series;
        }
        double yScale = this.numberScale(ids[0]), xScale = this.numberScale(ids[1]);
        double sizeScale = ids.length > 2 ? this.numberScaleByLevel(this.numberLevelFromSettings(ids[2])) : 1;
        HashMap<String, ArrayList<JSONArray>> seriesMap = new HashMap<String, ArrayList<JSONArray>>();
        Iterator iterator = originData.keys();
        while (iterator.hasNext()) {
            String key = iterator.next().toString();
            JSONArray children = originData.optJSONObject(key).optJSONArray("c");
            for(int i = 0, len = children.length(); i < len; i++){
                JSONObject obj = children.getJSONObject(i);
                String seriesName = obj.optString("n");
                ArrayList<JSONArray> seriesArray = seriesMap.containsKey(seriesName) ? seriesMap.get(seriesName) : new ArrayList<JSONArray>();
                seriesMap.put(seriesName, seriesArray);
                seriesArray.add(obj.optJSONArray("c"));
            }
        }
        iterator = seriesMap.keySet().iterator();
        while (iterator.hasNext()){
            String seriesName = iterator.next().toString();
            ArrayList<JSONArray> seriesArray = seriesMap.get(seriesName);
            JSONObject ser = JSONObject.create();
            JSONArray data = JSONArray.create();
            for(int i = 0, size = seriesArray.size(); i < size; i++){
                JSONArray dataArray = seriesArray.get(i);
                for(int j = 0, count = dataArray.length(); j < count; j++){
                    JSONObject obj = dataArray.optJSONObject(j);
                    JSONArray dimensions = obj.optJSONArray("s");

                    double y = dimensions.isNull(0) ? 0 : dimensions.optDouble(0);
                    double x = dimensions.isNull(1) ? 0 : dimensions.optDouble(1);
                    double value = (dimensions.length() > 2 && !dimensions.isNull(2)) ? dimensions.optDouble(2) : 0;

                    data.put(JSONObject.create().put("x", x/xScale).put("y", y/yScale).put("size", value/sizeScale));
                }
            }

            ser.put("data", data).put("name", seriesName).put("dimensionID", ids[ids.length - 1]);
            series.put(ser);
        }
        return series;
    }


    private JSONArray createBubbleScatterSeries(JSONObject originData) throws Exception{
        String[] ids = this.getUsedTargetID();

        JSONArray series = JSONArray.create();

        JSONArray c = originData.optJSONArray("c");

        if(c == null){
            return series;
        }

        for(int i = 0, len = c.length(); i < len; i++){
            JSONObject obj = c.getJSONObject(i);

            JSONArray dimensions = obj.optJSONArray("s");

            double y = dimensions.isNull(0) ? 0 : dimensions.optDouble(0);
            double x = dimensions.isNull(1) ? 0 : dimensions.optDouble(1);
            double value = (dimensions.length() > 2 && !dimensions.isNull(2)) ? dimensions.optDouble(2) : 0;

            JSONObject point = JSONObject.create().put("x", x).put("y", y).put("size", value);

            series.put(JSONObject.create().put("data", JSONArray.create().put(point))
                    .put("name", obj.optString("n")).put("dimensionID", ids[ids.length - 1])
            );
        }

        return series;
    }

    protected JSONArray parseCategoryAxis(JSONObject settings, Calculator calculator) throws JSONException{

        JSONObject baseAxis = this.parseRightValueAxis(settings, calculator).put("position", "bottom").put("type", "value");

        return JSONArray.create().put(baseAxis);
    }

    protected JSONArray parseValueAxis(JSONObject settings, Calculator calculator) throws JSONException{

        return JSONArray.create().put(this.parseLeftValueAxis(settings, calculator));
    }

    public String getSeriesType(String dimensionID){

        WidgetType chartType = this.getChartType();
        if(chartType == WidgetType.DOT){
            JSONObject scopes = this.getChartSetting().getScopes();

            int type = BUBBLE;
            try {
                if(scopes.has(TARGET)){
                    type = scopes.getJSONObject(TARGET).optInt("valueType", BUBBLE);
                }
            }catch (Exception e){
                FRLogger.getLogger().error(e.getMessage(), e);
            }

            int idCount = this.getUsedTargetID().length;

            return (idCount == BUBBLE_DIMENSION && type == BUBBLE ) ? "bubble" : "scatter";

        }else{

            return chartType == WidgetType.BUBBLE ? "bubble" : "scatter";

        }


    }

    protected String getTooltipIdentifier(){
        return X + Y + SIZE;
    }

    protected void formatSeriesDataLabelFormat(JSONObject options) throws Exception {
        JSONObject dataLabels = options.optJSONObject("plotOptions").optJSONObject("dataLabels");

        String[] ids = this.getUsedTargetID();
        String[] keys = {"sizeFormat", "YFormat", "XFormat"};
        int size = ids.length;

        if (dataLabels.optBoolean("enabled")) {
            JSONArray series = options.optJSONArray("series");

            for (int i = 0, len = series.length(); i < len; i++) {
                JSONObject ser = series.getJSONObject(i);

                JSONObject labels = new JSONObject(dataLabels.toString());

                JSONObject formatter = labels.optJSONObject("formatter");

                for(int j = size; j > 0; j--){
                    formatter.put(keys[size - j], this.dataLabelValueFormat(this.getBITargetByID(ids[j - 1])));
                }

                ser.put("dataLabels", labels);
            }
        }
    }
}
