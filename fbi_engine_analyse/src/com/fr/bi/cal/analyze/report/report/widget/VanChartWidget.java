package com.fr.bi.cal.analyze.report.report.widget;

import com.finebi.cube.common.log.BILoggerFactory;
import com.fr.base.FRContext;
import com.fr.bi.cal.analyze.cal.result.operator.BigDataChartOperator;
import com.fr.bi.conf.fs.BIChartStyleAttr;
import com.fr.bi.conf.fs.FBIConfig;
import com.fr.bi.conf.fs.tablechartstyle.BIChartFontStyleAttr;
import com.fr.bi.conf.report.WidgetType;
import com.fr.bi.conf.report.map.BIMapInfoManager;
import com.fr.bi.conf.report.style.DetailChartSetting;
import com.fr.bi.conf.report.widget.field.BITargetAndDimension;
import com.fr.bi.conf.report.widget.field.dimension.BIDimension;
import com.fr.bi.conf.session.BISessionProvider;
import com.fr.bi.field.target.target.BISummaryTarget;
import com.fr.bi.stable.constant.BIBaseConstant;
import com.fr.bi.stable.constant.BIChartSettingConstant;
import com.fr.bi.stable.constant.BIReportConstant;
import com.fr.bi.util.BIConfUtils;
import com.fr.general.ComparatorUtils;
import com.fr.general.IOUtils;
import com.fr.general.Inter;
import com.fr.json.JSONArray;
import com.fr.json.JSONException;
import com.fr.json.JSONObject;
import com.fr.stable.CodeUtils;
import com.fr.stable.StableUtils;
import com.fr.stable.StringUtils;
import com.fr.web.utils.WebUtils;

import javax.servlet.http.HttpServletRequest;
import java.awt.*;
import java.io.File;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

/**
 * Created by User on 2016/4/25.
 */
public abstract class VanChartWidget extends TableWidget {

    private static final double RED_DET = 0.299;
    private static final double GREEN_DET = 0.587;
    private static final double BLUE_DET = 0.114;
    private static final double GRAY = 192;

    //标签和数据点提示的内容
    public static final String CATEGORY = "${CATEGORY}";
    public static final String SERIES = "${SERIES}";
    public static final String VALUE = "${VALUE}";
    public static final String PERCENT = "${PERCENT}";
    public static final String X = "${X}";
    public static final String Y = "${Y}";
    public static final String SIZE = "${SIZE}";
    public static final String NAME = "${NAME}";
    public static final String DESCRIPTION = "${DESCRIPTION}";
    public static final String ARRIVALRATE = "${ARRIVALRATE}";

    protected static final String COMPONENT_MAX_SIZE = "30%";
    protected static final String TRANS_SERIES = "transSeries";

    public static final String LONG_DATE = "longDate";

    private static final int TARGET = 30000;
    private static final int TARGET_BASE = 10000;

    protected static final String PERCENT_SYMBOL = "%";
    private static final String WHITE = "#ffffff";
    protected static final String DARK = "#1a1a1a";

    private static final int WEEK_COUNT = 52;
    private static final int MONTH_COUNT = 12;

    public static final int AUTO = 1;
    public static final int CUSTOM = 2;

    private String requestURL = StringUtils.EMPTY;
    private WidgetType chartType = WidgetType.COLUMN;

    private HashMap<String, JSONArray> dimensionIdMap = new HashMap<String, JSONArray>();
    private HashMap<String, String> regionIdMap = new HashMap<String, String>();

    //存下每个指标和纬度的最大最小和平均值
    private HashMap<String, ArrayList<Double>> idValueMap = new HashMap<String, ArrayList<Double>>();

    private Locale locale;

    private int dim1Size, dim2Size, tar1Size, tar2Size, tar3Size;

    //todo:@shine 4.1版本整理一下settings globalstyle plateconfig
    private JSONObject globalStyle;

    public static final String[] FULL_QUARTER_NAMES = new String[]{
            Inter.getLocText("BI-Quarter_1"),
            Inter.getLocText("BI-Quarter_1"),
            Inter.getLocText("BI-Quarter_2"),
            Inter.getLocText("BI-Quarter_3"),
            Inter.getLocText("BI-Quarter_4")
    };

    public static final String[] FULL_MONTH_NAMES = new String[]{
            Inter.getLocText("BI-Basic_January"),
            Inter.getLocText("BI-Basic_January"),
            Inter.getLocText("BI-Basic_February"),
            Inter.getLocText("BI-Basic_March"),
            Inter.getLocText("BI-Basic_April"),
            Inter.getLocText("BI-Basic_May"),
            Inter.getLocText("BI-Basic_June"),
            Inter.getLocText("BI-Basic_July"),
            Inter.getLocText("BI-Basic_August"),
            Inter.getLocText("BI-Basic_September"),
            Inter.getLocText("BI-Basic_October"),
            Inter.getLocText("BI-Basic_November"),
            Inter.getLocText("BI-Basic_December")
    };

    public static final String[] FULL_WEEK_NAMES = new String[]{
            Inter.getLocText("BI-Basic_Sunday"),
            Inter.getLocText("BI-Basic_Monday"),
            Inter.getLocText("BI-Basic_Tuesday"),
            Inter.getLocText("BI-Basic_Wednesday"),
            Inter.getLocText("BI-Basic_Thursday"),
            Inter.getLocText("BI-Basic_Friday"),
            Inter.getLocText("BI-Basic_Saturday"),
            Inter.getLocText("BI-Basic_Sunday")
    };

    public abstract String getSeriesType(String dimensionID);

    public String getSeriesType(String dimensionID, String seriesName) {
        return this.getSeriesType(dimensionID);
    }

    public JSONObject createOptions(JSONObject globalStyle, JSONObject data) throws Exception {
        //todo:@shine 4.1版本整理下populatedefaultsetting和createplotoptions.原因：默认属性尽量放到一起，不要分开两处
        JSONObject options = JSONObject.create();
        JSONObject settings = this.getDetailChartSetting();
        //todo:@shine 4.1系统整理下platconfig.这边可以直接取，不用转成json
        JSONObject plateConfig = BIConfUtils.getPlateConfig();

        options.put("chartType", this.getSeriesType(StringUtils.EMPTY));

        options.put("colors", this.parseColors(settings, globalStyle, plateConfig));

        options.put("style", this.parseStyle(settings, globalStyle, plateConfig));

        toLegendJSON(options, settings);

        options.put("plotOptions", this.createPlotOptions(globalStyle, settings));

        options.put("series", this.createSeries(data));

        //处理格式的问题
        this.formatSeriesTooltipFormat(options);

        this.formatSeriesDataLabelFormat(options);

        options.put("tools", toolsJSON());


        return options;
    }

