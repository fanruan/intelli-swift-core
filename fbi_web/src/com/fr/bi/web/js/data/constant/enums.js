//日期控件
BICst.MULTI_DATE_YEAR_PREV = 1;
BICst.MULTI_DATE_YEAR_AFTER = 2;
BICst.MULTI_DATE_YEAR_BEGIN = 3;
BICst.MULTI_DATE_YEAR_END = 4;

BICst.MULTI_DATE_MONTH_PREV = 5;
BICst.MULTI_DATE_MONTH_AFTER = 6;
BICst.MULTI_DATE_MONTH_BEGIN = 7;
BICst.MULTI_DATE_MONTH_END = 8;

BICst.MULTI_DATE_QUARTER_PREV = 9;
BICst.MULTI_DATE_QUARTER_AFTER = 10;
BICst.MULTI_DATE_QUARTER_BEGIN = 11;
BICst.MULTI_DATE_QUARTER_END = 12;

BICst.MULTI_DATE_WEEK_PREV = 13;
BICst.MULTI_DATE_WEEK_AFTER = 14;

BICst.MULTI_DATE_DAY_PREV = 15;
BICst.MULTI_DATE_DAY_AFTER = 16;
BICst.MULTI_DATE_DAY_TODAY = 17;

BICst.MULTI_DATE_PARAM = 18;
BICst.MULTI_DATE_CALENDAR = 19;

BICst.YEAR_QUARTER = 20;
BICst.YEAR_MONTH = 21;
BICst.YEAR_WEEK = 22;
BICst.YEAR_DAY = 23;
BICst.MONTH_WEEK = 24;
BICst.MONTH_DAY = 25;
BICst.YEAR = 26;
BICst.SAME_PERIOD = 27;
BICst.LAST_SAME_PERIOD = 28;


//dashboard toolbar left button group
BICst.DASHBOARD_TOOLBAR_SAVEAS = 1;
BICst.DASHBOARD_TOOLBAR_UNDO = 2;
BICst.DASHBOARD_TOOLBAR_REDO = 3;

//dashboard toolbar right layout textcombo
BICst.DASHBOARD_LAYOUT_ADAPT = 0;
BICst.DASHBOARD_LAYOUT_FREE = 1;


//字段选择，业务包/复用
BICst.DETAIL_PACKAGES_FIELD = 1;
BICst.DETAIL_DIMENSION_REUSE = 2;

//类型&数据/样式tab
BICst.DETAIL_TAB_TYPE_DATA = 1;
BICst.DETAIL_TAB_STYLE = 2;

//etl manager
BICst.ETL_MANAGE_TABLE_NONE = -1;
BICst.ETL_MANAGE_TABLE_PREVIEW = 0;
BICst.ETL_MANAGE_TABLE_ADD_FIELD = 1;
BICst.ETL_MANAGE_TABLE_JOIN = 2;
BICst.ETL_MANAGE_TABLE_UNION = 3;
BICst.ETL_MANAGE_TABLE_CONVERT = 4;
BICst.ETL_MANAGE_TABLE_PARTIAL = 5;
BICst.ETL_MANAGE_TABLE_FILTER = 6;
BICst.ETL_MANAGE_TABLE_GROUP = 7;
BICst.ETL_MANAGE_TABLE_CIRCLE = 8;
BICst.ETL_MANAGE_TABLE_NEW_GROUP = 9;
BICst.ETL_MANAGE_TABLE_DELETE = 10;


BICst.Widget = {
    TABLE: 1,
    BAR: 2,
    ACCUMULATE_BAR: 3,
    PIE: 4,
    DASHBOARD: 5,
    AXIS: 6,
    MAP: 7,
    DETAIL: 8,
    DONUT: 9,
    BUBBLE: 10,
    SCATTER: 11,
    RADAR: 12,


    STRING: 13,
    NUMBER: 14,
    DATE: 15,

    YEAR: 16,
    QUARTER: 17,
    MONTH: 18,
    YMD: 19,

    TREE: 20,
    QUERY: 21,
    RESET: 22,

    CROSS_TABLE: 23,
    COMPLEX_TABLE: 24,

    CONTENT: 25,
    IMAGE: 26,
    WEB: 27,
    COLUMN: 28,
    LINE: 29,
    AREA: 30,
    ACCUMULATE_COLUMN: 31,
    TABLE_SHOW: 31,

    GENERAL_QUERY: 32,

    NONE: -1
};

/*
 *组件、控件的各个操作
 */
