package com.fr.bi.stable.constant;

import com.fr.base.CoreDecimalFormat;

import java.text.DecimalFormat;

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
    public final static String GIS_MAP_PATH = "http://webrd01.is.autonavi.com/appmaptile?lang=zh_cn&size=1&scale=1&style=8&x={x}&y={y}&z={z}";
    public final static String KNOWLEDGE_RIGHT = "<a><img src=\"http://webapi.amap.com/theme/v1.3/mapinfo_05.png\">&copy; 2016 AutoNavi</a>";
    public final static String GIS_ICON_PATH = "FR.serverURL + FR.servletURL + \"?op=resource&resource=/com/fr/bi/web/images/icon/chartsetting/address_marker_big.png\"";
    public static final class FONT_STYLE {
        public static final String FONTFAMILY = "inherit";
        public static final String COLOR = "#808080";
        public static final String FONTSIZE = "12px";
    }
    public final static boolean NUM_SEPARATORS = false;
    public final static String WMS_SERVER = "wms";

    public static final DecimalFormat TWOFIEXEDFORMAT = new CoreDecimalFormat(new DecimalFormat("##.##"), "");
    public static final DecimalFormat FOURFIEXEDFORMAT = new CoreDecimalFormat(new DecimalFormat("##.####"), "");

    public static final class CHART_STYLE {
        public static final int STYLE_NORMAL = 1;

        public static final int STYLE_GRADUAL = 2;
    }

    public static final class SCALE_SETTING {
        public static final int AUTO = 1;

        public static final int CUSTOM = 2;
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

    public static final String[] CHART_COLOR = new String[]{
            "#5caae4", "#70cc7f", "#ebbb67", "#e97e7b", "#6ed3c9"
    };

    public static final class DEFAULT_FORMAT_FUNCTIONS {
//        public static final String CONTENTFORMAT = "function(){return BH.contentFormat(arguments[0], '')}";
//        public static final String CONTENTFORMAT2DECIMAL = "function () {return BH.contentFormat(arguments[0], '#.##')}";
//        public static final String CONTENTFORMATPERCENTAGE = "function(){return BH.contentFormat(arguments[0], '#.##%')}";

        public static final String CONTENTFORMAT = "function(){return arguments[0]}";
        public static final String CONTENTFORMAT2DECIMAL = "function () {return arguments[0]}";
        public static final String CONTENTFORMATPERCENTAGE = "function(){return arguments[0]}";
    }
    
    public static final int[] MINIMALIST_WIDGET = new int[]{
            BIReportConstant.WIDGET.AXIS,
            BIReportConstant.WIDGET.ACCUMULATE_AXIS,
            BIReportConstant.WIDGET.PERCENT_ACCUMULATE_AXIS,
            BIReportConstant.WIDGET.COMPARE_AXIS,
            BIReportConstant.WIDGET.FALL_AXIS,
            BIReportConstant.WIDGET.BAR,
            BIReportConstant.WIDGET.ACCUMULATE_BAR,
            BIReportConstant.WIDGET.COMPARE_BAR,
            BIReportConstant.WIDGET.LINE,
            BIReportConstant.WIDGET.AREA,
            BIReportConstant.WIDGET.ACCUMULATE_AREA,
            BIReportConstant.WIDGET.PERCENT_ACCUMULATE_AREA,
            BIReportConstant.WIDGET.COMPARE_AREA,
            BIReportConstant.WIDGET.RANGE_AREA,
            BIReportConstant.WIDGET.COMBINE_CHART,
            BIReportConstant.WIDGET.MULTI_AXIS_COMBINE_CHART
    };
}