    private JSONObject toolsJSON() throws JSONException {
        return JSONObject.create()
                .put("hidden", false)
                .put("toImage", falseEnabledJSONObject())
                .put("sort", falseEnabledJSONObject())
                .put("fullScreen", falseEnabledJSONObject())
                .put("refresh", falseEnabledJSONObject());
    }

    protected JSONObject falseEnabledJSONObject() throws JSONException {
        return JSONObject.create().put("enabled", false);
    }

    protected void toLegendJSON(JSONObject options, JSONObject settings) throws JSONException {
        if (settings.optBoolean("miniMode", false)) {
            options.put("legend", JSONObject.create().put("enabled", false));
            options.put("rangeLegend", JSONObject.create().put("enabled", false));
        } else {
            options.put(this.getLegendType(settings), this.parseLegend(settings));
        }
    }

    public JSONArray createSeries(JSONObject data) throws Exception {
        return this.createXYSeries(data);
    }

    protected String getLegendType(JSONObject settings) {
        return "legend";
    }

    protected boolean isStacked(String dimensionID) {
        return false;
    }

    protected boolean isStacked(String dimensionID, String seriesName) {
        return this.isStacked(dimensionID);
    }

    protected String getStackedKey(String dimensionID) {
        return dimensionID;
    }

    protected String getStackedKey(String dimensionID, String seriesName) {
        return this.getStackedKey(dimensionID);
    }

    protected int yAxisIndex(String dimensionID) {
        int regionID = Integer.parseInt(this.getRegionID(dimensionID));

        return (regionID - TARGET) / TARGET_BASE;
    }

    //@shine todo:4.1版本整理下value的处理。所有图表value的 null infinity scale format 等统一在这边处理。
    // 处理原始数据。数量级和小数位数。
    private String numberScaleAndFormat(String dimensionID, double value) {
        double scale = numberScale(dimensionID);
        value = value / scale;
        return numberFormat(dimensionID, value);
    }

    protected double numberScale(String dimensionID) {

        int level = this.numberLevel(dimensionID);

        return this.numberScaleByLevel(level);
    }


    protected String numberFormat(String dimensionID, double y) {
        y = checkInfinity(y);
        try {
            BISummaryTarget target = this.getBITargetByID(dimensionID);
            JSONObject settings = target.getChartSetting().getSettings();
            int type = settings.optInt("formatDecimal", BIReportConstant.TARGET_STYLE.FORMAT.NORMAL);//默认为自动
            String format;
            switch (type) {
                case BIReportConstant.TARGET_STYLE.FORMAT.NORMAL:
                    format = "#.##";
                    break;
                case BIReportConstant.TARGET_STYLE.FORMAT.ZERO2POINT:
                    format = "#0";
                    break;
                default:
                    format = "#0.";
                    for (int i = 0; i < type; i++) {
                        format += "0";
                    }
            }
            DecimalFormat decimalFormat = new DecimalFormat(format);
            return decimalFormat.format(y);
        } catch (Exception e) {
            BILoggerFactory.getLogger().error(e.getMessage(), e);
        }
        return y + "";
    }

    protected double numberScaleByLevel(int level) {

        double scale = 1.0;

        if (level == BIReportConstant.TARGET_STYLE.NUM_LEVEL.TEN_THOUSAND) {
            scale = Math.pow(10, 4);
        } else if (level == BIReportConstant.TARGET_STYLE.NUM_LEVEL.MILLION) {
            scale = Math.pow(10, 6);
        } else if (level == BIReportConstant.TARGET_STYLE.NUM_LEVEL.YI) {
            scale = Math.pow(10, 8);
        } else if (level == BIReportConstant.TARGET_STYLE.NUM_LEVEL.PERCENT) {
            scale = 0.01;
        }

        return scale;
    }


    protected String scaleUnit(int level) {
        String unit = StringUtils.EMPTY;

        if (level == BIReportConstant.TARGET_STYLE.NUM_LEVEL.TEN_THOUSAND) {

            unit = getLocText("BI-Basic_Wan");

        } else if (level == BIReportConstant.TARGET_STYLE.NUM_LEVEL.MILLION) {

            unit = getLocText("BI-Basic_Million");

        } else if (level == BIReportConstant.TARGET_STYLE.NUM_LEVEL.YI) {

            unit = getLocText("BI-Basic_Yi");

        } else if (level == BIReportConstant.TARGET_STYLE.NUM_LEVEL.PERCENT) {

            unit = PERCENT_SYMBOL;

        }

        return unit;
    }

    protected int numberLevel(String dimensionID) {
        return this.numberLevelFromSettings(dimensionID);
    }

    protected int numberLevelFromSettings(String dimensionID) {
        try {
            BISummaryTarget target = this.getBITargetByID(dimensionID);

            return target.getChartSetting().getSettings().optInt("numLevel", BIReportConstant.TARGET_STYLE.NUM_LEVEL.NORMAL);
        } catch (Exception e) {
            BILoggerFactory.getLogger().error(e.getMessage(), e);
        }

        return BIReportConstant.TARGET_STYLE.NUM_LEVEL.NORMAL;
    }

    protected JSONArray getDimensionIDArray(String regionID) {
        return dimensionIdMap.get(regionID);
    }

    protected String getRegionID(String dimensionID) {
        return regionIdMap.get(dimensionID);
    }

    private int getDimSize(JSONObject view, String regionID){

        int used = 0;
        try {
            if(view.has(regionID)){
                JSONArray ids = view.optJSONArray(regionID);
                for(int i = 0, length = ids.length(); i < length; i++){
                    BITargetAndDimension dimension = this.getBITargetAndDimension(ids.optString(i));
                    if(dimension != null && dimension.isUsed()){
                        used++;
                    }
                }
            }
        }catch (Exception e){
            BILoggerFactory.getLogger(this.getClass()).error(e.getMessage(), e);
        }

        return used;
    }

    public void parseJSON(JSONObject jo, long userId) throws Exception {
        if (jo.has("view")) {
            JSONObject vjo = jo.optJSONObject("view");
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

            dealView(sorted, vjo);
        }

        this.requestURL = jo.optString("requestURL");
        this.chartType = WidgetType.parse(jo.optInt("type"));

        super.parseJSON(jo, userId);
    }

    protected String getCompleteImageUrl(String imageId) {
        return requestURL + "?op=fr_bi&cmd=get_uploaded_image&imageId=" + imageId;
    }

