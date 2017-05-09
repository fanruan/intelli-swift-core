package com.fr.bi.cal.analyze.report.report.widget;

import com.finebi.cube.common.log.BILoggerFactory;
import com.fr.bi.cal.analyze.session.BISession;
import com.fr.bi.conf.report.WidgetType;
import com.fr.bi.conf.report.widget.field.dimension.BIDimension;
import com.fr.bi.conf.session.BISessionProvider;
import com.fr.bi.field.target.target.BISummaryTarget;
import com.fr.bi.stable.constant.BIReportConstant;
import com.fr.bi.stable.constant.BIStyleConstant;
import com.fr.bi.tool.BIReadReportUtils;
import com.fr.bi.util.BIConfUtils;
import com.fr.general.Inter;
import com.fr.json.JSONArray;
import com.fr.json.JSONException;
import com.fr.json.JSONObject;
import com.fr.stable.StableUtils;
import com.fr.stable.StringUtils;
import com.fr.web.core.SessionDealWith;
import com.fr.web.utils.WebUtils;

import javax.servlet.http.HttpServletRequest;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;

/**
 * Created by User on 2016/4/25.
 */
public abstract class VanChartWidget extends TableWidget {

    private static final double RED_DET = 0.299;
    private static final double GREEN_DET = 0.587;
    private static final double BLUE_DET = 0.114;
    private static final double GRAY = 192;

    private static final String STACK_ID_PREFIX = "STACKID";

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

    //兼容前台用数字表示位置的写法，真xx丑
    private static final int TOP = 2;
    private static final int RIGHT = 3;
    private static final int BOTTOM = 4;
    private static final int LEFT = 5;

    //标签位置
    private static final int POSITION_INNER = 1;
    private static final int POSITION_OUTER = 2;
    private static final int POSITION_CENTER = 3;

    private static final int TARGET = 30000;
    private static final int TARGET_BASE = 10000;

    private static final String PERCENT_SYMBOL = "%";
    private static final String WHITE = "#ffffff";

    private static final int STYLE_NORMAL = 1; //普通风格
    private static final int STYLE_GRADUAL = 2; //渐变风格

    public static final int AUTO = 1;
    public static final int CUSTOM = 2;

    private String requestURL = StringUtils.EMPTY;
    private WidgetType chartType = WidgetType.COLUMN;

    private HashMap<String, JSONArray> dimensionIdMap = new HashMap<String, JSONArray>();
    private HashMap<String, String> regionIdMap = new HashMap<String, String>();

    //存下每个指标和纬度的最大最小和平均值
    private HashMap<String, ArrayList<Double>> idValueMap = new HashMap<String, ArrayList<Double>>();

    private Locale locale;

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

    public JSONObject createOptions(JSONObject globalStyle, JSONObject data) throws Exception {
        JSONObject options = JSONObject.create();
        JSONObject settings = this.getDetailChartSetting();
        JSONObject plateConfig = BIConfUtils.getPlateConfig();

        options.put("chartType", this.getSeriesType(StringUtils.EMPTY));

        options.put("colors", this.parseColors(settings, globalStyle, plateConfig));

        options.put("style", this.parseStyle(settings, globalStyle, plateConfig));

        options.put(this.getLegendType(), this.parseLegend(settings));

        options.put("plotOptions", this.createPlotOptions(globalStyle, settings));

        options.put("series", this.createSeries(data));

        //处理格式的问题
        this.formatSeriesTooltipFormat(options);

        this.formatSeriesDataLabelFormat(options);

        return options;
    }

    public JSONArray createSeries(JSONObject data) throws Exception {
        return this.createXYSeries(data);
    }

    protected String getLegendType() {
        return "legend";
    }

    protected boolean isStacked(String dimensionID) {
        return false;
    }

    protected String getStackedKey(String dimensionID) {
        return dimensionID;
    }

