package com.fr.bi.cal.analyze.report.report.widget;

import com.finebi.cube.common.log.BILoggerFactory;
import com.fr.bi.cal.analyze.session.BISession;
import com.fr.bi.conf.report.widget.field.dimension.BIDimension;
import com.fr.bi.conf.session.BISessionProvider;
import com.fr.bi.field.target.target.BINumberTarget;
import com.fr.bi.stable.constant.BIReportConstant;
import com.fr.bi.tool.BIReadReportUtils;
import com.fr.bi.util.BIConfUtils;
import com.fr.general.ComparatorUtils;
import com.fr.general.Inter;
import com.fr.json.JSONArray;
import com.fr.json.JSONException;
import com.fr.json.JSONObject;
import com.fr.stable.StringUtils;
import com.fr.web.core.SessionDealWith;

import java.awt.*;
import java.util.*;
import java.util.Iterator;
import java.util.List;

/**
 * Created by User on 2016/4/25.
 */
public abstract class VanChartWidget extends TableWidget {

    private static final double RED_DET = 0.299;
    private static final double GREEN_DET = 0.587;
    private static final double BLUE_DET = 0.114;
    private static final double GRAY = 192;

    //兼容前台用数字表示位置的写法，真xx丑
    private static final int TOP = 2;
    private static final int RIGHT = 3;
    private static final int BOTTOM = 4;
    private static final int LEFT = 5;

    //标签位置
    private static final int POSITION_INNER = 1;
    private static final int POSITION_OUTER = 2;
    private static final int POSITION_CENTER = 3;

    //气泡图和散点图的指标个数
    private static final int BUBBLE_COUNT = 3;
    private static final int SCATTER_COUNT = 2;

    private static final int TARGET = 30000;
    private static final int TARGET_BASE = 10000;

    private static final String PERCENT_SYMBOL = "%";
    private static final String WHITE = "#ffffff";

    private static final int STYLE_NORMAL = 1; //普通风格
    private static final int STYLE_GRADUAL = 2; //渐变风格


    private HashMap<String, JSONArray> dimensionIdMap = new HashMap<String, JSONArray>();
    private HashMap<String, String> regionIdMap = new HashMap<String, String>();

    public abstract JSONArray createSeries(JSONObject data) throws JSONException;

    public abstract String getSeriesType(String dimensionID);

    protected boolean isStacked(String dimensionID){
        return false;
    }

    protected String getStackedKey(String dimensionID){
        return dimensionID;
    }

    protected int yAxisIndex(String dimensionID){
        int regionID = Integer.parseInt(this.getRegionID(dimensionID));

        return (regionID - TARGET) / TARGET_BASE;
    }

    protected double numberScale(String dimensionID){

        int level = this.numberLevel(dimensionID);
        double scale = 1.0;

        if(level == BIReportConstant.TARGET_STYLE.NUM_LEVEL.TEN_THOUSAND){
            scale = Math.pow(10, 4);
        }else if(level == BIReportConstant.TARGET_STYLE.NUM_LEVEL.MILLION){
            scale = Math.pow(10, 6);
        }else if(level == BIReportConstant.TARGET_STYLE.NUM_LEVEL.YI){
            scale = Math.pow(10, 8);
        }else if(level == BIReportConstant.TARGET_STYLE.NUM_LEVEL.PERCENT){
            scale = 0.01;
        }

        return scale;
    }


    protected String scaleUnit(int level){
        String unit = StringUtils.EMPTY;

        if(level == BIReportConstant.TARGET_STYLE.NUM_LEVEL.TEN_THOUSAND){

            unit = Inter.getLocText("BI-Basic_Wan");

        }else if(level == BIReportConstant.TARGET_STYLE.NUM_LEVEL.MILLION){

            unit = Inter.getLocText("BI-Basic_Million");

        }else if(level == BIReportConstant.TARGET_STYLE.NUM_LEVEL.YI){

            unit = Inter.getLocText("BI-Basci_Yi");

        }else if(level == BIReportConstant.TARGET_STYLE.NUM_LEVEL.PERCENT){

            unit = PERCENT_SYMBOL;

        }

        return unit;
    }