    protected String getLocalImagePath(String url) {
        return FRContext.getCurrentEnv().getPath() + BIBaseConstant.UPLOAD_IMAGE.IMAGE_PATH + File.separator + url;
    }

    protected void dealView(List<String> sorted, JSONObject vjo) throws JSONException {
        JSONArray ja = JSONArray.create();

        for (String region : sorted) {

            if (Integer.parseInt(region) < TARGET) {
                continue;
            }

            JSONArray tmp = vjo.getJSONArray(region);

            dimensionIdMap.put(region, tmp);//后面用来计算坐标轴和堆积属性

            for (int j = 0; j < tmp.length(); j++) {
                String key = tmp.getString(j);
                ja.put(key);
                regionIdMap.put(key, region);
            }

            vjo.remove(region);
        }

        vjo.put(BIReportConstant.REGION.TARGET1, ja);
    }

    public JSONObject createPlotOptions(JSONObject globalStyle, JSONObject settings) throws Exception {

        JSONObject plotOptions = JSONObject.create();

        plotOptions.put("animation", true);

        //tooltip的默认配置
        JSONObject tooltip = JSONObject.create();

        String widgetBg = backgroundColor();

        tooltip.put("enabled", !settings.optBoolean("bigDataMode", false)).put("animation", true).put("padding", 10).put("backgroundColor", imageBack(widgetBg) ? DARK : widgetBg)
                .put("borderRadius", 2).put("borderWidth", 0).put("shadow", true)
                .put("shared", tooltipShared())
                .put("style", JSONObject.create()
                        .put("color", this.isDarkColor(widgetBg) ? WHITE : DARK)
                        .put("fontSize", "14px").put("fontFamily", "Verdana"));

        plotOptions.put("tooltip", tooltip);


        plotOptions.put("dataLabels", this.createDataLabels(settings));

        plotOptions.put("borderWidth", 0);//bi的配置默认没有边框

        return plotOptions;
    }

    protected boolean tooltipShared() {
        return false;
    }

