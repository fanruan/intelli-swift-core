package com.fr.bi.cal.analyze.report.report.widget.chart.types;

import com.fr.bi.conf.report.WidgetType;

/**
 * Created by eason on 2017/3/23.
 */
public enum VanCombineType {

    COLUMN(1),//柱状图
    STACKED_COLUMN(2),//堆积柱状图
    AREA_NORMAL(3),//面积图
    AREA_CURVE(4),
    AREA_RIGHT_ANGLE(5),
    STACKED_AREA_NORMAL(6),
    STACKED_AREA_CURVE(7),
    STACKED_AREA_RIGHT_ANGLE(8),
    LINE_NORMAL(9),
    LINE_CURVE(10),
    LINE_RIGHT_ANGLE(11);

    private int type;

    VanCombineType(int type){
        this.type = type;
    }

    public int getType() {
        return this.type;
    }

    private static VanCombineType[] types;

    public static VanCombineType parse(int type){
        if(types == null){
            types = VanCombineType.values();
        }
        for(VanCombineType widgetType : types){
            if(widgetType.getType() == type){
                return widgetType;
            }
        }
        return COLUMN;
    }

    public static String parseStringType(VanCombineType type){

        if(type == AREA_NORMAL || type == AREA_CURVE || type == AREA_RIGHT_ANGLE || type == STACKED_AREA_NORMAL || type == STACKED_AREA_CURVE || type == STACKED_AREA_RIGHT_ANGLE){
            return "area";
        }else if(type == LINE_NORMAL || type == LINE_CURVE || type == LINE_RIGHT_ANGLE){
            return "line";
        }

        return "column";
    }

    public static boolean isStacked(VanCombineType widgetType){

        return widgetType == STACKED_AREA_NORMAL || widgetType == STACKED_AREA_CURVE || widgetType == STACKED_AREA_RIGHT_ANGLE || widgetType == STACKED_COLUMN;
    }

    public static boolean isCurve(VanCombineType widgetType){

        return widgetType == AREA_CURVE || widgetType == STACKED_AREA_CURVE || widgetType == LINE_CURVE;
    }

    public static boolean isStep(VanCombineType widgetType){

        return widgetType == AREA_RIGHT_ANGLE || widgetType == STACKED_AREA_RIGHT_ANGLE || widgetType == LINE_RIGHT_ANGLE;
    }
}
