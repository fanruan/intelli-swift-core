package com.fr.bi.cal.analyze.report.report.widget.chart.types;

import com.finebi.cube.common.log.BILoggerFactory;
import com.fr.base.TemplateUtils;
import com.fr.bi.conf.report.WidgetType;
import com.fr.bi.conf.report.style.DetailChartSetting;
import com.fr.bi.conf.report.widget.field.dimension.BIDimension;
import com.fr.bi.field.target.target.BISummaryTarget;
import com.fr.bi.stable.constant.BIChartSettingConstant;
import com.fr.bi.stable.constant.BIReportConstant;
import com.fr.general.ComparatorUtils;
import com.fr.general.FRLogger;
import com.fr.json.JSONArray;
import com.fr.json.JSONException;
import com.fr.json.JSONObject;
import com.fr.stable.CoreConstants;
import com.fr.stable.StringUtils;

import java.util.*;

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
    private static final int MIN_SIZE = 12;
    private static final int MAX_SIZE = 40;

    //值区间的默认颜色
    private static final String[] INTERVAL_COLORS = new String[]{"#65B3EE", "#95E1AA", "#F8D08E"};
    private static final int INTERVAL = 100;

    private static final int BUBBLE_DIMENSION = 3;

    private List<String> seriesIDs = new ArrayList<String>();
    private List<String> categoryIDs = new ArrayList<String>();

    private static String tooltipTpl;

    private String getTooltipTpl() {
        if(StringUtils.isEmpty(tooltipTpl)){
            tooltipTpl = TemplateUtils.readTemplate2String(
                    "/com/fr/bi/cal/analyze/report/report/widget/chart/tpl/dotTooltip.tpl",
                    CoreConstants.CHARSET_OF_EMBEDDED_FILE);
        }
        return tooltipTpl;
    }

    protected void dealView(List<String> sorted, JSONObject vjo) throws JSONException{
        super.dealView(sorted, vjo);

        JSONArray ja = JSONArray.create();

        int seriesRegion = Integer.parseInt(BIReportConstant.REGION.DIMENSION2);

        for (String region : sorted) {

            if (Integer.parseInt(region) > seriesRegion) {
                continue;
            }

            JSONArray tmp = vjo.getJSONArray(region);

            for (int j = 0; j < tmp.length(); j++) {
                String key = tmp.getString(j);
                ja.put(key);

                if(Integer.parseInt(region) == seriesRegion){
                    this.seriesIDs.add(key);
                }else{
                    this.categoryIDs.add(key);
                }
            }

            vjo.remove(region);
        }

        vjo.put(BIReportConstant.REGION.DIMENSION1, ja);
    }

    protected JSONObject populateDefaultSettings() throws JSONException {
        JSONObject settings = super.populateDefaultSettings();

        settings.put("displayRules", SERIES_RULE);

        settings.put("bubbleStyle", NO_SHADOW);
        settings.put("dotStyle", BIChartSettingConstant.DOT_STYLE.SQUARE);

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
        gradualStyle.put("colorRange", JSONObject.create().put("fromColor", "#65B3EE").put("toColor", "#95E1AA"));

        settings.put("fixedStyle", fixedStyle);
        settings.put("gradientStyle", JSONArray.create().put(gradualStyle));

        return settings;
    }

    public JSONObject createPlotOptions(JSONObject globalStyle, JSONObject settings) throws Exception{
        JSONObject plotOptions = super.createPlotOptions(globalStyle, settings);

        plotOptions.put("sizeBy", "width");
        plotOptions.put("minSize", settings.optInt("bubbleSizeFrom"));
        plotOptions.put("maxSize", settings.optInt("bubbleSizeTo"));
        plotOptions.put("large", settings.optBoolean("bigDataMode"));

        plotOptions.put("shadow", settings.optInt("bubbleStyle") == SHADOW);

        plotOptions.put("marker", JSONObject.create().put("symbol", settings.optString("dotStyle")).put("enabled", true));

        return plotOptions;
    }

    protected boolean dotChartUseNormalLegend() {
        int idCount = this.getUsedTargetID().length;

        if(idCount < 3){
            return true;
        }

        JSONObject scopes = this.getChartSetting().getScopes();
        JSONObject target3 = scopes.optJSONObject(BIReportConstant.REGION.TARGET3);

        return target3 == null || target3.optInt("valueType") == BIChartSettingConstant.DOT_VALUE_TYPE.SIZE;
    }

    protected void toLegendJSON(JSONObject options, JSONObject settings) throws JSONException {
        if(dotChartUseNormalLegend()){
            settings.put("disPlayRules", SERIES_RULE);
        }
        super.toLegendJSON(options, settings);
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

    protected JSONObject parseLegend(JSONObject settings) throws JSONException{

        JSONObject legend = super.parseLegend(settings);

        int rule = settings.optInt("displayRules");
        if(rule == INTERVAL_RULE){
            legend.put("continuous", false);
            if(customFixedStyleRadio(settings)){
                legend.put("range", this.mapStyleToRange(settings.optJSONArray("fixedStyle")));
            }
        }else if(rule != SERIES_RULE){//只能是普通图例的，前台处理好了。如果是可选择的，默认什么都没传过来，默认是渐变色
            legend.put("continuous", true);
            if(customGradientStyleRadio(settings)){
                legend.put("range", this.gradualStyleToRange(settings.optJSONArray("gradientStyle")));
            }
        }

        return legend;
    }

    protected boolean customFixedStyleRadio(JSONObject settings) throws JSONException {
        return settings.optInt("fixedStyleRadio") == BIChartSettingConstant.SCALE_SETTING.CUSTOM;
    }

    protected boolean customGradientStyleRadio(JSONObject settings) throws JSONException {
        return settings.optInt("gradientStyleRadio") == BIChartSettingConstant.SCALE_SETTING.CUSTOM;
    }


    private JSONObject gradualStyleToRange(JSONArray style) throws JSONException{
        JSONArray colors = JSONArray.create();

        int count = style.length();
        if(count == 0){//把条件全删了
            return JSONObject.create();
        }
        double max = style.getJSONObject(count - 1).optJSONObject("range").optDouble("max", Integer.MAX_VALUE);
        double min = style.getJSONObject(0).optJSONObject("range").optDouble("min", Integer.MIN_VALUE);

        boolean first = true;
        for(int i = 0, len = style.length(); i < len; i++){
            JSONObject config = style.getJSONObject(i);
            JSONObject range = config.optJSONObject("range"), colorRange = config.optJSONObject("colorRange");
            if(colorRange == null){
                continue;
            }
            if(first) {
                double from = range.optDouble("min", Integer.MIN_VALUE) / max;
                colors.put(JSONArray.create().put(from).put(colorRange.optString("fromColor")));
                first = false;
            }
            double to = range.optDouble("max", Integer.MAX_VALUE) / max;
            colors.put(JSONArray.create().put(to).put(colorRange.optString("toColor")));

        }

        return JSONObject.create().put("color", colors).put("min", min).put("max", max);
    }

    protected String valueLabelKey() {
        return "{SIZE}";
    }

    //新的点图。系列无字段，所有点在一个name=vancharts中默认给的一个系列名 的系列里面
    public JSONArray createSeries(JSONObject originData) throws Exception{
        WidgetType chartType = this.getChartType();
        if(chartType == WidgetType.BUBBLE || chartType == WidgetType.SCATTER){
            return this.createBubbleScatterSeries(originData);
        }
        String[] ids = this.getUsedTargetID();
        if(ids.length < SCATTER_COUNT){
            return JSONArray.create();
        }

        return createDotSeries(getSeriesDimension() == null, originData);
    }

    private JSONArray createDotSeries(boolean noSeries, JSONObject originData) throws JSONException{
        HashMap<String, JSONArray> seriesMap = new HashMap<String, JSONArray>();

        dealChildren(noSeries, new ArrayList<String>(), originData.optJSONArray("c"), seriesMap);

        return dealSeriesMap(noSeries, this.getUsedTargetID(), seriesMap);
    }

    private void dealChildren(boolean noSeries, List<String> longDateDesc, JSONArray children,  HashMap<String, JSONArray> seriesMap) throws JSONException{

        if(children == null){
            return;
        }

        for(int i = 0, len = children.length(); i < len; i++){
            JSONObject child = children.getJSONObject(i);
            List<String> childDescription = new ArrayList<String>(longDateDesc);
            if(child.has("c") || noSeries){
                childDescription.add(child.optString("n"));
            }
            if(child.has("c")){
                dealChildren(noSeries, childDescription, child.optJSONArray("c"), seriesMap);
            } else {
                String seriesName = child.optString("n");
                JSONArray dataArray = seriesMap.containsKey(seriesName) ? seriesMap.get(seriesName) : JSONArray.create();
                seriesMap.put(seriesName, dataArray);

                List<String> desc = new ArrayList<String>();
                List<String> longDesc = new ArrayList<String>();

                for(int index = 0, count = childDescription.size(); index < count; index++){
                    String childDesc = childDescription.get(index);
                    BIDimension categoryDim = this.getCategoryDimension(index);

                    desc.add(this.formatDimension(categoryDim, childDesc));
                    longDesc.add(childDesc);
                }
                child.put("longDateDescription", longDesc);
                child.put("description", desc);

                dataArray.put(child);
            }
        }
    }

    private JSONArray dealSeriesMap(boolean noSeries, String[] ids, HashMap<String, JSONArray> seriesMap) throws JSONException{
        JSONArray series = JSONArray.create();
        BIDimension seriesDim = this.getSeriesDimension();
        String[] dimensionIDs = this.getUsedDimensionID();

        double yScale = this.numberScale(ids[0]), xScale = this.numberScale(ids[1]);
        double sizeScale = ids.length > 2 ? this.numberScaleByLevel(this.numberLevelFromSettings(ids[2])) : 1;

        JSONArray dotData = JSONArray.create();
        Iterator iterator = seriesMap.keySet().iterator();
        while (iterator.hasNext()){
            String seriesName = iterator.next().toString();
            JSONArray dataArray = seriesMap.get(seriesName);
            JSONObject ser = JSONObject.create();
            JSONArray data = JSONArray.create();
            for(int j = 0, count = dataArray.length(); j < count; j++){
                JSONObject obj = dataArray.optJSONObject(j);
                JSONArray dimensions = obj.optJSONArray("s");

                double y = dimensions.isNull(0) ? 0 : dimensions.optDouble(0);
                double x = dimensions.isNull(1) ? 0 : dimensions.optDouble(1);
                double value = (dimensions.length() > 2 && !dimensions.isNull(2)) ? dimensions.optDouble(2) : 0;
                String size = ids.length > 2 ? numberFormat(ids[2], value/sizeScale) : value/sizeScale + "";
                JSONObject point = JSONObject.create().put("x", numberFormat(ids[1], x/xScale)).put("y", numberFormat(ids[0],y/yScale)).put("size", size)
                        .put("description", obj.optJSONArray("description")).put("longDateDescription", obj.optJSONArray("longDateDescription"));

                if(noSeries) {
                    dotData.put(point);
                } else {
                    data.put(point);
                }
            }

            if(!noSeries) {
                String formattedName = this.formatDimension(seriesDim, seriesName);
                ser.put("data", data).put("name", formattedName).put(LONG_DATE, seriesName).put("dimensionIDs", dimensionIDs).put("targetIDs", new JSONArray(ids));
                series.put(ser);
            }
        }

        if(noSeries){
            series.put(JSONObject.create().put("data", dotData).put("dimensionIDs", dimensionIDs).put("targetIDs", new JSONArray(ids)));
        }

        return series;
    }


    //老的气泡图、散点图。原来的分类---->新点图的系列
    private JSONArray createBubbleScatterSeries(JSONObject originData) throws Exception{
        BIDimension category = this.getCategoryDimension();
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

            JSONObject point = JSONObject.create().put("x", checkInfinity(x)).put("y", checkInfinity(y)).put("size", checkInfinity(value));

            JSONObject ser = JSONObject.create().put("data", JSONArray.create().put(point))
                    .put("name", obj.optString("n")).put("targetIDs", new JSONArray(ids));

            if(category != null){
                ser.put("dimensionIDs", JSONArray.create().put(category.getValue()));
            }
            series.put(ser);

        }

        return series;
    }

    protected JSONArray parseCategoryAxis(JSONObject settings) throws JSONException{

        JSONObject baseAxis = this.parseRightValueAxis(settings)
                .put("position", "bottom").put("type", "value")
                .put("gridLineWidth", settings.optBoolean("vShowGridLine") ? 1 : 0)
                .put("gridLineColor", settings.optString("vGridLineColor"));

        if (baseAxis.has("title")) {
            baseAxis.optJSONObject("title").put("rotation", 0);
        }

        return JSONArray.create().put(baseAxis);
    }

    protected JSONArray parseValueAxis(JSONObject settings) throws JSONException{

        return JSONArray.create().put(this.parseLeftValueAxis(settings));
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

    private void addFormat2Map(Map<String, String> tplRenderMap, String[] ids, int index, String formatKey, String unitKey) throws Exception{
        if(ids.length > index){
            BISummaryTarget target = this.getBITargetByID(ids[index]);
            String format = this.valueFormat(target);
            String unit = this.valueUnit(target, true);
            tplRenderMap.put(formatKey, format);
            tplRenderMap.put(unitKey, unit);
        }
    }

    protected void formatSeriesTooltipFormat(JSONObject options) throws Exception {
        String[] ids = this.getUsedTargetID();

        Map<String, String> tplMap = new HashMap<String, String>();

        tplMap.put("key1X", "(X)");
        tplMap.put("key2X", "x");
        addFormat2Map(tplMap, ids, 1, "formatX", "unitX");

        tplMap.put("key1Y", "(Y)");
        tplMap.put("key2Y", "y");
        addFormat2Map(tplMap, ids, 0, "formatY", "unitY");

        tplMap.put("key1SIZE", "(" + getLocText("BI-Basic_Value") +")");
        tplMap.put("key2SIZE", "size");
        addFormat2Map(tplMap, ids, 2, "formatSIZE", "unitSIZE");

        String formatter = StringUtils.EMPTY;
        try {
            formatter = TemplateUtils.renderParameter4Tpl(getTooltipTpl(), tplMap);
        } catch (Exception e) {
            BILoggerFactory.getLogger().error(e.getMessage(),e);
        }

        JSONArray series = options.optJSONArray("series");
        JSONObject tooltip = options.optJSONObject("plotOptions").optJSONObject("tooltip");
        for (int i = 0, len = series.length(); i < len; i++) {
            JSONObject ser = series.getJSONObject(i);

            ser.put("tooltip", new JSONObject(tooltip.toString()).put("formatter", formatter));
        }
    }

    protected void formatSeriesDataLabelFormat(JSONObject options) throws Exception {
        JSONObject dataLabels = options.optJSONObject("plotOptions").optJSONObject("dataLabels");

        String[] ids = this.getUsedTargetID();
        String[] keys = {"sizeFormat", "XFormat", "YFormat"};
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

    protected String categoryLabelKey() {
        return DESCRIPTION;
    }

    protected JSONArray getDataLabelConditions(BISummaryTarget target){

        try {
            JSONObject settings = this.getDetailChartSetting();
            return settings.optJSONArray("dataLabel");
        }catch (Exception e){
            BILoggerFactory.getLogger().error(e.getMessage(), e);
        }

        return null;
    }

    protected Object findTarget(String id, JSONObject config, JSONObject datum, JSONObject ser){
        for(int i = 0, len = this.seriesIDs.size(); i < len; i++){
            if(ComparatorUtils.equals(this.seriesIDs.get(i), id)){
                return ser.optString(LONG_DATE);
            }
        }

        for(int i = 0, len = this.categoryIDs.size(); i < len; i++){
            if(ComparatorUtils.equals(this.categoryIDs.get(i), id)){
                return datum.optJSONArray("longDateDescription").optString(i);
            }
        }

        if(config.has("key")){
            String key = config.optString("key");
            if(ComparatorUtils.equals(key, "x")){
                return datum.optDouble("x", 0);
            }else if(ComparatorUtils.equals(key, "y")){
                return datum.optDouble("y", 0);
            }else if(ComparatorUtils.equals(key, "z")){
                return datum.optDouble("size", 0);
            }
        }

        return datum;
    }

    public BIDimension getSeriesDimension() {
        for(String id : this.seriesIDs){
            for (BIDimension dimension : getDimensions()) {
                if (ComparatorUtils.equals(dimension.getId(), id) && dimension.isUsed()) {
                    return dimension;
                }
            }
        }
        return null;
    }

    protected JSONObject defaultDataLabelSetting() throws JSONException {

        return JSONObject.create().put("showCategoryName", false).put("showSeriesName", false)
                .put("showXValue", true).put("showYValue", true).put("showValue", true)
                .put("textStyle", defaultFont());

    }

    protected boolean checkValid(){
        return this.getTar1Size() >= 2;
    }
}