    private boolean imageBack(String bg) {
        bg = bg.substring(1);

        try {
            new Color(Integer.parseInt(bg, 16));
            return false;
        }catch (Exception e){
            return true;
        }
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

    //默认是分类，系列，值的配置
    protected JSONObject createDataLabels(JSONObject settings) throws JSONException {
        boolean miniMode = settings.optBoolean("miniMode", false);//极简
        boolean largeMode = settings.optBoolean("bigDataMode", false);//大数据
        boolean showDataLabel = settings.optBoolean("showDataLabel", false) || miniMode;
        showDataLabel = showDataLabel && !largeMode;
        JSONObject dataLabels = JSONObject.create().put("enabled", showDataLabel).put("autoAdjust", true);

        if (showDataLabel) {
            JSONObject dataLabelSetting = this.defaultDataLabelSetting();
            dataLabelSetting = settings.has("dataLabelSetting") ? merge(settings.optJSONObject("dataLabelSetting"), dataLabelSetting) : dataLabelSetting;

            JSONObject formatter = JSONObject.create();

            formatter.put("identifier", miniMode ? VALUE : labelIdentifier(dataLabelSetting));

            dataLabels.put("formatter", formatter);
            dataLabels.put("style", dataLabelSetting.optJSONObject("textStyle"));
            dataLabels.put("align", this.dataLabelAlign(dataLabelSetting.optInt("position")));
            dataLabels.put("autoAdjust", dataLabelSetting.optBoolean("optimizeLabel"));

            dataLabels.put("connectorWidth", dataLabelSetting.optBoolean("showTractionLine") == true ? 1 : 0);
        }

        return dataLabels;
    }

    private String labelIdentifier(JSONObject dataLabelSetting) {
        String identifier = "";

        if (dataLabelSetting.optBoolean("showCategoryName")) {
            identifier += categoryLabelKey();
        }
        if (dataLabelSetting.optBoolean("showSeriesName")) {
            identifier += seriesLabelKey();
        }
        if (dataLabelSetting.optBoolean("showValue")) {
            identifier += valueLabelKey();
        }
        if (dataLabelSetting.optBoolean("showPercentage") || dataLabelSetting.optBoolean("showConversionRate")) {
            identifier += PERCENT;
        }
        if (dataLabelSetting.optBoolean("showArrivalRate")) {
            identifier += ARRIVALRATE;
        }
        if (dataLabelSetting.optBoolean("showXValue")) {
            identifier += X;
        }
        if (dataLabelSetting.optBoolean("showYValue")) {
            identifier += Y;
        }
        if (dataLabelSetting.optBoolean("showBlockName")) {
            identifier += NAME;
        }
        if (dataLabelSetting.optBoolean("showTargetName")) {
            identifier += SERIES;
        }
        return identifier;
    }

    protected String dataLabelAlign(int position) {
        if (position == BIChartSettingConstant.DATA_LABEL.POSITION_OUTER) {
            return "outside";
        } else if (position == BIChartSettingConstant.DATA_LABEL.POSITION_INNER) {
            return "inside";
        }
        return "center";
    }

    //勾选标签没具体配置的时候的默认值
    protected JSONObject defaultDataLabelSetting() throws JSONException {
        return JSONObject.create().put("showCategoryName", false)
                .put("showSeriesName", false).put("showValue", true).put("showPercentage", false)
                .put("position", BIChartSettingConstant.DATA_LABEL.POSITION_OUTER).put("showTractionLine", true)
                .put("textStyle", defaultFont());

    }

    private boolean isDarkColor(String colorStr) {

        if (StringUtils.isEmpty(colorStr) || ComparatorUtils.equals(colorStr, "transparent")) {
            return false;
        }

        colorStr = colorStr.substring(1);

        try {
            Color color = new Color(Integer.parseInt(colorStr, 16));
            return color.getRed() * RED_DET + color.getGreen() * GREEN_DET + color.getBlue() * BLUE_DET < GRAY;
        }catch (Exception e){
            //产品规定图片背景为深色
            return true;
        }
    }

    protected JSONObject populateDefaultSettings() throws JSONException {
        JSONObject settings = JSONObject.create();

        //图例
        settings.put("legend", BIChartSettingConstant.CHART_LEGENDS.BOTTOM)
                .put("legendStyle", this.defaultFont());

        settings.put("clickZoom", true);

        return settings;
    }

    private String checkTransparent(String color) {
        if (color.equals("transparent")) {
            return "rgba(0,0,0,0)";
        }
        return color;
    }

    private boolean isTransparent(String backgroundColor) {
        return backgroundColor.equals("transparent");
    }

    private boolean isAuto(String color) {
        return StringUtils.isEmpty(color);
    }

    private boolean hasValidColor(String back){
        return !isAuto(back) && !isTransparent(back);
    }

    private String backgroundColor() {
        boolean transparent = false;
        JSONObject settings = this.getChartSetting().getDetailChartSetting();
        if (settings.has("widgetBG")) {
            String widgetBG = settings.optJSONObject("widgetBG").optString("value");
            if(hasValidColor(widgetBG)){
                return widgetBG;
            }
            transparent = isTransparent(widgetBG);
        }

        if (!transparent && globalStyle.has("widgetBackground")) {
            String widgetBG = globalStyle.optJSONObject("widgetBackground").optString("value");
            if(hasValidColor(widgetBG)){
                return widgetBG;
            }
            transparent = isTransparent(widgetBG);
        }

        BIChartStyleAttr platConfig = FBIConfig.getProviderInstance().getChartStyleAttr();
        if (!transparent && platConfig.getWidgetBackground() != null) {
            String widgetBG = platConfig.getWidgetBackground().getValue();
            if(hasValidColor(widgetBG)){
                return widgetBG;
            } else if(isAuto(widgetBG)){
                return globalStyle.optString("theme").equals("bi-theme-dark") ? DARK : WHITE;
            }
        }

        if (globalStyle.has("mainBackground")) {
            String widgetBG = globalStyle.optJSONObject("mainBackground").optString("value");
            if(hasValidColor(widgetBG)){
                return widgetBG;
            }
            transparent = isTransparent(widgetBG);
        }

        if (!transparent && platConfig.getMainBackground() != null) {
            String widgetBG = platConfig.getMainBackground().getValue();
            if(hasValidColor(widgetBG)){
                return widgetBG;
            }
        }

        return WHITE;
    }


    //优先级从低到高：plat界面背景，global界面背景，主题，plat组件背景，global组件背景，setting组件背景，plat图表文字， global图表文字，settings图表文字
    protected JSONObject defaultFont() throws JSONException {
        BIChartStyleAttr platConfig = FBIConfig.getProviderInstance().getChartStyleAttr();
        String color = DARK, fontWeight = "normal", fontStyle = "normal";

        String back = backgroundColor();
        if (hasValidColor(back)) {
            color = isDarkColor(back) ? WHITE : DARK;
        }

        BIChartFontStyleAttr fontStyleAttr = platConfig.getChartFont();
        if (fontStyleAttr != null) {
            String fontColor = fontStyleAttr.getColor();
            if (StringUtils.isNotEmpty(fontColor)) {
                color = checkTransparent(fontColor);
            }
            fontWeight = fontStyleAttr.getFontWidget();
            fontStyle = fontStyleAttr.getFontStyle();
        }

        JSONObject chartFont = globalStyle.optJSONObject("chartFont");
        if (chartFont != null) {
            String fontColor = chartFont.optString("color");
            if (StringUtils.isNotEmpty(fontColor)) {
                color = checkTransparent(fontColor);
            }
            fontWeight = chartFont.optString("fontWeight", fontWeight);
            fontStyle = chartFont.optString("fontStyle", fontStyle);
        }
        return JSONObject.create().put("fontFamily", "Microsoft YaHei").put("fontSize", "12px")
                .put("color", color).put("fontWeight", fontWeight).put("fontStyle", fontStyle);
    }

    //颜色自动，则use default color
    private boolean autoColor(JSONObject target, String key) {
        return ComparatorUtils.equals("color", key) && StringUtils.isEmpty(target.optString(key));
    }

    //todo 不知道有没有实现过，先撸一下
    protected JSONObject merge(JSONObject target, JSONObject source) throws JSONException {
        Iterator it = source.keys();
        while (it.hasNext()) {
            String key = it.next().toString();
            if (!target.has(key) || autoColor(target, key)) {
                target.put(key, source.get(key));
            } else {//主要是想把style里面的颜色需要merge一下，自动的话用defaultColor
                JSONObject targetObject = target.optJSONObject(key);
                JSONObject sourceObject = source.optJSONObject(key);
                if (targetObject != null && sourceObject != null) {
                    merge(targetObject, sourceObject);
                }
            }
        }
        return target;
    }

    //todo:@shine 每次get都populate and merge一遍
    protected JSONObject getDetailChartSetting() throws JSONException {
        JSONObject settings = this.getChartSetting().getDetailChartSetting();

        return merge(settings, this.populateDefaultSettings());
    }

    public JSONObject createChartConfigWidthData(BISessionProvider session, HttpServletRequest req, JSONObject data) throws Exception {

        this.locale = WebUtils.getLocale(req);

        //globalStyle从前台传过来的json取，不从.fbi模板取原因：设置全局样式，先刷新图表，后save模板，所以刷新图表取得全局样式不是最新的
        this.globalStyle = this.getChartSetting().getGlobalStyle();
        this.globalStyle = this.globalStyle == null ? JSONObject.create() : this.globalStyle;

        return this.createOptions(globalStyle, data).put("data", data);
    }

    public JSONObject createDataJSON(BISessionProvider session, HttpServletRequest req) throws Exception {

        // 如果是实时数据
        if (needOpenBigDateModel()) {
            setOperator(BIReportConstant.TABLE_PAGE_OPERATOR.BIGDATACHART);
        }
        JSONObject data = super.createDataJSON(session, req).getJSONObject("data");

        JSONObject options = this.createChartConfigWidthData(session, req, data);

        // 如果是大数据模式,而且分组数大于BigDataChartOperator.MAXROW 或者是图表是交叉表类型且top分组大于BigDataChartOperator.MAXROW
        boolean isLarge = (data.has("c") && data.getJSONArray("c").length() > BigDataChartOperator.MAXROW)
                || (data.has("t") && data.getJSONObject("t").has("c") && data.getJSONObject("t").getJSONArray("c").length() > BigDataChartOperator.MAXROW);
        if (needOpenBigDateModel() && isLarge) {
            options.put("chartBigDataModel", true);
        }
        return options;
    }

    /**
     * 是否需要打开大数据模式
     *
     * @return
     */
    protected boolean needOpenBigDateModel() {

        DetailChartSetting cs = getChartSetting();
        JSONObject setting = cs.getDetailChartSetting();
        if (setting.has("bigDataMode") && setting.optBoolean("bigDataMode", false)) {
            return false;
        }
        return isRealData();
    }

    /*
    * 如果没有的话，使用默认值
    * */
    protected JSONArray parseColors(JSONObject settings, JSONObject globalStyle, JSONObject plateConfig) throws Exception {

        if (settings.has("chartColor")) {
            return settings.getJSONArray("chartColor");
        } else if (globalStyle.has("chartColor")) {
            return globalStyle.getJSONArray("chartColor");
        } else if (plateConfig.has("defaultColor")) {
            String key = plateConfig.optString("defaultColor");
            JSONArray styleList = plateConfig.optJSONArray("styleList");
            for (int i = 0, len = styleList.length(); i < len; i++) {
                JSONObject predefinedStyle = styleList.getJSONObject(i);
                if (key.equals(predefinedStyle.optString("value"))) {
                    return predefinedStyle.optJSONArray("colors");
                }
            }
        }

        return JSONArray.create().put("#5caae4").put("#70cc7f").put("#ebbb67").put("#e97e7b").put("#6ed3c9");
    }

    private String parseStyle(JSONObject settings, JSONObject globalStyle, JSONObject plateConfig) throws JSONException {
        int style = BIChartSettingConstant.CHART_STYLE.STYLE_NORMAL;
        try {
            if (settings.has("chartStyle")) {
                style = settings.optInt("chartStyle");
            } else if (null != globalStyle && globalStyle.has("chartStyle")) {
                style = globalStyle.optInt("chartStyle");
            } else if (plateConfig.has("chartStyle")) {
                style = plateConfig.optInt("chartStyle");
            }
        } catch (Exception e) {
            BILoggerFactory.getLogger(this.getClass()).error(e.getMessage(), e);
        }
        return style == BIChartSettingConstant.CHART_STYLE.STYLE_GRADUAL ? "gradual" : "normal";
    }

    protected String tooltipValueFormat(BISummaryTarget dimension) {
        return this.valueFormatFunc(dimension, true);
    }

    protected String dataLabelValueFormat(BISummaryTarget dimension) {
        return this.valueFormatFunc(dimension, true);
    }

    protected String decimalFormat(BISummaryTarget dimension, boolean hasSeparator) {
        JSONObject settings = dimension.getChartSetting().getSettings();
        int type = settings.optInt("formatDecimal", BIReportConstant.TARGET_STYLE.FORMAT.NORMAL);//默认为自动
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
                for (int i = 0; i < type; i++) {
                    format += "0";
                }
        }

        return format;
    }

