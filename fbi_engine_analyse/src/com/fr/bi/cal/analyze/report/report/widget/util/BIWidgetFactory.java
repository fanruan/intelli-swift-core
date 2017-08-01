package com.fr.bi.cal.analyze.report.report.widget.util;

import com.finebi.cube.common.log.BILoggerFactory;
import com.fr.bi.cal.analyze.report.report.widget.DetailWidget;
import com.fr.bi.cal.analyze.report.report.widget.ListLabelWidget;
import com.fr.bi.cal.analyze.report.report.widget.SingleSliderWidget;
import com.fr.bi.cal.analyze.report.report.widget.StringControlWidget;
import com.fr.bi.cal.analyze.report.report.widget.TableWidget;
import com.fr.bi.cal.analyze.report.report.widget.TreeLabelWidget;
import com.fr.bi.cal.analyze.report.report.widget.TreeWidget;
import com.fr.bi.cal.analyze.report.report.widget.VanChartWidget;
import com.fr.bi.cal.analyze.report.report.widget.chart.types.AbstractVanChartWidget;
import com.fr.bi.cal.analyze.report.report.widget.chart.types.VanAreaWidget;
import com.fr.bi.cal.analyze.report.report.widget.chart.types.VanBarWidget;
import com.fr.bi.cal.analyze.report.report.widget.chart.types.VanColumnWidget;
import com.fr.bi.cal.analyze.report.report.widget.chart.types.VanCombinationWidget;
import com.fr.bi.cal.analyze.report.report.widget.chart.types.VanCompareAreaWidget;
import com.fr.bi.cal.analyze.report.report.widget.chart.types.VanCompareBarWidget;
import com.fr.bi.cal.analyze.report.report.widget.chart.types.VanCompareColumnWidget;
import com.fr.bi.cal.analyze.report.report.widget.chart.types.VanDonutWidget;
import com.fr.bi.cal.analyze.report.report.widget.chart.types.VanDotWidget;
import com.fr.bi.cal.analyze.report.report.widget.chart.types.VanFallColumnWidget;
import com.fr.bi.cal.analyze.report.report.widget.chart.types.VanForceBubbleWidget;
import com.fr.bi.cal.analyze.report.report.widget.chart.types.VanFunnelWidget;
import com.fr.bi.cal.analyze.report.report.widget.chart.types.VanGaugeWidget;
import com.fr.bi.cal.analyze.report.report.widget.chart.types.VanGisWidget;
import com.fr.bi.cal.analyze.report.report.widget.chart.types.VanLineWidget;
import com.fr.bi.cal.analyze.report.report.widget.chart.types.VanMapWidget;
import com.fr.bi.cal.analyze.report.report.widget.chart.types.VanMultiAxisCombinationWidget;
import com.fr.bi.cal.analyze.report.report.widget.chart.types.VanMultiPieWidget;
import com.fr.bi.cal.analyze.report.report.widget.chart.types.VanPercentStackedAreaWidget;
import com.fr.bi.cal.analyze.report.report.widget.chart.types.VanPercentStackedColumnWidget;
import com.fr.bi.cal.analyze.report.report.widget.chart.types.VanPieWidget;
import com.fr.bi.cal.analyze.report.report.widget.chart.types.VanRadarWidget;
import com.fr.bi.cal.analyze.report.report.widget.chart.types.VanRangeAreaWidget;
import com.fr.bi.cal.analyze.report.report.widget.chart.types.VanStackedAreaWidget;
import com.fr.bi.cal.analyze.report.report.widget.chart.types.VanStackedBarWidget;
import com.fr.bi.cal.analyze.report.report.widget.chart.types.VanStackedColumnWidget;
import com.fr.bi.cal.analyze.report.report.widget.chart.types.VanStackedRadarWidget;
import com.fr.bi.cal.analyze.report.report.widget.chart.types.VanTreeMapWidget;
import com.fr.bi.conf.report.BIWidget;
import com.fr.bi.conf.report.WidgetType;
import com.fr.bi.stable.constant.BIReportConstant;
import com.fr.json.JSONArray;
import com.fr.json.JSONObject;

import java.util.HashMap;

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
//todo 交给图表组处理
    public static AbstractVanChartWidget createVanWidgetByType(WidgetType type) {
//        try {
//            return vancharts.get(type).newInstance();
//        } catch (Exception e) {
//            BILoggerFactory.getLogger().error("error in create chart widget");
//            return null;
//        }
        return null;
    }

    /**
     * 根据属性选择生成不同的widget
     *
     * @param jo JSONObject 对象
     * @return BIWidget
     * @throws Exception
     */
    public static BIWidget parseWidget(JSONObject jo, long userId) throws Exception {
        JSONObject view = jo.optJSONObject("view");
        if (view == null) {
            view = new JSONObject();
        }
        JSONArray viewTargets = getViewTarget(view);
        BIWidget widget = createWidgetByType(WidgetType.parse(jo.optInt("type")));
        widget.parseJSON(jo, userId);
        return widget;
    }

    public static BIWidget createWidgetByType(WidgetType type) throws Exception {
        BIWidget biWidget;
        switch (type) {
            case TABLE:
            case CROSS_TABLE:
            case COMPLEX_TABLE:
                biWidget = new TableWidget();
                break;
            case DETAIL:
                biWidget = new DetailWidget();
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

    public static JSONArray getViewTarget(JSONObject view) throws Exception {
        JSONArray ja = new JSONArray();
        if (view.optJSONArray(BIReportConstant.REGION.TARGET1) != null) {
            ja.put(view.optJSONArray(BIReportConstant.REGION.TARGET1));
        }
        if (view.optJSONArray(BIReportConstant.REGION.TARGET2) != null) {
            ja.put(view.optJSONArray(BIReportConstant.REGION.TARGET1));
        }
        if (view.optJSONArray(BIReportConstant.REGION.TARGET3) != null) {
            ja.put(view.optJSONArray(BIReportConstant.REGION.TARGET3));
        }
        return ja;
    }

//    /**
//     * 根据属性选择生成不同的widget
//     *
//     * @param jo JSONObject 对象
//     * @return BIWidget
//     * @throws Exception
//     */
//    public static BIWidget parseWidget(JSONObject jo, long userId) throws Exception {
//        return parseWidget(jo, userId, UserAnalysisCubeDataLoaderCreator.getInstance().fetchCubeLoader(userId));
//    }
}