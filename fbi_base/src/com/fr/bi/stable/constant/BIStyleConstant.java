package com.fr.bi.stable.constant;

/**
 * Created by Kary on 2017/4/7.
 */

public class BIStyleConstant {
    //表设置所有默认属性（应该是包含分组表、交叉表、复杂表和其他所有图表）
    public static final class DEFAULT_CHART_SETTING {
        public static final int TABLE_FORM_GROUP = TABLE_FORM.OPEN_ROW;
        public static final String THEME_COLOR = "#04b1c2";
        public static final int TABLE_STYLE_GROUP = TABLE_STYLE.STYLE_NORMAL;
        public static final boolean IS_CUSTOM_TABLE_STYLE = false;
        public static final boolean SHOW_NUMBER = false;
        public static final boolean SHOW_ROW_TOTAL = true;
        public static final boolean SHOW_COL_TOTAL = true;
        public static final boolean OPEN_ROW_NODE = false;
        public static final boolean OPEN_COL_NODE = false;
        public static final int MAX_ROW = TABLE_MAX_ROW;
        public static final int MAX_COL = TABLE_MAX_COL;
        public static final int ROW_HEIGHT = 25;
        public static final boolean FREEZE_DIM = true;
        public static final boolean FREEZE_FIRST_COLUMN = false;
        public static final boolean TRANSFER_FILTER = false;
        public static final boolean CLICK_ZOOM = true;
        public static final boolean SHOW_NAME = true;
        public static final int NAME_POS = DASHBOARD_WIDGET_NAME_POS_LEFT;

        //图
        public static final String[] CHART_COLOR = {"#5caae4", "#70cc7f", "#ebbb67", "#e97e7b", "#6ed3c9"};
    }

    public static final class TABLE_FORM {
        public static final int OPEN_COL = 1;//纵向展开表格
        public static final int OPEN_ROW = 2;//横向展开表格
    }

    public static final class TABLE_STYLE {
        public static final int STYLE_NORMAL = 1; //普通风格
        public static final int STYLE_GRADUAL = 2; //渐变风格
        public static final int STYLE_SEPERATE = 3; //渐变风格
    }

    public static final int TABLE_MAX_ROW = 20;
    public static final int TABLE_MAX_COL = 7;


/*
 *组件、控件的各个操作
 */

    public static final int DASHBOARD_WIDGET_NAME_POS = 19;           //标题位置
    public static final int DASHBOARD_WIDGET_NAME_POS_LEFT = 20;      //标题位置居左
    public static final int DASHBOARD_WIDGET_NAME_POS_CENTER = 21;     //标题位置居右

    public static final class BACKGROUND_STYLE_TYPE {
        public static final int COLOR = 1;
        public static final int PIC = 2;
    }

}