    //值标签和小数位数，千分富符，数量级和单位构成的后缀
    protected String valueFormatFunc(BISummaryTarget dimension, boolean isTooltip) {

        String format = this.valueFormat(dimension);

        String unit = this.valueUnit(dimension, isTooltip);

        return String.format("function(){return BI.contentFormat(arguments[0], \"%s\") + \"%s\"}", format, unit);
    }

    protected String unitFromSetting(BISummaryTarget dimension) {
        JSONObject settings = dimension.getChartSetting().getSettings();
        return settings.optString("unit", StringUtils.EMPTY);
    }

    protected boolean hasSeparatorFromSetting(BISummaryTarget dimension) {
        JSONObject settings = dimension.getChartSetting().getSettings();
        return settings.optBoolean("numSeparators", true);
    }

    //数量级和单位，直接在值后面加，不会改变数值的。（数值为数量级处理过的，不是原始数值）
    protected String valueUnit(BISummaryTarget dimension, boolean isTooltip) {
        String scaleUnit = this.scaleUnit(this.numberLevel(dimension.getId()));
        String unit = unitFromSetting(dimension);

        if (showUnit(isTooltip)) {
            return (scaleUnit + unit);
        } else if (scaleUnit.equals(PERCENT_SYMBOL)) {//标签也要把百分号加上
            return scaleUnit;
        }
        return StringUtils.EMPTY;
    }

    protected boolean showUnit(boolean isTooltip) {
        return isTooltip;
    }

    //小数位数和千分符，即会改变数值的
    protected String valueFormat(BISummaryTarget dimension) {
        boolean hasSeparator = hasSeparatorFromSetting(dimension);

        return this.decimalFormat(dimension, hasSeparator);
    }

    protected String intervalLegendFormatter(String format, String unit) {
        return String.format("function(){return BI.contentFormat(arguments[0].from, \"%s\") + \"%s\" + \"-\" + BI.contentFormat(arguments[0].to, \"%s\") + \"%s\"}", format, unit, format, unit);
    }

    protected String gradualLegendFormatter(String format) {
        return String.format("function(){return BI.contentFormat(arguments[0], \"%s\")}", format);
    }

    protected BISummaryTarget getSerBITarget(JSONObject ser) throws Exception {
        JSONArray ids = ser.optJSONArray("targetIDs");
        return ids == null ? null : getBITargetByID(ids.optString(0));
    }

    protected void formatSeriesTooltipFormat(JSONObject options) throws Exception {
        this.defaultFormatSeriesTooltipFormat(options);
    }

    protected void defaultFormatSeriesTooltipFormat(JSONObject options) throws Exception {
        JSONObject tooltip = options.optJSONObject("plotOptions").optJSONObject("tooltip");

        JSONArray series = options.optJSONArray("series");

        for (int i = 0, len = series.length(); i < len; i++) {
            JSONObject ser = series.getJSONObject(i);

            if (ser.optBoolean(TRANS_SERIES)) {
                continue;
            }

            JSONObject formatter = JSONObject.create();

            formatter.put("identifier", this.getTooltipIdentifier())
                    .put("valueFormat", this.tooltipValueFormat(this.getSerBITarget(ser)))
                    .put("percentFormat", "function(){return BI.contentFormat(arguments[0], \"#.##%\")}")
                    .put("arrivalRateFormat", "function(){return BI.contentFormat(arguments[0], \"#.##%\")}");

            ser.put("tooltip", new JSONObject(tooltip.toString()).put("formatter", formatter));
        }
    }

    protected String getTooltipIdentifier() {
        return CATEGORY + SERIES + VALUE;
    }

    //gauge deal valueLabel
    protected String dataLabelsKey() {
        return "dataLabels";
    }

    protected void formatSeriesDataLabelFormat(JSONObject options) throws Exception {
        this.defaultFormatSeriesDataLabelFormat(options);
    }