    protected int numberLevel(String dimensionID){
        try {
            BINumberTarget  target = ((BINumberTarget) this.getBITargetAndDimension(dimensionID));

            return target.getChartSetting().getSettings().optInt("numLevel", BIReportConstant.TARGET_STYLE.NUM_LEVEL.NORMAL);
        }catch (Exception e){
            BILoggerFactory.getLogger().error(e.getMessage(),e);
        }

        return BIReportConstant.TARGET_STYLE.NUM_LEVEL.NORMAL;
    }

    protected JSONArray getDimensionIDArray(String regionID){
        return dimensionIdMap.get(regionID);
    }

    protected String getRegionID(String dimensionID){
        return regionIdMap.get(dimensionID);
    }

    public void parseJSON(JSONObject jo, long userId) throws Exception {
        if (jo.has("view")) {
            JSONObject vjo = jo.optJSONObject("view");

            JSONArray ja = JSONArray.create();
            Iterator it = vjo.keys();
            List<String> sorted = new ArrayList<String>();
            while (it.hasNext()) {
                sorted.add(it.next().toString());
            }
            Collections.sort(sorted, new Comparator<String>() {
                @Override
                public int compare(String o1, String o2) {
                    return Integer.parseInt(o1) - Integer.parseInt(o2);
                }
            });

            for(String region : sorted){

                if(ComparatorUtils.equals(region, BIReportConstant.REGION.DIMENSION1) ||
                        ComparatorUtils.equals(region, BIReportConstant.REGION.DIMENSION2)){
                    continue;
                }

                JSONArray tmp =  vjo.getJSONArray(region);

                dimensionIdMap.put(region, tmp);//后面用来计算坐标轴和堆积属性

                for(int j = 0; j < tmp.length(); j++){
                    String key = tmp.getString(j);
                    ja.put(key);
                    regionIdMap.put(key, region);
                }

                vjo.remove(region);
            }

            vjo.put(BIReportConstant.REGION.TARGET1, ja);
        }

        super.parseJSON(jo, userId);
    }

    public JSONObject createPlotOptions(JSONObject globalStyle, JSONObject settings) throws Exception{

        JSONObject plotOptions = JSONObject.create();

        plotOptions.put("animation", true);

        //tooltip的默认配置
        JSONObject tooltip = JSONObject.create();

        String widgetBg = "#ffffff";
        if(globalStyle.has("widgetBackground")){
            widgetBg = globalStyle.optJSONObject("widgetBackground").optString("value");
            widgetBg = StringUtils.isBlank(widgetBg) ? WHITE : widgetBg;
        }

        tooltip.put("padding", 10).put("backgroundColor", widgetBg).put("borderRadius", 2).put("shadow", true)
                .put("style", JSONObject.create()
                        .put("color", this.isDarkColor(widgetBg) ? "#FFFFFF" : "#1A1A1A")
                        .put("fontSize", "14px").put("fontFamily", "Verdana"));

        plotOptions.put("tooltip", tooltip);


        plotOptions.put("dataLabels", this.createDataLabels(settings));

        return plotOptions;
    }

    //默认是分类，系列，值的配置
    protected JSONObject createDataLabels(JSONObject settings) throws JSONException{

        boolean showDataLabel = settings.optBoolean("showDataLabel", false);

        JSONObject dataLabels = JSONObject.create().put("enabled", showDataLabel);

        if(showDataLabel){

            JSONObject dataLabelSetting = settings.has("dataLabelSetting") ? settings.optJSONObject("dataLabelSetting") : this.defaultDataLabelSetting();

            JSONObject formatter = JSONObject.create();
            String identifier = "";

            if(dataLabelSetting.optBoolean("showCategoryName")){
                identifier += "${CATEGORY}";
            }

            if(dataLabelSetting.optBoolean("showSeriesName")){
                identifier += "${SERIES}";
            }

            if(dataLabelSetting.optBoolean("showValue")){
                identifier += "${VALUE}";
            }

            if(dataLabels.optBoolean("showPercentage")){
                identifier += "${PERCENT}";
            }

            formatter.put("identifier", identifier).put("style", dataLabelSetting.optJSONObject("textStyle"));
        }

        return dataLabels;
    }

