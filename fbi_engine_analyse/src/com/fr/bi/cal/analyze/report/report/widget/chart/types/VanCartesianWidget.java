package com.fr.bi.cal.analyze.report.report.widget.chart.types;

import com.finebi.cube.common.log.BILoggerFactory;
import com.fr.bi.cal.analyze.report.report.widget.VanChartWidget;
import com.fr.bi.cal.analyze.report.report.widget.chart.filter.ChartFilterFactory;
import com.fr.bi.conf.report.widget.field.dimension.BIDimension;
import com.fr.bi.conf.report.widget.field.filtervalue.FilterValue;
import com.fr.bi.field.target.target.BISummaryTarget;
import com.fr.bi.stable.constant.BIReportConstant;
import com.fr.general.ComparatorUtils;
import com.fr.general.IOUtils;
import com.fr.json.JSONArray;
import com.fr.json.JSONException;
import com.fr.json.JSONObject;
import com.fr.stable.StableUtils;
import com.fr.stable.StringUtils;

import java.awt.image.BufferedImage;

/**
 * Created by eason on 2017/3/2.
 */
public abstract class VanCartesianWidget extends VanChartWidget {

    //面积图的三种形态
    private static final int NORMAL = 1;
    private static final int STEP = 2;
    private static final int CURVE = 3;

    //这个是对比柱状图的stackid和transSeries的name，任意string都可以，不会展示到图上，不用国际化什么的。
    private static final String FALL_COLUMN = "fallColumn";
    protected static final String TRANS = "rgba(0,0,0,0)";

    protected static final int VERTICAL = 90;
    private static final String IMG_TMP = "function(){return \"<img src = %s>\"}";

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
                .put("rightYSeparator", true);

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
                .put("rightY2Separator", true);

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

    protected String unitFromSetting(BISummaryTarget dimension) {
        int yAxis = this.yAxisIndex(dimension.getId());

        try {
            JSONObject settings = this.getDetailChartSetting();

            if(yAxis == 0){
                return settings.optString("leftYUnit");
            }else if(yAxis == 1){
                return settings.optString("rightYUnit");
            }else if(yAxis == 2){
                return settings.optString("rightY2Unit");
            }
        }catch (Exception e){
            BILoggerFactory.getLogger().error(e.getMessage(),e);
        }

        return StringUtils.EMPTY;
    }

    protected boolean hasSeparatorFromSetting(BISummaryTarget dimension){
        int yAxis = this.yAxisIndex(dimension.getId());

        try {
            JSONObject settings = this.getDetailChartSetting();

            if(yAxis == 0){
                return settings.optBoolean("leftYSeparator");
            }else if(yAxis == 1){
                return settings.optBoolean("rightYSeparator");
            }else if(yAxis == 2){
                return settings.optBoolean("rightY2Separator");
            }

        }catch (Exception e){
            BILoggerFactory.getLogger().error(e.getMessage(),e);
        }
        return true;
    }

    //todo 坐标轴标题和数量级，单位构成的后缀
    protected String axisTitleUnit(int level, String unit){
        String result = this.scaleUnit(level);
        result += unit;
        return StringUtils.isEmpty(result) ? StringUtils.EMPTY : "(" + result + ")";
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
                    .put("clickColor", TRANS).put("mouseOverColor", TRANS).put("tooltip", falseEnabledJSONObject()).put("dataLabels", falseEnabledJSONObject())
                    .put("fillColor", TRANS).put("marker", falseEnabledJSONObject()).put(TRANS_SERIES, true);

            double stackValue = 0;
            JSONArray data = ser0.optJSONArray("data");
            for(int i = 0, len = data.length(); i < len; i++){
                JSONObject dataPoint = data.getJSONObject(i);
                double y = dataPoint.optDouble("y", 0);
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

        //没配置，默认true
        plotOptions.put("connectNulls", !settings.has("nullContinuity") || settings.optBoolean("nullContinuity"));

        return plotOptions;
    }

    public boolean isInverted(){
        return false;
    }

    public JSONObject createOptions(JSONObject globalStyle, JSONObject data) throws Exception{
        JSONObject settings = this.getDetailChartSetting();
        JSONObject options = super.createOptions(globalStyle, data);
        boolean isInverted = this.isInverted();//bar

        if(supportDataSheet()) {
            options.put("dataSheet", JSONObject.create().put("enabled", settings.optBoolean("showDataTable") && !isInverted)
                    .put("style", this.defaultFont()).put("borderColor", "#dddddd").put("borderWidth", 1));
        }
        if(settings.optBoolean("showZoom") && !settings.optBoolean("miniMode")){
            options.put("zoom", JSONObject.create().put("zoomTool", JSONObject.create().put("enabled", !isInverted)).put("zoomType", ""));
        }

        JSONArray cateArray = this.parseCategoryAxis(settings);
        JSONArray valueArray = this.parseValueAxis(settings);
        if(settings.optBoolean("miniMode", false)){
            checkMIniMode(cateArray, true);
            checkMIniMode(valueArray, false);
        }
        options.put(this.getCoordXKey(), cateArray);
        options.put(this.getCoordYKey(), valueArray);

        this.dealDataLabelsConditions(options);
        this.dealImageFillConditions(options);

        return options;
    }

    protected boolean supportDataSheet() throws Exception{
        return true;
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
            axis.put("showLabel", showLabelInMiniMode(cate, i)).put("lineWidth", 0).put("gridLineWidth", 0).put("enableTick", false).put("title", JSONObject.create().put("enabled", false));
        }
    }