    //todo: @shine 4.1版本现在的label是遍历series和point，每个都plotoptions。labeljaon。tostring。不好。
    //todo: @shine 4.1版本所有涉及到遍历point和series看看能不能归到一起。
    protected void defaultFormatSeriesDataLabelFormat(JSONObject options) throws Exception {
        JSONObject dataLabels = options.optJSONObject("plotOptions").optJSONObject(dataLabelsKey());

        if (dataLabels.optBoolean("enabled")) {
            JSONArray series = options.optJSONArray("series");

            for (int i = 0, len = series.length(); i < len; i++) {
                JSONObject ser = series.getJSONObject(i);

                if (ser.optBoolean(TRANS_SERIES)) {
                    continue;
                }

                JSONObject labels = new JSONObject(dataLabels.toString());
                labels.optJSONObject("formatter")
                        .put("valueFormat", this.dataLabelValueFormat(this.getSerBITarget(ser)))
                        .put("percentFormat", "function(){return BI.contentFormat(arguments[0], \"#.##%\")}")
                        .put("arrivalRateFormat", "function(){return BI.contentFormat(arguments[0], \"#.##%\")}");


                ser.put(dataLabelsKey(), labels);
            }
        }
    }

    protected String categoryKey() {
        return "x";
    }

    protected String valueKey() {
        return "y";
    }

    protected JSONArray createXYSeries(JSONObject originData) throws Exception {
        return originData.has("t") ? this.createSeriesWithTop(originData) : this.createSeriesWithChildren(originData);
    }

    private JSONArray createSeriesWithTop(JSONObject originData) throws Exception {
        BIDimension category = this.getCategoryDimension(), seriesDim = this.getSeriesDimension();
        JSONArray series = JSONArray.create();
        String[] targetIDs = this.getUsedTargetID();
        String[] dimensionIDs = this.getUsedDimensionID();
        String categoryKey = this.categoryKey(), valueKey = this.valueKey();
        ArrayList<Double> valueList = new ArrayList<Double>();
        JSONObject top = originData.optJSONObject("t"), left = originData.optJSONObject("l");
        if (targetIDs.length == 0 || !top.has("c")) {
            return series;
        }
        JSONArray topC = top.getJSONArray("c"), leftC = left.optJSONArray("c");
        String id = targetIDs[0];
        int yAxis = this.yAxisIndex(id);
        double numberScale = this.numberScale(targetIDs[0]);
        for (int i = 0; i < topC.length(); i++) {
            JSONObject tObj = topC.getJSONObject(i);
            String name = tObj.getString("n"), formattedName = this.formatDimension(seriesDim, name);
            String stackedKey = this.getStackedKey(id, formattedName);
            boolean isStacked = this.isStacked(id, formattedName);
            JSONArray data = JSONArray.create();
            if(leftC != null) {
                for (int j = 0; j < leftC.length(); j++) {
                    JSONObject lObj = leftC.getJSONObject(j);
                    String x = lObj.getString("n");
                    JSONArray array = lObj.getJSONObject("s").optJSONArray("c");
                    if (array == null) {
                        continue;
                    }
                    JSONArray s = array.getJSONObject(i).getJSONArray("s");
                    boolean isNull = s.isNull(0) || Double.isNaN(s.getDouble(0));
                    double y = (isNull ? 0 : s.getDouble(0)) / numberScale;
                    String formattedCategory = this.formatDimension(category, x);
                    data.put(JSONObject.create().put(categoryKey, formattedCategory).put(valueKey, isNull ? "-" : numberFormat(id, y)).put(LONG_DATE, x));
                    valueList.add(y);
                }
            } else {
                formattedName = StringUtils.EMPTY;
            }
            JSONObject ser = JSONObject.create().put("data", data).put("name", formattedName).put(LONG_DATE, name)
                    .put("type", this.getSeriesType(id, formattedName)).put("yAxis", yAxis)
                    .put("dimensionIDs", dimensionIDs)
                    .put("targetIDs", JSONArray.create().put(id));

            if (isStacked) {
                ser.put("stack", stackedKey);
            }
            series.put(ser);
        }
        this.idValueMap.put(targetIDs[0], valueList);

        return series;
    }

    protected double checkInfinity(double y) {
        return (y == Double.POSITIVE_INFINITY || y == Double.NEGATIVE_INFINITY) ? 0 : y;
    }

    protected JSONArray createSeriesWithChildren(JSONObject originData) throws Exception {
        BIDimension category = this.getCategoryDimension(), seriesDim = this.getSeriesDimension();

        if (seriesDim != null) {
            return this.createSeriesWithSeriesDimension(originData, seriesDim);
        }

        JSONArray series = JSONArray.create();
        String[] targetIDs = this.getUsedTargetID();
        String[] dimensionIDs = this.getUsedDimensionID();
        String categoryKey = this.categoryKey(), valueKey = this.valueKey();
        JSONArray children = originData.optJSONArray("c");
        for (int i = 0, len = targetIDs.length; i < len; i++) {
            String id = targetIDs[i], type = this.getSeriesType(id), stackedKey = this.getStackedKey(id);
            int yAxis = this.yAxisIndex(id);
            ArrayList<Double> valueList = new ArrayList<Double>();
            double numberScale = this.numberScale(id);
            JSONArray data = JSONArray.create();
            if (children != null) {
                for (int j = 0, count = children.length(); j < count; j++) {
                    JSONObject lObj = children.getJSONObject(j);
                    String x = lObj.getString("n");
                    JSONArray targetValues = lObj.getJSONArray("s");
                    double y = targetValues.isNull(i) ? 0 : targetValues.getDouble(i) / numberScale;
                    String formattedCategory = this.formatDimension(category, x);
                    data.put(
                            JSONObject.create().put(categoryKey, formattedCategory).put(valueKey, targetValues.isNull(i) ? "-" : numberFormat(id, y)).put(LONG_DATE, x)//lngData为原始值，column fill image condition
                    );
                    valueList.add(y);
                }
            } else {//没有分类，只有指标。会过来一个汇总值，没有child
                JSONArray targetValues = originData.optJSONArray("s");
                double y = targetValues.isNull(i) ? 0 : targetValues.getDouble(i) / numberScale;
                data.put(JSONObject.create().put(valueKey, numberFormat(id, y)).put(categoryKey, StringUtils.EMPTY));
                valueList.add(y);
            }
            JSONObject ser = JSONObject.create().put("data", data).put("name", getDimensionNameByID(id))
                    .put("type", type).put("yAxis", yAxis)
                    .put("dimensionIDs", dimensionIDs)
                    .put("targetIDs", JSONArray.create().put(id));
            if (this.isStacked(id)) {
                ser.put("stack", stackedKey + yAxis);
            }
            series.put(ser);
            this.idValueMap.put(id, valueList);
        }
        return series;
    }

