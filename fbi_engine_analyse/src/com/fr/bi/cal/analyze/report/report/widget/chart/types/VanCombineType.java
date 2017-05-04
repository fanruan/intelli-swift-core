package com.fr.bi.cal.analyze.report.report.widget.chart.types;

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

    public static String parseStringType(int type){

        switch (parse(type)){
            case AREA_NORMAL:
            case AREA_CURVE:
            case AREA_RIGHT_ANGLE:
            case STACKED_AREA_NORMAL:
            case STACKED_AREA_CURVE:
            case STACKED_AREA_RIGHT_ANGLE:
                return "area";

            case  LINE_NORMAL:
            case LINE_CURVE:
            case LINE_RIGHT_ANGLE:
                return "line";

            default:
                return "column";
        }

    }
}