    private JSONObject defaultDataLabelSetting() throws JSONException{

        return JSONObject.create().put("showCategoryName", true)
                .put("showSeriesName", true).put("showValue", true).put("showPercentage", false)
                .put("position", POSITION_OUTER).put("textStyle", defaultFont());

    }

    private boolean isDarkColor(String colorStr){

        colorStr = colorStr.substring(1);

        Color color =  new Color(Integer.parseInt(colorStr, 16));

        return color.getRed() * RED_DET + color.getGreen() * GREEN_DET + color.getBlue() * BLUE_DET < GRAY;
    }

    protected JSONObject populateDefaultSettings() throws JSONException{
        JSONObject settings = JSONObject.create();

        //图例
        settings.put("legend", BOTTOM)
                .put("legendStyle", this.defaultFont());

        return settings;
    }

    protected JSONObject defaultFont() throws JSONException{

        //todo 这边的字体要全局取一下
        return JSONObject.create()
                .put("fontFamily", "Microsoft YaHei")
                .put("color", "rgb(178, 178, 178)")
                .put("fontSize", "12px");

    }

    //todo 不知道有没有实现过，先撸一下
    private JSONObject merge(JSONObject target, JSONObject source) throws JSONException{
        Iterator it = source.keys();
        while(it.hasNext()){
            String key = it.next().toString();
            if(!target.has(key)){
                target.put(key, source.get(key));
            }
        }
        return target;
    }

    protected JSONObject getDetailChartSetting() throws JSONException{
        JSONObject settings = this.getChartSetting().getDetailChartSetting();

        return merge(settings, this.populateDefaultSettings());
    }

    public JSONObject createDataJSON(BISessionProvider session) throws Exception {

        JSONObject data = super.createDataJSON(session).getJSONObject("data");

        JSONObject reportSetting = BIReadReportUtils.getInstance().getBIReportNodeJSON(((BISession) session).getReportNode());
        JSONObject globalStyle = reportSetting.optJSONObject("globalStyle");

        return this.createOptions(globalStyle, data);
    }

    public JSONObject createOptions(JSONObject globalStyle, JSONObject data) throws Exception{
        JSONObject options = JSONObject.create();
        JSONObject settings = this.getDetailChartSetting();
        JSONObject plateConfig = BIConfUtils.getPlateConfig();

        options.put("chartType", this.getSeriesType(StringUtils.EMPTY));

        options.put("colors", this.parseColors(settings, globalStyle, plateConfig));

        options.put("style", this.parseStyle(settings, globalStyle, plateConfig));

        options.put("legend", this.parseLegend(settings));

        options.put("plotOptions", this.createPlotOptions(globalStyle, settings));

        options.put("series",  this.createSeries(data));

        //处理格式的问题
        this.formatSeriesTooltipFormat(options);

        this.formatSeriesDataLabelFormat(options);

        return options;
    }

    private JSONArray parseColors(JSONObject settings, JSONObject globalStyle, JSONObject plateConfig) throws Exception{

        if(settings.has("chartColor")){
            return settings.getJSONArray("chartColor");
        } else if (globalStyle.has("chartColor")) {
            return settings.getJSONArray("chartColor");
        }else if(plateConfig.has("defaultColor")){
            String key = plateConfig.optString("defaultColor");
            JSONArray styleList = plateConfig.optJSONArray("styleList");
            for(int i = 0, len = styleList.length(); i < len; i++){
                JSONObject predefinedStyle = styleList.getJSONObject(i);
                if(key == predefinedStyle.optString("value")){
                    return predefinedStyle.optJSONArray("colors");
                }
            }
        }

        return JSONArray.create().put("#5caae4").put("#70cc7f").put( "#ebbb67").put("#e97e7b").put("#6ed3c9");
    }

    private String parseStyle(JSONObject settings, JSONObject globalStyle, JSONObject plateConfig) throws JSONException{
        int style = STYLE_NORMAL;
        if(settings.has("chartStyle")){
            style = settings.optInt("chartStyle");
        }else if(globalStyle.has("chartStyle")){
            style = globalStyle.optInt("chartStyle");
        }else if(plateConfig.has("chartStyle")){
            style = plateConfig.optInt("chartStyle");
        }
        return style == STYLE_GRADUAL ? "gradual" : "normal";
    }