    //系列指标不为空的时候创建多个系列
    protected JSONArray createSeriesWithSeriesDimension(JSONObject originData, BIDimension seriesDim) throws Exception {
        JSONArray series = JSONArray.create();
        String[] targetIDs = this.getUsedTargetID();
        String[] dimensionIDs = this.getUsedDimensionID();
        String categoryKey = this.categoryKey(), valueKey = this.valueKey();
        JSONArray children = originData.optJSONArray("c");
        for (int i = 0, len = targetIDs.length; i < len; i++) {
            String id = targetIDs[i], type = this.getSeriesType(id);
            int yAxis = this.yAxisIndex(id);
            ArrayList<Double> valueList = new ArrayList<Double>();
            double numberScale = this.numberScale(id);
            if (children != null) {
                for (int j = 0, count = children.length(); j < count; j++) {
                    JSONObject lObj = children.getJSONObject(j);
                    String seriesName = lObj.getString("n");
                    String formattedName = this.formatDimension(seriesDim, seriesName);
                    JSONArray targetValues = lObj.getJSONArray("s");
                    double y = targetValues.isNull(i) ? 0 : targetValues.getDouble(i) / numberScale;

                    JSONObject datum = JSONObject.create().put(categoryKey, StringUtils.EMPTY).put(valueKey, targetValues.isNull(i) ? "-" : numberFormat(id, y));

                    JSONObject ser = JSONObject.create().put("data", JSONArray.create().put(datum)).put("name", getDimensionNameByID(id))
                            .put("type", type).put("yAxis", yAxis)
                            .put("dimensionIDs", dimensionIDs)
                            .put("targetIDs", JSONArray.create().put(id))
                            .put("name", formattedName).put(LONG_DATE, seriesName);
                    series.put(ser);
                    valueList.add(y);
                }
            }

            this.idValueMap.put(id, valueList);
        }

        return series;
    }

    protected String formatDimension(BIDimension dimension, String dimensionName) {
        if (dimension == null || StringUtils.isBlank(dimensionName)) {
            return dimensionName;
        }

        int groupType = dimension.getGroup().getType();
        JSONObject dateFormat = dimension.getChartSetting().getSettings().optJSONObject("dateFormat");

        int dateFormatType = dateFormat == null ? BIReportConstant.DATE_FORMAT.SPLIT : dateFormat.optInt("type", BIReportConstant.DATE_FORMAT.SPLIT);

        Number dateCategory = StableUtils.string2Number(dimensionName);
        long dateValue = dateCategory == null ? 0L : dateCategory.longValue();

        switch (groupType) {
            case BIReportConstant.GROUP.S:
                dimensionName = FULL_QUARTER_NAMES[(int) dateValue];
                break;
            case BIReportConstant.GROUP.M:
                dimensionName = FULL_MONTH_NAMES[(int) dateValue];
                break;
            case BIReportConstant.GROUP.W:
                dimensionName = FULL_WEEK_NAMES[(int) dateValue];
                break;
            case BIReportConstant.GROUP.YMD:
                dimensionName = this.formatYMDByDateFormat(dateValue, dateFormatType);
                break;
            case BIReportConstant.GROUP.YMDHMS:
                dimensionName = this.formatYMDHMSByDateFormat(dateValue, dateFormatType);
                break;
            case BIReportConstant.GROUP.YMDH:
                dimensionName = this.formatYMDHByDateFormat(dateValue, dateFormatType);
                break;
            case BIReportConstant.GROUP.YMDHM:
                dimensionName = this.formatYMDHMByDateFormat(dateValue, dateFormatType);
                break;
            case BIReportConstant.GROUP.YS:
                dimensionName = this.formatYSByDateFormat(dateValue, dateFormatType);
                break;
            case BIReportConstant.GROUP.YM:
                dimensionName = this.formatYMByDateFormat(dateValue, dateFormatType);
                break;
            case BIReportConstant.GROUP.YW:
                dimensionName = this.formatYWByDateFormat(dateValue, dateFormatType);
                break;
        }

        return dimensionName;
    }

    private String formatYMDByDateFormat(long dateValue, int dateFormatType) {
        Date date = new Date(dateValue);
        SimpleDateFormat formatter;
        if (dateFormatType == BIReportConstant.DATE_FORMAT.CHINESE) {
            formatter = new SimpleDateFormat(String.format("yyyy%sMM%sdd%s", getLocText("BI-Basic_Year"), getLocText("BI-Basic_Month"), getLocText("BI-Date_Day")));
        } else {
            formatter = new SimpleDateFormat("yyyy-MM-dd");
        }
        return formatter.format(date);
    }

    private String formatYMDHMSByDateFormat(long dateValue, int dateFormatType) {
        Date date = new Date(dateValue);
        SimpleDateFormat formatter;

        if (dateFormatType == BIReportConstant.DATE_FORMAT.CHINESE) {
            formatter = new SimpleDateFormat(String.format("yyyy%sMM%sdd%s H%sm%ss%s", getLocText("BI-Basic_Year"), getLocText("BI-Basic_Month"), getLocText("BI-Date_Day"),
                    getLocText("BI-Hour_Sin"), getLocText("BI-Basic_Minute"), getLocText("BI-Basic_Seconds")));
        } else {
            formatter = new SimpleDateFormat("yyyy-MM-dd H:m:s");
        }
        return formatter.format(date);
    }

    private String formatYMDHByDateFormat(long dateValue, int dateFormatType) {
        Date date = new Date(dateValue);
        SimpleDateFormat formatter;

        if (dateFormatType == BIReportConstant.DATE_FORMAT.CHINESE) {
            formatter = new SimpleDateFormat(String.format("yyyy%sMM%sdd%s H%s", getLocText("BI-Basic_Year"), getLocText("BI-Basic_Month"),
                    getLocText("BI-Date_Day"), getLocText("BI-Hour_Sin")));
        } else {
            formatter = new SimpleDateFormat("yyyy-MM-dd H");
        }
        return formatter.format(date);
    }

    private String formatYMDHMByDateFormat(long dateValue, int dateFormatType) {
        Date date = new Date(dateValue);
        SimpleDateFormat formatter;

        if (dateFormatType == BIReportConstant.DATE_FORMAT.CHINESE) {
            formatter = new SimpleDateFormat(String.format("yyyy%sMM%sdd%s H%sm%s", getLocText("BI-Basic_Year"), getLocText("BI-Basic_Month"), getLocText("BI-Date_Day"),
                    getLocText("BI-Hour_Sin"), getLocText("BI-Basic_Minute")));
        } else {
            formatter = new SimpleDateFormat("yyyy-MM-dd H:m");
        }
        return formatter.format(date);
    }

