package com.fr.bi.report.update.operation;

import com.finebi.cube.common.log.BILoggerFactory;
import com.fr.bi.report.update.ReportVersionEnum;
import com.fr.bi.stable.constant.BIChartSettingConstant;
import com.fr.bi.stable.constant.BIReportConstant;
import com.fr.bi.stable.utils.program.BIJsonUtils;
import com.fr.bi.stable.utils.program.BIStringUtils;
import com.fr.general.ComparatorUtils;
import com.fr.json.JSONArray;
import com.fr.json.JSONException;
import com.fr.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by kary on 2017/1/23.
 * 此处的升级内容：
 * 驼峰以及自定义key值修改
 * 图片uri修正
 * 散点气泡图type升级成点图的type，并使用特殊映射
 * 显示网格拆分成显示纵向和显示横向
 * 组合图重新分组
 * BI-6515 4.0->4.0.2的散点、气泡图兼容到点图样式不一致
 * 处理脏数据
 * //4.0的图表标签默认设置，和402默认有些不一样，所以在这边写。调整标签位置，雅黑12px, 颜色自动。
 * //gauge 402 settings.minScale ---> 4.1 settings.leftYCustomScale.minScale + settings.leftYShowCustomScale = true
 * //gis map version402（10000：lng,lat 20000:name 30000:value) --> version41(10000:name 30000:value)
 * //map version402 subType:MAP_map/中国.json --> version41 subType:MAP_geographic/中国.json
 */
public class ProfilesUpdateOperation implements ReportUpdateOperation {
    private static final String DEFAULT_FILE_NAME = "keys.json";
    private JSONObject keys;
    private static Pattern linePattern = Pattern.compile("(?!^)_(\\w)");
    //类型映射
    private static Map<Integer, Integer> chartTypeMap = new HashMap<Integer, Integer>();
    //点图key值映射
    private static Map<String, String> dotWidgetKeysMap = new HashMap<String, String>();

    public ProfilesUpdateOperation() {
        try {
            createDotWidgetKeysMap();
            if (null == keys) {
                keys = readKeyJson();
                formatValues();
            }
        } catch (Exception e) {
            BILoggerFactory.getLogger(this.getClass()).error(e.getMessage(), e);
        }

    }

    //  "rightYTitle": "(catTitle)",
//  "rightYTitle": "(x_axis_title)",
//          "rightYNumberLevel": "(x_axis_number_level)",
//          "rightYShowTitle": "(show_x_axis_title)",
//          "rightYUnit": "(x_axis_unit)"
    private void createDotWidgetKeysMap() {
        dotWidgetKeysMap.put("catTitle", "rightYTitle");
        dotWidgetKeysMap.put("x_axis_title", "rightYTitle");
        dotWidgetKeysMap.put("x_axis_number_level", "rightYNumberLevel");
        dotWidgetKeysMap.put("show_x_axis_title", "rightYShowTitle");
        dotWidgetKeysMap.put("x_axis_unit", "rightYUnit");
    }

    @Override
    public JSONObject update(JSONObject reportSetting) throws JSONException {
        if (BIJsonUtils.isKeyValueSet(reportSetting.toString())) {
            initChartTypeMap(reportSetting);
            reportSetting = recursionMapUpdate(reportSetting.toString());
            return reportSetting;
        } else {
            return reportSetting;
        }
    }

    //map 结构的递归
    public JSONObject recursionMapUpdate(String obj) throws JSONException {
        JSONObject json = new JSONObject(obj);
        JSONObject res = new JSONObject();
        Set<String> keySet = json.toMap().keySet();
        for (String s : keySet) {
            boolean flag = BIJsonUtils.isKeyValueSet(json.get(s).toString());
            if (flag) {
                if (ComparatorUtils.equals(s, "widgets")) {
                    addWId(json);
                    json = correctDataLabels(json);
                    json = correctGaugeAxisScale(json);
                    json = correctGisDimensions(json);
                    json = correctMapJSONUrl(json);
                    json = correctPreviousSrcError(json);
                    json = correctScatterType(json);
                    groupTargetsByType(json);
                    updateShowGridSettings(json);
                    updateSrcFieldId(json);
                    changeHyperLinkToJump(json);
                }
                res.put(updateKey(s), recursionMapUpdate(json.getString(s)));
            } else {
                res.put(updateKey(s), recursionListUpdate(json.get(s)));
            }
        }
        return res;
    }

