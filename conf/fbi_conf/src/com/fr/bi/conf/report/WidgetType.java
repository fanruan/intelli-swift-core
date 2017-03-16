package com.fr.bi.conf.report;

import com.fr.bi.stable.constant.BIReportConstant;

/**
 * Created by eason on 2017/2/27.
 */
public enum WidgetType {
    NONE(BIReportConstant.WIDGET.NONE),

    TABLE(BIReportConstant.WIDGET.TABLE),//分组表
    CROSS_TABLE(BIReportConstant.WIDGET.CROSS_TABLE),//交叉表
    COMPLEX_TABLE(BIReportConstant.WIDGET.COMPLEX_TABLE),//复杂表
    DETAIL(BIReportConstant.WIDGET.DETAIL),//明细表

    COLUMN(BIReportConstant.WIDGET.AXIS),//柱状图
    ACCUMULATE_COLUMN(BIReportConstant.WIDGET.ACCUMULATE_AXIS),//堆积柱状图
    PERCENT_ACCUMULATE_COLUMN(BIReportConstant.WIDGET.PERCENT_ACCUMULATE_AXIS),//百分比堆积柱状图
    COMPARE_COLUMN(BIReportConstant.WIDGET.COMPARE_AXIS),//对比柱状图
    FALL_COLUMN(BIReportConstant.WIDGET.FALL_AXIS),//瀑布图
    BAR(BIReportConstant.WIDGET.BAR),//条形图
    ACCUMULATE_BAR(BIReportConstant.WIDGET.ACCUMULATE_BAR),//堆积条形图
    COMPARE_BAR(BIReportConstant.WIDGET.COMPARE_BAR),//对比条形图

    LINE(BIReportConstant.WIDGET.LINE),//折线图
    AREA(BIReportConstant.WIDGET.AREA),//面积图
    ACCUMULATE_AREA(BIReportConstant.WIDGET.ACCUMULATE_AREA),//堆积面积图
    PERCENT_ACCUMULATE_AREA(BIReportConstant.WIDGET.PERCENT_ACCUMULATE_AREA),//百分比堆积面积图
    COMPARE_AREA(BIReportConstant.WIDGET.COMPARE_AREA),//对比面积图
    RANGE_AREA(BIReportConstant.WIDGET.RANGE_AREA),//范围面积图
    COMBINE_CHART(BIReportConstant.WIDGET.COMBINE_CHART),//组合图
    MULTI_AXIS_COMBINE_CHART(BIReportConstant.WIDGET.MULTI_AXIS_COMBINE_CHART),//多值轴组合图
    PIE(BIReportConstant.WIDGET.PIE),//饼图
    DONUT(BIReportConstant.WIDGET.DONUT),//圆环图
    MAP(BIReportConstant.WIDGET.MAP),//地图
    GIS_MAP(BIReportConstant.WIDGET.GIS_MAP),//GIS地图

    GAUGE(BIReportConstant.WIDGET.DASHBOARD),//仪表盘
    BUBBLE(BIReportConstant.WIDGET.BUBBLE),//气泡图
    FORCE_BUBBLE(BIReportConstant.WIDGET.FORCE_BUBBLE),//力学气泡图
    SCATTER(BIReportConstant.WIDGET.SCATTER),//散点图
    RADAR(BIReportConstant.WIDGET.RADAR),//雷达图
    ACCUMULATE_RADAR(BIReportConstant.WIDGET.ACCUMULATE_RADAR),//堆积雷达图
    FUNNEL(BIReportConstant.WIDGET.FUNNEL),//漏斗图
    TREE_MAP(BIReportConstant.WIDGET.RECT_TREE),//矩形树图
    MULTI_PIE(BIReportConstant.WIDGET.MULTI_PIE),//多层饼图
    HEAT_MAP(BIReportConstant.WIDGET.HEAT_MAP),//热力地图
    PARETO(BIReportConstant.WIDGET.PARETO),//帕累托图

    STRING(BIReportConstant.WIDGET.STRING),//文本控件
    NUMBER(BIReportConstant.WIDGET.NUMBER),//数值控件
    TREE(BIReportConstant.WIDGET.TREE),//树控件
    SINGLE_SLIDER(BIReportConstant.WIDGET.SINGLE_SLIDER),//单值滑块
    INTERVAL_SLIDER(BIReportConstant.WIDGET.INTERVAL_SLIDER),//区间滑块
    LIST_LABEL(BIReportConstant.WIDGET.LIST_LABEL),//文本标签
    TREE_LABEL(BIReportConstant.WIDGET.TREE_LABEL),//树标签
    STRING_LIST(BIReportConstant.WIDGET.STRING_LIST),//文本列表
    TREE_LIST(BIReportConstant.WIDGET.TREE_LIST),//树列表

    //仅前台使用的部分类型
    DATE(BIReportConstant.WIDGET.DATE),
    YEAR(BIReportConstant.WIDGET.YEAR),
    QUARTER(BIReportConstant.WIDGET.QUARTER),
    MONTH(BIReportConstant.WIDGET.MONTH),
    YMD(BIReportConstant.WIDGET.YMD),
    DATE_PANE(BIReportConstant.WIDGET.DATE_PANE),

    QUERY(BIReportConstant.WIDGET.QUERY),
    RESET(BIReportConstant.WIDGET.RESET),
    CONTENT(BIReportConstant.WIDGET.CONTENT),
    IMAGE(BIReportConstant.WIDGET.IMAGE),
    WEB(BIReportConstant.WIDGET.WEB),
    GENERAL_QUERY(BIReportConstant.WIDGET.GENERAL_QUERY),

    TABLE_SHOW(BIReportConstant.WIDGET.TABLE_SHOW);

    private int type;

    WidgetType(int type){
        this.type = type;
    }

    public int getType() {
        return this.type;
    }

    private static WidgetType[] types;

    public static WidgetType parse(int type){
        if(types == null){
            types = WidgetType.values();
        }
        for(WidgetType widgetType : types){
            if(widgetType.getType() == type){
                return widgetType;
            }
        }
        return NONE;
    }
}
