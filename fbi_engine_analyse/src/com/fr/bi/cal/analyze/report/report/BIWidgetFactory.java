package com.fr.bi.cal.analyze.report.report;

import com.finebi.cube.api.ICubeDataLoader;
import com.finebi.cube.api.UserAnalysisCubeDataLoaderCreator;
import com.finebi.cube.common.log.BILoggerFactory;
import com.fr.bi.cal.analyze.report.report.widget.*;
import com.fr.bi.cal.analyze.report.report.widget.chart.types.*;
import com.fr.bi.conf.report.BIWidget;
import com.fr.bi.conf.report.WidgetType;
import com.fr.bi.stable.constant.BIReportConstant;
import com.fr.general.ComparatorUtils;
import com.fr.json.JSONArray;
import com.fr.json.JSONException;
import com.fr.json.JSONObject;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Widget的静态方法
 *
 * @author Daniel-pc
 */
public class BIWidgetFactory {

    private static HashMap<WidgetType, Class<? extends VanChartWidget>> vancharts = new HashMap<WidgetType, Class<? extends VanChartWidget>>();

    static {
        vancharts.put(WidgetType.COLUMN, VanColumnWidget.class);
        vancharts.put(WidgetType.STACKED_COLUMN, VanStackedColumnWidget.class);
        vancharts.put(WidgetType.PERCENT_STACKED_COLUMN, VanPercentStackedColumnWidget.class);
        vancharts.put(WidgetType.COMPARE_COLUMN, VanCompareColumnWidget.class);
        vancharts.put(WidgetType.FALL_COLUMN, VanFallColumnWidget.class);

        vancharts.put(WidgetType.BAR, VanBarWidget.class);
        vancharts.put(WidgetType.STACKED_BAR, VanStackedBarWidget.class);
        vancharts.put(WidgetType.COMPARE_BAR, VanCompareBarWidget.class);

        vancharts.put(WidgetType.LINE, VanLineWidget.class);
        vancharts.put(WidgetType.AREA, VanAreaWidget.class);
        vancharts.put(WidgetType.STACKED_AREA, VanStackedAreaWidget.class);
        vancharts.put(WidgetType.PERCENT_STACKED_AREA, VanPercentStackedAreaWidget.class);
        vancharts.put(WidgetType.COMPARE_AREA, VanCompareAreaWidget.class);
        vancharts.put(WidgetType.RANGE_AREA, VanRangeAreaWidget.class);

        vancharts.put(WidgetType.COMBINE_CHART, VanCombinationWidget.class);
        vancharts.put(WidgetType.MULTI_AXIS_COMBINE_CHART, VanMultiAxisCombinationWidget.class);

        vancharts.put(WidgetType.PIE, VanPieWidget.class);
        vancharts.put(WidgetType.DONUT, VanDonutWidget.class);
        vancharts.put(WidgetType.GAUGE, VanGaugeWidget.class);

        vancharts.put(WidgetType.DOT, VanDotWidget.class);
        vancharts.put(WidgetType.SCATTER, VanDotWidget.class);
        vancharts.put(WidgetType.BUBBLE, VanDotWidget.class);
        vancharts.put(WidgetType.FORCE_BUBBLE, VanForceBubbleWidget.class);

        vancharts.put(WidgetType.RADAR, VanRadarWidget.class);
        vancharts.put(WidgetType.STACKED_RADAR, VanStackedRadarWidget.class);

        vancharts.put(WidgetType.FUNNEL, VanFunnelWidget.class);
        vancharts.put(WidgetType.TREE_MAP, VanTreeMapWidget.class);
        vancharts.put(WidgetType.MULTI_PIE, VanMultiPieWidget.class);

        vancharts.put(WidgetType.MAP, VanMapWidget.class);
        vancharts.put(WidgetType.GIS_MAP, VanGisWidget.class);
    }


    public static BIWidget createVanChartWidget(WidgetType type) {
        try {

            return vancharts.get(type).newInstance();

        } catch (Exception e) {

            BILoggerFactory.getLogger().error("error in create chart widget");

            return null;
        }

    }


    /**
     * 根据属性选择生成不同的widget
     *
     * @param jo JSONObject 对象
     * @return BIWidget
     * @throws Exception
     */
    public static BIWidget parseWidget(JSONObject jo, long userId, ICubeDataLoader loader) throws Exception {
        boolean isCombineChart = BIReportConstant.WIDGET.COMBINE_CHART == jo.getInt("type");
        if (isCombineChart) {
            updateCombineChartView(jo);
        }
        JSONArray viewTargets = getViewTarget(jo);
        BIWidget widget = newWidgetByType(WidgetType.parse(jo.optInt("type")), viewTargets);
        widget.parseJSON(jo, userId);
        return widget;
    }