    public void changeHyperLinkToJump(JSONObject json) throws JSONException{
        JSONArray jumps = JSONArray.create();
        if (BIJsonUtils.isKeyValueSet(json.getString("widgets"))) {
            Iterator keys = json.getJSONObject("widgets").keys();
            while (keys.hasNext()) {
                String widgetId = keys.next().toString();
                JSONObject widgetJo = json.getJSONObject("widgets").getJSONObject(widgetId);
                if (widgetJo.has("type") && widgetJo.getInt("type") == BIReportConstant.WIDGET.DETAIL) {
                    JSONObject dimensions = widgetJo.optJSONObject("dimensions");
                    JSONObject view = widgetJo.optJSONObject("view");
                    JSONArray region = view.optJSONArray(BIReportConstant.REGION.DIMENSION1);
                    for (int i = 0; i < region.length(); i++) {
                        JSONObject dimension = dimensions.optJSONObject(region.getString(i));
                        int type = dimension.optInt("type", BIReportConstant.TARGET_TYPE.STRING);
                        if(dimension.has("hyperlink")){
                            JSONObject hyperlink = dimension.getJSONObject("hyperlink");
                            if(hyperlink.optBoolean("used", true)){
                                if(type == BIReportConstant.TARGET_TYPE.STRING || type == BIReportConstant.TARGET_TYPE.NUMBER || type == BIReportConstant.TARGET_TYPE.DATE){
                                    JSONObject jump = JSONObject.create();
                                    JSONObject srcJo = dimension.getJSONObject("_src");
                                    String fieldId = srcJo.optString("fieldId");
                                    String url = hyperlink.optString("expression", "");
                                    String targetUrl = url.replaceAll("\\$\\{.*\\}", "\\$\\{" + fieldId +"\\}");
                                    jump.put("targetUrl", targetUrl);
                                    jump.put("openPosition", 1);
                                    jumps.put(jump);
                                }
                            }
                            dimension.remove("hyperlink");
                        }
                    }
                }
                widgetJo.put("jump", jumps);
            }
        }
    }

    private void addWId(JSONObject jo) {
        try {
            if (BIJsonUtils.isKeyValueSet(jo.getString("widgets"))) {
                Iterator keys = jo.getJSONObject("widgets").keys();
                while (keys.hasNext()) {
                    String widgetId = keys.next().toString();
                    JSONObject widgetJo = jo.getJSONObject("widgets").getJSONObject(widgetId);
                    widgetJo.put("wId", widgetId);
                }
            }
        } catch (JSONException e) {
            BILoggerFactory.getLogger(this.getClass()).error(e.getMessage(), e);
        }

    }

    /*4.0里面出现如下脏数据
     "_src": {
            "tableId": "16e9dc63df807d33",
            "fieldId": [
              "16e9dc63df807d33生产入库实际与预算记录数"
            ]
          },
    */
    private void updateSrcFieldId(JSONObject json) {
        try {
            if (BIJsonUtils.isKeyValueSet(json.getString("widgets"))) {
                Iterator keys = json.getJSONObject("widgets").keys();
                while (keys.hasNext()) {
                    String widgetId = keys.next().toString();
                    if (!json.getJSONObject("widgets").getJSONObject(widgetId).has("dimensions")) {
                        continue;
                    }
                    JSONObject dimensions = json.getJSONObject("widgets").getJSONObject(widgetId).getJSONObject("dimensions");
                    Iterator dimKeys = dimensions.keys();
                    while (dimKeys.hasNext()) {
                        String dimId = dimKeys.next().toString();
                        JSONObject dimJo = dimensions.getJSONObject(dimId);
                        if (dimJo.has("_src")) {
                            JSONObject srcJo = dimJo.getJSONObject("_src");
                            if (srcJo.has("fieldId") && BIJsonUtils.isArray(srcJo.getString("fieldId"))) {
                                JSONArray fieldIds = srcJo.getJSONArray("fieldId");
                                srcJo.put("fieldId", fieldIds.length() > 0 ? fieldIds.get(0) : "");
                            }
                            if (srcJo.has("field_id") && BIJsonUtils.isArray(srcJo.getString("field_id"))) {
                                JSONArray fieldIds = srcJo.getJSONArray("field_id");
                                srcJo.put("field_id", fieldIds.length() > 0 ? fieldIds.get(0) : "");
                            }
                        }
                    }
                }
            }
        } catch (JSONException e) {
            BILoggerFactory.getLogger(this.getClass()).error(e.getMessage(), e);
        }

    }

