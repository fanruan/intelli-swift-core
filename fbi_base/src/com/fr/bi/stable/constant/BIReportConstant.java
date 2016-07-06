package com.fr.bi.stable.constant;

/**
 * Created by GUY on 2015/4/3.
 */
public class BIReportConstant {

    public static final class BI_REPORT {
        public final static int NULL = 0x0;

        public final static int SUBMITED = 0x1;

        public final static int PUBLISHED = 0x2;
    }

    public static final class SUMMARY_TYPE {
        public final static int SUM = 0x0;

        public final static int MAX = 0x1;

        public final static int MIN = 0x2;

        public final static int AVG = 0x3;

        public final static int COUNT = 0x4;

        public final static int APPEND = 0x5;

        public final static int RECORD_COUNT = 0x6;

    }


    public static final class GROUP {

        public final static int NO_GROUP = 0x2;

        public final static int AUTO_GROUP = 0x3;

        public final static int CUSTOM_GROUP = 0x4;

        public final static int CUSTOM_NUMBER_GROUP = 0x5;

        public static final int Y = 0x6;

        public static final int S = 0x7;

        public static final int M = 0x8;

        public static final int W = 0x9;
        public static final int YMD = 0xA;

        public static final int YD = 0xB;

        public static final int MD = 0xC;

        public static final int YMDHMS = 0xD;

        public static final int ID_GROUP = 0xE;

    }

    public static final class DIMENSION_FILTER_STRING {
        public final static int BELONG_VALUE = 0x0;

        public final static int BELONG_USER = 0x1;

        public final static int NOT_BELONG_VALUE = 0x2;

        public final static int NOT_BELONG_USER = 0x3;

        public final static int CONTAIN = 0x4;

        public final static int NOT_CONTAIN = 0x5;

        public final static int IS_NULL = 0x6;

        public final static int NOT_NULL = 0x7;

        public final static int BEGIN_WITH = 0x8;

        public final static int END_WITH = 0x9;

        public final static int TOP_N = 0xa;

        public final static int BOTTOM_N = 0xb;

        public final static int NOT_BEGIN_WITH = 0xc;

        public final static int NOT_END_WITH = 0xd;

        public final static int VAGUE_CONTAIN = 0xe;

        public final static int NOT_VAGUE_CONTAIN = 0xf;
    }

    public static final class DIMENSION_FILTER_NUMBER {
        public final static int BELONG_VALUE = 0x10;

        public final static int BELONG_USER = 0x11;

        public final static int NOT_BELONG_VALUE = 0x12;

        public final static int NOT_BELONG_USER = 0x13;

        public final static int MORE_THAN_AVG = 0x14;

        public final static int LESS_THAN_AVG = 0x15;

        public final static int IS_NULL = 0x16;

        public final static int NOT_NULL = 0x17;

        public final static int TOP_N = 0x18;

        public final static int BOTTOM_N = 0x19;

    }

    public static final class TARGET_FILTER_STRING {
        public final static int BELONG_VALUE = 0x20;

        public final static int BELONG_USER = 0x21;

        public final static int NOT_BELONG_VALUE = 0x22;

        public final static int NOT_BELONG_USER = 0x23;

        public final static int CONTAIN = 0x24;

        public final static int NOT_CONTAIN = 0x25;

        public final static int IS_NULL = 0x26;

        public final static int NOT_NULL = 0x27;

        public final static int BEGIN_WITH = 0x28;

        public final static int END_WITH = 0x29;

        public static final int NOT_BEGIN_WITH = 0x2a;

        public static final int NOT_END_WITH = 0x2b;

        public final static int VAGUE_CONTAIN = 0x2e;

        public final static int NOT_VAGUE_CONTAIN = 0x2f;
    }

    public static final class TARGET_FILTER_NUMBER {
        public final static int EQUAL_TO = 0x30;

        public final static int NOT_EQUAL_TO = 0x31;

        public final static int BELONG_VALUE = 0x32;

        public final static int BELONG_USER = 0x33;

        public final static int NOT_BELONG_VALUE = 0x34;

        public final static int NOT_BELONG_USER = 0x35;

        public final static int IS_NULL = 0x36;

        public final static int NOT_NULL = 0x37;

        public final static int CONTAINS = 0x38;

        public final static int NOT_CONTAINS = 0x39;

        public final static int LARGE_THAN_CAL_LINE = 0x3a;

        public final static int LARGE_OR_EQUAL_CAL_LINE = 0x3b;

        public final static int SMALL_THAN_CAL_LINE = 0x3C;

        public final static int SMALL_OR_EQUAL_CAL_LINE = 0x3d;

        public final static int TOP_N = 0x3e;

        public final static int BOTTOM_N = 0x3f;
    }

    public static final class FILTER_DATE {
        public final static int BELONG_DATE_RANGE = 0x40;

        public final static int BELONG_WIDGET_VALUE = 0x41;

        public final static int NOT_BELONG_DATE_RANGE = 0x42;

        public final static int NOT_BELONG_WIDGET_VALUE = 0x43;