    protected String tooltipValueFormat(BINumberTarget dimension){
        return this.valueFormat(dimension, true);
    }

    protected String dataLabelValueFormat(BINumberTarget dimension){
        return this.valueFormat(dimension, true);
    }

    protected String decimalFormat(BINumberTarget dimension, boolean hasSeparator){
        JSONObject settings = dimension.getChartSetting().getSettings();
        int type = settings.optInt("format", 0);
        String format;
        switch (type) {
            case BIReportConstant.TARGET_STYLE.FORMAT.NORMAL:
                format = hasSeparator ? "#,###.##" : "#.##";
                break;
            case BIReportConstant.TARGET_STYLE.FORMAT.ZERO2POINT:
                format = hasSeparator ? "#,###" : "#0";
                break;
            default:
                format = hasSeparator ? "#,###." : "#0.";
                for(int i = 0; i < type; i++){
                    format += "0";
                }
        }

        return format;
    }

    //值标签和小数位数，千分富符，数量级和单位构成的后缀
    protected String valueFormat(BINumberTarget dimension, boolean isTooltip) {

        JSONObject settings = dimension.getChartSetting().getSettings();

        boolean hasSeparator = settings.optBoolean("numSeparators", true);

        String format = this.decimalFormat(dimension, hasSeparator);

        String scaleUnit = this.scaleUnit(this.numberLevel(dimension.getId()));

        String unit = settings.optString("unit", StringUtils.EMPTY);

        if(isTooltip){
            format += (scaleUnit + unit);
        }

        return String.format("function(){FR.contentFormat(arguments[0], %s)}", format);
    }

    private void formatSeriesTooltipFormat(JSONObject options) throws Exception{

        JSONObject tooltip = options.optJSONObject("plotOptions").optJSONObject("tooltip");

        JSONArray series = options.optJSONArray("series");

        for(int i = 0, len = series.length(); i < len; i++){
            JSONObject ser = series.getJSONObject(i);
            String dimensionID = ser.optString("dimensionID");

            ser.put("tooltip", new JSONObject(tooltip.toString())
                    .put("valueFormat", this.tooltipValueFormat((BINumberTarget) this.getBITargetAndDimension(dimensionID))));
        }
    }

    private void formatSeriesDataLabelFormat(JSONObject options) throws Exception{
        JSONObject dataLabels = options.optJSONObject("plotOptions").optJSONObject("dataLabels");

        if(dataLabels.optBoolean("enabled")){
            JSONArray series = options.optJSONArray("series");

            for(int i = 0, len = series.length(); i < len; i++){
                JSONObject ser = series.getJSONObject(i);
                String dimensionID = ser.optString("dimensionID");

                ser.put("dataLabels", new JSONObject(dataLabels.toString())
                        .put("valueFormat", this.dataLabelValueFormat((BINumberTarget) this.getBITargetAndDimension(dimensionID))));
            }
        }
    }

    protected String categoryKey(){
        return "x";
    }

    protected String valueKey(){
        return "y";
    }

