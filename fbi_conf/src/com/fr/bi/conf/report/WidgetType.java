package com.fr.bi.conf.report;

/**
 * Created by eason on 2017/2/27.
 */
public enum WidgetType {
    NONE(-1),

    TABLE(0x1),//分组表
    CROSS_TABLE(0x2),//交叉表
    COMPLEX_TABLE(0x3),//复杂表
    DETAIL(0x4),//明细表

    COLUMN(0x5),//柱状图
    ACCUMULATE_COLUMN(0x6),//堆积柱状图
    PERCENT_ACCUMULATE_COLUMN(0x7),//百分比堆积柱状图
    COMPARE_COLUMN(0x8),//对比柱状图
    FALL_COLUMN(0x9),//瀑布图
    BAR(0xa),//条形图
    ACCUMULATE_BAR(0xb),//堆积条形图
    COMPARE_BAR(0xc),//对比条形图

    LINE(0xd),//折线图
    AREA(0xe),//面积图
    ACCUMULATE_AREA(0xf),//堆积面积图
    PERCENT_ACCUMULATE_AREA(0x10),//百分比堆积面积图
    COMPARE_AREA(0x11),//对比面积图
    RANGE_AREA(0x12),//范围面积图
    COMBINE_CHART(0x13),//组合图
    MULTI_AXIS_COMBINE_CHART(0x14),//多值轴组合图
    PIE(0x15),//饼图
    DONUT(0x16),//圆环图
    MAP(0x17),//地图
    GIS_MAP(0x18),//GIS地图

    GAUGE(0x19),//仪表盘
    BUBBLE(0x1a),//气泡图
    FORCE_BUBBLE(0x1b),//力学气泡图
    SCATTER(0x1c),//散点图
    RADAR(0x1d),//雷达图
    ACCUMULATE_RADAR(0x1e),//堆积雷达图
    FUNNEL(0x1f),//漏斗图
    TREE_MAP(0x27),//矩形树图
    MULTI_PIE(0x28),//多层饼图
    HEAT_MAP(0x41),//热力地图
    PARETO(0x42),//帕累托图

    STRING(0x20),//文本控件
    NUMBER(0x21),//数值控件
    TREE(0x22),//树控件
    SINGLE_SLIDER(0x23),//单值滑块
    INTERVAL_SLIDER(0x24),//区间滑块
    LIST_LABEL(0x25),//文本标签
    TREE_LABEL(0x26),//树标签
    STRING_LIST(0x3b),//文本列表
    TREE_LIST(0x3c),//树列表

    //仅前台使用的部分类型
    DATE(0x30),
    YEAR(0x31),
    QUARTER(0x32),
    MONTH(0x33),
    YMD(0x34),

    QUERY(0x35),
    RESET(0x36),
    CONTENT(0x37),
    IMAGE(0x38),
    WEB(0x39),
    GENERAL_QUERY(0x3a),

    TABLE_SHOW(0x40);

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
