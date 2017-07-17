package com.fr.bi.cal.analyze.report.report;

import com.finebi.cube.common.log.BILoggerFactory;
import com.fr.bi.cal.analyze.report.report.widget.chart.types.*;
import com.fr.bi.conf.report.WidgetType;
import com.fr.bi.conf.report.conf.BIWidgetConf;
import com.fr.json.JSONObject;

import java.util.HashMap;

/**
 * widget 数据样式分离 这个工具类通过传过来的biWidgetConf和data处理各种类型的图表，生成view层需要的数据
 * Created by astronaut007 on 2017/7/15.
 */
public class BIWidgetUtils {

    private static HashMap<WidgetType, Class<? extends AbstractVanChartWidget>> vancharts = new HashMap<WidgetType, Class<? extends AbstractVanChartWidget>>();

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
        vancharts.put(WidgetType.WORD_CLOUD, VanWordCloudWidget.class);
        vancharts.put(WidgetType.HEAT_MAP, VanHeatMapWidget.class);
        vancharts.put(WidgetType.LINE_MAP, VanLineMapWidget.class);
    }


    private static AbstractVanChartWidget createVanChartWidget(WidgetType type){
        try {
            return vancharts.get(type).newInstance();
        }catch (Exception e){
            BILoggerFactory.getLogger().error("error in create chart widget");
            return null;
        }
    }

    public static JSONObject generateResult4View (BIWidgetConf widgetConf, JSONObject data) throws Exception {
        AbstractVanChartWidget vanChartWidget = createVanChartWidget(WidgetType.parse(widgetConf.getType()));
        vanChartWidget.init(widgetConf);
        return vanChartWidget.generateResult(widgetConf, data);
    }
}