    protected int yAxisIndex(String dimensionID) {
        int regionID = Integer.parseInt(this.getRegionID(dimensionID));

        return (regionID - TARGET) / TARGET_BASE;
    }

    protected double numberScale(String dimensionID) {

        int level = this.numberLevel(dimensionID);

        return this.numberScaleByLevel(level);
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

    protected void dealView(List<String> sorted, JSONObject vjo) throws JSONException{
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

        String widgetBg = "#ffffff";
        if (null != globalStyle && globalStyle.has("widgetBackground")) {
            widgetBg = globalStyle.optJSONObject("widgetBackground").optString("value");
            widgetBg = StringUtils.isBlank(widgetBg) ? WHITE : widgetBg;
        }

        tooltip.put("enabled", true).put("animation", true).put("padding", 10).put("backgroundColor", widgetBg)
                .put("borderRadius", 2).put("borderWidth", 0).put("shadow", true)
                .put("style", JSONObject.create()
                        .put("color", this.isDarkColor(widgetBg) ? "#FFFFFF" : "#1A1A1A")
                        .put("fontSize", "14px").put("fontFamily", "Verdana"));

        plotOptions.put("tooltip", tooltip);


        plotOptions.put("dataLabels", this.createDataLabels(settings));

        return plotOptions;
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
        boolean showDataLabel = settings.optBoolean("showDataLabel", false);
        JSONObject dataLabels = JSONObject.create().put("enabled", showDataLabel);

        if (showDataLabel) {
            JSONObject dataLabelSetting = settings.has("dataLabelSetting") ? settings.optJSONObject("dataLabelSetting") : this.defaultDataLabelSetting();

            JSONObject formatter = JSONObject.create();
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
                identifier += "${PERCENT}";
            }
            if (dataLabelSetting.optBoolean("showXValue")) {
                identifier += "${X}";
            }
            if (dataLabelSetting.optBoolean("showYValue")) {
                identifier += "${Y}";
            }
            if(dataLabelSetting.optBoolean("showBlockName")){
                identifier += "${NAME}";
            }
            if(dataLabelSetting.optBoolean("showTargetName")){
                identifier += "${SERIES}";
            }

            formatter.put("identifier", identifier);

            dataLabels.put("formatter", formatter);
            dataLabels.put("style", dataLabelSetting.optJSONObject("textStyle"));
            dataLabels.put("align", this.dataLabelAlign(dataLabelSetting.optInt("position")));

            dataLabels.put("connectorWidth", dataLabelSetting.optBoolean("showTractionLine") == true ? 1 : 0);
        }

        return dataLabels;
    }

    protected String dataLabelAlign(int position) {
        if (position == POSITION_OUTER) {
            return "outside";
        } else if (position == POSITION_INNER) {
            return "inside";
        }
        return "center";
    }

    private JSONObject defaultDataLabelSetting() throws JSONException {

        return JSONObject.create().put("showCategoryName", true)
                .put("showSeriesName", true).put("showValue", true).put("showPercentage", false)
                .put("position", POSITION_OUTER).put("showTractionLine", false)
                .put("textStyle", defaultFont());

    }

    private boolean isDarkColor(String colorStr) {

        colorStr = colorStr.substring(1);

        Color color = new Color(Integer.parseInt(colorStr, 16));

        return color.getRed() * RED_DET + color.getGreen() * GREEN_DET + color.getBlue() * BLUE_DET < GRAY;
    }

    protected JSONObject populateDefaultSettings() throws JSONException {
        JSONObject settings = JSONObject.create();

        //图例
        settings.put("legend", BOTTOM)
                .put("legendStyle", this.defaultFont());

        return settings;
    }

    protected JSONObject defaultFont() throws JSONException {

        //todo 这边的字体要全局取一下
        return JSONObject.create()
                .put("fontFamily", "Microsoft YaHei")
                .put("color", "rgb(178, 178, 178)")
                .put("fontSize", "12px");

    }