    //这个暂时只是为了对比条形柱状图极简模式下，第二个分类轴标签不显示
    protected boolean showLabelInMiniMode(boolean cate, int index) {
        return cate;
    }

    private void dealImageFillConditions(JSONObject options){
        JSONArray series = options.optJSONArray("series");
        for(int i = 0, count = series.length(); i < count; i++){
            JSONObject ser = series.optJSONObject(i);
            JSONArray targetIDs = ser.optJSONArray("targetIDs");
            String type = ser.optString("type");
            if(targetIDs == null){
                continue;
            }
            try{
                String targetID = targetIDs.optString(0);
                BISummaryTarget target = this.getBITargetByID(targetID);
                JSONArray dataImage = target.getChartSetting().getDataImage();
                if(dataImage != null) {
                    int filterCount = dataImage.length();
                    JSONArray data = ser.optJSONArray("data");
                    FilterValue[] filterValues = this.createFilterValues(dataImage, data);
                    for (int dataIndex = 0, dataCount = data.length(); dataIndex < dataCount; dataIndex++) {
                        JSONObject datum = data.optJSONObject(dataIndex);
                        for (int filterIndex = filterCount - 1; filterIndex >= 0; filterIndex--) {
                            FilterValue filter = filterValues[filterIndex];
                            JSONObject config = dataImage.optJSONObject(filterIndex);
                            String id = config.optString("targetId");
                            if(filter.isMatchValue(this.findTarget(id, datum, ser))) {
                                JSONObject styleSetting = config.optJSONObject("styleSetting");
                                if(styleSetting.has("src")){
                                    String url = styleSetting.optString("src");
                                    BufferedImage bufferedImage = IOUtils.readImage(this.getLocalImagePath(url));
                                    if(type == "area" || type == "line"){
                                        datum.put("marker", JSONObject.create().put("symbol", this.getCompleteImageUrl(url)).put("width", bufferedImage.getWidth()).put("height", bufferedImage.getHeight()));
                                    }else{
                                        datum.put("image", this.getCompleteImageUrl(url));
                                        datum.put("imageWidth", bufferedImage.getWidth());
                                        datum.put("imageHeight", bufferedImage.getHeight());
                                    }

                                }
                            }
                        }
                    }
                }
            }catch (Exception e){
                BILoggerFactory.getLogger().error(e.getMessage(),e);
            }

        }
    }

    protected FilterValue[] createFilterValues(JSONArray config, JSONArray data){
        int filterCount = config.length();
        FilterValue[] filterValues = new FilterValue[filterCount];
        for (int filterIndex = 0; filterIndex < filterCount; filterIndex++) {
            try {
                filterValues[filterIndex] = ChartFilterFactory.parseFilterValue(config.optJSONObject(filterIndex), this.getUserId(), data);
            }catch (Exception e){
                BILoggerFactory.getLogger().error(e.getMessage(),e);
            }
        }

        return filterValues;
    }

