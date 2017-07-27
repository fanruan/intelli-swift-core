package com.fr.bi.stable.constant;

/**
 * Created by User on 2016/8/31.
 */
public class BIChartSettingConstant {

    public final static int AUTO = 1;
    public final static int X_AXIS = 3;
    public final static int NORMAL = 1;
    public final static int POLYGON = 7;
    public final static int NOT_SHOW = 2;
    public final static int ONE2POINT = 3;
    public final static int TWO2POINT = 4;
    public final static int LEFT_AXIS = 0;
    public final static int LNG_FIRST = 3;
    public final static int LAT_FIRST = 4;
    public final static int FIX_COUNT = 6;
    public final static int ROTATION = -90;
    public final static int RIGHT_AXIS = 1;
    public final static int ZERO2POINT = 2;
    public final static int NO_PROJECT = 16;
    public final static double MINLIMIT = 1e-5;
    public final static int ONE_POINTER = 1;
    public final static int MULTI_POINTER = 2;
    public final static int LEGEND_BOTTOM = 4;
    public final static int STYLE_NORMAL = 21;
    public final static int LEGEND_HEIGHT = 80;
    public final static String LEGEND_WIDTH = "30.0%";
    public final static int HALF_DASHBOARD = 9;
    public final static int VERTICAL_TUBE = 12;
    public final static int DASHBOARD_AXIS = 4;
    public final static int HORIZONTAL_TUBE = 13;
    public final static int RIGHT_AXIS_SECOND = 2;
    public final static int PERCENT_DASHBOARD = 10;
    public final static int PERCENT_SCALE_SLOT = 11;
    public final static int INTERVAL = 100;
    public final static String GIS_MAP_PATH = "http://webrd01.is.autonavi.com/appmaptile?lang=zh_cn&size=1&scale=1&style=8&x={x}&y={y}&z={z}";
    public final static String KNOWLEDGE_RIGHT = "<a><img src='http://webapi.amap.com/theme/v1.3/mapinfo_05.png'>&copy; 2016 AutoNavi</a>";

    public static final class FONT_STYLE {
        public static final String FONTFAMILY = "inherit";
        public static final String COLOR = "#808080";
        public static final String FONTSIZE = "12px";
    }

    public final static boolean NUM_SEPARATORS = false;
    public final static String WMS_SERVER = "wms";

    public static final class CHART_STYLE {
        public static final int STYLE_NORMAL = 1;

        public static final int STYLE_GRADUAL = 2;
    }

    public static final class SCALE_SETTING {
        public static final int AUTO = 1;

        public static final int CUSTOM = 2;
    }

    //数据标签样式的几个常量
    public static final class DATA_LABEL_RANGE {
        public static final int ALL = 140;
        public static final int CLASSIFICATION = 141;
        public static final int SERIES = 142;
    }

    public static final class DATA_LABEL_STYLE_TYPE {
        public static final int TEXT = 143;
        public static final int IMG = 144;
    }

    //数据标签filter类型
    public static final class DATACOLUMN {
        public static final int X = 96;
        public static final int Y = 112;
        public static final int Z = 128;
        public static final int XANDY = 144;
        public static final int XANDYANDSIZE = 160;
    }

    public static final class GIS_POSITION_TYPE {
        public static final int ADDRESS = 1;
        public static final int LNG_LAT = 2;
        public static final int LNG_FIRST = 3;
        public static final int LAT_FIRST = 4;
    }

    public static final class PIE_ANGLES {
        public static final int THREE_FOURTHS = 270;
        public static final int HALF = 180;
        public static final int TOTAL = 360;
        public static final int CUSTOM = 0;
    }

    public static final class MULTI_PIE_GRADIENT_STYLE {
        public static final int LIGHTER = 1;
        public static final int DARKER = 2;
    }

    public static final class FUNNEL_SLANT_STYLE {
        public static final int SAME = 1;
        public static final int DIFF = 2;
    }

    public static final class CHART_LEGENDS {
        public static final int NOT_SHOW = 1;
        public static final int TOP = 2;
        public static final int RIGHT = 3;
        public static final int BOTTOM = 4;
        public static final int LEFT = 5;
    }

    public static final class PERCENTAGE {
        public static final int SHOW = 1;
        public static final int NOT_SHOW = 2;
    }

    public static final class DASHBOARD_CHART_STYLE_AUTO {
        public static final class FIRST {
            public static final int FROM = 2;
            public static final int TO = 2;
            public static final String COLOR = "";
        }

        public static final class SECOND {
            public static final int FROM = 2;
            public static final int TO = 2;
            public static final String COLOR = "";
        }

        public static final class THIRD {
            public static final int FROM = 2;
            public static final int TO = 2;
            public static final String COLOR = "";
        }
    }