    //todo 不知道有没有实现过，先撸一下
    private JSONObject merge(JSONObject target, JSONObject source) throws JSONException {
        Iterator it = source.keys();
        while (it.hasNext()) {
            String key = it.next().toString();
            if (!target.has(key)) {
                target.put(key, source.get(key));
            }
        }
        return target;
    }

    protected JSONObject getDetailChartSetting() throws JSONException {
        JSONObject settings = this.getChartSetting().getDetailChartSetting();

        return merge(settings, this.populateDefaultSettings());
    }

    public JSONObject createDataJSON(BISessionProvider session, HttpServletRequest req) throws Exception {

        this.locale = WebUtils.getLocale(req);

        JSONObject data = super.createDataJSON(session, req).getJSONObject("data");

        JSONObject reportSetting = BIReadReportUtils.getInstance().getBIReportNodeJSON(((BISession) session).getReportNode());
        JSONObject globalStyle = reportSetting.optJSONObject("globalStyle");
        globalStyle = globalStyle == null ? JSONObject.create() : globalStyle;

        return this.createOptions(globalStyle, data).put("data", data);
    }

/*
* 如果没有的话，使用默认值
* */
    protected JSONArray parseColors(JSONObject settings, JSONObject globalStyle, JSONObject plateConfig) throws Exception {

        if (settings.has("chartColor")) {
            return settings.getJSONArray("chartColor");
        } else if (globalStyle.has("chartColor")) {
            if (settings.has("chartColor")) {
                return settings.getJSONArray("chartColor");
            } else {
                String[] defaultColors = BIStyleConstant.DEFAULT_CHART_SETTING.CHART_COLOR;
                JSONArray array = new JSONArray();
                for (int i = 0; i < defaultColors.length; i++) {
                    array.put(defaultColors[i]);
                }
                return array;
            }
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
        int style = STYLE_NORMAL;
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
        return style == STYLE_GRADUAL ? "gradual" : "normal";
    }

    protected String tooltipValueFormat(BISummaryTarget dimension) {
        return this.valueFormatFunc(dimension, true);
    }

    protected String dataLabelValueFormat(BISummaryTarget dimension) {
        return this.valueFormatFunc(dimension, true);
    }

    protected String decimalFormat(BISummaryTarget dimension, boolean hasSeparator) {
        JSONObject settings = dimension.getChartSetting().getSettings();
        int type = settings.optInt("format", BIReportConstant.TARGET_STYLE.FORMAT.NORMAL);//默认为自动
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

        String format = this.valueFormat(dimension, isTooltip);

        return String.format("function(){return BI.contentFormat(arguments[0], \"%s\")}", format);
    }

    protected String valueFormat(BISummaryTarget dimension, boolean isTooltip) {
        JSONObject settings = dimension.getChartSetting().getSettings();

        boolean hasSeparator = settings.optBoolean("numSeparators", true);

        String format = this.decimalFormat(dimension, hasSeparator);

        String scaleUnit = this.scaleUnit(this.numberLevel(dimension.getId()));

        String unit = settings.optString("unit", StringUtils.EMPTY);

        if (isTooltip) {
            format += (scaleUnit + unit);
        }

        return format;
    }

    protected String intervalLegendFormatter(String format) {
        return String.format("function(){return BI.contentFormat(arguments[0].from, \"%s\") + \"-\" + BI.contentFormat(arguments[0].to, \"%s\")}", format, format);
    }

    protected String gradualLegendFormatter(String format) {
        return String.format("function(){return BI.contentFormat(arguments[0], \"%s\")}", format);
    }

    protected void formatSeriesTooltipFormat(JSONObject options) throws Exception {

        JSONObject tooltip = options.optJSONObject("plotOptions").optJSONObject("tooltip");

        JSONArray series = options.optJSONArray("series");

        for (int i = 0, len = series.length(); i < len; i++) {
            JSONObject ser = series.getJSONObject(i);
            String dimensionID = ser.optString("dimensionID");

            JSONObject formatter = JSONObject.create();

            formatter.put("identifier", this.getTooltipIdentifier()).put("valueFormat", this.tooltipValueFormat(this.getBITargetByID(dimensionID)));

            ser.put("tooltip", new JSONObject(tooltip.toString()).put("formatter", formatter));
        }
    }

    protected String getTooltipIdentifier() {
        return CATEGORY + SERIES + VALUE;
    }

    protected void formatSeriesDataLabelFormat(JSONObject options) throws Exception {
        JSONObject dataLabels = options.optJSONObject("plotOptions").optJSONObject("dataLabels");

        if (dataLabels.optBoolean("enabled")) {
            JSONArray series = options.optJSONArray("series");

            for (int i = 0, len = series.length(); i < len; i++) {
                JSONObject ser = series.getJSONObject(i);
                String dimensionID = ser.optString("dimensionID");

                JSONObject labels = new JSONObject(dataLabels.toString());
                labels.optJSONObject("formatter")
                        .put("valueFormat", this.dataLabelValueFormat(this.getBITargetByID(dimensionID)))
                        .put("percentFormat", "function(){return BI.contentFormat(arguments[0], \"#.##%\")}");

                ser.put("dataLabels", labels);
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
        BIDimension category = this.getCategoryDimension();
        JSONArray series = JSONArray.create();
        String[] targetIDs = this.getUsedTargetID();
        if(targetIDs.length == 0){
            return series;
        }
        String categoryKey = this.categoryKey(), valueKey = this.valueKey();
        ArrayList<Double> valueList = new ArrayList<Double>();
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
                JSONArray s = lObj.getJSONObject("s").getJSONArray("c").getJSONObject(i).getJSONArray("s");
                double y = (s.isNull(0) ? 0 : s.getDouble(0)) / numberScale;
                data.put(JSONObject.create().put(categoryKey, this.formatCategory(category, x)).put(valueKey, y));
                valueList.add(y);
            }
            JSONObject ser = JSONObject.create().put("data", data).put("name", name)
                    .put("type", this.getSeriesType(targetIDs[0])).put("dimensionID", targetIDs[0]);
            if (isStacked) {
                //todo:应该也有问题，不知道怎么改，遇到bug的话参照createSeriesWithChildren里面的改法
                ser.put("stack", targetIDs[0]);
            }
            series.put(ser);
        }
        this.idValueMap.put(targetIDs[0], valueList);

        return series;
    }

    private JSONArray createSeriesWithChildren(JSONObject originData) throws Exception {
        BIDimension category = this.getCategoryDimension();
        JSONArray series = JSONArray.create();
        String[] targetIDs = this.getUsedTargetID();
        String categoryKey = this.categoryKey(), valueKey = this.valueKey();
        JSONArray children = originData.optJSONArray("c");
        for (int i = 0, len = targetIDs.length; i < len; i++) {
            String id = targetIDs[i], type = this.getSeriesType(id), stackedKey = this.getStackedKey(id);
            int yAxis = this.yAxisIndex(id);
            ArrayList<Double> valueList = new ArrayList<Double>();
            double numberScale = this.numberScale(id);
            JSONArray data = JSONArray.create();
            if(children != null) {
                for (int j = 0, count = children.length(); j < count; j++) {
                    JSONObject lObj = children.getJSONObject(j);
                    String x = lObj.getString("n");
                    JSONArray targetValues = lObj.getJSONArray("s");
                    double y = targetValues.isNull(i) ? 0 : targetValues.getDouble(i) / numberScale;
                    data.put(JSONObject.create().put(categoryKey, this.formatCategory(category, x)).put(valueKey, y));
                    valueList.add(y);
                }
            } else {//饼图没有分类，只有指标。会过来一个汇总值，没有child
                JSONArray targetValues = originData.optJSONArray("s");
                double y = targetValues.isNull(i) ? 0 : targetValues.getDouble(i) / numberScale;
                data.put(JSONObject.create().put(valueKey, y));
                valueList.add(y);
            }
            JSONObject ser = JSONObject.create().put("data", data).put("name", getDimensionNameByID(id))
                    .put("type", type).put("yAxis", yAxis).put("dimensionID", id);
            if (this.isStacked(id)) {
                ser.put("stack", STACK_ID_PREFIX + yAxis);
            }
            series.put(ser);
            this.idValueMap.put(id, valueList);
        }
        return series;
    }

    protected String formatCategory(BIDimension categoryDimension, String category){
        if(categoryDimension == null || StringUtils.isBlank(category)){
            return category;
        }

        int groupType = categoryDimension.getGroup().getType();
        JSONObject dateFormat = categoryDimension.getChartSetting().getSettings().optJSONObject("dateFormat");

        int dateFormatType = dateFormat == null ? BIReportConstant.DATE_FORMAT.SPLIT : dateFormat.optInt("type", BIReportConstant.DATE_FORMAT.SPLIT);

        Number dateCategory = StableUtils.string2Number(category);
        long dateValue = dateCategory == null ? 0L : dateCategory.longValue();

        switch (groupType) {
            case BIReportConstant.GROUP.S:
                category = FULL_QUARTER_NAMES[(int)dateValue];
                break;
            case BIReportConstant.GROUP.M:
                category = FULL_MONTH_NAMES[(int)dateValue];
                break;
            case BIReportConstant.GROUP.W:
                category = FULL_WEEK_NAMES[(int)dateValue];
                break;
            case BIReportConstant.GROUP.YMD:
                category =  this.formatYMDByDateFormat(dateValue, dateFormatType);
                break;
            case BIReportConstant.GROUP.YMDHMS:
                category = this.formatYMDHMSByDateFormat(dateValue, dateFormatType);
                break;
            case BIReportConstant.GROUP.YMDH:
                category = this.formatYMDHByDateFormat(dateValue, dateFormatType);
                break;
            case BIReportConstant.GROUP.YMDHM:
                category = this.formatYMDHMByDateFormat(dateValue, dateFormatType);
                break;
            case BIReportConstant.GROUP.YS:
                category = this.formatCombineDateByDateFormat(category, dateFormatType, new String[]{getLocText("BI-Basic_Year"), getLocText("BI-Basic_Quarter")});
                break;
            case BIReportConstant.GROUP.YM:
                category = this.formatCombineDateByDateFormat(category, dateFormatType, new String[]{getLocText("BI-Basic_Year"), getLocText("BI-Basic_Month")});
                break;
            case BIReportConstant.GROUP.YW:
                category = this.formatCombineDateByDateFormat(category, dateFormatType, new String[]{getLocText("BI-Basic_Year"), getLocText("BI-Week_Simple")});
                break;
        }

        return category;
    }

    private String formatYMDByDateFormat(long dateValue, int dateFormatType){
        Date date = new Date(dateValue);
        SimpleDateFormat formatter;
        if(dateFormatType == BIReportConstant.DATE_FORMAT.CHINESE){
            formatter = new SimpleDateFormat(String.format("yyyy%sMM%sdd%s", getLocText("BI-Basic_Year"), getLocText("BI-Basic_Month"), getLocText("BI-Date_Day")));
        }else{
            formatter = new SimpleDateFormat("yyyy-MM-dd");
        }
        return formatter.format(date);
    }

    private String formatYMDHMSByDateFormat(long dateValue, int dateFormatType){
        Date date = new Date(dateValue);
        SimpleDateFormat formatter;

        if(dateFormatType == BIReportConstant.DATE_FORMAT.CHINESE){
            formatter = new SimpleDateFormat(String.format("yyyy%sMM%sdd%s H%sm%ss%s", getLocText("BI-Basic_Year"), getLocText("BI-Basic_Month"), getLocText("BI-Date_Day"),
                    getLocText("BI-Hour_Sin"), getLocText("BI-Basic_Minute"), getLocText("BI-Basic_Second")));
        }else{
            formatter = new SimpleDateFormat("yyyy-MM-dd H:m:s");
        }
        return formatter.format(date);
    }

    private String formatYMDHByDateFormat(long dateValue, int dateFormatType){
        Date date = new Date(dateValue);
        SimpleDateFormat formatter;

        if(dateFormatType == BIReportConstant.DATE_FORMAT.CHINESE){
            formatter = new SimpleDateFormat(String.format("yyyy%sMM%sdd%s H%s", getLocText("BI-Basic_Year"), getLocText("BI-Basic_Month"),
                    getLocText("BI-Date_Day"), getLocText("BI-Hour_Sin")));
        }else{
            formatter = new SimpleDateFormat("yyyy-MM-dd H");
        }
        return formatter.format(date);
    }

    private String formatYMDHMByDateFormat(long dateValue, int dateFormatType){
        Date date = new Date(dateValue);
        SimpleDateFormat formatter;

        if(dateFormatType == BIReportConstant.DATE_FORMAT.CHINESE){
            formatter = new SimpleDateFormat(String.format("yyyy%sMM%sdd%s H%sm%s", getLocText("BI-Basic_Year"), getLocText("BI-Basic_Month"), getLocText("BI-Date_Day"),
                    getLocText("BI-Hour_Sin"), getLocText("BI-Basic_Minute")));
        }else{
            formatter = new SimpleDateFormat("yyyy-MM-dd H:m");
        }
        return formatter.format(date);
    }

    private String formatCombineDateByDateFormat(String category, int dateFormatType, String[] format){

        if(dateFormatType == BIReportConstant.DATE_FORMAT.CHINESE){
            String[] text = category.split("-");

            if(text.length == format.length){
                String resultText = "";

                for(int i = 0, len = text.length; i < len; i++){
                    resultText += (text[i] + format[i]);
                }

                return resultText;
            }
        }

        return category;
    }

    protected JSONObject parseLegend(JSONObject settings) throws JSONException {

        int legend = settings.optInt("legend");
        String position = "top";

        if (legend == RIGHT) {
            position = "right";
        } else if (legend == BOTTOM) {
            position = "bottom";
        } else if (legend == LEFT) {
            position = "left";
        }

        return JSONObject.create()
                .put("enabled", legend >= TOP)
                .put("position", position)
                .put("style", settings.optJSONObject("legendStyle"));
    }

    protected JSONArray mapStyleToRange(JSONArray mapStyle) throws JSONException {
        JSONArray ranges = JSONArray.create();

        for (int i = 0, len = mapStyle.length(); i < len; i++) {
            JSONObject config = mapStyle.getJSONObject(i), range = config.optJSONObject("range");

            ranges.put(
                    JSONObject.create()
                            .put("from", range.optDouble("min"))
                            .put("to", range.optDouble("max"))
                            .put("color", config.optString("color"))
            );


        }

        return ranges;
    }

    public BIDimension getCategoryDimension() {
        List<String> dimensionIds = view.get(Integer.parseInt(BIReportConstant.REGION.DIMENSION1));
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

    public JSONObject getPostOptions(String sessionId, HttpServletRequest req) throws Exception {
        JSONObject chartOptions = this.createDataJSON((BISessionProvider) SessionDealWith.getSessionIDInfor(sessionId), req);
        JSONObject plotOptions = chartOptions.optJSONObject("plotOptions");
        plotOptions.put("animation", false);
        chartOptions.put("plotOptions", plotOptions);
        return chartOptions;
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

    protected WidgetType getChartType(){
        return this.chartType;
    }

    protected String getLocText(String key){
        return Inter.getLocText(key, this.locale);
    }

}