    public static BIWidget newWidgetByType(WidgetType type, JSONArray viewTargets) throws Exception {
        BIWidget biWidget;
        switch (type) {
            case TABLE:
            case CROSS_TABLE:
            case COMPLEX_TABLE:
                biWidget = new TableWidget();
                break;
            case DETAIL:
                biWidget = new BIDetailWidget();
                break;
            case STRING:
            case STRING_LIST:
                biWidget = new StringControlWidget();
                break;
            case LIST_LABEL:
                biWidget = new ListLabelWidget();
                break;
            case TREE:
            case TREE_LIST:
                biWidget = new TreeWidget();
                break;
            case TREE_LABEL:
                biWidget = new TreeLabelWidget();
                break;
            case SINGLE_SLIDER:
            case INTERVAL_SLIDER:
                biWidget = new SingleSliderWidget();
                break;
            default:
                biWidget = createVanChartWidget(type);
        }

        return biWidget;
    }
  /*
    *  vanChart需要根据chartType对指标进行分组
    * */

    public static JSONArray getViewTarget(JSONObject jo) throws Exception {
        JSONObject view = jo.optJSONObject("view");
        if (view == null) {
            return new JSONArray();
        }
        JSONArray ja = new JSONArray();
//        if (view.optJSONArray(BIReportConstant.REGION.TARGET1) != null) {
//            ja.put(view.optJSONArray(BIReportConstant.REGION.TARGET1));
//        }
//        if (view.optJSONArray(BIReportConstant.REGION.TARGET2) != null) {
//            ja.put(view.optJSONArray(BIReportConstant.REGION.TARGET2));
//        }
//        if (view.optJSONArray(BIReportConstant.REGION.TARGET3) != null) {
//            ja.put(view.optJSONArray(BIReportConstant.REGION.TARGET3));
//        }
        Iterator keys = view.keys();
        while (keys.hasNext()){
            String key = keys.next().toString();
            if (Integer.valueOf(key)>=Integer.valueOf(BIReportConstant.REGION.TARGET1)&&Integer.valueOf(key)<=Integer.valueOf(BIReportConstant.REGION.TARGET3)) {
                if (view.optJSONArray(key)!=null) {
                    ja.put(view.getJSONArray(key));
                }
            }
        }
        return ja;
    }

    /*
    * 根据chartType设定分组
    * */
    private static void updateCombineChartView(JSONObject jo) throws JSONException {
        JSONObject view = jo.optJSONObject("view");
        Integer basicTarget;
        if (view.optJSONArray(BIReportConstant.REGION.TARGET1) != null) {
            basicTarget = Integer.valueOf(BIReportConstant.REGION.TARGET1);
            Map<Integer, JSONArray> chartTypeMap = createDimensionChartTypeMap(jo.optJSONObject("dimensions"), view.optJSONArray(BIReportConstant.REGION.TARGET1));
            Iterator<Integer> iterator = chartTypeMap.keySet().iterator();
            while (iterator.hasNext()) {
                Integer type = iterator.next();
                view.put(String.valueOf(basicTarget), chartTypeMap.get(type));
                basicTarget++;
            }
        }
        if (view.optJSONArray(BIReportConstant.REGION.TARGET2) != null) {
            basicTarget = Integer.valueOf(BIReportConstant.REGION.TARGET2);
            Map<Integer, JSONArray> chartTypeMap = createDimensionChartTypeMap(jo.optJSONObject("dimensions"), view.optJSONArray(BIReportConstant.REGION.TARGET2));
            Iterator<Integer> iterator = chartTypeMap.keySet().iterator();
            while (iterator.hasNext()) {
                Integer type = iterator.next();
                view.put(String.valueOf(basicTarget), chartTypeMap.get(type));
                basicTarget++;
            }
        }

    }

    private static Map<Integer, JSONArray> createDimensionChartTypeMap(JSONObject dimensions, JSONArray array) throws JSONException {
        Map<Integer, JSONArray> typeMap = new HashMap<>();
        if (null != dimensions) {
            Iterator keys = dimensions.keys();
            while (keys.hasNext()) {
                String dimId = keys.next().toString();
                if (!includedInArray(dimId, array)) {
                    continue;
                }
                JSONObject dimension = dimensions.getJSONObject(dimId);
                //默认柱形图
                int chartType = BIReportConstant.WIDGET.AXIS;
                if (dimension.has("styleOfChart") && dimension.getJSONObject("styleOfChart").has("type")) {
                    chartType = dimension.getJSONObject("styleOfChart").getInt("type");
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

    private static boolean includedInArray(String dimId, JSONArray array) throws JSONException {
        for (int i = 0; i < array.length(); i++) {
            if (ComparatorUtils.equals(array.getString(i), dimId)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 根据属性选择生成不同的widget
     *
     * @param jo JSONObject 对象
     * @return BIWidget
     * @throws Exception
     */
    public static BIWidget parseWidget(JSONObject jo, long userId) throws Exception {
        return parseWidget(jo, userId, UserAnalysisCubeDataLoaderCreator.getInstance().fetchCubeLoader(userId));
    }
}