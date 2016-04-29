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

        public final static int DIMENSION_SELF_FILTER = 0x611;

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

        public static final int TABLE = 1;

        public static final int BAR = 2;

        public static final int ACCUMULATED_BAR = 3;

        public static final int PIE = 4;

        public static final int DASHBOARD = 5;

        public static final int AXIS = 6;

        public static final int MAP = 7;

        public static final int DETAIL = 8;

        public static final int DONUT = 9;

        public static final int BUBBLE = 10;

        public static final int SCATTER = 11;

        public static final int RADAR = 12;

        public static final int STRING = 13;

        public static final int TREE = 20;
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


}