    protected JSONArray createXYSeries(JSONObject originData) throws JSONException{
        JSONArray series = JSONArray.create();
        String[] targetIDs = this.getUsedTargetID();
        String categoryKey = this.categoryKey(), valueKey = this.valueKey();
        if (originData.has("t")) {//有列表头，多系列
            JSONObject top = originData.getJSONObject("t"), left = originData.getJSONObject("l");
            JSONArray topC = top.getJSONArray("c"), leftC = left.getJSONArray("c");
            boolean isStacked = this.isStacked(targetIDs[0]);
            double numberScale = this.numberScale(targetIDs[0]);
            for (int i = 0; i < topC.length(); i++) {
                JSONObject tObj = topC.getJSONObject(i);
                String name = tObj.getString("n");
                JSONArray data = JSONArray.create();
                for (int j = 0; j < leftC.length(); j++) {
                    JSONObject lObj = leftC.getJSONObject(j);
                    String x = lObj.getString("n");
                    double y = lObj.getJSONObject("s").getJSONArray("c").getJSONObject(i).getJSONArray("s").getDouble(0);
                    data.put(JSONObject.create().put(categoryKey, x).put(valueKey, y / numberScale));
                }
                JSONObject ser = JSONObject.create().put("data", data).put("name", name)
                        .put("type", this.getSeriesType(targetIDs[0])).put("dimensionID", targetIDs[0]);
                if(isStacked){
                    ser.put("stacked", targetIDs[0]);
                }
                series.put(ser);
            }
        }else if(originData.has("c")){
            JSONArray children = originData.getJSONArray("c");
            for(int i = 0, len = targetIDs.length; i < len; i++){
                String id = targetIDs[i], type = this.getSeriesType(id), stackedKey = this.getStackedKey(id);
                int yAxis = this.yAxisIndex(id);
                double numberScale = this.numberScale(id);
                JSONArray data = JSONArray.create();
                for (int j = 0, count = children.length(); j < count; j++) {
                    JSONObject lObj = children.getJSONObject(j);
                    String x = lObj.getString("n");
                    double y = lObj.getJSONArray("s").getDouble(i) / numberScale;
                    data.put(JSONObject.create().put(categoryKey, x).put(valueKey, y));
                }
                JSONObject ser = JSONObject.create().put("data", data).put("name", id)
                        .put("type", type).put("yAxis", yAxis).put("dimensionID", id);
                if(this.isStacked(id)){
                    ser.put("stacked", stackedKey);
                }
                series.put(ser);
            }
        }

        return series;
    }

    protected JSONArray createBubbleSeries(JSONObject originData) throws JSONException{
        JSONArray series = JSONArray.create();
        String type = this.getSeriesType(StringUtils.EMPTY);
        int targetCount = type == "bubble" ? BUBBLE_COUNT : SCATTER_COUNT;

        JSONArray children = originData.optJSONArray("c");

        for(int i = 0, len = children.length(); i < len; i++){

            JSONObject obj = children.optJSONObject(i);
            JSONArray data = obj.optJSONArray("s");
            int dataLen = data.length();

            if(dataLen < targetCount){
                continue;
            }

            double x = data.optDouble(0), y = data.optDouble(1), size = data.optDouble(2, y);

            series.put(
                JSONObject.create()
                        .put("type", type)
                        .put("name",  obj.optString("n"))
                        .put("data", JSONArray.create().put(JSONObject.create().put("x", x).put("y", y).put("size", size)))
            );
        }

        return series;
    }

    protected JSONObject parseLegend(JSONObject settings) throws JSONException{

        int legend = settings.optInt("legend");
        String position = "top";

        if(legend == RIGHT){
            position = "right";
        }else if(legend == BOTTOM){
            position = "bottom";
        }else if(legend == LEFT){
            position = "left";
        }

        return JSONObject.create()
                .put("enabled", legend >= TOP)
                .put("position", position)
                .put("style", settings.optJSONObject("legendStyle"));
    }

    public BIDimension getCategoryDimension(){
        List<String> dimensionIds = view.get(Integer.parseInt(BIReportConstant.REGION.DIMENSION1));
        if(dimensionIds == null){
            return null;
        }
        for(BIDimension dimension : this.getDimensions()){
            if(dimensionIds.contains(dimension.getValue()) && dimension.isUsed()){
                return dimension;
            }
        }
        return null;
    }

    public BIDimension getSeriesDimension(){
        List<String> dimensionIds = view.get(Integer.parseInt(BIReportConstant.REGION.DIMENSION2));
        if(dimensionIds == null){
            return null;
        }
        for(BIDimension dimension : this.getDimensions()){
            if(dimensionIds.contains(dimension.getValue()) && dimension.isUsed()){
                return dimension;
            }
        }
        return null;
    }

    public JSONObject getPostOptions(String sessionId) throws Exception {
        JSONObject chartOptions = this.createDataJSON((BISessionProvider) SessionDealWith.getSessionIDInfor(sessionId));
        JSONObject plotOptions = chartOptions.optJSONObject("plotOptions");
        plotOptions.put("animation", false);
        chartOptions.put("plotOptions", plotOptions);
        return chartOptions;
    }

}