BICst.DASHBOARD_WIDGET_DELETE = -1;   //删除
BICst.DASHBOARD_WIDGET_EXPAND = 1;   //详细设置
BICst.DASHBOARD_WIDGET_SHRINK = 2;   //回到组件
BICst.DASHBOARD_WIDGET_LINKAGE = 3;    //联动
BICst.DASHBOARD_DETAIL_WIDGET_DRILL = 13; //钻取到明细表
BICst.DASHBOARD_WIDGET_EXCEL = 4;    //组件、明细表的导出为excel
BICst.DASHBOARD_CONTROL_RANG_ASC = 5;
BICst.DASHBOARD_CONTROL_RANG_DESC = 6;    //文本，下拉树的升序，降序
BICst.DASHBOARD_TIEM_CONTROL_DETAIL = 8;

BICst.DASHBOARD_TIEM_CONTROL_YEAR = 9;
BICst.DASHBOARD_TIEM_CONTROL_SEASON = 10;
BICst.DASHBOARD_TIEM_CONTROL_MONTH = 11;
BICst.DASHBOARD_TIEM_CONTROL_YMD = 13; //日期控件的具体选择、年份、季度、月份
BICst.DASHBOARD_WIDGET_EASY_SETTING = 12;  //快捷设置
BICst.DASHBOARD_CONTROL_CLEAR = 14;       //控件的清空按钮

BICst.DASHBOARD_WIDGET_COPY = 15;  //复制控件
BICst.DASHBOARD_TABLE_FREEZE = 16;

BICst.DASHBOARD_WIDGET_SHOW_NAME = 17;      //显示标题
BICst.DASHBOARD_WIDGET_RENAME = 18;         //重命名
BICst.DASHBOARD_WIDGET_NAME_POS = 19;           //标题位置
BICst.DASHBOARD_WIDGET_NAME_POS_LEFT = 20;      //标题位置居左
BICst.DASHBOARD_WIDGET_NAME_POS_CENTER = 21;     //标题位置居右
BICst.DASHBOARD_WIDGET_FILTER = 22;         //查看过滤条件

BICst.DASHBOARD_WIDGET_SWITCH_CHART = 23;   //查看状态下的切换图表

//维度下拉选项
BICst.DIMENSION_STRING_COMBO = {
    ASCEND: 100,
    DESCEND: 101,
    SORT_BY_CUSTOM: 102,
    GROUP_BY_VALUE: 103,
    GROUP_BY_CUSTOM: 104,
    FILTER: 105,
    DT_RELATION: 106,
    COPY: 107,
    DELETE: 108,
    INFO: 109
};

BICst.DIMENSION_NUMBER_COMBO = {
    ASCEND: 200,
    DESCEND: 201,
    NOT_SORT: 202,
    SORT_BY_CUSTOM: 203,
    GROUP_BY_VALUE: 204,
    GROUP_SETTING: 205,
    FILTER: 206,
    DT_RELATION: 207,
    COPY: 208,
    DELETE: 209,
    INFO: 210
};

BICst.DIMENSION_DATE_COMBO = {
    DATE: 300,
    YEAR: 301,
    QUARTER: 302,
    MONTH: 303,
    WEEK: 304,
    ASCEND: 305,
    DESCEND: 306,
    FILTER: 307,
    DT_RELATION: 308,
    COPY: 309,
    DELETE: 310,
    INFO: 31
};

//指标下拉选项
BICst.TARGET_COMBO = {
    SUMMERY_TYPE: 400,
    CHART_TYPE: 401,
    STYLE_SETTING: 402,
    FILTER: 403,
    DISPLAY: 404,
    HIDDEN: 405,
    COPY: 406,
    DELETE: 407,
    INFO: 408,
    DEPEND_TYPE: 409
};

//明细表维度下拉选项
BICst.DETAIL_STRING_COMBO = {
    FILTER: 500,
    HYPERLINK: 501,
    DELETE: 502,
    INFO: 503
};

BICst.DETAIL_NUMBER_COMBO = {
    FORM_SETTING: 600,
    FILTER: 601,
    HYPERLINK: 602,
    DELETE: 603,
    INFO: 604
};

BICst.DETAIL_DATE_COMBO = {
    YMD: 700,
    YMD_HMS: 701,
    YEAR: 702,
    SEASON: 703,
    MONTH: 704,
    WEEK: 705,
    FILTER: 706,
    HYPERLINK: 707,
    DELETE: 708,
    INFO: 709
};