    protected Object findTarget(String id, JSONObject datum, JSONObject ser){

        BIDimension categoryDim = this.getCategoryDimension();
        BIDimension seriesDim = this.getSeriesDimension();

        if(categoryDim != null && ComparatorUtils.equals(categoryDim.getId(), id)){
            return datum.optString(LONG_DATE);
        }

        if(seriesDim != null && ComparatorUtils.equals(seriesDim.getId(), id)){
            return ser.optString(LONG_DATE);
        }

        return datum.optDouble("y", 0);
    }

    protected Object findTarget(String id, JSONObject config, JSONObject datum, JSONObject ser){
        return findTarget(id, datum, ser);
    }

    protected JSONArray getDataLabelConditions(BISummaryTarget target){
        return target.getChartSetting().getDataLabels();
    }

    private void dealDataLabelsConditions(JSONObject options) {
        JSONArray series = options.optJSONArray("series");
        for (int i = 0, count = series.length(); i < count; i++) {
            JSONObject ser = series.optJSONObject(i);
            JSONArray targetIDs = ser.optJSONArray("targetIDs");
            if (targetIDs == null) {
                continue;
            }
            try {
                String targetID = targetIDs.optString(0);
                BISummaryTarget target = this.getBITargetByID(targetID);
                //标签的条件属性
                JSONObject dataLabels = ser.optJSONObject("dataLabels");
                JSONArray labelCondition = this.getDataLabelConditions(target);
                if (labelCondition != null && dataLabels != null && dataLabels.optBoolean("enabled") == true) {
                    int filterCount = labelCondition.length();
                    JSONArray data = ser.optJSONArray("data");
                    FilterValue[] filterValues = this.createFilterValues(labelCondition, data);
                    for (int dataIndex = 0, dataCount = data.length(); dataIndex < dataCount; dataIndex++) {
                        JSONObject datum = data.optJSONObject(dataIndex);
                        for (int filterIndex = filterCount - 1; filterIndex >= 0; filterIndex--) {
                            FilterValue filter = filterValues[filterIndex];
                            JSONObject config = labelCondition.optJSONObject(filterIndex);
                            String id = config.optString("targetId");
                            Object matchTarget = this.findTarget(id, config, datum, ser);
                            if (target != null && filter.isMatchValue(matchTarget)){
                                JSONObject styleSetting = config.optJSONObject("styleSetting");
                                JSONObject textStyle = styleSetting.optJSONObject("textStyle");
                                JSONObject imgStyle = styleSetting.optJSONObject("imgStyle");
                                JSONObject customDataLabels = new JSONObject(dataLabels.toString());
                                if (textStyle.has("fontFamily")) {
                                    customDataLabels.put("style", textStyle);
                                }
                                if (imgStyle.has("src")) {
                                    String url = imgStyle.optString("src");
                                    BufferedImage img = IOUtils.readImage(this.getLocalImagePath(url));
                                    customDataLabels.put("formatter", String.format(IMG_TMP, this.getCompleteImageUrl(url))).put("useHtml", true).put("labelWidth", img.getWidth()).put("labelHeight", img.getHeight());
                                }
                                datum.put("dataLabels", customDataLabels);
                                break;
                            }
                        }
                    }
                }
            } catch (Exception e) {
                BILoggerFactory.getLogger().error(e.getMessage(), e);
            }
        }
    }

    protected String getCoordXKey(){
        return "xAxis";
    }

    protected String getCoordYKey(){
        return "yAxis";
    }

    protected JSONArray parseCategoryAxis(JSONObject settings) throws JSONException{
        JSONObject labelStyle = settings.optJSONObject("catLabelStyle");

        JSONObject category = JSONObject.create();
        boolean enabled = settings.optBoolean("catShowTitle");

        category
                .put("maxWidth", COMPONENT_MAX_SIZE).put("maxHeight", COMPONENT_MAX_SIZE)
                .put("type", "category").put("position", "bottom")
                .put("title", JSONObject.create().put("rotation", cateAxisRotation()).put("style", settings.optJSONObject("catTitleStyle")).put("text", enabled ?settings.optString("catTitle") : StringUtils.EMPTY))
                .put("showLabel", settings.optBoolean("catShowLabel") && !hasDataSheet(settings))
                .put("labelStyle", labelStyle.optJSONObject("textStyle"))
                .put("labelRotation", labelStyle.optInt("textDirection"))
                .put("lineColor", settings.optString("catLineColor"))
                .put("gridLineWidth", settings.optBoolean("vShowGridLine") ? 1 : 0)
                .put("gridLineColor", settings.optString("vGridLineColor"))
                .put("reversed", false);

        return JSONArray.create().put(category);
    }