        public final static int MORE_THAN = 0x44;

        public final static int LESS_THAN = 0x45;

        public final static int EQUAL_TO = 0x46;

        public final static int NOT_EQUAL_TO = 0x47;

        public final static int IS_NULL = 0x48;

        public final static int NOT_NULL = 0x49;

        public final static int EARLY_THAN = 0x4a;

        public final static int LATER_THAN = 0x4b;

        public final static int CONTAINS = 0x4c;

        public final static int CONTAINS_DAY = 0x4d;

        public final static int DAY_EQUAL_TO = 0x4e;

        public final static int DAY_NOT_EQUAL_TO = 0x4f;

    }

    public static final class FILTER_TYPE {
        public final static int AND = 0x50;

        public final static int OR = 0x51;

        public final static int FORMULA = 0x52;

        public final static int EMPTY_FORMULA = 0x5a;

        public final static int EMPTY_CONDITION = 0x5b;

        public final static int NUMBER_SUM = 0x53;

        public final static int NUMBER_AVG = 0x54;

        public final static int NUMBER_MAX = 0x55;

        public final static int NUMBER_MIN = 0x56;

        public final static int NUMBER_COUNT = 0x57;

        public final static int TREE_FILTER = 0x58;

        public final static int COLUMNFILTER = 0x59;

        public final static int DIMENSION_TARGET_VALUE_FILTER = 0x60;

        public final static int DIMENSION_SELF_FILTER = 0x61;

    }

    public static final class DIMENSION_FILTER_DATE {

        public final static int BELONG_VALUE = 0x62;

        public final static int NOT_BELONG_VALUE = 0x63;

        public final static int IS_NULL = 0x64;

        public final static int NOT_NULL = 0x65;

        public final static int TOP_N = 0x66;

        public final static int BOTTOM_N = 0x67;

        public final static int CONTAIN = 0x68;

        public final static int NOT_CONTAIN = 0x69;

        public final static int BEGIN_WITH = 0x6a;

        public final static int END_WITH = 0x6b;
    }

    public static final class TARGET_TYPE {

        public static final int STRING = 0x1;

        public static final int NUMBER = 0x2;

        public static final int DATE = 0x3;

        public static final int COUNTER = 0x4;

        public static final int FORMULA = 0x5;
        public static final int YEAR_ON_YEAR_RATE = 0x6;
        public static final int MONTH_ON_MONTH_RATE = 0x7;
        public static final int YEAR_ON_YEAR_VALUE = 0x8;
        public static final int MONTH_ON_MONTH_VALUE = 0x9;
        public static final int SUM_OF_ABOVE = 0xa;
        public static final int SUM_OF_ABOVE_IN_GROUP = 0xb;
        public static final int SUM_OF_ALL = 0xc;
        public static final int SUM_OF_ALL_IN_GROUP = 0xd;
        public static final int RANK = 0xe;
        public static final int RANK_IN_GROUP = 0xf;


//        public static final int CALCULATOR = 0x5;

        public static final class CAL {

            public static final int FORMULA = 0x0;

            public static final int CONFIGURATION = 0x1;

        }

        public static final class CAL_VALUE {

            public static final int SUM_OF_ALL = 0x0;

            public static final int PERIOD = 0x1;

            public static final int SUM_OF_ABOVE = 0x2;

            public static final int RANK = 0x3;

            public static final class RANK_TPYE {

                public static final int ASC = 0x0;

                public static final int DESC = 0x1;
            }

            public static final class SUMMARY_TYPE {
                public final static int SUM = 0x0;

                public final static int MAX = 0x1;

                public final static int MIN = 0x2;

                public final static int AVG = 0x3;
            }

            public static final class PERIOD_TYPE {
                public final static int VALUE = 0x0;

                public final static int RATE = 0x1;
            }


        }


        public static final class CAL_POSITION {

            public static final int ALL = 0x0;

            public static final int INGROUP = 0x1;
        }
    }

    /**
     * 控件类型
     *
     * @author frank
     */
    public static final class WIDGET {

        public static final int TABLE = 0x1;              //分组表
        public static final int CROSS_TABLE = 0x2;       //交叉表
        public static final int COMPLEX_TABLE = 0x3;     //复杂表
        public static final int DETAIL = 0x4;             //明细表

        public static final int AXIS = 0x5;               //柱状图
        public static final int ACCUMULATE_AXIS = 0x6;   //堆积柱状图
        public static final int PERCENT_ACCUMULATE_AXIS = 0x7;   //百分比堆积柱状图
        public static final int COMPARE_AXIS = 0x8;      //对比柱状图
        public static final int FALL_AXIS = 0x9;         //瀑布图

        public static final int BAR = 0xa;                //条形图
        public static final int ACCUMULATE_BAR = 0xb;    //堆积条形图
        public static final int COMPARE_BAR = 0xc;       //对比条形图

        public static final int LINE = 0xd;              //折线图