BICst.DETAIL_FORMULA_COMBO = {
    FORM_SETTING: 800,
    UPDATE_FORMULA: 801,
    HYPERLINK: 802,
    DISPLAY: 803,
    HIDDEN: 804,
    RENAME: 805,
    DELETE: 806,
    INFO: 807
};

BICst.CALCULATE_TARGET_COMBO = {
    FORM_SETTING: 900,
    UPDATE_TARGET: 901,
    DISPLAY: 902,
    HIDDEN: 903,
    RENAME: 904,
    COPY: 905,
    DELETE: 906,
    INFO: 907
};


BICst.CONTROL_COMBO = {
    DELETE: 505,
    INFO: 506,
    RENAME: 507
};

//分组统计下拉选项
BICst.STATISTICS_GROUP_DATE_COMBO = {
    DATE: 900,
    YEAR: 901,
    QUARTER: 902,
    MONTH: 903,
    WEEK: 904,
    DELETE: 905,
    No_Repeat_Count: 899,
    DISPLAY: 898,
    HIDDEN: 897,
    RENAME: 896,
    RECORD_COUNT: 895
};

BICst.STATISTICS_GROUP_NUMBER_COMBO = {
    SUM: 906,
    AVG: 907,
    MAX: 908,
    MIN: 909,
    No_Repeat_Count: 910,
    DELETE: 911,
    GROUP_SETTING: 912,
    GROUP_BY_VALUE: 913,
    DISPLAY: 904,
    HIDDEN: 905,
    RENAME: 903,
    RECORD_COUNT: 902
};

BICst.STATISTICS_GROUP_STRING_COMBO = {
    GROUP_BY_VALUE: 913,
    GROUP_BY_CUSTOM: 914,
    No_Repeat_Count: 915,
    DELETE: 916,
    APPEND: 917,
    DISPLAY: 912,
    HIDDEN: 911,
    RENAME: 910,
    RECORD_COUNT: 909
};

BICst.CHART_VIEW_STYLE_BAR = 1;
BICst.CHART_VIEW_STYLE_ACCUMULATED_BAR = 2;
BICst.CHART_VIEW_STYLE_LINE = 3;
BICst.CHART_VIEW_STYLE_SQUARE = 4;

BICst.SUMMARY_TABLE_PAGE_OPERATOR = {
    COLUMN_PRE: 1,
    COLUMN_NEXT: 2,
    ROW_PRE: 3,
    ROW_NEXT: 4,
    EXPAND: 5
};

BICst.FILTER_TYPE_FORMULA = 10002;
BICst.FILTER_TYPE_FORMULA_EMPTY = -10002;

BICst.TARGET_FILTER_TYPE_NONE = -1;         //含有combo，类型待确定
BICst.DIMENSION_FILTER_TYPE_NONE = -2;
BICst.TARGET_FILTER_CONTAINER_EMPTY = -3;   //empty的
BICst.DIMENSION_FILTER_CONTAINER_EMPTY = -4;   //empty的
BICst.FILTER_CONTAINER_EMPTY = -100;

BICst.FILTER_OPERATION_FORMULA = 1;
BICst.FILTER_OPERATION_CONDITION = 2;
BICst.FILTER_OPERATION_CONDITION_AND = 3;
BICst.FILTER_OPERATION_CONDITION_OR = 4;
BICst.FILTER_OPERATION_FORMULA_AND = 5;
BICst.FILTER_OPERATION_FORMULA_OR = 6;

BICst.NUMBER_INTERVAL_CUSTOM_GROUP_CUSTOM = 5;
BICst.NUMBER_INTERVAL_CUSTOM_GROUP_VALUE = 4;
BICst.NUMBER_INTERVAL_CUSTOM_GROUP_AUTO = 3;

BICst.FILTER_PANE_CLICK_REMOVE = -1;
BICst.FILTER_PANE_CLICK_EXPANDER = 1;
BICst.FILTER_PANE_CLICK_ITEM = 2;

BICst.RELATION_TYPE = {
    ONE_TO_ONE: 1,
    ONE_TO_N: 2,
    N_TO_ONE: 3
};

//选择join方式
BICst.ETL_JOIN_STYLE = {
    LEFT_JOIN: 1,
    RIGHT_JOIN: 2,
    INNER_JOIN: 3,
    OUTER_JOIN: 4
};

BICst.ADD_NEW_TABLE = {
    DATABASE_OR_PACKAGE: 1,
    ETL: 2,
    SQL: 3,
    EXCEL: 4,
    NONE: -1
};

BICst.TABLES_VIEW = {
    TILE: 1,
    RELATION: 2
};