    protected boolean hasDataSheet(JSONObject settings) {
        return settings.optBoolean("showDataTable");
    }

    protected double cateAxisRotation() {
        return 0;
    }

    protected double valueAxisRotation() {
        return VERTICAL;
    }

    protected JSONArray parseValueAxis(JSONObject settings) throws JSONException{

        JSONArray axis = JSONArray.create();

        axis.put(this.parseLeftValueAxis(settings));

        axis.put(this.parseRightValueAxis(settings));

        axis.put(this.parseThirdValueAxis(settings));

        return axis;
    }

    protected JSONObject parseLeftValueAxis(JSONObject settings) throws JSONException{

        JSONObject labelStyle = settings.optJSONObject("leftYLabelStyle");
        String axisTitle = this.axisTitleUnit(settings.optInt("leftYNumberLevel"), settings.optString("leftYUnit"));
        boolean enabled = settings.optBoolean("leftYShowTitle");

        JSONObject left = JSONObject.create()
                .put("maxWidth", COMPONENT_MAX_SIZE).put("maxHeight", COMPONENT_MAX_SIZE)
                .put("type", "value")
                .put("title", JSONObject.create()
                        .put("style", settings.optJSONObject("leftYTitleStyle"))
                        .put("text", enabled ? settings.optString("leftYTitle") + axisTitle : axisTitle)
                        .put("rotation", valueAxisRotation())
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
            this.putMinMaxInterval(left, settings.optJSONObject("leftYCustomScale"));
        }

        left.put("plotLines", this.parsePlotLines(BIReportConstant.REGION.TARGET1));

        return left;
    }

    protected JSONObject parseRightValueAxis(JSONObject settings) throws JSONException{

        String axisTitle = this.axisTitleUnit(settings.optInt("rightYNumberLevel"), settings.optString("rightYUnit"));
        boolean enabled = settings.optBoolean("rightYShowTitle");
        boolean showGridLine = settings.optBoolean("hShowGridLine") && !hasData(BIReportConstant.REGION.TARGET1);
        JSONObject labelStyle = settings.optJSONObject("rightYLabelStyle");
        JSONObject right = JSONObject.create()
                .put("maxWidth", COMPONENT_MAX_SIZE).put("maxHeight", COMPONENT_MAX_SIZE)
                .put("type", "value")
                .put("title", JSONObject.create()
                        .put("style", settings.optJSONObject("rightYTitleStyle"))
                        .put("text", enabled ? settings.optString("rightYTitle") + axisTitle : axisTitle)
                        .put("rotation", valueAxisRotation())
                )
                .put("showLabel", settings.optBoolean("rightYShowLabel"))
                .put("formatter", this.tickFormatter(1))
                .put("labelStyle", labelStyle.optJSONObject("textStyle"))
                .put("labelRotation", labelStyle.optInt("textDirection"))
                .put("lineColor", settings.optString("rightYLineColor")).put("lineWidth", 1)
                .put("position", "right").put("reversed", settings.optBoolean("rightYReverse", false))
                .put("gridLineWidth", showGridLine ? 1 : 0)
                .put("gridLineColor", settings.optString("hGridLineColor"));

        if(settings.optBoolean("rightYShowCustomScale")){
            this.putMinMaxInterval(right, settings.optJSONObject("rightYCustomScale"));
        }

        right.put("plotLines", this.parsePlotLines(BIReportConstant.REGION.TARGET2));

        return right;
    }