        public static final int AREA = 0xe;               //面积图
        public static final int ACCUMULATE_AREA = 0xf;   //堆积面积图
        public static final int PERCENT_ACCUMULATE_AREA = 0x10;       //百分比堆积面积图
        public static final int COMPARE_AREA = 0x11;      //对比面积图
        public static final int RANGE_AREA = 0x12;        //范围面积图

        public static final int COMBINE_CHART = 0x13;     //组合图
        public static final int MULTI_AXIS_COMBINE_CHART = 0x14;  //多值轴组合图

        public static final int PIE = 0x15;                //饼图

        public static final int DONUT = 0x16;              //donut

        public static final int MAP = 0x17;                //地图
        public static final int GIS_MAP = 0x18;           //GIS地图

        public static final int DASHBOARD = 0x19;          //仪表盘

        public static final int BUBBLE = 0x1a;            //气泡图
        public static final int FORCE_BUBBLE = 0x1b;      //力学气泡图

        public static final int SCATTER = 0x1c;           //散点图

        public static final int RADAR = 0x1d;             //雷达图
        public static final int ACCUMULATE_RADAR = 0x1e;  //堆积雷达图

        public static final int FUNNEL = 0x1f;            //漏斗图


        public static final int STRING = 0x20;            //文本控件
        public static final int NUMBER = 0x21;            //数值控件
        public static final int TREE = 0x22;              //树控件

        //仅前台使用的部分类型
        public static final int DATE = 0x30;
        public static final int YEAR = 0x31;
        public static final int QUARTER = 0x32;
        public static final int MONTH = 0x33;
        public static final int YMD = 0x34;

        public static final int QUERY = 0x35;
        public static final int RESET = 0x36;
        public static final int CONTENT = 0x37;
        public static final int IMAGE = 0x38;
        public static final int WEB = 0x39;
        public static final int GENERAL_QUERY = 0x3a;

        public static final int TABLE_SHOW = 0x40;

        public static final int NONE = -1;
    }

    public static final class REGION {
        public static final String DIMENSION1 = "10000";

        public static final String DIMENSION2 = "20000";

        public static final String TARGET1 = "30000";

        public static final String TARGET2 = "40000";

        public static final String TARGET3 = "50000";
    }


    public static class TABLE_WIDGET {
        public static final int GROUP_TYPE = 1;
        public static final int CROSS_TYPE = 2;
        public static final int COMPLEX_TYPE = 3;
    }

    public static class TABLE_PAGE {
        public static final int VERTICAL_PRE = 0;
        public static final int VERTICAL_NEXT = 1;
        public static final int HORIZON_PRE = 2;
        public static final int HORIZON_NEXT = 3;
        public static final int TOTAL_PAGE = 4;
    }

    public static class TABLE_PAGE_OPERATOR {
        public static final int ALL_PAGE = -1;
        public static final int REFRESH = 0;
        public static final int COLUMN_PRE = 1;
        public static final int COLUMN_NEXT = 2;
        public static final int ROW_PRE = 3;
        public static final int ROW_NEXT = 4;
        public static final int EXPAND = 5;
    }

    /**
     * 排序
     *
     * @author frank
     */
    public static final class SORT {
        // number string date
        public static final int ASC = 0x0;
        // number string date
        public static final int DESC = 0x1;
        // String
        public static final int CUSTOM = 0x2;

        public static final int NONE = 0x3;
        // number string date
        public static final int NUMBER_ASC = 0x4;
        // number string date
        public static final int NUMBER_DESC = 0x5;

    }

    public static final class EXPANDER_TYPE {

        public static final boolean NONE = false;

        public static final boolean ALL = true;
    }

    public static final class BUSINESS_TABLE_TYPE {
        public static final int NORMAL = 0;
    }

    public static final class TREE {
        public static final class TREE_REQ_TYPE {
            public static final int INIT_DATA = 0x0;

            public static final int SEARCH_DATA = 0x1;

            public static final int SELECTED_DATA = 0x3;

            public static final int ADJUST_DATA = 0x2;

            public static final int DISPLAY_DATA = 0x4;
        }

        public static final int TREE_ITEM_COUNT_PER_PAGE = 0x64;
    }

    public static final class FIELD_ID {
        public static final String HEAD = "81c48028-1401-11e6-a148-3e1d05defe78";
    }

    public static final String SYSTEM_TIME = "__system_time-3e1d05defe78__";

    public static final class REPORT_STATUS {
        public static final int APPLYING = 1;
        public static final int HANGOUT = 2;
        public static final int NORMAL = 3;
    }

    public static final class CUSTOM_GROUP {
        public static final class UNGROUP2OTHER {
            public static final int NOTSELECTED = 0;
            public static final int SELECTED = 1;
        }

    }

    public enum MULTI_PATH_STATUS {
        NEED_GENERATE_CUBE(0),
        NOT_NEED_GENERATE_CUBE(1);

        private int status;

        MULTI_PATH_STATUS(int status) {
            this.status = status;
        }

        public int getStatus() {
            return this.status;
        }

        @Override
        public String toString(){
            return String.valueOf(this.status);
        }
    }


}