    //  显示网格线拆分成显示纵向和显示横向，settings.textDirection to settings.catLabelStyle.textDirection
    private void updateShowGridSettings(JSONObject jo) {
        try {
            if (BIJsonUtils.isKeyValueSet(jo.getString("widgets"))) {
                Iterator keys = jo.getJSONObject("widgets").keys();
                while (keys.hasNext()) {
                    String widgetId = keys.next().toString();
                    JSONObject widgetJo = jo.getJSONObject("widgets").getJSONObject(widgetId);
                    JSONObject settings = widgetJo.optJSONObject("settings");
                    if (settings != null && settings.has("show_grid_line")) {
                        boolean needUpdate = !settings.has("hShowGridLine") && !settings.has("xShowGridLine");
                        if (needUpdate) {
                            boolean isShowGridLine = settings.optBoolean("show_grid_line", false);
                            widgetJo.getJSONObject("settings").put("hShowGridLine", isShowGridLine);
                            widgetJo.getJSONObject("settings").put("vShowGridLine", isShowGridLine);
                        }
                    }
                    if (settings != null && settings.has("text_direction")) {
                        settings.put("catLabelStyle", JSONObject.create().put("text_direction", settings.optInt("text_direction")));
                    }
                }
            }
        } catch (JSONException e) {
            BILoggerFactory.getLogger(this.getClass()).error(e.getMessage(), e);
        }

    }

    //gauge version402 settings.minScale ---> version41 settings.leftYCustomScale.minScale + settings.leftYShowCustomScale = true
    private JSONObject correctGaugeAxisScale(JSONObject json) throws JSONException {
            if (BIJsonUtils.isKeyValueSet(json.getString("widgets"))) {
                Iterator keys = json.getJSONObject("widgets").keys();
                while (keys.hasNext()) {
                    String dimId = keys.next().toString();
                    JSONObject dimJson = json.getJSONObject("widgets").getJSONObject(dimId);
                    if (dimJson.has("type") && dimJson.has("settings")) {

                        if(dimJson.optInt("type") == BIReportConstant.WIDGET.DASHBOARD){
                            JSONObject settings = dimJson.optJSONObject("settings");

                            if(settings.has("minScale") || settings.has("maxScale")) {
                                settings.put("leftYShowCustomScale", true)
                                        .put("leftYCustomScale", JSONObject.create()
                                        .put("minScale", settings.optString("minScale"))
                                        .put("maxScale", settings.optString("maxScale")));

                                settings.remove("minScale");
                                settings.remove("maxScale");
                            }

                            if(settings.has("showPercentage")){
                                int show = settings.optInt("showPercentage");
                                if(show == BIChartSettingConstant.PERCENTAGE.SHOW){
                                    settings.put("showPercentage", true);
                                } else if(show == BIChartSettingConstant.PERCENTAGE.NOT_SHOW){
                                    settings.put("showPercentage", false);
                                }
                            }
                        }
                    }
                }
            }
        return json;
    }

    //gis map version402（10000：lng,lat 20000:name 30000:value) --> version41(10000:name 30000:value)
    private JSONObject correctGisDimensions(JSONObject json) throws JSONException {
        if (BIJsonUtils.isKeyValueSet(json.getString("widgets"))) {
            Iterator keys = json.getJSONObject("widgets").keys();
            while (keys.hasNext()) {
                String id = keys.next().toString();
                JSONObject widget = json.getJSONObject("widgets").getJSONObject(id);
                if (widget.has("type") && widget.optInt("type") == BIReportConstant.WIDGET.GIS_MAP) {

                    if(widget.has("view")){
                        JSONObject view = widget.optJSONObject("view");

                        JSONArray name = view.optJSONArray(BIReportConstant.REGION.DIMENSION2);

                        if(name != null) {
                            view.remove(BIReportConstant.REGION.DIMENSION2);
                            view.remove(BIReportConstant.REGION.DIMENSION1);

                            view.put(BIReportConstant.REGION.DIMENSION1, name);
                        }
                    }

                }
            }
        }
        return json;
    }