    protected JSONObject parseThirdValueAxis(JSONObject settings) throws JSONException{
        String axisTitle = this.axisTitleUnit(settings.optInt("rightY2NumberLevel"), settings.optString("rightY2Unit"));
        boolean enabled = settings.optBoolean("rightY2ShowTitle");
        boolean showGridLine = settings.optBoolean("hShowGridLine") && !hasData(BIReportConstant.REGION.TARGET1) && !hasData(BIReportConstant.REGION.TARGET2);
        JSONObject labelStyle = settings.optJSONObject("rightY2LabelStyle");
        JSONObject right2 = JSONObject.create()
                .put("maxWidth", COMPONENT_MAX_SIZE).put("maxHeight", COMPONENT_MAX_SIZE)
                .put("type", "value")
                .put("title", JSONObject.create()
                        .put("style", settings.optJSONObject("rightY2TitleStyle"))
                        .put("text", enabled ? settings.optString("rightY2Title") + axisTitle : axisTitle)
                        .put("rotation", valueAxisRotation())
                )
                .put("showLabel", settings.optBoolean("rightY2ShowLabel"))
                .put("formatter", this.tickFormatter(2))
                .put("labelStyle", labelStyle.optJSONObject("textStyle"))
                .put("labelRotation", labelStyle.optInt("textDirection"))
                .put("lineColor", settings.optString("rightY2LineColor")).put("lineWidth", 1)
                .put("position", "right").put("reversed", settings.optBoolean("rightY2Reverse", false))
                .put("gridLineWidth", showGridLine ? 1 : 0)
                .put("gridLineColor", settings.optString("hGridLineColor"));

        if(settings.optBoolean("rightY2ShowCustomScale")){
            this.putMinMaxInterval(right2, settings.optJSONObject("rightY2CustomScale"));
        }

        right2.put("plotLines", this.parsePlotLines(BIReportConstant.REGION.TARGET2));

        return right2;
    }

    private boolean hasData(String regionID) {
        JSONArray dIDs = this.getDimensionIDArray(regionID);

        if(dIDs == null){
            return false;
        }

        for(int i = 0, len = dIDs.length(); i < len; i++){
            try {
                BISummaryTarget dimension = this.getBITargetByID(dIDs.optString(i));
                if(dimension.isUsed()) {
                    return true;
                }
            }catch (Exception ex){
                BILoggerFactory.getLogger().error(ex.getMessage(), ex);
            }
        }
        return false;
    }

    private JSONArray parsePlotLines(String regionID){

        JSONArray dIDs = this.getDimensionIDArray(regionID);
        JSONArray plotLines = JSONArray.create();

        if(dIDs == null){
            return plotLines;
        }

        for(int i = 0, len = dIDs.length(); i < len; i++){
            try {
                BISummaryTarget dimension = this.getBITargetByID(dIDs.optString(i));
                double scale = this.numberScale(dIDs.optString(i));
                if(dimension.isUsed()) {
                    JSONArray cordons = dimension.getChartSetting().getCordon();

                    for (int j = 0, count = cordons.length(); j < count; j++) {
                        JSONObject config = cordons.optJSONObject(j);

                        plotLines.put(
                                JSONObject.create().put("value", config.optDouble("cordonValue") / scale)
                                        .put("width", 1)
                                        .put("color", config.optString("cordonColor"))
                                        .put("label", JSONObject.create().put("text", config.optString("cordonName")).put("style", defaultFont()).put("align", "right"))
                        );

                    }
                }

            }catch (Exception ex){
                BILoggerFactory.getLogger().error(ex.getMessage(), ex);
            }

        }

        return plotLines;
    }

    private void putMinMaxInterval(JSONObject axis, JSONObject scale) throws JSONException{

        if(scale == null){
            return;
        }

        String min = StringUtils.EMPTY, max = StringUtils.EMPTY, interval = StringUtils.EMPTY;
        if(scale.has("minScale")) {
            min = scale.optString("minScale");
        }
        if(scale.has("maxScale")) {
            max = scale.optString("maxScale");
        }
        if(scale.has("interval")) {
            interval = scale.optString("interval");
        }

        if(StringUtils.isNotBlank(min)){
            axis.put("min", StableUtils.string2Number(min).doubleValue());
        }

        if(StringUtils.isNotBlank(max)){
            axis.put("max", StableUtils.string2Number(max).doubleValue());
        }

        if(StringUtils.isNotBlank(interval)){
            axis.put("tickInterval", StableUtils.string2Number(interval).doubleValue());
        }
    }


    //=========================about compare chart==========================================================
    private static final double DEFAULT_MAX = 100;

    private static final double TICK_COUNT = 5;

    private static final double STEP10 = 10;
    private static final double STEP5 = 5;
    private static final double STEP2 = 2;

    private static final double ERROR15 = .15;
    private static final double ERROR35 = .35;
    private static final double ERROR75 = .75;