BICst.TABLE_FORM = {
    OPEN_COL: 1,    //纵向展开表格
    OPEN_ROW: 2     //横向展开表格
};

BICst.TABLE_STYLE = {
    STYLE1: 1,      //普通风格
    STYLE2: 2,        //蓝色表头的
    STYLE3: 3     //内容间隔色
};

BICst.CHART_STYLE = {
    //类型
    NORMAL: 1,      //普通形态
    RIGHT_ANGLE: 2,        //直角线形
    CURVE: 3,     //曲线
    EQUAL_ARC_ROSE: 4, //等弧玫瑰图
    NOT_EQUAL_ARC_ROSE: 5, //不等弧玫瑰图
    CIRCLE: 6,          //圆形雷达
    POLYGON: 7,          //多边形雷达
    HALF_DASHBOARD: 9,//180'的仪表盘
    PERCENT_DASHBOARD: 10,//百分比的仪表盘
    PERCENT_SCALE_SLOT: 11,//带刻度槽的仪表盘
    VERTICAL_TUBE: 12,      //竖起来的试管型仪表盘
    HORIZONTAL_TUBE: 13,//横过来的试管型仪表盘

    //风格
    STYLE_NORMAL: 21,       //普通风格
    STYLE_GRADUAL: 22,      //渐变
    STYLE_TRANSPARENT: 23,   //透明
    STYLE_GRADUAL_HIGHLIGHT: 24, //渐变高亮
    STYLE_3D: 25            //3d
};

BICst.PIE_ANGLES = {
    THREE_FOURTHS: 270,
    HALF: 180,
    TOTAL: 360
};

BICst.CHART_LEGENDS = {
    NOT_SHOW: 1,
    TOP: 2,
    RIGHT: 3,
    BOTTOM: 4,
    LEFT: 5
};

BICst.MAP_TYPE = {};
BICst.MAP_TYPE.WORLD = 10000;
BICst.MAP_TYPE.CHINA = 11000;
BICst.MAP_TYPE.JIANGSU = 11010;
BICst.MAP_TYPE.SHANDONG = 11020;

BICst.MAP_PATH = {};
BICst.MAP_PATH[BICst.MAP_TYPE.WORLD] = FR.servletURL + "?op=resource&resource=/com/fr/bi/web/js/data/map/china.json";
BICst.MAP_PATH[BICst.MAP_TYPE.CHINA] = FR.servletURL + "?op=resource&resource=/com/fr/bi/web/js/data/map/china.json";
BICst.MAP_PATH[BICst.MAP_TYPE.JIANGSU] = FR.servletURL + "?op=resource&resource=/com/fr/bi/web/js/data/map/jiangsu.json";
BICst.MAP_PATH[BICst.MAP_TYPE.SHANDONG] = FR.servletURL + "?op=resource&resource=/com/fr/bi/web/js/data/map/shandong.json";

BICst.MAP_NAME = {};
BICst.MAP_NAME["世界"] = BICst.MAP_TYPE.WORLD;
BICst.MAP_NAME["中国"] = BICst.MAP_TYPE.CHINA;
BICst.MAP_NAME["江苏省"] = BICst.MAP_TYPE.JIANGSU;
BICst.MAP_NAME["山东省"] = BICst.MAP_TYPE.SHANDONG;

BICst.TABLE_MAX_ROW = 20;
BICst.TABLE_MAX_COL = 7;

//指标样式设置
BICst.TARGET_STYLE = {
    FORMAT: {
        NORMAL: 1,      //原始
        ZERO2POINT: 2,  //整数
        ONE2POINT: 3,   //小数点后一位
        TWO2POINT: 4    //小数点后两位
    },
    NUM_LEVEL: {
        NORMAL: 1,          //个
        TEN_THOUSAND: 2,    //万
        MILLION: 3,         //百万
        YI: 4,              //亿 T_T
        PERCENT: 5
    },
    ICON_STYLE: {
        NONE: 0,
        POINT: 1,
        ARROW: 2
    }
};

//钻取
BICst.DRILL = {
    UP: 1,
    DOWN: 2
};

//图样式常量
BICst.VALUE_SAVE_BY_NORMAL = 0;
BICst.VALUE_SAVE_NO_DECAML = 1;
BICst.VALUE_SAVE_ONE_DECAML = 2;
BICst.VALUE_SAVE_TWO_DECAML = 3;

//定义组件的三个状态，查看状态的dashboard、编辑状态的dashboard、详细设置
BICst.WIDGET_STATUS = {
    SHOW: 1,
    EDIT: 2,
    DETAIL: 3
};