    private String formatYMByDateFormat(long dateValue, int dateFormatType) {
        Date date = new Date(dateValue);
        SimpleDateFormat formatter;

        if (dateFormatType == BIReportConstant.DATE_FORMAT.CHINESE) {
            formatter = new SimpleDateFormat(String.format("yyyy%sMM%s", getLocText("BI-Basic_Year"), getLocText("BI-Basic_Month")));
        } else {
            formatter = new SimpleDateFormat("yyyy-MM");
        }
        return formatter.format(date);
    }

    private int getWeekOfYear(Date date) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        int weekOfYear = c.get(Calendar.WEEK_OF_YEAR);
        int mouth = c.get(Calendar.MONTH);
        //如果月份是12月，且求出来的周数是第一周，说明该日期实质上是这一年的第53周，也是下一年的第一周
        if (mouth >= MONTH_COUNT - 1 && weekOfYear <= 1) {
            weekOfYear += WEEK_COUNT;
        }
        return weekOfYear;
    }

    private String formatYWByDateFormat(long dateValue, int dateFormatType) {
        Date date = new Date(dateValue);
        SimpleDateFormat formatter;
        int week = getWeekOfYear(date);
        if (dateFormatType == BIReportConstant.DATE_FORMAT.CHINESE) {
            formatter = new SimpleDateFormat(String.format("yyyy%s" + week + "%s", getLocText("BI-Basic_Year"), getLocText("BI-Week_Simple")));
        } else {
            formatter = new SimpleDateFormat("yyyy-" + week);
        }
        return formatter.format(date);
    }

    private int getSeason(Date date) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        int month = c.get(Calendar.MONTH);
        return month / 3 + 1;
    }

    private String formatYSByDateFormat(long dateValue, int dateFormatType) {
        Date date = new Date(dateValue);
        SimpleDateFormat formatter;
        int season = getSeason(date);
        if (dateFormatType == BIReportConstant.DATE_FORMAT.CHINESE) {
            formatter = new SimpleDateFormat(String.format("yyyy%s" + season + "%s", getLocText("BI-Basic_Year"), getLocText("BI-Basic_Quarter")));
        } else {
            formatter = new SimpleDateFormat("yyyy-" + season);
        }
        return formatter.format(date);
    }

    protected JSONObject parseLegend(JSONObject settings) throws JSONException {

        int legend = settings.optInt("legend");
        String position = "top";

        if (legend == BIChartSettingConstant.CHART_LEGENDS.RIGHT) {
            position = "right";
        } else if (legend == BIChartSettingConstant.CHART_LEGENDS.BOTTOM) {
            position = "bottom";
        } else if (legend == BIChartSettingConstant.CHART_LEGENDS.LEFT) {
            position = "left";
        }

        return JSONObject.create()
                .put("maxHeight", COMPONENT_MAX_SIZE)
                .put("maxWidth", COMPONENT_MAX_SIZE)
                .put("visible", legend >= BIChartSettingConstant.CHART_LEGENDS.TOP)
                .put("enabled", true)
                .put("position", position)
                .put("style", settings.optJSONObject("legendStyle"));
    }

    protected JSONArray mapStyleToRange(JSONArray mapStyle) throws JSONException {
        JSONArray ranges = JSONArray.create();

        for (int i = 0, len = mapStyle.length(); i < len; i++) {
            JSONObject config = mapStyle.getJSONObject(i), range = config.optJSONObject("range");

            ranges.put(
                    JSONObject.create()
                            .put("from", range.optDouble("min", Integer.MIN_VALUE))
                            .put("to", range.optDouble("max", Integer.MAX_VALUE))
                            .put("color", config.optString("color"))
            );


        }

        return ranges;
    }

    public BIDimension getCategoryDimension(int index) {
        List<String> dimensionIds = view.get(Integer.parseInt(BIReportConstant.REGION.DIMENSION1));
        if (dimensionIds == null) {
            return null;
        }

        int dIndex = 0;
        for (BIDimension dimension : this.getDimensions()) {
            if (dimensionIds.contains(dimension.getValue()) && dimension.isUsed()) {
                if (dIndex == index) {
                    return dimension;
                }
                dIndex++;
            }
        }
        return null;
    }

    public BIDimension getCategoryDimension() {
        return this.getCategoryDimension(0);
    }

    public BIDimension getSeriesDimension() {
        List<String> dimensionIds = view.get(Integer.parseInt(BIReportConstant.REGION.DIMENSION2));
        if (dimensionIds == null) {
            return null;
        }
        for (BIDimension dimension : this.getDimensions()) {
            if (dimensionIds.contains(dimension.getValue()) && dimension.isUsed()) {
                return dimension;
            }
        }
        return null;
    }

    public JSONObject createPhantomJSONConfig(BISessionProvider session, HttpServletRequest req) throws Exception {
        JSONObject options = this.createDataJSON(session, req);

        if (options.has("geo")) {
            JSONObject geo = options.optJSONObject("geo");
            String path = geo.optString("data", StringUtils.EMPTY).replace(BIMapInfoManager.ACTION_PREFIX, StringUtils.EMPTY);
            InputStream in = FRContext.getCurrentEnv().readResource(StableUtils.pathJoin(new String[]{BIMapInfoManager.JSON_FOLDER, CodeUtils.cjkDecode(path)}));
            String string = IOUtils.inputStream2String(in);
            geo.put("data", new JSONObject(string.replace('\uFEFF', ' ')));
            options.put("geo", geo);
        }

        options.remove("zoom");

        JSONObject plotOptions = options.optJSONObject("plotOptions");
        options.put("plotOptions", plotOptions.put("animation", false));

        options.put("toPhantom", true);

        return options;
    }

    protected String getRequestURL() {
        return this.requestURL;
    }

    public Double[] getValuesByID(String id) {
        if (this.idValueMap.containsKey(id)) {
            return this.idValueMap.get(id).toArray(new Double[0]);
        }
        return new Double[0];
    }

    protected WidgetType getChartType() {
        return this.chartType;
    }

    protected String getLocText(String key) {
        return Inter.getLocText(key, this.locale);
    }

    public int getDim1Size() {
        return dim1Size;
    }

    public int getDim2Size() {
        return dim2Size;
    }

    public int getTar1Size() {
        return tar1Size;
    }

    public int getTar2Size() {
        return tar2Size;
    }

    public int getTar3Size() {
        return tar3Size;
    }

    protected boolean checkValid(){
        return this.getTar1Size() > 0;
    }

    protected boolean hasTarget(){
        return this.tar1Size > 0 || this.tar2Size > 0 || this.tar3Size > 0;
    }
}