    //make yaxis maxValue Double
    protected void dealCompareChartYAxis(JSONObject settings) throws JSONException{

        double leftYMax = -Double.MAX_VALUE, rightYMax = -Double.MAX_VALUE, leftYMin = Double.MAX_VALUE, rightYMin = Double.MAX_VALUE;
        String[] ids = this.getUsedTargetID();

        for(String id : ids){

            Double[] values = this.getValuesByID(id);

            int yAxis = this.yAxisIndex(id);
            for (int j = 0, count = values.length; j < count; j++) {
                double value = values[j].doubleValue();
                if(yAxis == 0) {
                    leftYMax = Math.max(leftYMax, value);
                    leftYMin = Math.min(leftYMin, value);
                } else {
                    rightYMax = Math.max(rightYMax, value);
                    rightYMin = Math.min(rightYMin, value);
                }
            }
        }

        double[] leftDomain = calculateValueTimeNiceDomain(leftYMin, leftYMax);
        double[] rightDomain = calculateValueTimeNiceDomain(rightYMin, rightYMax);

        settings.put("rightYReverse", true);

        if(!settings.optBoolean("leftYShowCustomScale")){
            settings
                    .put("leftYShowCustomScale", true)
                    .put("leftYCustomScale", JSONObject.create().put("maxScale", 2 * leftDomain[1]).put("minScale", leftDomain[0]));
        }

        if(!settings.optBoolean("rightYShowCustomScale")){
            settings
                    .put("rightYShowCustomScale", true)
                    .put("rightYCustomScale", JSONObject.create().put("maxScale", 2 * rightDomain[1]).put("minScale", rightDomain[0]));
        }

    }

    private double linearTickInterval(double min, double max, double m){

        if (m == 0) {
            m = TICK_COUNT;
        }

        double span = max - min,
                step = Math.pow(10, Math.floor(Math.log(span / m) / Math.log(10))),
                err = m / span * step;

        if (err <= ERROR15) {
            step *= STEP10;
        } else if (err <= ERROR35) {
            step *= STEP5;
        } else if (err <= ERROR75) {
            step *= STEP2;
        }

        return step;
    }

    private double[] linearNiceDomain(double min, double max, double tickInterval){

        min = Math.floor(min / tickInterval) * tickInterval;

        max = Math.ceil(max / tickInterval) * tickInterval;

        return new double[]{min, max};
    }

    protected double[] calculateValueTimeNiceDomain(double minValue, double maxValue){

        if(minValue > 0){
            minValue = 0;
        } else if(maxValue < 0){
            maxValue = 0;
        }

        // if any exceeded min, adjust max to min + 100
        if(minValue >= maxValue){
            maxValue = minValue + DEFAULT_MAX;
        }

        double tickInterval = linearTickInterval(minValue, maxValue, 0);

        double[] domain = linearNiceDomain(minValue, maxValue, tickInterval);

        minValue = domain[0];
        maxValue = domain[1];

        if(minValue >= maxValue){
            maxValue = minValue + DEFAULT_MAX;
        }

        return new double[]{minValue, maxValue};
    }

    protected JSONObject createEmptyCategoryAxis(JSONObject settings) throws JSONException{
        return JSONObject.create()
                .put("type", "category")
                .put("position", "top")
                .put("showLabel", false)
                .put("lineWidth", 0)
                .put("enableTick", false)
                .put("gridLineWidth", 0)
                .put("labelStyle", settings.optJSONObject("catLabelStyle"));
    }

    protected JSONArray dealSeriesWithEmptyAxis(JSONArray series) throws JSONException{
        for(int i = 0, len = series.length(); i < len; i++){
            JSONObject ser = series.getJSONObject(i);

            int yAxisIndex = ser.optInt("yAxis");
            if(yAxisIndex == 1){
                ser.put("xAxis", 1);
            }
        }

        return series;
    }

    protected JSONObject getSeriesAccumulationItem(String seriesName){
        BIDimension seriesDim = this.getSeriesDimension();

        if(seriesDim != null && seriesDim.getChartSetting().hasSeriesAccumulation()){
            JSONArray items = seriesDim.getChartSetting().getSeriesAccumulation();
            for(int i = 0, count = items.length(); i < count; i++){
                JSONObject obj = items.optJSONObject(i);
                JSONArray objItems = obj.optJSONArray("items");
                for(int j = objItems.length() - 1; j >=0; j--){
                    if(ComparatorUtils.equals(objItems.optString(j), seriesName)){
                        return obj;
                    }
                }
            }
        }
        return null;
    }

    protected boolean checkValid(){
        return this.hasTarget();
    }
}