    public static final class CHART_SHAPE {
        public static final int NORMAL = 1;
        public static final int RIGHT_ANGLE = 2;
        public static final int CURVE = 3;
        public static final int EQUAL_ARC_ROSE = 4;
        public static final int NOT_EQUAL_ARC_ROSE = 5;
        public static final int CIRCLE = 6;
        public static final int POLYGON = 7;
        public static final int HALF_DASHBOARD = 9;
        public static final int PERCENT_DASHBOARD = 10;
        public static final int PERCENT_SCALE_SLOT = 11;
        public static final int VERTICAL_TUBE = 12;
        public static final int HORIZONTAL_TUBE = 13;
        public static final int NO_PROJECTOR = 16;
        public static final int PROJECTOR = 17;
    }

    public static final class DOT_STYLE {
        public static final String LOCATION = "location";
        public static final String SQUARE = "square";
        public static final String TRIANGLE = "triangle";
        public static final String CIRCLE = "circle";
        public static final String DIAMOND = "diamond";
        public static final String HOLLOW_SQUARE = "square_hollow";
        public static final String HOLLOW_TRIANGLE = "triangle_hollow";
        public static final String HOLLOW_CIRCLE = "circle_hollow";
        public static final String HOLLOW_DIAMON = "diamond_hollow";
    }

    public static final class DATA_LABEL {
        public static final int POSITION_INNER = 1;
        public static final int POSITION_OUTER = 2;
        public static final int POSITION_CENTER = 3;
    }

    public static final class ACCUMULATE_TYPE {
        public static final int OLD_COLUMN = 5;
        public static final int OLD_AREA_CURVE = 14;
        public static final int OLD_STACKED_AREA = 15;
        public static final int OLD_LINE = 13;
        public static final int OLD_ACCUMULATE_AXIS = 6;
        public static final int COLUMN = 1;
        public static final int STACKED_COLUMN = 2;
        public static final int AREA_NORMAL = 3;
        public static final int AREA_CURVE = 4;
        public static final int AREA_RIGHT_ANGLE = 5;
        public static final int STACKED_AREA_NORMAL = 6;
        public static final int STACKED_AREA_CURVE = 7;
        public static final int STACKED_AREA_RIGHT_ANGLE = 8;
        public static final int LINE_NORMAL = 9;
        public static final int LINE_CURVE = 10;
        public static final int LINE_RIGHT_ANGLE = 11;
    }

    public static final class DOT_VALUE_TYPE {
        public static final int SIZE = 1;
        public static final int COLOR = 2;
    }

    public static final class CHART_TARGET_STYLE {
        public static final class FORMAT {
            public static final int NORMAL = 1;
            public static final int ZERO2POINT = 2;
            public static final int ONE2POINT = 3;
            public static final int TWO2POINT = 4;
        }

        public static final class NUM_LEVEL {
            public static final int NORMAL = 1;
            public static final int TEN_THOUSAND = 2;
            public static final int MILLION = 3;
            public static final int YI = 4;
            public static final int PERCENT = 5;
        }
    }

    public static final class POINTER {
        public static final int ONE = 1;
        public static final int SOME = 2;
    }

    public static final class LINE_WIDTH {
        public static final int ZERO = 0;
        public static final int ONE = 1;
    }

    public static final class DISPLAY_RULES {
        public static final int DIMENSION = 1;
        public static final int FIXED = 2;
        public static final int GRADIENT = 3;
    }

    public static final class CHART_COLOR {
        public static final String COLOR_A = "#5caae4";
        public static final String COLOR_B = "#70cc7f";
        public static final String COLOR_C = "#ebbb67";
        public static final String COLOR_D = "#e97e7b";
        public static final String COLOR_E = "#6ed3c9";
    }

    public static final class LENEGD_DEFAULT_COLOR {
        public static final String COLOR_A = "#65B3EE";
        public static final String COLOR_B = "#95E1AA";
        public static final String COLOR_C = "#F8D08E";
        public static final String COLOR_D = "#e697c8";
        public static final String COLOR_E = "#a484b9";
    }

    public static final class DEFAULT_FORMAT_FUNCTIONS {
        public static final String CONTENTFORMAT = "function(){return window.BH ? BH.contentFormat(arguments[0], '') : arguments[0]}";
        public static final String CONTENTFORMAT2DECIMAL = "function () {return window.BH ? BH.contentFormat(arguments[0], '#.##') : arguments[0]}";
        public static final String CONTENTFORMATPERCENTAGE = "function(){return window.BH ? BH.contentFormat(arguments[0], '#.##%') : arguments[0]}";
    }

    public static final class CUSTOM_FORMAT {
        public static final String VALUEFORMAT = "(window.BH ? BH.contentFormat(this.value, '#.##;-#.##') : this.value)";
        public static final String PERCENTVALUEFORMAT = "(window.BH ? BH.contentFormat(this.value, '#0.00%') : this.value)";
        public static final String THISPERCENTVALUEFORMAT = "(window.BH ? BH.contentFormat(this, '#0.00%') : this)";
        public static final String NUMSEPARATORS = "(window.BH ? BH.contentFormat(this.value, '#,###.##') : this.value)";
        public static final String PERCENTNUMSEPARATORS = "(window.BH ? BH.contentFormat(this.value, '#,##0%') : this.value)";
        public static final String THISPERCENTNUMSEPARATORS = "(window.BH ? BH.contentFormat(this, '#,##0%') : this)";
    }
}