    //map version402 subType:MAP_map/中国.json --> version41 subType:MAP_geographic/中国.json
    private JSONObject correctMapJSONUrl(JSONObject json) throws JSONException {
        if (BIJsonUtils.isKeyValueSet(json.getString("widgets"))) {
            Iterator keys = json.getJSONObject("widgets").keys();
            while (keys.hasNext()) {
                String id = keys.next().toString();
                JSONObject widget = json.getJSONObject("widgets").getJSONObject(id);
                if (widget.has("type") && widget.optInt("type") == BIReportConstant.WIDGET.MAP) {

                    if(widget.has("subType")){
                        String subType = widget.optString("subType");
                        subType = subType.replace("MAP_map", "Map_geographic");
                        widget.put("subType", subType);
                    }

                }
            }
        }
        return json;
    }

    //4.0的图表标签默认设置，和402默认有些不一样，所以在这边写。调整标签位置，雅黑12px, 颜色自动。
    private JSONObject correctDataLabels(JSONObject json) throws JSONException {
        if (ReportVersionEnum.VERSION_4_0.getVersion().equals(json.optString("version"))) {
            if (BIJsonUtils.isKeyValueSet(json.getString("widgets"))) {
                Iterator keys = json.getJSONObject("widgets").keys();
                while (keys.hasNext()) {
                    String dimId = keys.next().toString();
                    JSONObject dimJson = json.getJSONObject("widgets").getJSONObject(dimId);
                    if (dimJson.has("type") && dimJson.has("settings")) {
                        JSONObject settings = dimJson.optJSONObject("settings");
                        int type = dimJson.optInt("type");

                        JSONObject dataLabelSettings = JSONObject.create().put("optimizeLabel", true).put("showTractionLine", true)
                                .put("textStyle", JSONObject.create().put("fontFamily", "Microsoft YaHei").put("fontSize", "12px").put("color", ""));

                        switch (type) {
                            case BIReportConstant.WIDGET.PIE:
                            case BIReportConstant.WIDGET.DONUT:
                                dataLabelSettings.put("showCategoryName", false).put("showSeriesName", false)
                                        .put("showValue", true).put("showPercentage", true)
                                        .put("position", BIChartSettingConstant.DATA_LABEL.POSITION_OUTER);
                                break;
                            case BIReportConstant.WIDGET.FORCE_BUBBLE:
                                dataLabelSettings.put("showCategoryName", true).put("showSeriesName", false)
                                        .put("showValue", true).put("showPercentage", false);
                                break;
                            case BIReportConstant.WIDGET.BUBBLE:
                                dataLabelSettings.put("showCategoryName", false).put("showSeriesName", false)
                                        .put("showXValue", true).put("showYValue", true).put("showValue", true);
                                break;
                            case BIReportConstant.WIDGET.SCATTER:
                                dataLabelSettings.put("showCategoryName", false).put("showSeriesName", false)
                                        .put("showXValue", true).put("showYValue", true).put("showValue", false);
                                break;
                            case BIReportConstant.WIDGET.DASHBOARD:
                                settings.put("showDataLabel", true);
                                dataLabelSettings.put("showCategoryName", true).put("showSeriesName", false)
                                        .put("showValue", true).put("showPercentage", false);
                                break;
                            default:
                                dataLabelSettings.put("showCategoryName", false).put("showSeriesName", false)
                                        .put("showValue", true).put("showPercentage", false)
                                        .put("position", BIChartSettingConstant.DATA_LABEL.POSITION_OUTER);
                                break;
                        }
                        settings.put("dataLabelSetting", dataLabelSettings);
                    }
                }
            }
        }
        return json;
    }

