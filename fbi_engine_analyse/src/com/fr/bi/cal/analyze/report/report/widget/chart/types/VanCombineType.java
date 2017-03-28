package com.fr.bi.cal.analyze.report.report.widget.chart.types;

/**
 * Created by eason on 2017/3/23.
 */
public enum VanCombineType {

    COLUMN(1),//柱状图
    STACKED_COLUMN(2),//堆积柱状图
    AREA(3);//面积图

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

            case COLUMN:
                return "column";


            default:
                return "column";
        }

    }
}