    /*
   * 散点气泡图type升级
   * type 26，28->67
   * view中region变动
    *  BI-6852 散点图点图的升级需要单独的映射
   * */
    private JSONObject correctScatterType(JSONObject json) throws JSONException {
        if (BIJsonUtils.isKeyValueSet(json.getString("widgets"))) {
            Iterator keys = json.getJSONObject("widgets").keys();
            while (keys.hasNext()) {
                String widgetId = keys.next().toString();
                JSONObject widgetJo = json.getJSONObject("widgets").getJSONObject(widgetId);
                if (widgetJo.has("type")) {
                    if (widgetJo.getInt("type") == BIReportConstant.WIDGET.BUBBLE || widgetJo.getInt("type") == BIReportConstant.WIDGET.SCATTER) {
                        if (widgetJo.has("view") && widgetJo.getJSONObject("view").has(BIReportConstant.REGION.DIMENSION1)) {
                            widgetJo.getJSONObject("view").put(BIReportConstant.REGION.DIMENSION2, widgetJo.getJSONObject("view").get(BIReportConstant.REGION.DIMENSION1));
                            widgetJo.getJSONObject("view").remove(BIReportConstant.REGION.DIMENSION1);
                        }
                        widgetJo.put("type", BIReportConstant.WIDGET.DOT);
                        if (widgetJo.has("settings")) {
                            JSONObject settingsJo = widgetJo.getJSONObject("settings");
                            for (String s : dotWidgetKeysMap.keySet()) {
                                if (settingsJo.has(s)) {
                                    settingsJo.put(dotWidgetKeysMap.get(s).toString(), settingsJo.get(s));
                                    settingsJo.remove(s);
                                }
                            }
                        }
                    }
                }
            }
        }
        return json;
    }

    /*
    *  * 组合图里面指标按照type来进行分组
    * */
    private void groupTargetsByType(JSONObject jo) {
        try {
            if (BIJsonUtils.isKeyValueSet(jo.getString("widgets"))) {
                Iterator keys = jo.getJSONObject("widgets").keys();
                while (keys.hasNext()) {
                    String widgetId = keys.next().toString();
                    JSONObject widgetJo = jo.getJSONObject("widgets").getJSONObject(widgetId);
                    boolean isCombineChart = BIReportConstant.WIDGET.COMBINE_CHART == widgetJo.getInt("type") || BIReportConstant.WIDGET.MULTI_AXIS_COMBINE_CHART == widgetJo.getInt("type");
                    if (isCombineChart && !widgetJo.has("scopes")) {
                        updateCombineChartView(widgetJo);
                    }
                }
            }
        } catch (JSONException e) {
            BILoggerFactory.getLogger(this.getClass()).error(e.getMessage(), e);
        }
    }

    /*
    * 根据chartType设定分组
    * */
    private void updateCombineChartView(JSONObject jo) throws JSONException {
        JSONObject view = jo.optJSONObject("view");
        JSONObject scopes = new JSONObject();
        String[] targets = {BIReportConstant.REGION.TARGET1, BIReportConstant.REGION.TARGET2, BIReportConstant.REGION.TARGET3};
        for (int i = 0; i < targets.length; i++) {
            String target = targets[i];
            if (view.optJSONArray(target) != null) {
                Integer basicTarget;
                basicTarget = Integer.valueOf(target);
                Map<Integer, JSONArray> chartTypeMap = createDimensionChartTypeMap(jo.optJSONObject("dimensions"), view.optJSONArray(target));
                Iterator<Integer> iterator = chartTypeMap.keySet().iterator();
                while (iterator.hasNext()) {
                    Integer type = iterator.next();
                    scopes.put(String.valueOf(basicTarget), JSONObject.create().put("chartType", type));
                    view.put(String.valueOf(basicTarget), chartTypeMap.get(type));
                    basicTarget++;
                }
            }

        }
        jo.put("scopes", scopes);
    }

    private Map<Integer, JSONArray> createDimensionChartTypeMap(JSONObject dimensions, JSONArray array) throws JSONException {
        Map<Integer, JSONArray> typeMap = new HashMap<Integer, JSONArray>();
        if (null != dimensions) {
            Iterator keys = dimensions.keys();
            while (keys.hasNext()) {
                String dimId = keys.next().toString();
                if (!includedInArray(dimId, array)) {
                    continue;
                }
                JSONObject dimension = dimensions.getJSONObject(dimId);
                //默认柱形图
                int chartType = BIChartSettingConstant.ACCUMULATE_TYPE.COLUMN;
                if (dimension.has("styleOfChart") && dimension.getJSONObject("styleOfChart").has("type")) {
                    chartType = updateChartType(dimension.getJSONObject("styleOfChart").getInt("type"));
                    dimension.getJSONObject("styleOfChart").put("type", chartType);
                } else {
                    if (dimension.has("style_of_chart") && dimension.getJSONObject("style_of_chart").has("type")) {
                        chartType = updateChartType(dimension.getJSONObject("style_of_chart").getInt("type"));
                        dimension.getJSONObject("style_of_chart").put("type", chartType);
                    }
                }
                if (typeMap.containsKey(chartType)) {
                    typeMap.get(chartType).put(dimId);
                } else {
                    typeMap.put(chartType, new JSONArray().put(dimId));
                }
            }
        }
        return typeMap;
    }

    /*
    * 映射图标type
    * */
    private int updateChartType(int chartType) {
//        if (chartTypeMap.containsKey(chartType)) {
//            return chartTypeMap.get(chartType);
//        } else {
//            BILoggerFactory.getLogger().error("the chartType: " + chartType + " is absent in this version");
//            return BIChartSettingConstant.ACCUMULATE_TYPE.COLUMN;
//        }
        return chartTypeMap.containsKey(chartType) ? chartTypeMap.get(chartType) : chartType;
    }

    /* * 规则：
    柱状图 5 -> 柱状图
    面积图 14 -> 面积图
    堆积面积图 15-> 堆积面积图（折线）15
    折线图 13-> （折线）9
     堆积柱状图 6 -> （堆积柱状图）2
    * BI-6186  存在同一版本下重复升级的情况，若当前version相同的话，直接返回原值就可以了
    */
    private void initChartTypeMap(JSONObject reportSetting) {
        Map<Integer, Integer> convertMap = new HashMap<Integer, Integer>();
        boolean isVersion402 = ReportVersionEnum.VERSION_4_0_2.getVersion().equals(reportSetting.optString("version"));
        if (!isVersion402) {
            convertMap.put(BIChartSettingConstant.ACCUMULATE_TYPE.OLD_COLUMN, BIChartSettingConstant.ACCUMULATE_TYPE.COLUMN);
            convertMap.put(BIChartSettingConstant.ACCUMULATE_TYPE.OLD_AREA_CURVE, BIChartSettingConstant.ACCUMULATE_TYPE.AREA_CURVE);
            convertMap.put(BIChartSettingConstant.ACCUMULATE_TYPE.OLD_STACKED_AREA, BIChartSettingConstant.ACCUMULATE_TYPE.STACKED_AREA_NORMAL);
            convertMap.put(BIChartSettingConstant.ACCUMULATE_TYPE.OLD_LINE, BIChartSettingConstant.ACCUMULATE_TYPE.LINE_NORMAL);
            convertMap.put(BIChartSettingConstant.ACCUMULATE_TYPE.OLD_ACCUMULATE_AXIS, BIChartSettingConstant.ACCUMULATE_TYPE.STACKED_COLUMN);
            //default type=1
            convertMap.put(BIChartSettingConstant.ACCUMULATE_TYPE.COLUMN, BIChartSettingConstant.ACCUMULATE_TYPE.COLUMN);
        }
        this.chartTypeMap = convertMap;
    }

    private boolean includedInArray(String dimId, JSONArray array) throws JSONException {
        for (int i = 0; i < array.length(); i++) {
            if (ComparatorUtils.equals(array.getString(i), dimId)) {
                return true;
            }
        }
        return false;
    }

    /*
    * 处理之前stable版本保存图片时把整个url全保存进去了，没有地方拦截了，先在此处修正
    * */
    private JSONObject correctPreviousSrcError(JSONObject json) throws JSONException {
        if (BIJsonUtils.isKeyValueSet(json.getString("widgets"))) {
            Iterator keys = json.getJSONObject("widgets").keys();
            while (keys.hasNext()) {
                String dimId = keys.next().toString();
                JSONObject dimJson = json.getJSONObject("widgets").getJSONObject(dimId);
                if (dimJson.has("src")) {
                    dimJson.put("src", correctImgSrc(dimJson.getString("src")).toLowerCase());
                }
            }
        }
        return json;
    }

    //所有不符合get格式的都当异常处理掉
    private String correctImgSrc(String src) {
        try {
            String content = src.split("//?")[src.split("//?").length - 1];
            String imageInfo = content.split("//&")[content.split("//&").length - 1];
            return imageInfo.substring(imageInfo.lastIndexOf("=") + 1, imageInfo.length());
        } catch (Exception e) {
            BILoggerFactory.getLogger(this.getClass()).error(e.getMessage(), e);
        }
        return src;
    }

    //list结构的递归
    public Object recursionListUpdate(Object object) throws JSONException {
        String str = object.toString();
        if (BIJsonUtils.isArray(str)) {
            JSONArray array = new JSONArray(str);
            for (int i = 0; i < array.length(); i++) {
                array.put(i, recursionListUpdate(array.get(i)));
            }
            return array;
        } else {
            if (BIJsonUtils.isKeyValueSet(str)) {
                return recursionMapUpdate(str);
            } else {
                return object;
            }
        }
    }

    /*
    * 如果对应关系能在keys.json中找到，使用keys.json
    * 如果获取不了，默认转驼峰
    * */
    public String updateKey(String str) {
        String res;
        if (keys.has(str)) {
            res = matchKeysJson(str);
        } else {
            res = lineToCamels(str);
        }
        return res;
    }

    private String matchKeysJson(String str) {
        try {
            String updatedKeys = null != keys ? keys.optString(str, str) : str;
            if (!ComparatorUtils.equals(updatedKeys, str)) {
                BILoggerFactory.getLogger(this.getClass()).debug(BIStringUtils.append("compatibility warning! the parameter whose name is ", str, " should be transferred"));
            }
            return updatedKeys;
        } catch (Exception e) {
            BILoggerFactory.getLogger(this.getClass()).error(e.getMessage(), e);
        }
        return str;
    }

    private String lineToCamels(String str) {
        if (str.contains("__")) {
            return str;
        }
        Matcher matcher = linePattern.matcher(str);
        StringBuffer sb = new StringBuffer();
        while (matcher.find()) {
            matcher.appendReplacement(sb, matcher.group(1).toUpperCase());
        }
        matcher.appendTail(sb);
        if (!ComparatorUtils.equals(str, sb.toString())) {
            BILoggerFactory.getLogger(this.getClass()).debug(BIStringUtils.append("compatibility warning! the parameter whose name is ", str, " should be transferd"));
        }
        return sb.toString();
    }

    private void formatValues() throws JSONException {
        JSONObject finalJson = new JSONObject();
        Iterator iterator = keys.keys();
        while (iterator.hasNext()) {
            String key = (String) iterator.next();
            String[] originalKeys = patterValues(keys.getString(key)).split("/");
            for (String oriKey : originalKeys) {
                finalJson.put(oriKey, key);
            }
        }
        keys = finalJson;
    }

    private String patterValues(String originalKey) {
        Pattern pattern = Pattern.compile("(?<=\\()(.+?)(?=\\))");
        Matcher matcher = pattern.matcher(originalKey);
        while (matcher.find()) {
            return matcher.group(0);
        }
        return originalKey;
    }

    private JSONObject readKeyJson() throws JSONException, ClassNotFoundException, IOException {
        StringBuffer keysStr = new StringBuffer();
        InputStream is = this.getClass().getResourceAsStream(DEFAULT_FILE_NAME);
        if (is == null) {
            BILoggerFactory.getLogger(this.getClass()).error("keys.json not existed in this path" + this.getClass().getResource("").toString());
            return new JSONObject();
        }
        BufferedReader br = new BufferedReader(new InputStreamReader(is));
        String s;
        while ((s = br.readLine()) != null) {
            keysStr.append(s);
        }
        return new JSONObject(keysStr.toString());
    }
}
