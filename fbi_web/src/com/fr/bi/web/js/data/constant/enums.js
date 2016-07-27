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
BICst.ETL_MANAGE_EXCEL_CHANGE = 11;
BICst.ETL_MANAGE_SQL_CHANGE = 12;


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
    INFO: 109,
    ADDRESS: 110,
    LNG_LAT: 111,
    LNG: 112,
    LAT: 113
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
    INFO: 210,
    CORDON: 211,
    ADDRESS: 212,
    LNG_LAT: 213,
    LNG: 214,
    LAT: 215
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
    INFO: 31,
    ADDRESS: 32,
    LNG_LAT: 33,
    LNG: 34,
    LAT: 35
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
    DEPEND_TYPE: 409,
    CORDON: 410
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

BICst.CHART_SHAPE = {
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
    BAIDU: 14,           //百度地图
    GOOGLE: 15,          //谷歌地图
    NO_PROJECTOR: 16,    //没有投影的气泡
    PROJECTOR: 17       //有投影的气泡
};

BICst.CHART_STYLE = {
    //风格
    STYLE_NORMAL: 1,       //普通风格
    STYLE_GRADUAL: 2      //渐变
};

BICst.SCALE_SETTING = {
    AUTO: 1,
    CUSTOM: 2
};

BICst.GIS_POSITION_TYPE = {
    ADDRESS: 1,
    LNG_LAT: 2,
    LNG_FIRST: 3,
    LAT_FIRST: 4
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

BICst.STYLE_CONDITIONS = {
    FIRST: 1,
    SECOND: 2,
    THIRD: 3,
    FOURTH: 4,
    FIFTH: 5
};

BICst.MAP_TYPE = {};
BICst.MAP_TYPE.WORLD = 100000;
BICst.MAP_TYPE.CHINA = 100001;
BICst.MAP_TYPE.QITAIHESHI=100002;
BICst.MAP_TYPE.SANYASHI=100003;
BICst.MAP_TYPE.SANMINGSHI=100004;
BICst.MAP_TYPE.SANMENXIASHI=100005;
BICst.MAP_TYPE.SHANGHAISHI=100006;
BICst.MAP_TYPE.SHANGRAOSHI=100007;
BICst.MAP_TYPE.DONGGUANSHI=100008;
BICst.MAP_TYPE.DONGYINGSHI=100009;
BICst.MAP_TYPE.ZHONGWEISHI=100010;
BICst.MAP_TYPE.ZHONGSHANSHI=100011;
BICst.MAP_TYPE.LINXIAHUIZUZIZHIZHOU=100012;
BICst.MAP_TYPE.LINFENSHI=100013;
BICst.MAP_TYPE.LINYISHI=100014;
BICst.MAP_TYPE.LINCANGSHI=100015;
BICst.MAP_TYPE.DANDONGSHI=100016;
BICst.MAP_TYPE.LISHUISHI=100017;
BICst.MAP_TYPE.LIJIANGSHI=100018;
BICst.MAP_TYPE.WULANCHABUSHI=100019;
BICst.MAP_TYPE.WUHAISHI=100020;
BICst.MAP_TYPE.WULUMUQISHI=100021;
BICst.MAP_TYPE.LESHANSHI=100022;
BICst.MAP_TYPE.JIUJIANGSHI=100023;
BICst.MAP_TYPE.YUNNANSHENG=100024;
BICst.MAP_TYPE.YUNFUSHI=100025;
BICst.MAP_TYPE.YICHUNSHI=100026;
BICst.MAP_TYPE.YILIHASAKEZIZHIZHOU=100027;
BICst.MAP_TYPE.FOSHANSHI=100028;
BICst.MAP_TYPE.JIAMUSISHI=100029;
BICst.MAP_TYPE.BAODINGSHI=100030;
BICst.MAP_TYPE.BAOSHANSHI=100031;
BICst.MAP_TYPE.XINYANGSHI=100032;
BICst.MAP_TYPE.KEZILESUKEERKEZIZIZHIZHOU=100033;
BICst.MAP_TYPE.KELAMAYISHI=100034;
BICst.MAP_TYPE.LUANSHI=100035;
BICst.MAP_TYPE.LIUPANSHUISHI=100036;
BICst.MAP_TYPE.LANZHOUSHI=100037;
BICst.MAP_TYPE.XINGANMENG=100038;
BICst.MAP_TYPE.NEIJIANGSHI=100039;
BICst.MAP_TYPE.NEIMENGGUZIZHIQU=100040;
BICst.MAP_TYPE.LIANGSHANYIZUZIZHIZHOU=100041;
BICst.MAP_TYPE.BAOTOUSHI=100042;
BICst.MAP_TYPE.BEIJINGSHI=100043;
BICst.MAP_TYPE.BEIHAISHI=100044;
BICst.MAP_TYPE.SHIYANSHI=100045;
BICst.MAP_TYPE.NANJINGSHI=100046;
BICst.MAP_TYPE.NANCHONGSHI=100047;
BICst.MAP_TYPE.NANNINGSHI=100048;
BICst.MAP_TYPE.NANPINGSHI=100049;
BICst.MAP_TYPE.NANCHANGSHI=100050;
BICst.MAP_TYPE.NANTONGSHI=100051;
BICst.MAP_TYPE.NANYANGSHI=100052;
BICst.MAP_TYPE.BOERTALAMENGGUZIZHIZHOU=100053;
BICst.MAP_TYPE.XIAMENSHI=100054;
BICst.MAP_TYPE.SHUANGYASHANSHI=100055;
BICst.MAP_TYPE.TAIZHOUSHI=100056;
BICst.MAP_TYPE.TAIWANSHENG=100057;
BICst.MAP_TYPE.HEFEISHI=100058;
BICst.MAP_TYPE.JIANSHI=100059;
BICst.MAP_TYPE.JILINSHI=100060;
BICst.MAP_TYPE.JILINSHENG=100061;
BICst.MAP_TYPE.TULUFANDIQU=100062;
BICst.MAP_TYPE.LVLIANGSHI=100063;
BICst.MAP_TYPE.WUZHONGSHI=100064;
BICst.MAP_TYPE.ZHOUKOUSHI=100065;
BICst.MAP_TYPE.HULUNBEIERSHI=100066;
BICst.MAP_TYPE.HUHEHAOTESHI=100067;
BICst.MAP_TYPE.HETIANDIQU=100068;
BICst.MAP_TYPE.XIANNINGSHI=100069;
BICst.MAP_TYPE.XIANYANGSHI=100070;
BICst.MAP_TYPE.HAMIDIQU=100071;
BICst.MAP_TYPE.HAERBINSHI=100072;
BICst.MAP_TYPE.TANGSHANSHI=100073;
BICst.MAP_TYPE.SHANGQIUSHI=100074;
BICst.MAP_TYPE.SHANGLUOSHI=100075;
BICst.MAP_TYPE.KASHENDIQU=100076;
BICst.MAP_TYPE.JIAXINGSHI=100077;
BICst.MAP_TYPE.JIAYUGUANSHI=100078;
BICst.MAP_TYPE.SICHUANSHENG=100079;
BICst.MAP_TYPE.SIPINGSHI=100080;
BICst.MAP_TYPE.GUYUANSHI=100081;
BICst.MAP_TYPE.TACHENGDIQU=100082;
BICst.MAP_TYPE.DAXINGANLINGDIQU=100083;
BICst.MAP_TYPE.DATONGSHI=100084;
BICst.MAP_TYPE.DAQINGSHI=100085;
BICst.MAP_TYPE.DALIBAIZUZIZHIZHOU=100086;
BICst.MAP_TYPE.DALIANSHI=100087;
BICst.MAP_TYPE.TIANSHUISHI=100088;
BICst.MAP_TYPE.TIANJINSHI=100089;
BICst.MAP_TYPE.TAIYUANSHI=100090;
BICst.MAP_TYPE.WEIHAISHI=100091;
BICst.MAP_TYPE.LOUDISHI=100092;
BICst.MAP_TYPE.XIAOGANSHI=100093;
BICst.MAP_TYPE.NINGXIAHUIZUZIZHIQU=100094;
BICst.MAP_TYPE.NINGDESHI=100095;
BICst.MAP_TYPE.NINGBOSHI=100096;
BICst.MAP_TYPE.ANQINGSHI=100097;
BICst.MAP_TYPE.ANKANGSHI=100098;
BICst.MAP_TYPE.ANHUISHENG=100099;
BICst.MAP_TYPE.ANYANGSHI=100100;
BICst.MAP_TYPE.ANSHUNSHI=100101;
BICst.MAP_TYPE.DINGXISHI=100102;
BICst.MAP_TYPE.YIBINSHI=100103;
BICst.MAP_TYPE.YICHANGSHI=100104;
BICst.MAP_TYPE.YICHUNSHI1=100105;
BICst.MAP_TYPE.BAOJISHI=100106;
BICst.MAP_TYPE.XUANCHENGSHI=100107;
BICst.MAP_TYPE.SUZHOUSHI=100108;
BICst.MAP_TYPE.SUQIANSHI=100109;
BICst.MAP_TYPE.SHANDONGSHENG=100110;
BICst.MAP_TYPE.SHANNANDIQU=100111;
BICst.MAP_TYPE.SHANXISHENG=100112;
BICst.MAP_TYPE.YUEYANGSHI=100113;
BICst.MAP_TYPE.CHONGZUOSHI=100114;
BICst.MAP_TYPE.BAZHONGSHI=100115;
BICst.MAP_TYPE.BAYANNAOERSHI=100116;
BICst.MAP_TYPE.BAYINGUOLENGMENGGUZIZHIZHOU=100117;
BICst.MAP_TYPE.CHANGZHOUSHI=100118;
BICst.MAP_TYPE.CHANGDESHI=100119;
BICst.MAP_TYPE.PINGLIANGSHI=100120;
BICst.MAP_TYPE.PINGDINGSHANSHI=100121;
BICst.MAP_TYPE.GUANGDONGSHENG=100122;
BICst.MAP_TYPE.GUANGYUANSHI=100123;
BICst.MAP_TYPE.GUANGANSHI=100124;
BICst.MAP_TYPE.GUANGZHOUSHI=100125;
BICst.MAP_TYPE.GUANGXIZHUANGZUZIZHIQU=100126;
BICst.MAP_TYPE.QINGYANGSHI=100127;
BICst.MAP_TYPE.LANGFANGSHI=100128;
BICst.MAP_TYPE.YANANSHI=100129;
BICst.MAP_TYPE.YANBIANCHAOXIANZUZIZHIZHOU=100130;
BICst.MAP_TYPE.KAIFENGSHI=100131;
BICst.MAP_TYPE.ZHANGJIAKOUSHI=100132;
BICst.MAP_TYPE.ZHANGJIAJIESHI=100133;
BICst.MAP_TYPE.ZHANGYESHI=100134;
BICst.MAP_TYPE.XUZHOUSHI=100135;
BICst.MAP_TYPE.DEHONGDAIZUJINGPOZUZIZHIZHOU=100136;
BICst.MAP_TYPE.DEZHOUSHI=100137;
BICst.MAP_TYPE.DEYANGSHI=100138;
BICst.MAP_TYPE.XINZHOUSHI=100139;
BICst.MAP_TYPE.HUAIHUASHI=100140;
BICst.MAP_TYPE.NUJIANGLISUZUZIZHIZHOU=100141;
BICst.MAP_TYPE.ENSHITUJIAZUMIAOZUZIZHIZHOU=100142;
BICst.MAP_TYPE.HUIZHOUSHI=100143;
BICst.MAP_TYPE.CHENGDUSHI=100144;
BICst.MAP_TYPE.YANGZHOUSHI=100145;
BICst.MAP_TYPE.CHENGDESHI=100146;
BICst.MAP_TYPE.FUZHOUSHI=100147;
BICst.MAP_TYPE.FUSHUNSHI=100148;
BICst.MAP_TYPE.LASASHI=100149;
BICst.MAP_TYPE.JIEYANGSHI=100150;
BICst.MAP_TYPE.PANZHIHUASHI=100151;
BICst.MAP_TYPE.WENSHANZHUANGZUMIAOZUZIZHIZHOU=100152;
BICst.MAP_TYPE.XINXIANGSHI=100153;
BICst.MAP_TYPE.XINYUSHI=100154;
BICst.MAP_TYPE.XINJIANGWEIWUERZIZHIQU=100155;
BICst.MAP_TYPE.WUXISHI=100156;
BICst.MAP_TYPE.RIKAZEDIQU=100157;
BICst.MAP_TYPE.RIZHAOSHI=100158;
BICst.MAP_TYPE.KUNMINGSHI=100159;
BICst.MAP_TYPE.CHANGJIHUIZUZIZHIZHOU=100160;
BICst.MAP_TYPE.CHANGDOUDIQU=100161;
BICst.MAP_TYPE.ZHAOTONGSHI=100162;
BICst.MAP_TYPE.JINZHONGSHI=100163;
BICst.MAP_TYPE.JINCHENGSHI=100164;
BICst.MAP_TYPE.PUERSHI=100165;
BICst.MAP_TYPE.JINGDEZHENSHI=100166;
BICst.MAP_TYPE.QUJINGSHI=100167;
BICst.MAP_TYPE.SHUOZHOUSHI=100168;
BICst.MAP_TYPE.CHAOYANGSHI=100169;
BICst.MAP_TYPE.BENXISHI=100170;
BICst.MAP_TYPE.LAIBINSHI=100171;
BICst.MAP_TYPE.HANGZHOUSHI=100172;
BICst.MAP_TYPE.SONGYUANSHI=100173;
BICst.MAP_TYPE.LINZHIDIQU=100174;
BICst.MAP_TYPE.GUOLUOCANGZUZIZHIZHOU=100175;
BICst.MAP_TYPE.ZAOZHUANGSHI=100176;
BICst.MAP_TYPE.LIUZHOUSHI=100177;
BICst.MAP_TYPE.ZHUZHOUSHI=100178;
BICst.MAP_TYPE.GUILINSHI=100179;
BICst.MAP_TYPE.MEIZHOUSHI=100180;
BICst.MAP_TYPE.WUZHOUSHI=100181;
BICst.MAP_TYPE.CHUXIONGYIZUZIZHIZHOU=100182;
BICst.MAP_TYPE.YULINSHI=100183;
BICst.MAP_TYPE.WUWEISHI=100184;
BICst.MAP_TYPE.WUHANSHI=100185;
BICst.MAP_TYPE.BIJIEDIQU=100186;
BICst.MAP_TYPE.HAOZHOUSHI=100187;
BICst.MAP_TYPE.YONGZHOUSHI=100188;
BICst.MAP_TYPE.HANZHONGSHI=100189;
BICst.MAP_TYPE.SHANTOUSHI=100190;
BICst.MAP_TYPE.SHANWEISHI=100191;
BICst.MAP_TYPE.JIANGSUSHENG=100192;
BICst.MAP_TYPE.JIANGXISHENG=100193;
BICst.MAP_TYPE.JIANGMENSHI=100194;
BICst.MAP_TYPE.CHIZHOUSHI=100195;
BICst.MAP_TYPE.SHENYANGSHI=100196;
BICst.MAP_TYPE.CANGZHOUSHI=100197;
BICst.MAP_TYPE.HEBEISHENG=100198;
BICst.MAP_TYPE.HENANSHENG=100199;
BICst.MAP_TYPE.HECHISHI=100200;
BICst.MAP_TYPE.HEYUANSHI=100201;
BICst.MAP_TYPE.QUANZHOUSHI=100202;
BICst.MAP_TYPE.TAIANSHI=100203;
BICst.MAP_TYPE.TAIZHOUSHI1=100204;
BICst.MAP_TYPE.LUZHOUSHI=100205;
BICst.MAP_TYPE.LUOYANGSHI=100206;
BICst.MAP_TYPE.JINANSHI=100207;
BICst.MAP_TYPE.JININGSHI=100208;
BICst.MAP_TYPE.ZHEJIANGSHENG=100209;
BICst.MAP_TYPE.HAIDONGDIQU=100210;
BICst.MAP_TYPE.HAIBEICANGZUZIZHIZHOU=100211;
BICst.MAP_TYPE.HAINANSHENG=100212;
BICst.MAP_TYPE.HAINANCANGZUZIZHIZHOU=100213;
BICst.MAP_TYPE.HAIKOUSHI=100214;
BICst.MAP_TYPE.HAIXIMENGGUZUCANGZUZIZHIZHOU=100215;
BICst.MAP_TYPE.ZIBOSHI=100216;
BICst.MAP_TYPE.HUAIBEISHI=100217;
BICst.MAP_TYPE.HUAINANSHI=100218;
BICst.MAP_TYPE.HUAIANSHI=100219;
BICst.MAP_TYPE.SHENZHENSHI=100220;
BICst.MAP_TYPE.QINGYUANSHI=100221;
BICst.MAP_TYPE.WENZHOUSHI=100222;
BICst.MAP_TYPE.WEINANSHI=100223;
BICst.MAP_TYPE.HUBEISHENG=100224;
BICst.MAP_TYPE.HUNANSHENG=100225;
BICst.MAP_TYPE.HUZHOUSHI=100226;
BICst.MAP_TYPE.XIANGTANSHI=100227;
BICst.MAP_TYPE.XIANGXITUJIAZUMIAOZUZIZHIZHOU=100228;
BICst.MAP_TYPE.ZHANJIANGSHI=100229;
BICst.MAP_TYPE.CHUZHOUSHI=100230;
BICst.MAP_TYPE.BINZHOUSHI=100231;
BICst.MAP_TYPE.LUOHESHI=100232;
BICst.MAP_TYPE.ZHANGZHOUSHI=100233;
BICst.MAP_TYPE.WEIFANGSHI=100234;
BICst.MAP_TYPE.CHAOZHOUSHI=100235;
BICst.MAP_TYPE.AOMENTEBIEXINGZHENGQU=100236;
BICst.MAP_TYPE.PUYANGSHI=100237;
BICst.MAP_TYPE.YANTAISHI=100238;
BICst.MAP_TYPE.JIAOZUOSHI=100239;
BICst.MAP_TYPE.MUDANJIANGSHI=100240;
BICst.MAP_TYPE.YULINSHI1=100241;
BICst.MAP_TYPE.YUSHUCANGZUZIZHIZHOU=100242;
BICst.MAP_TYPE.YUXISHI=100243;
BICst.MAP_TYPE.ZHUHAISHI=100244;
BICst.MAP_TYPE.GANNANCANGZUZIZHIZHOU=100245;
BICst.MAP_TYPE.GANZICANGZUZIZHIZHOU=100246;
BICst.MAP_TYPE.GANSUSHENG=100247;
BICst.MAP_TYPE.BAICHENGSHI=100248;
BICst.MAP_TYPE.BAISHANSHI=100249;
BICst.MAP_TYPE.BAIYINSHI=100250;
BICst.MAP_TYPE.BAISESHI=100251;
BICst.MAP_TYPE.YIYANGSHI=100252;
BICst.MAP_TYPE.YANCHENGSHI=100253;
BICst.MAP_TYPE.PANJINSHI=100254;
BICst.MAP_TYPE.MEISHANSHI=100255;
BICst.MAP_TYPE.SHIZUISHANSHI=100256;
BICst.MAP_TYPE.SHIJIAZHUANGSHI=100257;
BICst.MAP_TYPE.FUZHOUSHI1=100258;
BICst.MAP_TYPE.FUJIANSHENG=100259;
BICst.MAP_TYPE.QINHUANGDAOSHI=100260;
BICst.MAP_TYPE.HONGHEHANIZUYIZUZIZHIZHOU=100261;
BICst.MAP_TYPE.SHAOXINGSHI=100262;
BICst.MAP_TYPE.SUIHUASHI=100263;
BICst.MAP_TYPE.MIANYANGSHI=100264;
BICst.MAP_TYPE.LIAOCHENGSHI=100265;
BICst.MAP_TYPE.ZHAOQINGSHI=100266;
BICst.MAP_TYPE.ZIGONGSHI=100267;
BICst.MAP_TYPE.ZHOUSHANSHI=100268;
BICst.MAP_TYPE.WUHUSHI=100269;
BICst.MAP_TYPE.SUZHOUSHI1=100270;
BICst.MAP_TYPE.MAOMINGSHI=100271;
BICst.MAP_TYPE.JINGZHOUSHI=100272;
BICst.MAP_TYPE.JINGMENSHI=100273;
BICst.MAP_TYPE.PUTIANSHI=100274;
BICst.MAP_TYPE.LAIWUSHI=100275;
BICst.MAP_TYPE.HEZESHI=100276;
BICst.MAP_TYPE.PINGXIANGSHI=100277;
BICst.MAP_TYPE.YINGKOUSHI=100278;
BICst.MAP_TYPE.HULUDAOSHI=100279;
BICst.MAP_TYPE.BENGBUSHI=100280;
BICst.MAP_TYPE.HENGSHUISHI=100281;
BICst.MAP_TYPE.HENGYANGSHI=100282;
BICst.MAP_TYPE.QUZHOUSHI=100283;
BICst.MAP_TYPE.XIANGFANSHI=100284;
BICst.MAP_TYPE.XISHUANGBANNADAIZUZIZHIZHOU=100285;
BICst.MAP_TYPE.XININGSHI=100286;
BICst.MAP_TYPE.XIANSHI=100287;
BICst.MAP_TYPE.XICANGZIZHIQU=100288;
BICst.MAP_TYPE.XUCHANGSHI=100289;
BICst.MAP_TYPE.GUIZHOUSHENG=100290;
BICst.MAP_TYPE.GUIGANGSHI=100291;
BICst.MAP_TYPE.GUIYANGSHI=100292;
BICst.MAP_TYPE.HEZHOUSHI=100293;
BICst.MAP_TYPE.ZIYANGSHI=100294;
BICst.MAP_TYPE.GANZHOUSHI=100295;
BICst.MAP_TYPE.CHIFENGSHI=100296;
BICst.MAP_TYPE.LIAONINGSHENG=100297;
BICst.MAP_TYPE.LIAOYUANSHI=100298;
BICst.MAP_TYPE.LIAOYANGSHI=100299;
BICst.MAP_TYPE.DAZHOUSHI=100300;
BICst.MAP_TYPE.YUNCHENGSHI=100301;
BICst.MAP_TYPE.LIANYUNGANGSHI=100302;
BICst.MAP_TYPE.DIQINGCANGZUZIZHIZHOU=100303;
BICst.MAP_TYPE.TONGHUASHI=100304;
BICst.MAP_TYPE.TONGLIAOSHI=100305;
BICst.MAP_TYPE.SUININGSHI=100306;
BICst.MAP_TYPE.ZUNYISHI=100307;
BICst.MAP_TYPE.XINGTAISHI=100308;
BICst.MAP_TYPE.NAQUDIQU=100309;
BICst.MAP_TYPE.HANDANSHI=100310;
BICst.MAP_TYPE.SHAOYANGSHI=100311;
BICst.MAP_TYPE.ZHENGZHOUSHI=100312;
BICst.MAP_TYPE.CHENZHOUSHI=100313;
BICst.MAP_TYPE.EERDUOSISHI=100314;
BICst.MAP_TYPE.EZHOUSHI=100315;
BICst.MAP_TYPE.JIUQUANSHI=100316;
BICst.MAP_TYPE.CHONGQINGSHI=100317;
BICst.MAP_TYPE.JINHUASHI=100318;
BICst.MAP_TYPE.JINCHANGSHI=100319;
BICst.MAP_TYPE.QINZHOUSHI=100320;
BICst.MAP_TYPE.TIELINGSHI=100321;
BICst.MAP_TYPE.TONGRENDIQU=100322;
BICst.MAP_TYPE.TONGCHUANSHI=100323;
BICst.MAP_TYPE.TONGLINGSHI=100324;
BICst.MAP_TYPE.YINCHUANSHI=100325;
BICst.MAP_TYPE.XILINGUOLEMENG=100326;
BICst.MAP_TYPE.JINZHOUSHI=100327;
BICst.MAP_TYPE.ZHENJIANGSHI=100328;
BICst.MAP_TYPE.CHANGCHUNSHI=100329;
BICst.MAP_TYPE.CHANGSHASHI=100330;
BICst.MAP_TYPE.CHANGZHISHI=100331;
BICst.MAP_TYPE.FUXINSHI=100332;
BICst.MAP_TYPE.FUYANGSHI=100333;
BICst.MAP_TYPE.FANGCHENGGANGSHI=100334;
BICst.MAP_TYPE.YANGJIANGSHI=100335;
BICst.MAP_TYPE.YANGQUANSHI=100336;
BICst.MAP_TYPE.AKESUDIQU=100337;
BICst.MAP_TYPE.ALETAIDIQU=100338;
BICst.MAP_TYPE.ABACANGZUQIANGZUZIZHIZHOU=100339;
BICst.MAP_TYPE.ALASHANMENG=100340;
BICst.MAP_TYPE.ALIDIQU=100341;
BICst.MAP_TYPE.LONGNANSHI=100342;
BICst.MAP_TYPE.SHANXISHENG1=100343;
BICst.MAP_TYPE.SUIZHOUSHI=100344;
BICst.MAP_TYPE.YAANSHI=100345;
BICst.MAP_TYPE.QINGDAOSHI=100346;
BICst.MAP_TYPE.QINGHAISHENG=100347;
BICst.MAP_TYPE.ANSHANSHI=100348;
BICst.MAP_TYPE.SHAOGUANSHI=100349;
BICst.MAP_TYPE.XIANGGANGTEBIEXINGZHENGQU=100350;
BICst.MAP_TYPE.MAANSHANSHI=100351;
BICst.MAP_TYPE.ZHUMADIANSHI=100352;
BICst.MAP_TYPE.JIXISHI=100353;
BICst.MAP_TYPE.HEBISHI=100354;
BICst.MAP_TYPE.HEGANGSHI=100355;
BICst.MAP_TYPE.YINGTANSHI=100356;
BICst.MAP_TYPE.HUANGGANGSHI=100357;
BICst.MAP_TYPE.HUANGNANCANGZUZIZHIZHOU=100358;
BICst.MAP_TYPE.HUANGSHANSHI=100359;
BICst.MAP_TYPE.HUANGSHISHI=100360;
BICst.MAP_TYPE.HEIHESHI=100361;
BICst.MAP_TYPE.HEILONGJIANGSHENG=100362;
BICst.MAP_TYPE.QIANDONGNANMIAOZUDONGZUZIZHIZHOU=100363;
BICst.MAP_TYPE.QIANNANBUYIZUMIAOZUZIZHIZHOU=100364;
BICst.MAP_TYPE.QIANXINANBUYIZUMIAOZUZIZHIZHOU=100365;
BICst.MAP_TYPE.QIQIHAERSHI=100366;
BICst.MAP_TYPE.LONGYANSHI=100367;

BICst.MAP_PATH = {};
BICst.MAP_PATH[BICst.MAP_TYPE.CHINA] = FR.serverURL + FR.servletURL + "?op=resource&resource=/com/fr/bi/web/js/data/map/china.json";
BICst.MAP_PATH[BICst.MAP_TYPE.QITAIHESHI] = FR.serverURL + FR.servletURL + "?op=resource&resource=/com/fr/bi/web/js/data/map/qitaiheshi.json";
BICst.MAP_PATH[BICst.MAP_TYPE.SANYASHI] = FR.serverURL + FR.servletURL + "?op=resource&resource=/com/fr/bi/web/js/data/map/sanyashi.json";
BICst.MAP_PATH[BICst.MAP_TYPE.SANMINGSHI] = FR.serverURL + FR.servletURL + "?op=resource&resource=/com/fr/bi/web/js/data/map/sanmingshi.json";
BICst.MAP_PATH[BICst.MAP_TYPE.SANMENXIASHI] = FR.serverURL + FR.servletURL + "?op=resource&resource=/com/fr/bi/web/js/data/map/sanmenxiashi.json";
BICst.MAP_PATH[BICst.MAP_TYPE.SHANGHAISHI] = FR.serverURL + FR.servletURL + "?op=resource&resource=/com/fr/bi/web/js/data/map/shanghaishi.json";
BICst.MAP_PATH[BICst.MAP_TYPE.SHANGRAOSHI] = FR.serverURL + FR.servletURL + "?op=resource&resource=/com/fr/bi/web/js/data/map/shangraoshi.json";
BICst.MAP_PATH[BICst.MAP_TYPE.DONGGUANSHI] = FR.serverURL + FR.servletURL + "?op=resource&resource=/com/fr/bi/web/js/data/map/dongguanshi.json";
BICst.MAP_PATH[BICst.MAP_TYPE.DONGYINGSHI] = FR.serverURL + FR.servletURL + "?op=resource&resource=/com/fr/bi/web/js/data/map/dongyingshi.json";
BICst.MAP_PATH[BICst.MAP_TYPE.ZHONGWEISHI] = FR.serverURL + FR.servletURL + "?op=resource&resource=/com/fr/bi/web/js/data/map/zhongweishi.json";
BICst.MAP_PATH[BICst.MAP_TYPE.ZHONGSHANSHI] = FR.serverURL + FR.servletURL + "?op=resource&resource=/com/fr/bi/web/js/data/map/zhongshanshi.json";
BICst.MAP_PATH[BICst.MAP_TYPE.LINXIAHUIZUZIZHIZHOU] = FR.serverURL + FR.servletURL + "?op=resource&resource=/com/fr/bi/web/js/data/map/linxiahuizuzizhizhou.json";
BICst.MAP_PATH[BICst.MAP_TYPE.LINFENSHI] = FR.serverURL + FR.servletURL + "?op=resource&resource=/com/fr/bi/web/js/data/map/linfenshi.json";
BICst.MAP_PATH[BICst.MAP_TYPE.LINYISHI] = FR.serverURL + FR.servletURL + "?op=resource&resource=/com/fr/bi/web/js/data/map/linyishi.json";
BICst.MAP_PATH[BICst.MAP_TYPE.LINCANGSHI] = FR.serverURL + FR.servletURL + "?op=resource&resource=/com/fr/bi/web/js/data/map/lincangshi.json";
BICst.MAP_PATH[BICst.MAP_TYPE.DANDONGSHI] = FR.serverURL + FR.servletURL + "?op=resource&resource=/com/fr/bi/web/js/data/map/dandongshi.json";
BICst.MAP_PATH[BICst.MAP_TYPE.LISHUISHI] = FR.serverURL + FR.servletURL + "?op=resource&resource=/com/fr/bi/web/js/data/map/lishuishi.json";
BICst.MAP_PATH[BICst.MAP_TYPE.LIJIANGSHI] = FR.serverURL + FR.servletURL + "?op=resource&resource=/com/fr/bi/web/js/data/map/lijiangshi.json";
BICst.MAP_PATH[BICst.MAP_TYPE.WULANCHABUSHI] = FR.serverURL + FR.servletURL + "?op=resource&resource=/com/fr/bi/web/js/data/map/wulanchabushi.json";
BICst.MAP_PATH[BICst.MAP_TYPE.WUHAISHI] = FR.serverURL + FR.servletURL + "?op=resource&resource=/com/fr/bi/web/js/data/map/wuhaishi.json";
BICst.MAP_PATH[BICst.MAP_TYPE.WULUMUQISHI] = FR.serverURL + FR.servletURL + "?op=resource&resource=/com/fr/bi/web/js/data/map/wulumuqishi.json";
BICst.MAP_PATH[BICst.MAP_TYPE.LESHANSHI] = FR.serverURL + FR.servletURL + "?op=resource&resource=/com/fr/bi/web/js/data/map/leshanshi.json";
BICst.MAP_PATH[BICst.MAP_TYPE.JIUJIANGSHI] = FR.serverURL + FR.servletURL + "?op=resource&resource=/com/fr/bi/web/js/data/map/jiujiangshi.json";
BICst.MAP_PATH[BICst.MAP_TYPE.YUNNANSHENG] = FR.serverURL + FR.servletURL + "?op=resource&resource=/com/fr/bi/web/js/data/map/yunnansheng.json";
BICst.MAP_PATH[BICst.MAP_TYPE.YUNFUSHI] = FR.serverURL + FR.servletURL + "?op=resource&resource=/com/fr/bi/web/js/data/map/yunfushi.json";
BICst.MAP_PATH[BICst.MAP_TYPE.YICHUNSHI] = FR.serverURL + FR.servletURL + "?op=resource&resource=/com/fr/bi/web/js/data/map/yichunshi.json";
BICst.MAP_PATH[BICst.MAP_TYPE.YILIHASAKEZIZHIZHOU] = FR.serverURL + FR.servletURL + "?op=resource&resource=/com/fr/bi/web/js/data/map/yilihasakezizhizhou.json";
BICst.MAP_PATH[BICst.MAP_TYPE.FOSHANSHI] = FR.serverURL + FR.servletURL + "?op=resource&resource=/com/fr/bi/web/js/data/map/foshanshi.json";
BICst.MAP_PATH[BICst.MAP_TYPE.JIAMUSISHI] = FR.serverURL + FR.servletURL + "?op=resource&resource=/com/fr/bi/web/js/data/map/jiamusishi.json";
BICst.MAP_PATH[BICst.MAP_TYPE.BAODINGSHI] = FR.serverURL + FR.servletURL + "?op=resource&resource=/com/fr/bi/web/js/data/map/baodingshi.json";
BICst.MAP_PATH[BICst.MAP_TYPE.BAOSHANSHI] = FR.serverURL + FR.servletURL + "?op=resource&resource=/com/fr/bi/web/js/data/map/baoshanshi.json";
BICst.MAP_PATH[BICst.MAP_TYPE.XINYANGSHI] = FR.serverURL + FR.servletURL + "?op=resource&resource=/com/fr/bi/web/js/data/map/xinyangshi.json";
BICst.MAP_PATH[BICst.MAP_TYPE.KEZILESUKEERKEZIZIZHIZHOU] = FR.serverURL + FR.servletURL + "?op=resource&resource=/com/fr/bi/web/js/data/map/kezilesukeerkezizizhizhou.json";
BICst.MAP_PATH[BICst.MAP_TYPE.KELAMAYISHI] = FR.serverURL + FR.servletURL + "?op=resource&resource=/com/fr/bi/web/js/data/map/kelamayishi.json";
BICst.MAP_PATH[BICst.MAP_TYPE.LUANSHI] = FR.serverURL + FR.servletURL + "?op=resource&resource=/com/fr/bi/web/js/data/map/luanshi.json";
BICst.MAP_PATH[BICst.MAP_TYPE.LIUPANSHUISHI] = FR.serverURL + FR.servletURL + "?op=resource&resource=/com/fr/bi/web/js/data/map/liupanshuishi.json";
BICst.MAP_PATH[BICst.MAP_TYPE.LANZHOUSHI] = FR.serverURL + FR.servletURL + "?op=resource&resource=/com/fr/bi/web/js/data/map/lanzhoushi.json";
BICst.MAP_PATH[BICst.MAP_TYPE.XINGANMENG] = FR.serverURL + FR.servletURL + "?op=resource&resource=/com/fr/bi/web/js/data/map/xinganmeng.json";
BICst.MAP_PATH[BICst.MAP_TYPE.NEIJIANGSHI] = FR.serverURL + FR.servletURL + "?op=resource&resource=/com/fr/bi/web/js/data/map/neijiangshi.json";
BICst.MAP_PATH[BICst.MAP_TYPE.NEIMENGGUZIZHIQU] = FR.serverURL + FR.servletURL + "?op=resource&resource=/com/fr/bi/web/js/data/map/neimengguzizhiqu.json";
BICst.MAP_PATH[BICst.MAP_TYPE.LIANGSHANYIZUZIZHIZHOU] = FR.serverURL + FR.servletURL + "?op=resource&resource=/com/fr/bi/web/js/data/map/liangshanyizuzizhizhou.json";
BICst.MAP_PATH[BICst.MAP_TYPE.BAOTOUSHI] = FR.serverURL + FR.servletURL + "?op=resource&resource=/com/fr/bi/web/js/data/map/baotoushi.json";
BICst.MAP_PATH[BICst.MAP_TYPE.BEIJINGSHI] = FR.serverURL + FR.servletURL + "?op=resource&resource=/com/fr/bi/web/js/data/map/beijingshi.json";
BICst.MAP_PATH[BICst.MAP_TYPE.BEIHAISHI] = FR.serverURL + FR.servletURL + "?op=resource&resource=/com/fr/bi/web/js/data/map/beihaishi.json";
BICst.MAP_PATH[BICst.MAP_TYPE.SHIYANSHI] = FR.serverURL + FR.servletURL + "?op=resource&resource=/com/fr/bi/web/js/data/map/shiyanshi.json";
BICst.MAP_PATH[BICst.MAP_TYPE.NANJINGSHI] = FR.serverURL + FR.servletURL + "?op=resource&resource=/com/fr/bi/web/js/data/map/nanjingshi.json";
BICst.MAP_PATH[BICst.MAP_TYPE.NANCHONGSHI] = FR.serverURL + FR.servletURL + "?op=resource&resource=/com/fr/bi/web/js/data/map/nanchongshi.json";
BICst.MAP_PATH[BICst.MAP_TYPE.NANNINGSHI] = FR.serverURL + FR.servletURL + "?op=resource&resource=/com/fr/bi/web/js/data/map/nanningshi.json";
BICst.MAP_PATH[BICst.MAP_TYPE.NANPINGSHI] = FR.serverURL + FR.servletURL + "?op=resource&resource=/com/fr/bi/web/js/data/map/nanpingshi.json";
BICst.MAP_PATH[BICst.MAP_TYPE.NANCHANGSHI] = FR.serverURL + FR.servletURL + "?op=resource&resource=/com/fr/bi/web/js/data/map/nanchangshi.json";
BICst.MAP_PATH[BICst.MAP_TYPE.NANTONGSHI] = FR.serverURL + FR.servletURL + "?op=resource&resource=/com/fr/bi/web/js/data/map/nantongshi.json";
BICst.MAP_PATH[BICst.MAP_TYPE.NANYANGSHI] = FR.serverURL + FR.servletURL + "?op=resource&resource=/com/fr/bi/web/js/data/map/nanyangshi.json";
BICst.MAP_PATH[BICst.MAP_TYPE.BOERTALAMENGGUZIZHIZHOU] = FR.serverURL + FR.servletURL + "?op=resource&resource=/com/fr/bi/web/js/data/map/boertalamengguzizhizhou.json";
BICst.MAP_PATH[BICst.MAP_TYPE.XIAMENSHI] = FR.serverURL + FR.servletURL + "?op=resource&resource=/com/fr/bi/web/js/data/map/xiamenshi.json";
BICst.MAP_PATH[BICst.MAP_TYPE.SHUANGYASHANSHI] = FR.serverURL + FR.servletURL + "?op=resource&resource=/com/fr/bi/web/js/data/map/shuangyashanshi.json";
BICst.MAP_PATH[BICst.MAP_TYPE.TAIZHOUSHI] = FR.serverURL + FR.servletURL + "?op=resource&resource=/com/fr/bi/web/js/data/map/taizhoushi.json";
BICst.MAP_PATH[BICst.MAP_TYPE.TAIWANSHENG] = FR.serverURL + FR.servletURL + "?op=resource&resource=/com/fr/bi/web/js/data/map/taiwansheng.json";
BICst.MAP_PATH[BICst.MAP_TYPE.HEFEISHI] = FR.serverURL + FR.servletURL + "?op=resource&resource=/com/fr/bi/web/js/data/map/hefeishi.json";
BICst.MAP_PATH[BICst.MAP_TYPE.JIANSHI] = FR.serverURL + FR.servletURL + "?op=resource&resource=/com/fr/bi/web/js/data/map/jianshi.json";
BICst.MAP_PATH[BICst.MAP_TYPE.JILINSHI] = FR.serverURL + FR.servletURL + "?op=resource&resource=/com/fr/bi/web/js/data/map/jilinshi.json";
BICst.MAP_PATH[BICst.MAP_TYPE.JILINSHENG] = FR.serverURL + FR.servletURL + "?op=resource&resource=/com/fr/bi/web/js/data/map/jilinsheng.json";
BICst.MAP_PATH[BICst.MAP_TYPE.TULUFANDIQU] = FR.serverURL + FR.servletURL + "?op=resource&resource=/com/fr/bi/web/js/data/map/tulufandiqu.json";
BICst.MAP_PATH[BICst.MAP_TYPE.LVLIANGSHI] = FR.serverURL + FR.servletURL + "?op=resource&resource=/com/fr/bi/web/js/data/map/lvliangshi.json";
BICst.MAP_PATH[BICst.MAP_TYPE.WUZHONGSHI] = FR.serverURL + FR.servletURL + "?op=resource&resource=/com/fr/bi/web/js/data/map/wuzhongshi.json";
BICst.MAP_PATH[BICst.MAP_TYPE.ZHOUKOUSHI] = FR.serverURL + FR.servletURL + "?op=resource&resource=/com/fr/bi/web/js/data/map/zhoukoushi.json";
BICst.MAP_PATH[BICst.MAP_TYPE.HULUNBEIERSHI] = FR.serverURL + FR.servletURL + "?op=resource&resource=/com/fr/bi/web/js/data/map/hulunbeiershi.json";
BICst.MAP_PATH[BICst.MAP_TYPE.HUHEHAOTESHI] = FR.serverURL + FR.servletURL + "?op=resource&resource=/com/fr/bi/web/js/data/map/huhehaoteshi.json";
BICst.MAP_PATH[BICst.MAP_TYPE.HETIANDIQU] = FR.serverURL + FR.servletURL + "?op=resource&resource=/com/fr/bi/web/js/data/map/hetiandiqu.json";
BICst.MAP_PATH[BICst.MAP_TYPE.XIANNINGSHI] = FR.serverURL + FR.servletURL + "?op=resource&resource=/com/fr/bi/web/js/data/map/xianningshi.json";
BICst.MAP_PATH[BICst.MAP_TYPE.XIANYANGSHI] = FR.serverURL + FR.servletURL + "?op=resource&resource=/com/fr/bi/web/js/data/map/xianyangshi.json";
BICst.MAP_PATH[BICst.MAP_TYPE.HAMIDIQU] = FR.serverURL + FR.servletURL + "?op=resource&resource=/com/fr/bi/web/js/data/map/hamidiqu.json";
BICst.MAP_PATH[BICst.MAP_TYPE.HAERBINSHI] = FR.serverURL + FR.servletURL + "?op=resource&resource=/com/fr/bi/web/js/data/map/haerbinshi.json";
BICst.MAP_PATH[BICst.MAP_TYPE.TANGSHANSHI] = FR.serverURL + FR.servletURL + "?op=resource&resource=/com/fr/bi/web/js/data/map/tangshanshi.json";
BICst.MAP_PATH[BICst.MAP_TYPE.SHANGQIUSHI] = FR.serverURL + FR.servletURL + "?op=resource&resource=/com/fr/bi/web/js/data/map/shangqiushi.json";
BICst.MAP_PATH[BICst.MAP_TYPE.SHANGLUOSHI] = FR.serverURL + FR.servletURL + "?op=resource&resource=/com/fr/bi/web/js/data/map/shangluoshi.json";
BICst.MAP_PATH[BICst.MAP_TYPE.KASHENDIQU] = FR.serverURL + FR.servletURL + "?op=resource&resource=/com/fr/bi/web/js/data/map/kashendiqu.json";
BICst.MAP_PATH[BICst.MAP_TYPE.JIAXINGSHI] = FR.serverURL + FR.servletURL + "?op=resource&resource=/com/fr/bi/web/js/data/map/jiaxingshi.json";
BICst.MAP_PATH[BICst.MAP_TYPE.JIAYUGUANSHI] = FR.serverURL + FR.servletURL + "?op=resource&resource=/com/fr/bi/web/js/data/map/jiayuguanshi.json";
BICst.MAP_PATH[BICst.MAP_TYPE.SICHUANSHENG] = FR.serverURL + FR.servletURL + "?op=resource&resource=/com/fr/bi/web/js/data/map/sichuansheng.json";
BICst.MAP_PATH[BICst.MAP_TYPE.SIPINGSHI] = FR.serverURL + FR.servletURL + "?op=resource&resource=/com/fr/bi/web/js/data/map/sipingshi.json";
BICst.MAP_PATH[BICst.MAP_TYPE.GUYUANSHI] = FR.serverURL + FR.servletURL + "?op=resource&resource=/com/fr/bi/web/js/data/map/guyuanshi.json";
BICst.MAP_PATH[BICst.MAP_TYPE.TACHENGDIQU] = FR.serverURL + FR.servletURL + "?op=resource&resource=/com/fr/bi/web/js/data/map/tachengdiqu.json";
BICst.MAP_PATH[BICst.MAP_TYPE.DAXINGANLINGDIQU] = FR.serverURL + FR.servletURL + "?op=resource&resource=/com/fr/bi/web/js/data/map/daxinganlingdiqu.json";
BICst.MAP_PATH[BICst.MAP_TYPE.DATONGSHI] = FR.serverURL + FR.servletURL + "?op=resource&resource=/com/fr/bi/web/js/data/map/datongshi.json";
BICst.MAP_PATH[BICst.MAP_TYPE.DAQINGSHI] = FR.serverURL + FR.servletURL + "?op=resource&resource=/com/fr/bi/web/js/data/map/daqingshi.json";
BICst.MAP_PATH[BICst.MAP_TYPE.DALIBAIZUZIZHIZHOU] = FR.serverURL + FR.servletURL + "?op=resource&resource=/com/fr/bi/web/js/data/map/dalibaizuzizhizhou.json";
BICst.MAP_PATH[BICst.MAP_TYPE.DALIANSHI] = FR.serverURL + FR.servletURL + "?op=resource&resource=/com/fr/bi/web/js/data/map/dalianshi.json";
BICst.MAP_PATH[BICst.MAP_TYPE.TIANSHUISHI] = FR.serverURL + FR.servletURL + "?op=resource&resource=/com/fr/bi/web/js/data/map/tianshuishi.json";
BICst.MAP_PATH[BICst.MAP_TYPE.TIANJINSHI] = FR.serverURL + FR.servletURL + "?op=resource&resource=/com/fr/bi/web/js/data/map/tianjinshi.json";
BICst.MAP_PATH[BICst.MAP_TYPE.TAIYUANSHI] = FR.serverURL + FR.servletURL + "?op=resource&resource=/com/fr/bi/web/js/data/map/taiyuanshi.json";
BICst.MAP_PATH[BICst.MAP_TYPE.WEIHAISHI] = FR.serverURL + FR.servletURL + "?op=resource&resource=/com/fr/bi/web/js/data/map/weihaishi.json";
BICst.MAP_PATH[BICst.MAP_TYPE.LOUDISHI] = FR.serverURL + FR.servletURL + "?op=resource&resource=/com/fr/bi/web/js/data/map/loudishi.json";
BICst.MAP_PATH[BICst.MAP_TYPE.XIAOGANSHI] = FR.serverURL + FR.servletURL + "?op=resource&resource=/com/fr/bi/web/js/data/map/xiaoganshi.json";
BICst.MAP_PATH[BICst.MAP_TYPE.NINGXIAHUIZUZIZHIQU] = FR.serverURL + FR.servletURL + "?op=resource&resource=/com/fr/bi/web/js/data/map/ningxiahuizuzizhiqu.json";
BICst.MAP_PATH[BICst.MAP_TYPE.NINGDESHI] = FR.serverURL + FR.servletURL + "?op=resource&resource=/com/fr/bi/web/js/data/map/ningdeshi.json";
BICst.MAP_PATH[BICst.MAP_TYPE.NINGBOSHI] = FR.serverURL + FR.servletURL + "?op=resource&resource=/com/fr/bi/web/js/data/map/ningboshi.json";
BICst.MAP_PATH[BICst.MAP_TYPE.ANQINGSHI] = FR.serverURL + FR.servletURL + "?op=resource&resource=/com/fr/bi/web/js/data/map/anqingshi.json";
BICst.MAP_PATH[BICst.MAP_TYPE.ANKANGSHI] = FR.serverURL + FR.servletURL + "?op=resource&resource=/com/fr/bi/web/js/data/map/ankangshi.json";
BICst.MAP_PATH[BICst.MAP_TYPE.ANHUISHENG] = FR.serverURL + FR.servletURL + "?op=resource&resource=/com/fr/bi/web/js/data/map/anhuisheng.json";
BICst.MAP_PATH[BICst.MAP_TYPE.ANYANGSHI] = FR.serverURL + FR.servletURL + "?op=resource&resource=/com/fr/bi/web/js/data/map/anyangshi.json";
BICst.MAP_PATH[BICst.MAP_TYPE.ANSHUNSHI] = FR.serverURL + FR.servletURL + "?op=resource&resource=/com/fr/bi/web/js/data/map/anshunshi.json";
BICst.MAP_PATH[BICst.MAP_TYPE.DINGXISHI] = FR.serverURL + FR.servletURL + "?op=resource&resource=/com/fr/bi/web/js/data/map/dingxishi.json";
BICst.MAP_PATH[BICst.MAP_TYPE.YIBINSHI] = FR.serverURL + FR.servletURL + "?op=resource&resource=/com/fr/bi/web/js/data/map/yibinshi.json";
BICst.MAP_PATH[BICst.MAP_TYPE.YICHANGSHI] = FR.serverURL + FR.servletURL + "?op=resource&resource=/com/fr/bi/web/js/data/map/yichangshi.json";
BICst.MAP_PATH[BICst.MAP_TYPE.YICHUNSHI1] = FR.serverURL + FR.servletURL + "?op=resource&resource=/com/fr/bi/web/js/data/map/yichunshi1.json";
BICst.MAP_PATH[BICst.MAP_TYPE.BAOJISHI] = FR.serverURL + FR.servletURL + "?op=resource&resource=/com/fr/bi/web/js/data/map/baojishi.json";
BICst.MAP_PATH[BICst.MAP_TYPE.XUANCHENGSHI] = FR.serverURL + FR.servletURL + "?op=resource&resource=/com/fr/bi/web/js/data/map/xuanchengshi.json";
BICst.MAP_PATH[BICst.MAP_TYPE.SUZHOUSHI] = FR.serverURL + FR.servletURL + "?op=resource&resource=/com/fr/bi/web/js/data/map/suzhoushi.json";
BICst.MAP_PATH[BICst.MAP_TYPE.SUQIANSHI] = FR.serverURL + FR.servletURL + "?op=resource&resource=/com/fr/bi/web/js/data/map/suqianshi.json";
BICst.MAP_PATH[BICst.MAP_TYPE.SHANDONGSHENG] = FR.serverURL + FR.servletURL + "?op=resource&resource=/com/fr/bi/web/js/data/map/shandongsheng.json";
BICst.MAP_PATH[BICst.MAP_TYPE.SHANNANDIQU] = FR.serverURL + FR.servletURL + "?op=resource&resource=/com/fr/bi/web/js/data/map/shannandiqu.json";
BICst.MAP_PATH[BICst.MAP_TYPE.SHANXISHENG] = FR.serverURL + FR.servletURL + "?op=resource&resource=/com/fr/bi/web/js/data/map/shanxisheng.json";
BICst.MAP_PATH[BICst.MAP_TYPE.YUEYANGSHI] = FR.serverURL + FR.servletURL + "?op=resource&resource=/com/fr/bi/web/js/data/map/yueyangshi.json";
BICst.MAP_PATH[BICst.MAP_TYPE.CHONGZUOSHI] = FR.serverURL + FR.servletURL + "?op=resource&resource=/com/fr/bi/web/js/data/map/chongzuoshi.json";
BICst.MAP_PATH[BICst.MAP_TYPE.BAZHONGSHI] = FR.serverURL + FR.servletURL + "?op=resource&resource=/com/fr/bi/web/js/data/map/bazhongshi.json";
BICst.MAP_PATH[BICst.MAP_TYPE.BAYANNAOERSHI] = FR.serverURL + FR.servletURL + "?op=resource&resource=/com/fr/bi/web/js/data/map/bayannaoershi.json";
BICst.MAP_PATH[BICst.MAP_TYPE.BAYINGUOLENGMENGGUZIZHIZHOU] = FR.serverURL + FR.servletURL + "?op=resource&resource=/com/fr/bi/web/js/data/map/bayinguolengmengguzizhizhou.json";
BICst.MAP_PATH[BICst.MAP_TYPE.CHANGZHOUSHI] = FR.serverURL + FR.servletURL + "?op=resource&resource=/com/fr/bi/web/js/data/map/changzhoushi.json";
BICst.MAP_PATH[BICst.MAP_TYPE.CHANGDESHI] = FR.serverURL + FR.servletURL + "?op=resource&resource=/com/fr/bi/web/js/data/map/changdeshi.json";
BICst.MAP_PATH[BICst.MAP_TYPE.PINGLIANGSHI] = FR.serverURL + FR.servletURL + "?op=resource&resource=/com/fr/bi/web/js/data/map/pingliangshi.json";
BICst.MAP_PATH[BICst.MAP_TYPE.PINGDINGSHANSHI] = FR.serverURL + FR.servletURL + "?op=resource&resource=/com/fr/bi/web/js/data/map/pingdingshanshi.json";
BICst.MAP_PATH[BICst.MAP_TYPE.GUANGDONGSHENG] = FR.serverURL + FR.servletURL + "?op=resource&resource=/com/fr/bi/web/js/data/map/guangdongsheng.json";
BICst.MAP_PATH[BICst.MAP_TYPE.GUANGYUANSHI] = FR.serverURL + FR.servletURL + "?op=resource&resource=/com/fr/bi/web/js/data/map/guangyuanshi.json";
BICst.MAP_PATH[BICst.MAP_TYPE.GUANGANSHI] = FR.serverURL + FR.servletURL + "?op=resource&resource=/com/fr/bi/web/js/data/map/guanganshi.json";
BICst.MAP_PATH[BICst.MAP_TYPE.GUANGZHOUSHI] = FR.serverURL + FR.servletURL + "?op=resource&resource=/com/fr/bi/web/js/data/map/guangzhoushi.json";
BICst.MAP_PATH[BICst.MAP_TYPE.GUANGXIZHUANGZUZIZHIQU] = FR.serverURL + FR.servletURL + "?op=resource&resource=/com/fr/bi/web/js/data/map/guangxizhuangzuzizhiqu.json";
BICst.MAP_PATH[BICst.MAP_TYPE.QINGYANGSHI] = FR.serverURL + FR.servletURL + "?op=resource&resource=/com/fr/bi/web/js/data/map/qingyangshi.json";
BICst.MAP_PATH[BICst.MAP_TYPE.LANGFANGSHI] = FR.serverURL + FR.servletURL + "?op=resource&resource=/com/fr/bi/web/js/data/map/langfangshi.json";
BICst.MAP_PATH[BICst.MAP_TYPE.YANANSHI] = FR.serverURL + FR.servletURL + "?op=resource&resource=/com/fr/bi/web/js/data/map/yananshi.json";
BICst.MAP_PATH[BICst.MAP_TYPE.YANBIANCHAOXIANZUZIZHIZHOU] = FR.serverURL + FR.servletURL + "?op=resource&resource=/com/fr/bi/web/js/data/map/yanbianchaoxianzuzizhizhou.json";
BICst.MAP_PATH[BICst.MAP_TYPE.KAIFENGSHI] = FR.serverURL + FR.servletURL + "?op=resource&resource=/com/fr/bi/web/js/data/map/kaifengshi.json";
BICst.MAP_PATH[BICst.MAP_TYPE.ZHANGJIAKOUSHI] = FR.serverURL + FR.servletURL + "?op=resource&resource=/com/fr/bi/web/js/data/map/zhangjiakoushi.json";
BICst.MAP_PATH[BICst.MAP_TYPE.ZHANGJIAJIESHI] = FR.serverURL + FR.servletURL + "?op=resource&resource=/com/fr/bi/web/js/data/map/zhangjiajieshi.json";
BICst.MAP_PATH[BICst.MAP_TYPE.ZHANGYESHI] = FR.serverURL + FR.servletURL + "?op=resource&resource=/com/fr/bi/web/js/data/map/zhangyeshi.json";
BICst.MAP_PATH[BICst.MAP_TYPE.XUZHOUSHI] = FR.serverURL + FR.servletURL + "?op=resource&resource=/com/fr/bi/web/js/data/map/xuzhoushi.json";
BICst.MAP_PATH[BICst.MAP_TYPE.DEHONGDAIZUJINGPOZUZIZHIZHOU] = FR.serverURL + FR.servletURL + "?op=resource&resource=/com/fr/bi/web/js/data/map/dehongdaizujingpozuzizhizhou.json";
BICst.MAP_PATH[BICst.MAP_TYPE.DEZHOUSHI] = FR.serverURL + FR.servletURL + "?op=resource&resource=/com/fr/bi/web/js/data/map/dezhoushi.json";
BICst.MAP_PATH[BICst.MAP_TYPE.DEYANGSHI] = FR.serverURL + FR.servletURL + "?op=resource&resource=/com/fr/bi/web/js/data/map/deyangshi.json";
BICst.MAP_PATH[BICst.MAP_TYPE.XINZHOUSHI] = FR.serverURL + FR.servletURL + "?op=resource&resource=/com/fr/bi/web/js/data/map/xinzhoushi.json";
BICst.MAP_PATH[BICst.MAP_TYPE.HUAIHUASHI] = FR.serverURL + FR.servletURL + "?op=resource&resource=/com/fr/bi/web/js/data/map/huaihuashi.json";
BICst.MAP_PATH[BICst.MAP_TYPE.NUJIANGLISUZUZIZHIZHOU] = FR.serverURL + FR.servletURL + "?op=resource&resource=/com/fr/bi/web/js/data/map/nujianglisuzuzizhizhou.json";
BICst.MAP_PATH[BICst.MAP_TYPE.ENSHITUJIAZUMIAOZUZIZHIZHOU] = FR.serverURL + FR.servletURL + "?op=resource&resource=/com/fr/bi/web/js/data/map/enshitujiazumiaozuzizhizhou.json";
BICst.MAP_PATH[BICst.MAP_TYPE.HUIZHOUSHI] = FR.serverURL + FR.servletURL + "?op=resource&resource=/com/fr/bi/web/js/data/map/huizhoushi.json";
BICst.MAP_PATH[BICst.MAP_TYPE.CHENGDUSHI] = FR.serverURL + FR.servletURL + "?op=resource&resource=/com/fr/bi/web/js/data/map/chengdushi.json";
BICst.MAP_PATH[BICst.MAP_TYPE.YANGZHOUSHI] = FR.serverURL + FR.servletURL + "?op=resource&resource=/com/fr/bi/web/js/data/map/yangzhoushi.json";
BICst.MAP_PATH[BICst.MAP_TYPE.CHENGDESHI] = FR.serverURL + FR.servletURL + "?op=resource&resource=/com/fr/bi/web/js/data/map/chengdeshi.json";
BICst.MAP_PATH[BICst.MAP_TYPE.FUZHOUSHI] = FR.serverURL + FR.servletURL + "?op=resource&resource=/com/fr/bi/web/js/data/map/fuzhoushi.json";
BICst.MAP_PATH[BICst.MAP_TYPE.FUSHUNSHI] = FR.serverURL + FR.servletURL + "?op=resource&resource=/com/fr/bi/web/js/data/map/fushunshi.json";
BICst.MAP_PATH[BICst.MAP_TYPE.LASASHI] = FR.serverURL + FR.servletURL + "?op=resource&resource=/com/fr/bi/web/js/data/map/lasashi.json";
BICst.MAP_PATH[BICst.MAP_TYPE.JIEYANGSHI] = FR.serverURL + FR.servletURL + "?op=resource&resource=/com/fr/bi/web/js/data/map/jieyangshi.json";
BICst.MAP_PATH[BICst.MAP_TYPE.PANZHIHUASHI] = FR.serverURL + FR.servletURL + "?op=resource&resource=/com/fr/bi/web/js/data/map/panzhihuashi.json";
BICst.MAP_PATH[BICst.MAP_TYPE.WENSHANZHUANGZUMIAOZUZIZHIZHOU] = FR.serverURL + FR.servletURL + "?op=resource&resource=/com/fr/bi/web/js/data/map/wenshanzhuangzumiaozuzizhizhou.json";
BICst.MAP_PATH[BICst.MAP_TYPE.XINXIANGSHI] = FR.serverURL + FR.servletURL + "?op=resource&resource=/com/fr/bi/web/js/data/map/xinxiangshi.json";
BICst.MAP_PATH[BICst.MAP_TYPE.XINYUSHI] = FR.serverURL + FR.servletURL + "?op=resource&resource=/com/fr/bi/web/js/data/map/xinyushi.json";
BICst.MAP_PATH[BICst.MAP_TYPE.XINJIANGWEIWUERZIZHIQU] = FR.serverURL + FR.servletURL + "?op=resource&resource=/com/fr/bi/web/js/data/map/xinjiangweiwuerzizhiqu.json";
BICst.MAP_PATH[BICst.MAP_TYPE.WUXISHI] = FR.serverURL + FR.servletURL + "?op=resource&resource=/com/fr/bi/web/js/data/map/wuxishi.json";
BICst.MAP_PATH[BICst.MAP_TYPE.RIKAZEDIQU] = FR.serverURL + FR.servletURL + "?op=resource&resource=/com/fr/bi/web/js/data/map/rikazediqu.json";
BICst.MAP_PATH[BICst.MAP_TYPE.RIZHAOSHI] = FR.serverURL + FR.servletURL + "?op=resource&resource=/com/fr/bi/web/js/data/map/rizhaoshi.json";
BICst.MAP_PATH[BICst.MAP_TYPE.KUNMINGSHI] = FR.serverURL + FR.servletURL + "?op=resource&resource=/com/fr/bi/web/js/data/map/kunmingshi.json";
BICst.MAP_PATH[BICst.MAP_TYPE.CHANGJIHUIZUZIZHIZHOU] = FR.serverURL + FR.servletURL + "?op=resource&resource=/com/fr/bi/web/js/data/map/changjihuizuzizhizhou.json";
BICst.MAP_PATH[BICst.MAP_TYPE.CHANGDOUDIQU] = FR.serverURL + FR.servletURL + "?op=resource&resource=/com/fr/bi/web/js/data/map/changdoudiqu.json";
BICst.MAP_PATH[BICst.MAP_TYPE.ZHAOTONGSHI] = FR.serverURL + FR.servletURL + "?op=resource&resource=/com/fr/bi/web/js/data/map/zhaotongshi.json";
BICst.MAP_PATH[BICst.MAP_TYPE.JINZHONGSHI] = FR.serverURL + FR.servletURL + "?op=resource&resource=/com/fr/bi/web/js/data/map/jinzhongshi.json";
BICst.MAP_PATH[BICst.MAP_TYPE.JINCHENGSHI] = FR.serverURL + FR.servletURL + "?op=resource&resource=/com/fr/bi/web/js/data/map/jinchengshi.json";
BICst.MAP_PATH[BICst.MAP_TYPE.PUERSHI] = FR.serverURL + FR.servletURL + "?op=resource&resource=/com/fr/bi/web/js/data/map/puershi.json";
BICst.MAP_PATH[BICst.MAP_TYPE.JINGDEZHENSHI] = FR.serverURL + FR.servletURL + "?op=resource&resource=/com/fr/bi/web/js/data/map/jingdezhenshi.json";
BICst.MAP_PATH[BICst.MAP_TYPE.QUJINGSHI] = FR.serverURL + FR.servletURL + "?op=resource&resource=/com/fr/bi/web/js/data/map/qujingshi.json";
BICst.MAP_PATH[BICst.MAP_TYPE.SHUOZHOUSHI] = FR.serverURL + FR.servletURL + "?op=resource&resource=/com/fr/bi/web/js/data/map/shuozhoushi.json";
BICst.MAP_PATH[BICst.MAP_TYPE.CHAOYANGSHI] = FR.serverURL + FR.servletURL + "?op=resource&resource=/com/fr/bi/web/js/data/map/chaoyangshi.json";
BICst.MAP_PATH[BICst.MAP_TYPE.BENXISHI] = FR.serverURL + FR.servletURL + "?op=resource&resource=/com/fr/bi/web/js/data/map/benxishi.json";
BICst.MAP_PATH[BICst.MAP_TYPE.LAIBINSHI] = FR.serverURL + FR.servletURL + "?op=resource&resource=/com/fr/bi/web/js/data/map/laibinshi.json";
BICst.MAP_PATH[BICst.MAP_TYPE.HANGZHOUSHI] = FR.serverURL + FR.servletURL + "?op=resource&resource=/com/fr/bi/web/js/data/map/hangzhoushi.json";
BICst.MAP_PATH[BICst.MAP_TYPE.SONGYUANSHI] = FR.serverURL + FR.servletURL + "?op=resource&resource=/com/fr/bi/web/js/data/map/songyuanshi.json";
BICst.MAP_PATH[BICst.MAP_TYPE.LINZHIDIQU] = FR.serverURL + FR.servletURL + "?op=resource&resource=/com/fr/bi/web/js/data/map/linzhidiqu.json";
BICst.MAP_PATH[BICst.MAP_TYPE.GUOLUOCANGZUZIZHIZHOU] = FR.serverURL + FR.servletURL + "?op=resource&resource=/com/fr/bi/web/js/data/map/guoluocangzuzizhizhou.json";
BICst.MAP_PATH[BICst.MAP_TYPE.ZAOZHUANGSHI] = FR.serverURL + FR.servletURL + "?op=resource&resource=/com/fr/bi/web/js/data/map/zaozhuangshi.json";
BICst.MAP_PATH[BICst.MAP_TYPE.LIUZHOUSHI] = FR.serverURL + FR.servletURL + "?op=resource&resource=/com/fr/bi/web/js/data/map/liuzhoushi.json";
BICst.MAP_PATH[BICst.MAP_TYPE.ZHUZHOUSHI] = FR.serverURL + FR.servletURL + "?op=resource&resource=/com/fr/bi/web/js/data/map/zhuzhoushi.json";
BICst.MAP_PATH[BICst.MAP_TYPE.GUILINSHI] = FR.serverURL + FR.servletURL + "?op=resource&resource=/com/fr/bi/web/js/data/map/guilinshi.json";
BICst.MAP_PATH[BICst.MAP_TYPE.MEIZHOUSHI] = FR.serverURL + FR.servletURL + "?op=resource&resource=/com/fr/bi/web/js/data/map/meizhoushi.json";
BICst.MAP_PATH[BICst.MAP_TYPE.WUZHOUSHI] = FR.serverURL + FR.servletURL + "?op=resource&resource=/com/fr/bi/web/js/data/map/wuzhoushi.json";
BICst.MAP_PATH[BICst.MAP_TYPE.CHUXIONGYIZUZIZHIZHOU] = FR.serverURL + FR.servletURL + "?op=resource&resource=/com/fr/bi/web/js/data/map/chuxiongyizuzizhizhou.json";
BICst.MAP_PATH[BICst.MAP_TYPE.YULINSHI] = FR.serverURL + FR.servletURL + "?op=resource&resource=/com/fr/bi/web/js/data/map/yulinshi.json";
BICst.MAP_PATH[BICst.MAP_TYPE.WUWEISHI] = FR.serverURL + FR.servletURL + "?op=resource&resource=/com/fr/bi/web/js/data/map/wuweishi.json";
BICst.MAP_PATH[BICst.MAP_TYPE.WUHANSHI] = FR.serverURL + FR.servletURL + "?op=resource&resource=/com/fr/bi/web/js/data/map/wuhanshi.json";
BICst.MAP_PATH[BICst.MAP_TYPE.BIJIEDIQU] = FR.serverURL + FR.servletURL + "?op=resource&resource=/com/fr/bi/web/js/data/map/bijiediqu.json";
BICst.MAP_PATH[BICst.MAP_TYPE.HAOZHOUSHI] = FR.serverURL + FR.servletURL + "?op=resource&resource=/com/fr/bi/web/js/data/map/haozhoushi.json";
BICst.MAP_PATH[BICst.MAP_TYPE.YONGZHOUSHI] = FR.serverURL + FR.servletURL + "?op=resource&resource=/com/fr/bi/web/js/data/map/yongzhoushi.json";
BICst.MAP_PATH[BICst.MAP_TYPE.HANZHONGSHI] = FR.serverURL + FR.servletURL + "?op=resource&resource=/com/fr/bi/web/js/data/map/hanzhongshi.json";
BICst.MAP_PATH[BICst.MAP_TYPE.SHANTOUSHI] = FR.serverURL + FR.servletURL + "?op=resource&resource=/com/fr/bi/web/js/data/map/shantoushi.json";
BICst.MAP_PATH[BICst.MAP_TYPE.SHANWEISHI] = FR.serverURL + FR.servletURL + "?op=resource&resource=/com/fr/bi/web/js/data/map/shanweishi.json";
BICst.MAP_PATH[BICst.MAP_TYPE.JIANGSUSHENG] = FR.serverURL + FR.servletURL + "?op=resource&resource=/com/fr/bi/web/js/data/map/jiangsusheng.json";
BICst.MAP_PATH[BICst.MAP_TYPE.JIANGXISHENG] = FR.serverURL + FR.servletURL + "?op=resource&resource=/com/fr/bi/web/js/data/map/jiangxisheng.json";
BICst.MAP_PATH[BICst.MAP_TYPE.JIANGMENSHI] = FR.serverURL + FR.servletURL + "?op=resource&resource=/com/fr/bi/web/js/data/map/jiangmenshi.json";
BICst.MAP_PATH[BICst.MAP_TYPE.CHIZHOUSHI] = FR.serverURL + FR.servletURL + "?op=resource&resource=/com/fr/bi/web/js/data/map/chizhoushi.json";
BICst.MAP_PATH[BICst.MAP_TYPE.SHENYANGSHI] = FR.serverURL + FR.servletURL + "?op=resource&resource=/com/fr/bi/web/js/data/map/shenyangshi.json";
BICst.MAP_PATH[BICst.MAP_TYPE.CANGZHOUSHI] = FR.serverURL + FR.servletURL + "?op=resource&resource=/com/fr/bi/web/js/data/map/cangzhoushi.json";
BICst.MAP_PATH[BICst.MAP_TYPE.HEBEISHENG] = FR.serverURL + FR.servletURL + "?op=resource&resource=/com/fr/bi/web/js/data/map/hebeisheng.json";
BICst.MAP_PATH[BICst.MAP_TYPE.HENANSHENG] = FR.serverURL + FR.servletURL + "?op=resource&resource=/com/fr/bi/web/js/data/map/henansheng.json";
BICst.MAP_PATH[BICst.MAP_TYPE.HECHISHI] = FR.serverURL + FR.servletURL + "?op=resource&resource=/com/fr/bi/web/js/data/map/hechishi.json";
BICst.MAP_PATH[BICst.MAP_TYPE.HEYUANSHI] = FR.serverURL + FR.servletURL + "?op=resource&resource=/com/fr/bi/web/js/data/map/heyuanshi.json";
BICst.MAP_PATH[BICst.MAP_TYPE.QUANZHOUSHI] = FR.serverURL + FR.servletURL + "?op=resource&resource=/com/fr/bi/web/js/data/map/quanzhoushi.json";
BICst.MAP_PATH[BICst.MAP_TYPE.TAIANSHI] = FR.serverURL + FR.servletURL + "?op=resource&resource=/com/fr/bi/web/js/data/map/taianshi.json";
BICst.MAP_PATH[BICst.MAP_TYPE.TAIZHOUSHI1] = FR.serverURL + FR.servletURL + "?op=resource&resource=/com/fr/bi/web/js/data/map/taizhoushi1.json";
BICst.MAP_PATH[BICst.MAP_TYPE.LUZHOUSHI] = FR.serverURL + FR.servletURL + "?op=resource&resource=/com/fr/bi/web/js/data/map/luzhoushi.json";
BICst.MAP_PATH[BICst.MAP_TYPE.LUOYANGSHI] = FR.serverURL + FR.servletURL + "?op=resource&resource=/com/fr/bi/web/js/data/map/luoyangshi.json";
BICst.MAP_PATH[BICst.MAP_TYPE.JINANSHI] = FR.serverURL + FR.servletURL + "?op=resource&resource=/com/fr/bi/web/js/data/map/jinanshi.json";
BICst.MAP_PATH[BICst.MAP_TYPE.JININGSHI] = FR.serverURL + FR.servletURL + "?op=resource&resource=/com/fr/bi/web/js/data/map/jiningshi.json";
BICst.MAP_PATH[BICst.MAP_TYPE.ZHEJIANGSHENG] = FR.serverURL + FR.servletURL + "?op=resource&resource=/com/fr/bi/web/js/data/map/zhejiangsheng.json";
BICst.MAP_PATH[BICst.MAP_TYPE.HAIDONGDIQU] = FR.serverURL + FR.servletURL + "?op=resource&resource=/com/fr/bi/web/js/data/map/haidongdiqu.json";
BICst.MAP_PATH[BICst.MAP_TYPE.HAIBEICANGZUZIZHIZHOU] = FR.serverURL + FR.servletURL + "?op=resource&resource=/com/fr/bi/web/js/data/map/haibeicangzuzizhizhou.json";
BICst.MAP_PATH[BICst.MAP_TYPE.HAINANSHENG] = FR.serverURL + FR.servletURL + "?op=resource&resource=/com/fr/bi/web/js/data/map/hainansheng.json";
BICst.MAP_PATH[BICst.MAP_TYPE.HAINANCANGZUZIZHIZHOU] = FR.serverURL + FR.servletURL + "?op=resource&resource=/com/fr/bi/web/js/data/map/hainancangzuzizhizhou.json";
BICst.MAP_PATH[BICst.MAP_TYPE.HAIKOUSHI] = FR.serverURL + FR.servletURL + "?op=resource&resource=/com/fr/bi/web/js/data/map/haikoushi.json";
BICst.MAP_PATH[BICst.MAP_TYPE.HAIXIMENGGUZUCANGZUZIZHIZHOU] = FR.serverURL + FR.servletURL + "?op=resource&resource=/com/fr/bi/web/js/data/map/haiximengguzucangzuzizhizhou.json";
BICst.MAP_PATH[BICst.MAP_TYPE.ZIBOSHI] = FR.serverURL + FR.servletURL + "?op=resource&resource=/com/fr/bi/web/js/data/map/ziboshi.json";
BICst.MAP_PATH[BICst.MAP_TYPE.HUAIBEISHI] = FR.serverURL + FR.servletURL + "?op=resource&resource=/com/fr/bi/web/js/data/map/huaibeishi.json";
BICst.MAP_PATH[BICst.MAP_TYPE.HUAINANSHI] = FR.serverURL + FR.servletURL + "?op=resource&resource=/com/fr/bi/web/js/data/map/huainanshi.json";
BICst.MAP_PATH[BICst.MAP_TYPE.HUAIANSHI] = FR.serverURL + FR.servletURL + "?op=resource&resource=/com/fr/bi/web/js/data/map/huaianshi.json";
BICst.MAP_PATH[BICst.MAP_TYPE.SHENZHENSHI] = FR.serverURL + FR.servletURL + "?op=resource&resource=/com/fr/bi/web/js/data/map/shenzhenshi.json";
BICst.MAP_PATH[BICst.MAP_TYPE.QINGYUANSHI] = FR.serverURL + FR.servletURL + "?op=resource&resource=/com/fr/bi/web/js/data/map/qingyuanshi.json";
BICst.MAP_PATH[BICst.MAP_TYPE.WENZHOUSHI] = FR.serverURL + FR.servletURL + "?op=resource&resource=/com/fr/bi/web/js/data/map/wenzhoushi.json";
BICst.MAP_PATH[BICst.MAP_TYPE.WEINANSHI] = FR.serverURL + FR.servletURL + "?op=resource&resource=/com/fr/bi/web/js/data/map/weinanshi.json";
BICst.MAP_PATH[BICst.MAP_TYPE.HUBEISHENG] = FR.serverURL + FR.servletURL + "?op=resource&resource=/com/fr/bi/web/js/data/map/hubeisheng.json";
BICst.MAP_PATH[BICst.MAP_TYPE.HUNANSHENG] = FR.serverURL + FR.servletURL + "?op=resource&resource=/com/fr/bi/web/js/data/map/hunansheng.json";
BICst.MAP_PATH[BICst.MAP_TYPE.HUZHOUSHI] = FR.serverURL + FR.servletURL + "?op=resource&resource=/com/fr/bi/web/js/data/map/huzhoushi.json";
BICst.MAP_PATH[BICst.MAP_TYPE.XIANGTANSHI] = FR.serverURL + FR.servletURL + "?op=resource&resource=/com/fr/bi/web/js/data/map/xiangtanshi.json";
BICst.MAP_PATH[BICst.MAP_TYPE.XIANGXITUJIAZUMIAOZUZIZHIZHOU] = FR.serverURL + FR.servletURL + "?op=resource&resource=/com/fr/bi/web/js/data/map/xiangxitujiazumiaozuzizhizhou.json";
BICst.MAP_PATH[BICst.MAP_TYPE.ZHANJIANGSHI] = FR.serverURL + FR.servletURL + "?op=resource&resource=/com/fr/bi/web/js/data/map/zhanjiangshi.json";
BICst.MAP_PATH[BICst.MAP_TYPE.CHUZHOUSHI] = FR.serverURL + FR.servletURL + "?op=resource&resource=/com/fr/bi/web/js/data/map/chuzhoushi.json";
BICst.MAP_PATH[BICst.MAP_TYPE.BINZHOUSHI] = FR.serverURL + FR.servletURL + "?op=resource&resource=/com/fr/bi/web/js/data/map/binzhoushi.json";
BICst.MAP_PATH[BICst.MAP_TYPE.LUOHESHI] = FR.serverURL + FR.servletURL + "?op=resource&resource=/com/fr/bi/web/js/data/map/luoheshi.json";
BICst.MAP_PATH[BICst.MAP_TYPE.ZHANGZHOUSHI] = FR.serverURL + FR.servletURL + "?op=resource&resource=/com/fr/bi/web/js/data/map/zhangzhoushi.json";
BICst.MAP_PATH[BICst.MAP_TYPE.WEIFANGSHI] = FR.serverURL + FR.servletURL + "?op=resource&resource=/com/fr/bi/web/js/data/map/weifangshi.json";
BICst.MAP_PATH[BICst.MAP_TYPE.CHAOZHOUSHI] = FR.serverURL + FR.servletURL + "?op=resource&resource=/com/fr/bi/web/js/data/map/chaozhoushi.json";
BICst.MAP_PATH[BICst.MAP_TYPE.AOMENTEBIEXINGZHENGQU] = FR.serverURL + FR.servletURL + "?op=resource&resource=/com/fr/bi/web/js/data/map/aomentebiexingzhengqu.json";
BICst.MAP_PATH[BICst.MAP_TYPE.PUYANGSHI] = FR.serverURL + FR.servletURL + "?op=resource&resource=/com/fr/bi/web/js/data/map/puyangshi.json";
BICst.MAP_PATH[BICst.MAP_TYPE.YANTAISHI] = FR.serverURL + FR.servletURL + "?op=resource&resource=/com/fr/bi/web/js/data/map/yantaishi.json";
BICst.MAP_PATH[BICst.MAP_TYPE.JIAOZUOSHI] = FR.serverURL + FR.servletURL + "?op=resource&resource=/com/fr/bi/web/js/data/map/jiaozuoshi.json";
BICst.MAP_PATH[BICst.MAP_TYPE.MUDANJIANGSHI] = FR.serverURL + FR.servletURL + "?op=resource&resource=/com/fr/bi/web/js/data/map/mudanjiangshi.json";
BICst.MAP_PATH[BICst.MAP_TYPE.YULINSHI1] = FR.serverURL + FR.servletURL + "?op=resource&resource=/com/fr/bi/web/js/data/map/yulinshi1.json";
BICst.MAP_PATH[BICst.MAP_TYPE.YUSHUCANGZUZIZHIZHOU] = FR.serverURL + FR.servletURL + "?op=resource&resource=/com/fr/bi/web/js/data/map/yushucangzuzizhizhou.json";
BICst.MAP_PATH[BICst.MAP_TYPE.YUXISHI] = FR.serverURL + FR.servletURL + "?op=resource&resource=/com/fr/bi/web/js/data/map/yuxishi.json";
BICst.MAP_PATH[BICst.MAP_TYPE.ZHUHAISHI] = FR.serverURL + FR.servletURL + "?op=resource&resource=/com/fr/bi/web/js/data/map/zhuhaishi.json";
BICst.MAP_PATH[BICst.MAP_TYPE.GANNANCANGZUZIZHIZHOU] = FR.serverURL + FR.servletURL + "?op=resource&resource=/com/fr/bi/web/js/data/map/gannancangzuzizhizhou.json";
BICst.MAP_PATH[BICst.MAP_TYPE.GANZICANGZUZIZHIZHOU] = FR.serverURL + FR.servletURL + "?op=resource&resource=/com/fr/bi/web/js/data/map/ganzicangzuzizhizhou.json";
BICst.MAP_PATH[BICst.MAP_TYPE.GANSUSHENG] = FR.serverURL + FR.servletURL + "?op=resource&resource=/com/fr/bi/web/js/data/map/gansusheng.json";
BICst.MAP_PATH[BICst.MAP_TYPE.BAICHENGSHI] = FR.serverURL + FR.servletURL + "?op=resource&resource=/com/fr/bi/web/js/data/map/baichengshi.json";
BICst.MAP_PATH[BICst.MAP_TYPE.BAISHANSHI] = FR.serverURL + FR.servletURL + "?op=resource&resource=/com/fr/bi/web/js/data/map/baishanshi.json";
BICst.MAP_PATH[BICst.MAP_TYPE.BAIYINSHI] = FR.serverURL + FR.servletURL + "?op=resource&resource=/com/fr/bi/web/js/data/map/baiyinshi.json";
BICst.MAP_PATH[BICst.MAP_TYPE.BAISESHI] = FR.serverURL + FR.servletURL + "?op=resource&resource=/com/fr/bi/web/js/data/map/baiseshi.json";
BICst.MAP_PATH[BICst.MAP_TYPE.YIYANGSHI] = FR.serverURL + FR.servletURL + "?op=resource&resource=/com/fr/bi/web/js/data/map/yiyangshi.json";
BICst.MAP_PATH[BICst.MAP_TYPE.YANCHENGSHI] = FR.serverURL + FR.servletURL + "?op=resource&resource=/com/fr/bi/web/js/data/map/yanchengshi.json";
BICst.MAP_PATH[BICst.MAP_TYPE.PANJINSHI] = FR.serverURL + FR.servletURL + "?op=resource&resource=/com/fr/bi/web/js/data/map/panjinshi.json";
BICst.MAP_PATH[BICst.MAP_TYPE.MEISHANSHI] = FR.serverURL + FR.servletURL + "?op=resource&resource=/com/fr/bi/web/js/data/map/meishanshi.json";
BICst.MAP_PATH[BICst.MAP_TYPE.SHIZUISHANSHI] = FR.serverURL + FR.servletURL + "?op=resource&resource=/com/fr/bi/web/js/data/map/shizuishanshi.json";
BICst.MAP_PATH[BICst.MAP_TYPE.SHIJIAZHUANGSHI] = FR.serverURL + FR.servletURL + "?op=resource&resource=/com/fr/bi/web/js/data/map/shijiazhuangshi.json";
BICst.MAP_PATH[BICst.MAP_TYPE.FUZHOUSHI1] = FR.serverURL + FR.servletURL + "?op=resource&resource=/com/fr/bi/web/js/data/map/fuzhoushi1.json";
BICst.MAP_PATH[BICst.MAP_TYPE.FUJIANSHENG] = FR.serverURL + FR.servletURL + "?op=resource&resource=/com/fr/bi/web/js/data/map/fujiansheng.json";
BICst.MAP_PATH[BICst.MAP_TYPE.QINHUANGDAOSHI] = FR.serverURL + FR.servletURL + "?op=resource&resource=/com/fr/bi/web/js/data/map/qinhuangdaoshi.json";
BICst.MAP_PATH[BICst.MAP_TYPE.HONGHEHANIZUYIZUZIZHIZHOU] = FR.serverURL + FR.servletURL + "?op=resource&resource=/com/fr/bi/web/js/data/map/honghehanizuyizuzizhizhou.json";
BICst.MAP_PATH[BICst.MAP_TYPE.SHAOXINGSHI] = FR.serverURL + FR.servletURL + "?op=resource&resource=/com/fr/bi/web/js/data/map/shaoxingshi.json";
BICst.MAP_PATH[BICst.MAP_TYPE.SUIHUASHI] = FR.serverURL + FR.servletURL + "?op=resource&resource=/com/fr/bi/web/js/data/map/suihuashi.json";
BICst.MAP_PATH[BICst.MAP_TYPE.MIANYANGSHI] = FR.serverURL + FR.servletURL + "?op=resource&resource=/com/fr/bi/web/js/data/map/mianyangshi.json";
BICst.MAP_PATH[BICst.MAP_TYPE.LIAOCHENGSHI] = FR.serverURL + FR.servletURL + "?op=resource&resource=/com/fr/bi/web/js/data/map/liaochengshi.json";
BICst.MAP_PATH[BICst.MAP_TYPE.ZHAOQINGSHI] = FR.serverURL + FR.servletURL + "?op=resource&resource=/com/fr/bi/web/js/data/map/zhaoqingshi.json";
BICst.MAP_PATH[BICst.MAP_TYPE.ZIGONGSHI] = FR.serverURL + FR.servletURL + "?op=resource&resource=/com/fr/bi/web/js/data/map/zigongshi.json";
BICst.MAP_PATH[BICst.MAP_TYPE.ZHOUSHANSHI] = FR.serverURL + FR.servletURL + "?op=resource&resource=/com/fr/bi/web/js/data/map/zhoushanshi.json";
BICst.MAP_PATH[BICst.MAP_TYPE.WUHUSHI] = FR.serverURL + FR.servletURL + "?op=resource&resource=/com/fr/bi/web/js/data/map/wuhushi.json";
BICst.MAP_PATH[BICst.MAP_TYPE.SUZHOUSHI1] = FR.serverURL + FR.servletURL + "?op=resource&resource=/com/fr/bi/web/js/data/map/suzhoushi1.json";
BICst.MAP_PATH[BICst.MAP_TYPE.MAOMINGSHI] = FR.serverURL + FR.servletURL + "?op=resource&resource=/com/fr/bi/web/js/data/map/maomingshi.json";
BICst.MAP_PATH[BICst.MAP_TYPE.JINGZHOUSHI] = FR.serverURL + FR.servletURL + "?op=resource&resource=/com/fr/bi/web/js/data/map/jingzhoushi.json";
BICst.MAP_PATH[BICst.MAP_TYPE.JINGMENSHI] = FR.serverURL + FR.servletURL + "?op=resource&resource=/com/fr/bi/web/js/data/map/jingmenshi.json";
BICst.MAP_PATH[BICst.MAP_TYPE.PUTIANSHI] = FR.serverURL + FR.servletURL + "?op=resource&resource=/com/fr/bi/web/js/data/map/putianshi.json";
BICst.MAP_PATH[BICst.MAP_TYPE.LAIWUSHI] = FR.serverURL + FR.servletURL + "?op=resource&resource=/com/fr/bi/web/js/data/map/laiwushi.json";
BICst.MAP_PATH[BICst.MAP_TYPE.HEZESHI] = FR.serverURL + FR.servletURL + "?op=resource&resource=/com/fr/bi/web/js/data/map/hezeshi.json";
BICst.MAP_PATH[BICst.MAP_TYPE.PINGXIANGSHI] = FR.serverURL + FR.servletURL + "?op=resource&resource=/com/fr/bi/web/js/data/map/pingxiangshi.json";
BICst.MAP_PATH[BICst.MAP_TYPE.YINGKOUSHI] = FR.serverURL + FR.servletURL + "?op=resource&resource=/com/fr/bi/web/js/data/map/yingkoushi.json";
BICst.MAP_PATH[BICst.MAP_TYPE.HULUDAOSHI] = FR.serverURL + FR.servletURL + "?op=resource&resource=/com/fr/bi/web/js/data/map/huludaoshi.json";
BICst.MAP_PATH[BICst.MAP_TYPE.BENGBUSHI] = FR.serverURL + FR.servletURL + "?op=resource&resource=/com/fr/bi/web/js/data/map/bengbushi.json";
BICst.MAP_PATH[BICst.MAP_TYPE.HENGSHUISHI] = FR.serverURL + FR.servletURL + "?op=resource&resource=/com/fr/bi/web/js/data/map/hengshuishi.json";
BICst.MAP_PATH[BICst.MAP_TYPE.HENGYANGSHI] = FR.serverURL + FR.servletURL + "?op=resource&resource=/com/fr/bi/web/js/data/map/hengyangshi.json";
BICst.MAP_PATH[BICst.MAP_TYPE.QUZHOUSHI] = FR.serverURL + FR.servletURL + "?op=resource&resource=/com/fr/bi/web/js/data/map/quzhoushi.json";
BICst.MAP_PATH[BICst.MAP_TYPE.XIANGFANSHI] = FR.serverURL + FR.servletURL + "?op=resource&resource=/com/fr/bi/web/js/data/map/xiangfanshi.json";
BICst.MAP_PATH[BICst.MAP_TYPE.XISHUANGBANNADAIZUZIZHIZHOU] = FR.serverURL + FR.servletURL + "?op=resource&resource=/com/fr/bi/web/js/data/map/xishuangbannadaizuzizhizhou.json";
BICst.MAP_PATH[BICst.MAP_TYPE.XININGSHI] = FR.serverURL + FR.servletURL + "?op=resource&resource=/com/fr/bi/web/js/data/map/xiningshi.json";
BICst.MAP_PATH[BICst.MAP_TYPE.XIANSHI] = FR.serverURL + FR.servletURL + "?op=resource&resource=/com/fr/bi/web/js/data/map/xianshi.json";
BICst.MAP_PATH[BICst.MAP_TYPE.XICANGZIZHIQU] = FR.serverURL + FR.servletURL + "?op=resource&resource=/com/fr/bi/web/js/data/map/xicangzizhiqu.json";
BICst.MAP_PATH[BICst.MAP_TYPE.XUCHANGSHI] = FR.serverURL + FR.servletURL + "?op=resource&resource=/com/fr/bi/web/js/data/map/xuchangshi.json";
BICst.MAP_PATH[BICst.MAP_TYPE.GUIZHOUSHENG] = FR.serverURL + FR.servletURL + "?op=resource&resource=/com/fr/bi/web/js/data/map/guizhousheng.json";
BICst.MAP_PATH[BICst.MAP_TYPE.GUIGANGSHI] = FR.serverURL + FR.servletURL + "?op=resource&resource=/com/fr/bi/web/js/data/map/guigangshi.json";
BICst.MAP_PATH[BICst.MAP_TYPE.GUIYANGSHI] = FR.serverURL + FR.servletURL + "?op=resource&resource=/com/fr/bi/web/js/data/map/guiyangshi.json";
BICst.MAP_PATH[BICst.MAP_TYPE.HEZHOUSHI] = FR.serverURL + FR.servletURL + "?op=resource&resource=/com/fr/bi/web/js/data/map/hezhoushi.json";
BICst.MAP_PATH[BICst.MAP_TYPE.ZIYANGSHI] = FR.serverURL + FR.servletURL + "?op=resource&resource=/com/fr/bi/web/js/data/map/ziyangshi.json";
BICst.MAP_PATH[BICst.MAP_TYPE.GANZHOUSHI] = FR.serverURL + FR.servletURL + "?op=resource&resource=/com/fr/bi/web/js/data/map/ganzhoushi.json";
BICst.MAP_PATH[BICst.MAP_TYPE.CHIFENGSHI] = FR.serverURL + FR.servletURL + "?op=resource&resource=/com/fr/bi/web/js/data/map/chifengshi.json";
BICst.MAP_PATH[BICst.MAP_TYPE.LIAONINGSHENG] = FR.serverURL + FR.servletURL + "?op=resource&resource=/com/fr/bi/web/js/data/map/liaoningsheng.json";
BICst.MAP_PATH[BICst.MAP_TYPE.LIAOYUANSHI] = FR.serverURL + FR.servletURL + "?op=resource&resource=/com/fr/bi/web/js/data/map/liaoyuanshi.json";
BICst.MAP_PATH[BICst.MAP_TYPE.LIAOYANGSHI] = FR.serverURL + FR.servletURL + "?op=resource&resource=/com/fr/bi/web/js/data/map/liaoyangshi.json";
BICst.MAP_PATH[BICst.MAP_TYPE.DAZHOUSHI] = FR.serverURL + FR.servletURL + "?op=resource&resource=/com/fr/bi/web/js/data/map/dazhoushi.json";
BICst.MAP_PATH[BICst.MAP_TYPE.YUNCHENGSHI] = FR.serverURL + FR.servletURL + "?op=resource&resource=/com/fr/bi/web/js/data/map/yunchengshi.json";
BICst.MAP_PATH[BICst.MAP_TYPE.LIANYUNGANGSHI] = FR.serverURL + FR.servletURL + "?op=resource&resource=/com/fr/bi/web/js/data/map/lianyungangshi.json";
BICst.MAP_PATH[BICst.MAP_TYPE.DIQINGCANGZUZIZHIZHOU] = FR.serverURL + FR.servletURL + "?op=resource&resource=/com/fr/bi/web/js/data/map/diqingcangzuzizhizhou.json";
BICst.MAP_PATH[BICst.MAP_TYPE.TONGHUASHI] = FR.serverURL + FR.servletURL + "?op=resource&resource=/com/fr/bi/web/js/data/map/tonghuashi.json";
BICst.MAP_PATH[BICst.MAP_TYPE.TONGLIAOSHI] = FR.serverURL + FR.servletURL + "?op=resource&resource=/com/fr/bi/web/js/data/map/tongliaoshi.json";
BICst.MAP_PATH[BICst.MAP_TYPE.SUININGSHI] = FR.serverURL + FR.servletURL + "?op=resource&resource=/com/fr/bi/web/js/data/map/suiningshi.json";
BICst.MAP_PATH[BICst.MAP_TYPE.ZUNYISHI] = FR.serverURL + FR.servletURL + "?op=resource&resource=/com/fr/bi/web/js/data/map/zunyishi.json";
BICst.MAP_PATH[BICst.MAP_TYPE.XINGTAISHI] = FR.serverURL + FR.servletURL + "?op=resource&resource=/com/fr/bi/web/js/data/map/xingtaishi.json";
BICst.MAP_PATH[BICst.MAP_TYPE.NAQUDIQU] = FR.serverURL + FR.servletURL + "?op=resource&resource=/com/fr/bi/web/js/data/map/naqudiqu.json";
BICst.MAP_PATH[BICst.MAP_TYPE.HANDANSHI] = FR.serverURL + FR.servletURL + "?op=resource&resource=/com/fr/bi/web/js/data/map/handanshi.json";
BICst.MAP_PATH[BICst.MAP_TYPE.SHAOYANGSHI] = FR.serverURL + FR.servletURL + "?op=resource&resource=/com/fr/bi/web/js/data/map/shaoyangshi.json";
BICst.MAP_PATH[BICst.MAP_TYPE.ZHENGZHOUSHI] = FR.serverURL + FR.servletURL + "?op=resource&resource=/com/fr/bi/web/js/data/map/zhengzhoushi.json";
BICst.MAP_PATH[BICst.MAP_TYPE.CHENZHOUSHI] = FR.serverURL + FR.servletURL + "?op=resource&resource=/com/fr/bi/web/js/data/map/chenzhoushi.json";
BICst.MAP_PATH[BICst.MAP_TYPE.EERDUOSISHI] = FR.serverURL + FR.servletURL + "?op=resource&resource=/com/fr/bi/web/js/data/map/eerduosishi.json";
BICst.MAP_PATH[BICst.MAP_TYPE.EZHOUSHI] = FR.serverURL + FR.servletURL + "?op=resource&resource=/com/fr/bi/web/js/data/map/ezhoushi.json";
BICst.MAP_PATH[BICst.MAP_TYPE.JIUQUANSHI] = FR.serverURL + FR.servletURL + "?op=resource&resource=/com/fr/bi/web/js/data/map/jiuquanshi.json";
BICst.MAP_PATH[BICst.MAP_TYPE.CHONGQINGSHI] = FR.serverURL + FR.servletURL + "?op=resource&resource=/com/fr/bi/web/js/data/map/chongqingshi.json";
BICst.MAP_PATH[BICst.MAP_TYPE.JINHUASHI] = FR.serverURL + FR.servletURL + "?op=resource&resource=/com/fr/bi/web/js/data/map/jinhuashi.json";
BICst.MAP_PATH[BICst.MAP_TYPE.JINCHANGSHI] = FR.serverURL + FR.servletURL + "?op=resource&resource=/com/fr/bi/web/js/data/map/jinchangshi.json";
BICst.MAP_PATH[BICst.MAP_TYPE.QINZHOUSHI] = FR.serverURL + FR.servletURL + "?op=resource&resource=/com/fr/bi/web/js/data/map/qinzhoushi.json";
BICst.MAP_PATH[BICst.MAP_TYPE.TIELINGSHI] = FR.serverURL + FR.servletURL + "?op=resource&resource=/com/fr/bi/web/js/data/map/tielingshi.json";
BICst.MAP_PATH[BICst.MAP_TYPE.TONGRENDIQU] = FR.serverURL + FR.servletURL + "?op=resource&resource=/com/fr/bi/web/js/data/map/tongrendiqu.json";
BICst.MAP_PATH[BICst.MAP_TYPE.TONGCHUANSHI] = FR.serverURL + FR.servletURL + "?op=resource&resource=/com/fr/bi/web/js/data/map/tongchuanshi.json";
BICst.MAP_PATH[BICst.MAP_TYPE.TONGLINGSHI] = FR.serverURL + FR.servletURL + "?op=resource&resource=/com/fr/bi/web/js/data/map/tonglingshi.json";
BICst.MAP_PATH[BICst.MAP_TYPE.YINCHUANSHI] = FR.serverURL + FR.servletURL + "?op=resource&resource=/com/fr/bi/web/js/data/map/yinchuanshi.json";
BICst.MAP_PATH[BICst.MAP_TYPE.XILINGUOLEMENG] = FR.serverURL + FR.servletURL + "?op=resource&resource=/com/fr/bi/web/js/data/map/xilinguolemeng.json";
BICst.MAP_PATH[BICst.MAP_TYPE.JINZHOUSHI] = FR.serverURL + FR.servletURL + "?op=resource&resource=/com/fr/bi/web/js/data/map/jinzhoushi.json";
BICst.MAP_PATH[BICst.MAP_TYPE.ZHENJIANGSHI] = FR.serverURL + FR.servletURL + "?op=resource&resource=/com/fr/bi/web/js/data/map/zhenjiangshi.json";
BICst.MAP_PATH[BICst.MAP_TYPE.CHANGCHUNSHI] = FR.serverURL + FR.servletURL + "?op=resource&resource=/com/fr/bi/web/js/data/map/changchunshi.json";
BICst.MAP_PATH[BICst.MAP_TYPE.CHANGSHASHI] = FR.serverURL + FR.servletURL + "?op=resource&resource=/com/fr/bi/web/js/data/map/changshashi.json";
BICst.MAP_PATH[BICst.MAP_TYPE.CHANGZHISHI] = FR.serverURL + FR.servletURL + "?op=resource&resource=/com/fr/bi/web/js/data/map/changzhishi.json";
BICst.MAP_PATH[BICst.MAP_TYPE.FUXINSHI] = FR.serverURL + FR.servletURL + "?op=resource&resource=/com/fr/bi/web/js/data/map/fuxinshi.json";
BICst.MAP_PATH[BICst.MAP_TYPE.FUYANGSHI] = FR.serverURL + FR.servletURL + "?op=resource&resource=/com/fr/bi/web/js/data/map/fuyangshi.json";
BICst.MAP_PATH[BICst.MAP_TYPE.FANGCHENGGANGSHI] = FR.serverURL + FR.servletURL + "?op=resource&resource=/com/fr/bi/web/js/data/map/fangchenggangshi.json";
BICst.MAP_PATH[BICst.MAP_TYPE.YANGJIANGSHI] = FR.serverURL + FR.servletURL + "?op=resource&resource=/com/fr/bi/web/js/data/map/yangjiangshi.json";
BICst.MAP_PATH[BICst.MAP_TYPE.YANGQUANSHI] = FR.serverURL + FR.servletURL + "?op=resource&resource=/com/fr/bi/web/js/data/map/yangquanshi.json";
BICst.MAP_PATH[BICst.MAP_TYPE.AKESUDIQU] = FR.serverURL + FR.servletURL + "?op=resource&resource=/com/fr/bi/web/js/data/map/akesudiqu.json";
BICst.MAP_PATH[BICst.MAP_TYPE.ALETAIDIQU] = FR.serverURL + FR.servletURL + "?op=resource&resource=/com/fr/bi/web/js/data/map/aletaidiqu.json";
BICst.MAP_PATH[BICst.MAP_TYPE.ABACANGZUQIANGZUZIZHIZHOU] = FR.serverURL + FR.servletURL + "?op=resource&resource=/com/fr/bi/web/js/data/map/abacangzuqiangzuzizhizhou.json";
BICst.MAP_PATH[BICst.MAP_TYPE.ALASHANMENG] = FR.serverURL + FR.servletURL + "?op=resource&resource=/com/fr/bi/web/js/data/map/alashanmeng.json";
BICst.MAP_PATH[BICst.MAP_TYPE.ALIDIQU] = FR.serverURL + FR.servletURL + "?op=resource&resource=/com/fr/bi/web/js/data/map/alidiqu.json";
BICst.MAP_PATH[BICst.MAP_TYPE.LONGNANSHI] = FR.serverURL + FR.servletURL + "?op=resource&resource=/com/fr/bi/web/js/data/map/longnanshi.json";
BICst.MAP_PATH[BICst.MAP_TYPE.SHANXISHENG1] = FR.serverURL + FR.servletURL + "?op=resource&resource=/com/fr/bi/web/js/data/map/shanxisheng1.json";
BICst.MAP_PATH[BICst.MAP_TYPE.SUIZHOUSHI] = FR.serverURL + FR.servletURL + "?op=resource&resource=/com/fr/bi/web/js/data/map/suizhoushi.json";
BICst.MAP_PATH[BICst.MAP_TYPE.YAANSHI] = FR.serverURL + FR.servletURL + "?op=resource&resource=/com/fr/bi/web/js/data/map/yaanshi.json";
BICst.MAP_PATH[BICst.MAP_TYPE.QINGDAOSHI] = FR.serverURL + FR.servletURL + "?op=resource&resource=/com/fr/bi/web/js/data/map/qingdaoshi.json";
BICst.MAP_PATH[BICst.MAP_TYPE.QINGHAISHENG] = FR.serverURL + FR.servletURL + "?op=resource&resource=/com/fr/bi/web/js/data/map/qinghaisheng.json";
BICst.MAP_PATH[BICst.MAP_TYPE.ANSHANSHI] = FR.serverURL + FR.servletURL + "?op=resource&resource=/com/fr/bi/web/js/data/map/anshanshi.json";
BICst.MAP_PATH[BICst.MAP_TYPE.SHAOGUANSHI] = FR.serverURL + FR.servletURL + "?op=resource&resource=/com/fr/bi/web/js/data/map/shaoguanshi.json";
BICst.MAP_PATH[BICst.MAP_TYPE.XIANGGANGTEBIEXINGZHENGQU] = FR.serverURL + FR.servletURL + "?op=resource&resource=/com/fr/bi/web/js/data/map/xianggangtebiexingzhengqu.json";
BICst.MAP_PATH[BICst.MAP_TYPE.MAANSHANSHI] = FR.serverURL + FR.servletURL + "?op=resource&resource=/com/fr/bi/web/js/data/map/maanshanshi.json";
BICst.MAP_PATH[BICst.MAP_TYPE.ZHUMADIANSHI] = FR.serverURL + FR.servletURL + "?op=resource&resource=/com/fr/bi/web/js/data/map/zhumadianshi.json";
BICst.MAP_PATH[BICst.MAP_TYPE.JIXISHI] = FR.serverURL + FR.servletURL + "?op=resource&resource=/com/fr/bi/web/js/data/map/jixishi.json";
BICst.MAP_PATH[BICst.MAP_TYPE.HEBISHI] = FR.serverURL + FR.servletURL + "?op=resource&resource=/com/fr/bi/web/js/data/map/hebishi.json";
BICst.MAP_PATH[BICst.MAP_TYPE.HEGANGSHI] = FR.serverURL + FR.servletURL + "?op=resource&resource=/com/fr/bi/web/js/data/map/hegangshi.json";
BICst.MAP_PATH[BICst.MAP_TYPE.YINGTANSHI] = FR.serverURL + FR.servletURL + "?op=resource&resource=/com/fr/bi/web/js/data/map/yingtanshi.json";
BICst.MAP_PATH[BICst.MAP_TYPE.HUANGGANGSHI] = FR.serverURL + FR.servletURL + "?op=resource&resource=/com/fr/bi/web/js/data/map/huanggangshi.json";
BICst.MAP_PATH[BICst.MAP_TYPE.HUANGNANCANGZUZIZHIZHOU] = FR.serverURL + FR.servletURL + "?op=resource&resource=/com/fr/bi/web/js/data/map/huangnancangzuzizhizhou.json";
BICst.MAP_PATH[BICst.MAP_TYPE.HUANGSHANSHI] = FR.serverURL + FR.servletURL + "?op=resource&resource=/com/fr/bi/web/js/data/map/huangshanshi.json";
BICst.MAP_PATH[BICst.MAP_TYPE.HUANGSHISHI] = FR.serverURL + FR.servletURL + "?op=resource&resource=/com/fr/bi/web/js/data/map/huangshishi.json";
BICst.MAP_PATH[BICst.MAP_TYPE.HEIHESHI] = FR.serverURL + FR.servletURL + "?op=resource&resource=/com/fr/bi/web/js/data/map/heiheshi.json";
BICst.MAP_PATH[BICst.MAP_TYPE.HEILONGJIANGSHENG] = FR.serverURL + FR.servletURL + "?op=resource&resource=/com/fr/bi/web/js/data/map/heilongjiangsheng.json";
BICst.MAP_PATH[BICst.MAP_TYPE.QIANDONGNANMIAOZUDONGZUZIZHIZHOU] = FR.serverURL + FR.servletURL + "?op=resource&resource=/com/fr/bi/web/js/data/map/qiandongnanmiaozudongzuzizhizhou.json";
BICst.MAP_PATH[BICst.MAP_TYPE.QIANNANBUYIZUMIAOZUZIZHIZHOU] = FR.serverURL + FR.servletURL + "?op=resource&resource=/com/fr/bi/web/js/data/map/qiannanbuyizumiaozuzizhizhou.json";
BICst.MAP_PATH[BICst.MAP_TYPE.QIANXINANBUYIZUMIAOZUZIZHIZHOU] = FR.serverURL + FR.servletURL + "?op=resource&resource=/com/fr/bi/web/js/data/map/qianxinanbuyizumiaozuzizhizhou.json";
BICst.MAP_PATH[BICst.MAP_TYPE.QIQIHAERSHI] = FR.serverURL + FR.servletURL + "?op=resource&resource=/com/fr/bi/web/js/data/map/qiqihaershi.json";
BICst.MAP_PATH[BICst.MAP_TYPE.LONGYANSHI] = FR.serverURL + FR.servletURL + "?op=resource&resource=/com/fr/bi/web/js/data/map/longyanshi.json";
BICst.GIS_ICON_PATH = FR.serverURL + FR.servletURL + "?op=resource&resource=/com/fr/bi/web/images/icon/chartsetting/address_marker_big.png";

BICst.MAP_NAME = {};
BICst.MAP_NAME["世界"] = BICst.MAP_TYPE.WORLD;
BICst.MAP_NAME["中国"] = BICst.MAP_TYPE.CHINA;
BICst.MAP_NAME["七台河市"] = BICst.MAP_TYPE.QITAIHESHI;
BICst.MAP_NAME["三亚市"] = BICst.MAP_TYPE.SANYASHI;
BICst.MAP_NAME["三明市"] = BICst.MAP_TYPE.SANMINGSHI;
BICst.MAP_NAME["三门峡市"] = BICst.MAP_TYPE.SANMENXIASHI;
BICst.MAP_NAME["上海市"] = BICst.MAP_TYPE.SHANGHAISHI;
BICst.MAP_NAME["上饶市"] = BICst.MAP_TYPE.SHANGRAOSHI;
BICst.MAP_NAME["东莞市"] = BICst.MAP_TYPE.DONGGUANSHI;
BICst.MAP_NAME["东营市"] = BICst.MAP_TYPE.DONGYINGSHI;
BICst.MAP_NAME["中卫市"] = BICst.MAP_TYPE.ZHONGWEISHI;
BICst.MAP_NAME["中山市"] = BICst.MAP_TYPE.ZHONGSHANSHI;
BICst.MAP_NAME["临夏回族自治州"] = BICst.MAP_TYPE.LINXIAHUIZUZIZHIZHOU;
BICst.MAP_NAME["临汾市"] = BICst.MAP_TYPE.LINFENSHI;
BICst.MAP_NAME["临沂市"] = BICst.MAP_TYPE.LINYISHI;
BICst.MAP_NAME["临沧市"] = BICst.MAP_TYPE.LINCANGSHI;
BICst.MAP_NAME["丹东市"] = BICst.MAP_TYPE.DANDONGSHI;
BICst.MAP_NAME["丽水市"] = BICst.MAP_TYPE.LISHUISHI;
BICst.MAP_NAME["丽江市"] = BICst.MAP_TYPE.LIJIANGSHI;
BICst.MAP_NAME["乌兰察布市"] = BICst.MAP_TYPE.WULANCHABUSHI;
BICst.MAP_NAME["乌海市"] = BICst.MAP_TYPE.WUHAISHI;
BICst.MAP_NAME["乌鲁木齐市"] = BICst.MAP_TYPE.WULUMUQISHI;
BICst.MAP_NAME["乐山市"] = BICst.MAP_TYPE.LESHANSHI;
BICst.MAP_NAME["九江市"] = BICst.MAP_TYPE.JIUJIANGSHI;
BICst.MAP_NAME["云南省"] = BICst.MAP_TYPE.YUNNANSHENG;
BICst.MAP_NAME["云浮市"] = BICst.MAP_TYPE.YUNFUSHI;
BICst.MAP_NAME["伊春市"] = BICst.MAP_TYPE.YICHUNSHI;
BICst.MAP_NAME["伊犁哈萨克自治州"] = BICst.MAP_TYPE.YILIHASAKEZIZHIZHOU;
BICst.MAP_NAME["佛山市"] = BICst.MAP_TYPE.FOSHANSHI;
BICst.MAP_NAME["佳木斯市"] = BICst.MAP_TYPE.JIAMUSISHI;
BICst.MAP_NAME["保定市"] = BICst.MAP_TYPE.BAODINGSHI;
BICst.MAP_NAME["保山市"] = BICst.MAP_TYPE.BAOSHANSHI;
BICst.MAP_NAME["信阳市"] = BICst.MAP_TYPE.XINYANGSHI;
BICst.MAP_NAME["克孜勒苏柯尔克孜自治州"] = BICst.MAP_TYPE.KEZILESUKEERKEZIZIZHIZHOU;
BICst.MAP_NAME["克拉玛依市"] = BICst.MAP_TYPE.KELAMAYISHI;
BICst.MAP_NAME["六安市"] = BICst.MAP_TYPE.LUANSHI;
BICst.MAP_NAME["六盘水市"] = BICst.MAP_TYPE.LIUPANSHUISHI;
BICst.MAP_NAME["兰州市"] = BICst.MAP_TYPE.LANZHOUSHI;
BICst.MAP_NAME["兴安盟"] = BICst.MAP_TYPE.XINGANMENG;
BICst.MAP_NAME["内江市"] = BICst.MAP_TYPE.NEIJIANGSHI;
BICst.MAP_NAME["内蒙古自治区"] = BICst.MAP_TYPE.NEIMENGGUZIZHIQU;
BICst.MAP_NAME["凉山彝族自治州"] = BICst.MAP_TYPE.LIANGSHANYIZUZIZHIZHOU;
BICst.MAP_NAME["包头市"] = BICst.MAP_TYPE.BAOTOUSHI;
BICst.MAP_NAME["北京市"] = BICst.MAP_TYPE.BEIJINGSHI;
BICst.MAP_NAME["北海市"] = BICst.MAP_TYPE.BEIHAISHI;
BICst.MAP_NAME["十堰市"] = BICst.MAP_TYPE.SHIYANSHI;
BICst.MAP_NAME["南京市"] = BICst.MAP_TYPE.NANJINGSHI;
BICst.MAP_NAME["南充市"] = BICst.MAP_TYPE.NANCHONGSHI;
BICst.MAP_NAME["南宁市"] = BICst.MAP_TYPE.NANNINGSHI;
BICst.MAP_NAME["南平市"] = BICst.MAP_TYPE.NANPINGSHI;
BICst.MAP_NAME["南昌市"] = BICst.MAP_TYPE.NANCHANGSHI;
BICst.MAP_NAME["南通市"] = BICst.MAP_TYPE.NANTONGSHI;
BICst.MAP_NAME["南阳市"] = BICst.MAP_TYPE.NANYANGSHI;
BICst.MAP_NAME["博尔塔拉蒙古自治州"] = BICst.MAP_TYPE.BOERTALAMENGGUZIZHIZHOU;
BICst.MAP_NAME["厦门市"] = BICst.MAP_TYPE.XIAMENSHI;
BICst.MAP_NAME["双鸭山市"] = BICst.MAP_TYPE.SHUANGYASHANSHI;
BICst.MAP_NAME["台州市"] = BICst.MAP_TYPE.TAIZHOUSHI;
BICst.MAP_NAME["台湾省"] = BICst.MAP_TYPE.TAIWANSHENG;
BICst.MAP_NAME["合肥市"] = BICst.MAP_TYPE.HEFEISHI;
BICst.MAP_NAME["吉安市"] = BICst.MAP_TYPE.JIANSHI;
BICst.MAP_NAME["吉林市"] = BICst.MAP_TYPE.JILINSHI;
BICst.MAP_NAME["吉林省"] = BICst.MAP_TYPE.JILINSHENG;
BICst.MAP_NAME["吐鲁番地区"] = BICst.MAP_TYPE.TULUFANDIQU;
BICst.MAP_NAME["吕梁市"] = BICst.MAP_TYPE.LVLIANGSHI;
BICst.MAP_NAME["吴忠市"] = BICst.MAP_TYPE.WUZHONGSHI;
BICst.MAP_NAME["周口市"] = BICst.MAP_TYPE.ZHOUKOUSHI;
BICst.MAP_NAME["呼伦贝尔市"] = BICst.MAP_TYPE.HULUNBEIERSHI;
BICst.MAP_NAME["呼和浩特市"] = BICst.MAP_TYPE.HUHEHAOTESHI;
BICst.MAP_NAME["和田地区"] = BICst.MAP_TYPE.HETIANDIQU;
BICst.MAP_NAME["咸宁市"] = BICst.MAP_TYPE.XIANNINGSHI;
BICst.MAP_NAME["咸阳市"] = BICst.MAP_TYPE.XIANYANGSHI;
BICst.MAP_NAME["哈密地区"] = BICst.MAP_TYPE.HAMIDIQU;
BICst.MAP_NAME["哈尔滨市"] = BICst.MAP_TYPE.HAERBINSHI;
BICst.MAP_NAME["唐山市"] = BICst.MAP_TYPE.TANGSHANSHI;
BICst.MAP_NAME["商丘市"] = BICst.MAP_TYPE.SHANGQIUSHI;
BICst.MAP_NAME["商洛市"] = BICst.MAP_TYPE.SHANGLUOSHI;
BICst.MAP_NAME["喀什地区"] = BICst.MAP_TYPE.KASHENDIQU;
BICst.MAP_NAME["嘉兴市"] = BICst.MAP_TYPE.JIAXINGSHI;
BICst.MAP_NAME["嘉峪关市"] = BICst.MAP_TYPE.JIAYUGUANSHI;
BICst.MAP_NAME["四川省"] = BICst.MAP_TYPE.SICHUANSHENG;
BICst.MAP_NAME["四平市"] = BICst.MAP_TYPE.SIPINGSHI;
BICst.MAP_NAME["固原市"] = BICst.MAP_TYPE.GUYUANSHI;
BICst.MAP_NAME["塔城地区"] = BICst.MAP_TYPE.TACHENGDIQU;
BICst.MAP_NAME["大兴安岭地区"] = BICst.MAP_TYPE.DAXINGANLINGDIQU;
BICst.MAP_NAME["大同市"] = BICst.MAP_TYPE.DATONGSHI;
BICst.MAP_NAME["大庆市"] = BICst.MAP_TYPE.DAQINGSHI;
BICst.MAP_NAME["大理白族自治州"] = BICst.MAP_TYPE.DALIBAIZUZIZHIZHOU;
BICst.MAP_NAME["大连市"] = BICst.MAP_TYPE.DALIANSHI;
BICst.MAP_NAME["天水市"] = BICst.MAP_TYPE.TIANSHUISHI;
BICst.MAP_NAME["天津市"] = BICst.MAP_TYPE.TIANJINSHI;
BICst.MAP_NAME["太原市"] = BICst.MAP_TYPE.TAIYUANSHI;
BICst.MAP_NAME["威海市"] = BICst.MAP_TYPE.WEIHAISHI;
BICst.MAP_NAME["娄底市"] = BICst.MAP_TYPE.LOUDISHI;
BICst.MAP_NAME["孝感市"] = BICst.MAP_TYPE.XIAOGANSHI;
BICst.MAP_NAME["宁夏回族自治区"] = BICst.MAP_TYPE.NINGXIAHUIZUZIZHIQU;
BICst.MAP_NAME["宁德市"] = BICst.MAP_TYPE.NINGDESHI;
BICst.MAP_NAME["宁波市"] = BICst.MAP_TYPE.NINGBOSHI;
BICst.MAP_NAME["安庆市"] = BICst.MAP_TYPE.ANQINGSHI;
BICst.MAP_NAME["安康市"] = BICst.MAP_TYPE.ANKANGSHI;
BICst.MAP_NAME["安徽省"] = BICst.MAP_TYPE.ANHUISHENG;
BICst.MAP_NAME["安阳市"] = BICst.MAP_TYPE.ANYANGSHI;
BICst.MAP_NAME["安顺市"] = BICst.MAP_TYPE.ANSHUNSHI;
BICst.MAP_NAME["定西市"] = BICst.MAP_TYPE.DINGXISHI;
BICst.MAP_NAME["宜宾市"] = BICst.MAP_TYPE.YIBINSHI;
BICst.MAP_NAME["宜昌市"] = BICst.MAP_TYPE.YICHANGSHI;
BICst.MAP_NAME["宜春市"] = BICst.MAP_TYPE.YICHUNSHI1;
BICst.MAP_NAME["宝鸡市"] = BICst.MAP_TYPE.BAOJISHI;
BICst.MAP_NAME["宣城市"] = BICst.MAP_TYPE.XUANCHENGSHI;
BICst.MAP_NAME["宿州市"] = BICst.MAP_TYPE.SUZHOUSHI;
BICst.MAP_NAME["宿迁市"] = BICst.MAP_TYPE.SUQIANSHI;
BICst.MAP_NAME["山东省"] = BICst.MAP_TYPE.SHANDONGSHENG;
BICst.MAP_NAME["山南地区"] = BICst.MAP_TYPE.SHANNANDIQU;
BICst.MAP_NAME["山西省"] = BICst.MAP_TYPE.SHANXISHENG;
BICst.MAP_NAME["岳阳市"] = BICst.MAP_TYPE.YUEYANGSHI;
BICst.MAP_NAME["崇左市"] = BICst.MAP_TYPE.CHONGZUOSHI;
BICst.MAP_NAME["巴中市"] = BICst.MAP_TYPE.BAZHONGSHI;
BICst.MAP_NAME["巴彦淖尔市"] = BICst.MAP_TYPE.BAYANNAOERSHI;
BICst.MAP_NAME["巴音郭楞蒙古自治州"] = BICst.MAP_TYPE.BAYINGUOLENGMENGGUZIZHIZHOU;
BICst.MAP_NAME["常州市"] = BICst.MAP_TYPE.CHANGZHOUSHI;
BICst.MAP_NAME["常德市"] = BICst.MAP_TYPE.CHANGDESHI;
BICst.MAP_NAME["平凉市"] = BICst.MAP_TYPE.PINGLIANGSHI;
BICst.MAP_NAME["平顶山市"] = BICst.MAP_TYPE.PINGDINGSHANSHI;
BICst.MAP_NAME["广东省"] = BICst.MAP_TYPE.GUANGDONGSHENG;
BICst.MAP_NAME["广元市"] = BICst.MAP_TYPE.GUANGYUANSHI;
BICst.MAP_NAME["广安市"] = BICst.MAP_TYPE.GUANGANSHI;
BICst.MAP_NAME["广州市"] = BICst.MAP_TYPE.GUANGZHOUSHI;
BICst.MAP_NAME["广西壮族自治区"] = BICst.MAP_TYPE.GUANGXIZHUANGZUZIZHIQU;
BICst.MAP_NAME["庆阳市"] = BICst.MAP_TYPE.QINGYANGSHI;
BICst.MAP_NAME["廊坊市"] = BICst.MAP_TYPE.LANGFANGSHI;
BICst.MAP_NAME["延安市"] = BICst.MAP_TYPE.YANANSHI;
BICst.MAP_NAME["延边朝鲜族自治州"] = BICst.MAP_TYPE.YANBIANCHAOXIANZUZIZHIZHOU;
BICst.MAP_NAME["开封市"] = BICst.MAP_TYPE.KAIFENGSHI;
BICst.MAP_NAME["张家口市"] = BICst.MAP_TYPE.ZHANGJIAKOUSHI;
BICst.MAP_NAME["张家界市"] = BICst.MAP_TYPE.ZHANGJIAJIESHI;
BICst.MAP_NAME["张掖市"] = BICst.MAP_TYPE.ZHANGYESHI;
BICst.MAP_NAME["徐州市"] = BICst.MAP_TYPE.XUZHOUSHI;
BICst.MAP_NAME["德宏傣族景颇族自治州"] = BICst.MAP_TYPE.DEHONGDAIZUJINGPOZUZIZHIZHOU;
BICst.MAP_NAME["德州市"] = BICst.MAP_TYPE.DEZHOUSHI;
BICst.MAP_NAME["德阳市"] = BICst.MAP_TYPE.DEYANGSHI;
BICst.MAP_NAME["忻州市"] = BICst.MAP_TYPE.XINZHOUSHI;
BICst.MAP_NAME["怀化市"] = BICst.MAP_TYPE.HUAIHUASHI;
BICst.MAP_NAME["怒江傈僳族自治州"] = BICst.MAP_TYPE.NUJIANGLISUZUZIZHIZHOU;
BICst.MAP_NAME["恩施土家族苗族自治州"] = BICst.MAP_TYPE.ENSHITUJIAZUMIAOZUZIZHIZHOU;
BICst.MAP_NAME["惠州市"] = BICst.MAP_TYPE.HUIZHOUSHI;
BICst.MAP_NAME["成都市"] = BICst.MAP_TYPE.CHENGDUSHI;
BICst.MAP_NAME["扬州市"] = BICst.MAP_TYPE.YANGZHOUSHI;
BICst.MAP_NAME["承德市"] = BICst.MAP_TYPE.CHENGDESHI;
BICst.MAP_NAME["抚州市"] = BICst.MAP_TYPE.FUZHOUSHI;
BICst.MAP_NAME["抚顺市"] = BICst.MAP_TYPE.FUSHUNSHI;
BICst.MAP_NAME["拉萨市"] = BICst.MAP_TYPE.LASASHI;
BICst.MAP_NAME["揭阳市"] = BICst.MAP_TYPE.JIEYANGSHI;
BICst.MAP_NAME["攀枝花市"] = BICst.MAP_TYPE.PANZHIHUASHI;
BICst.MAP_NAME["文山壮族苗族自治州"] = BICst.MAP_TYPE.WENSHANZHUANGZUMIAOZUZIZHIZHOU;
BICst.MAP_NAME["新乡市"] = BICst.MAP_TYPE.XINXIANGSHI;
BICst.MAP_NAME["新余市"] = BICst.MAP_TYPE.XINYUSHI;
BICst.MAP_NAME["新疆维吾尔自治区"] = BICst.MAP_TYPE.XINJIANGWEIWUERZIZHIQU;
BICst.MAP_NAME["无锡市"] = BICst.MAP_TYPE.WUXISHI;
BICst.MAP_NAME["日喀则地区"] = BICst.MAP_TYPE.RIKAZEDIQU;
BICst.MAP_NAME["日照市"] = BICst.MAP_TYPE.RIZHAOSHI;
BICst.MAP_NAME["昆明市"] = BICst.MAP_TYPE.KUNMINGSHI;
BICst.MAP_NAME["昌吉回族自治州"] = BICst.MAP_TYPE.CHANGJIHUIZUZIZHIZHOU;
BICst.MAP_NAME["昌都地区"] = BICst.MAP_TYPE.CHANGDOUDIQU;
BICst.MAP_NAME["昭通市"] = BICst.MAP_TYPE.ZHAOTONGSHI;
BICst.MAP_NAME["晋中市"] = BICst.MAP_TYPE.JINZHONGSHI;
BICst.MAP_NAME["晋城市"] = BICst.MAP_TYPE.JINCHENGSHI;
BICst.MAP_NAME["普洱市"] = BICst.MAP_TYPE.PUERSHI;
BICst.MAP_NAME["景德镇市"] = BICst.MAP_TYPE.JINGDEZHENSHI;
BICst.MAP_NAME["曲靖市"] = BICst.MAP_TYPE.QUJINGSHI;
BICst.MAP_NAME["朔州市"] = BICst.MAP_TYPE.SHUOZHOUSHI;
BICst.MAP_NAME["朝阳市"] = BICst.MAP_TYPE.CHAOYANGSHI;
BICst.MAP_NAME["本溪市"] = BICst.MAP_TYPE.BENXISHI;
BICst.MAP_NAME["来宾市"] = BICst.MAP_TYPE.LAIBINSHI;
BICst.MAP_NAME["杭州市"] = BICst.MAP_TYPE.HANGZHOUSHI;
BICst.MAP_NAME["松原市"] = BICst.MAP_TYPE.SONGYUANSHI;
BICst.MAP_NAME["林芝地区"] = BICst.MAP_TYPE.LINZHIDIQU;
BICst.MAP_NAME["果洛藏族自治州"] = BICst.MAP_TYPE.GUOLUOCANGZUZIZHIZHOU;
BICst.MAP_NAME["枣庄市"] = BICst.MAP_TYPE.ZAOZHUANGSHI;
BICst.MAP_NAME["柳州市"] = BICst.MAP_TYPE.LIUZHOUSHI;
BICst.MAP_NAME["株洲市"] = BICst.MAP_TYPE.ZHUZHOUSHI;
BICst.MAP_NAME["桂林市"] = BICst.MAP_TYPE.GUILINSHI;
BICst.MAP_NAME["梅州市"] = BICst.MAP_TYPE.MEIZHOUSHI;
BICst.MAP_NAME["梧州市"] = BICst.MAP_TYPE.WUZHOUSHI;
BICst.MAP_NAME["楚雄彝族自治州"] = BICst.MAP_TYPE.CHUXIONGYIZUZIZHIZHOU;
BICst.MAP_NAME["榆林市"] = BICst.MAP_TYPE.YULINSHI;
BICst.MAP_NAME["武威市"] = BICst.MAP_TYPE.WUWEISHI;
BICst.MAP_NAME["武汉市"] = BICst.MAP_TYPE.WUHANSHI;
BICst.MAP_NAME["毕节地区"] = BICst.MAP_TYPE.BIJIEDIQU;
BICst.MAP_NAME["毫州市"] = BICst.MAP_TYPE.HAOZHOUSHI;
BICst.MAP_NAME["永州市"] = BICst.MAP_TYPE.YONGZHOUSHI;
BICst.MAP_NAME["汉中市"] = BICst.MAP_TYPE.HANZHONGSHI;
BICst.MAP_NAME["汕头市"] = BICst.MAP_TYPE.SHANTOUSHI;
BICst.MAP_NAME["汕尾市"] = BICst.MAP_TYPE.SHANWEISHI;
BICst.MAP_NAME["江苏省"] = BICst.MAP_TYPE.JIANGSUSHENG;
BICst.MAP_NAME["江西省"] = BICst.MAP_TYPE.JIANGXISHENG;
BICst.MAP_NAME["江门市"] = BICst.MAP_TYPE.JIANGMENSHI;
BICst.MAP_NAME["池州市"] = BICst.MAP_TYPE.CHIZHOUSHI;
BICst.MAP_NAME["沈阳市"] = BICst.MAP_TYPE.SHENYANGSHI;
BICst.MAP_NAME["沧州市"] = BICst.MAP_TYPE.CANGZHOUSHI;
BICst.MAP_NAME["河北省"] = BICst.MAP_TYPE.HEBEISHENG;
BICst.MAP_NAME["河南省"] = BICst.MAP_TYPE.HENANSHENG;
BICst.MAP_NAME["河池市"] = BICst.MAP_TYPE.HECHISHI;
BICst.MAP_NAME["河源市"] = BICst.MAP_TYPE.HEYUANSHI;
BICst.MAP_NAME["泉州市"] = BICst.MAP_TYPE.QUANZHOUSHI;
BICst.MAP_NAME["泰安市"] = BICst.MAP_TYPE.TAIANSHI;
BICst.MAP_NAME["泰州市"] = BICst.MAP_TYPE.TAIZHOUSHI1;
BICst.MAP_NAME["泸州市"] = BICst.MAP_TYPE.LUZHOUSHI;
BICst.MAP_NAME["洛阳市"] = BICst.MAP_TYPE.LUOYANGSHI;
BICst.MAP_NAME["济南市"] = BICst.MAP_TYPE.JINANSHI;
BICst.MAP_NAME["济宁市"] = BICst.MAP_TYPE.JININGSHI;
BICst.MAP_NAME["浙江省"] = BICst.MAP_TYPE.ZHEJIANGSHENG;
BICst.MAP_NAME["海东地区"] = BICst.MAP_TYPE.HAIDONGDIQU;
BICst.MAP_NAME["海北藏族自治州"] = BICst.MAP_TYPE.HAIBEICANGZUZIZHIZHOU;
BICst.MAP_NAME["海南省"] = BICst.MAP_TYPE.HAINANSHENG;
BICst.MAP_NAME["海南藏族自治州"] = BICst.MAP_TYPE.HAINANCANGZUZIZHIZHOU;
BICst.MAP_NAME["海口市"] = BICst.MAP_TYPE.HAIKOUSHI;
BICst.MAP_NAME["海西蒙古族藏族自治州"] = BICst.MAP_TYPE.HAIXIMENGGUZUCANGZUZIZHIZHOU;
BICst.MAP_NAME["淄博市"] = BICst.MAP_TYPE.ZIBOSHI;
BICst.MAP_NAME["淮北市"] = BICst.MAP_TYPE.HUAIBEISHI;
BICst.MAP_NAME["淮南市"] = BICst.MAP_TYPE.HUAINANSHI;
BICst.MAP_NAME["淮安市"] = BICst.MAP_TYPE.HUAIANSHI;
BICst.MAP_NAME["深圳市"] = BICst.MAP_TYPE.SHENZHENSHI;
BICst.MAP_NAME["清远市"] = BICst.MAP_TYPE.QINGYUANSHI;
BICst.MAP_NAME["温州市"] = BICst.MAP_TYPE.WENZHOUSHI;
BICst.MAP_NAME["渭南市"] = BICst.MAP_TYPE.WEINANSHI;
BICst.MAP_NAME["湖北省"] = BICst.MAP_TYPE.HUBEISHENG;
BICst.MAP_NAME["湖南省"] = BICst.MAP_TYPE.HUNANSHENG;
BICst.MAP_NAME["湖州市"] = BICst.MAP_TYPE.HUZHOUSHI;
BICst.MAP_NAME["湘潭市"] = BICst.MAP_TYPE.XIANGTANSHI;
BICst.MAP_NAME["湘西土家族苗族自治州"] = BICst.MAP_TYPE.XIANGXITUJIAZUMIAOZUZIZHIZHOU;
BICst.MAP_NAME["湛江市"] = BICst.MAP_TYPE.ZHANJIANGSHI;
BICst.MAP_NAME["滁州市"] = BICst.MAP_TYPE.CHUZHOUSHI;
BICst.MAP_NAME["滨州市"] = BICst.MAP_TYPE.BINZHOUSHI;
BICst.MAP_NAME["漯河市"] = BICst.MAP_TYPE.LUOHESHI;
BICst.MAP_NAME["漳州市"] = BICst.MAP_TYPE.ZHANGZHOUSHI;
BICst.MAP_NAME["潍坊市"] = BICst.MAP_TYPE.WEIFANGSHI;
BICst.MAP_NAME["潮州市"] = BICst.MAP_TYPE.CHAOZHOUSHI;
BICst.MAP_NAME["澳门特别行政区"] = BICst.MAP_TYPE.AOMENTEBIEXINGZHENGQU;
BICst.MAP_NAME["濮阳市"] = BICst.MAP_TYPE.PUYANGSHI;
BICst.MAP_NAME["烟台市"] = BICst.MAP_TYPE.YANTAISHI;
BICst.MAP_NAME["焦作市"] = BICst.MAP_TYPE.JIAOZUOSHI;
BICst.MAP_NAME["牡丹江市"] = BICst.MAP_TYPE.MUDANJIANGSHI;
BICst.MAP_NAME["玉林市"] = BICst.MAP_TYPE.YULINSHI1;
BICst.MAP_NAME["玉树藏族自治州"] = BICst.MAP_TYPE.YUSHUCANGZUZIZHIZHOU;
BICst.MAP_NAME["玉溪市"] = BICst.MAP_TYPE.YUXISHI;
BICst.MAP_NAME["珠海市"] = BICst.MAP_TYPE.ZHUHAISHI;
BICst.MAP_NAME["甘南藏族自治州"] = BICst.MAP_TYPE.GANNANCANGZUZIZHIZHOU;
BICst.MAP_NAME["甘孜藏族自治州"] = BICst.MAP_TYPE.GANZICANGZUZIZHIZHOU;
BICst.MAP_NAME["甘肃省"] = BICst.MAP_TYPE.GANSUSHENG;
BICst.MAP_NAME["白城市"] = BICst.MAP_TYPE.BAICHENGSHI;
BICst.MAP_NAME["白山市"] = BICst.MAP_TYPE.BAISHANSHI;
BICst.MAP_NAME["白银市"] = BICst.MAP_TYPE.BAIYINSHI;
BICst.MAP_NAME["百色市"] = BICst.MAP_TYPE.BAISESHI;
BICst.MAP_NAME["益阳市"] = BICst.MAP_TYPE.YIYANGSHI;
BICst.MAP_NAME["盐城市"] = BICst.MAP_TYPE.YANCHENGSHI;
BICst.MAP_NAME["盘锦市"] = BICst.MAP_TYPE.PANJINSHI;
BICst.MAP_NAME["眉山市"] = BICst.MAP_TYPE.MEISHANSHI;
BICst.MAP_NAME["石嘴山市"] = BICst.MAP_TYPE.SHIZUISHANSHI;
BICst.MAP_NAME["石家庄市"] = BICst.MAP_TYPE.SHIJIAZHUANGSHI;
BICst.MAP_NAME["福州市"] = BICst.MAP_TYPE.FUZHOUSHI1;
BICst.MAP_NAME["福建省"] = BICst.MAP_TYPE.FUJIANSHENG;
BICst.MAP_NAME["秦皇岛市"] = BICst.MAP_TYPE.QINHUANGDAOSHI;
BICst.MAP_NAME["红河哈尼族彝族自治州"] = BICst.MAP_TYPE.HONGHEHANIZUYIZUZIZHIZHOU;
BICst.MAP_NAME["绍兴市"] = BICst.MAP_TYPE.SHAOXINGSHI;
BICst.MAP_NAME["绥化市"] = BICst.MAP_TYPE.SUIHUASHI;
BICst.MAP_NAME["绵阳市"] = BICst.MAP_TYPE.MIANYANGSHI;
BICst.MAP_NAME["聊城市"] = BICst.MAP_TYPE.LIAOCHENGSHI;
BICst.MAP_NAME["肇庆市"] = BICst.MAP_TYPE.ZHAOQINGSHI;
BICst.MAP_NAME["自贡市"] = BICst.MAP_TYPE.ZIGONGSHI;
BICst.MAP_NAME["舟山市"] = BICst.MAP_TYPE.ZHOUSHANSHI;
BICst.MAP_NAME["芜湖市"] = BICst.MAP_TYPE.WUHUSHI;
BICst.MAP_NAME["苏州市"] = BICst.MAP_TYPE.SUZHOUSHI1;
BICst.MAP_NAME["茂名市"] = BICst.MAP_TYPE.MAOMINGSHI;
BICst.MAP_NAME["荆州市"] = BICst.MAP_TYPE.JINGZHOUSHI;
BICst.MAP_NAME["荆门市"] = BICst.MAP_TYPE.JINGMENSHI;
BICst.MAP_NAME["莆田市"] = BICst.MAP_TYPE.PUTIANSHI;
BICst.MAP_NAME["莱芜市"] = BICst.MAP_TYPE.LAIWUSHI;
BICst.MAP_NAME["菏泽市"] = BICst.MAP_TYPE.HEZESHI;
BICst.MAP_NAME["萍乡市"] = BICst.MAP_TYPE.PINGXIANGSHI;
BICst.MAP_NAME["营口市"] = BICst.MAP_TYPE.YINGKOUSHI;
BICst.MAP_NAME["葫芦岛市"] = BICst.MAP_TYPE.HULUDAOSHI;
BICst.MAP_NAME["蚌埠市"] = BICst.MAP_TYPE.BENGBUSHI;
BICst.MAP_NAME["衡水市"] = BICst.MAP_TYPE.HENGSHUISHI;
BICst.MAP_NAME["衡阳市"] = BICst.MAP_TYPE.HENGYANGSHI;
BICst.MAP_NAME["衢州市"] = BICst.MAP_TYPE.QUZHOUSHI;
BICst.MAP_NAME["襄樊市"] = BICst.MAP_TYPE.XIANGFANSHI;
BICst.MAP_NAME["西双版纳傣族自治州"] = BICst.MAP_TYPE.XISHUANGBANNADAIZUZIZHIZHOU;
BICst.MAP_NAME["西宁市"] = BICst.MAP_TYPE.XININGSHI;
BICst.MAP_NAME["西安市"] = BICst.MAP_TYPE.XIANSHI;
BICst.MAP_NAME["西藏自治区"] = BICst.MAP_TYPE.XICANGZIZHIQU;
BICst.MAP_NAME["许昌市"] = BICst.MAP_TYPE.XUCHANGSHI;
BICst.MAP_NAME["贵州省"] = BICst.MAP_TYPE.GUIZHOUSHENG;
BICst.MAP_NAME["贵港市"] = BICst.MAP_TYPE.GUIGANGSHI;
BICst.MAP_NAME["贵阳市"] = BICst.MAP_TYPE.GUIYANGSHI;
BICst.MAP_NAME["贺州市"] = BICst.MAP_TYPE.HEZHOUSHI;
BICst.MAP_NAME["资阳市"] = BICst.MAP_TYPE.ZIYANGSHI;
BICst.MAP_NAME["赣州市"] = BICst.MAP_TYPE.GANZHOUSHI;
BICst.MAP_NAME["赤峰市"] = BICst.MAP_TYPE.CHIFENGSHI;
BICst.MAP_NAME["辽宁省"] = BICst.MAP_TYPE.LIAONINGSHENG;
BICst.MAP_NAME["辽源市"] = BICst.MAP_TYPE.LIAOYUANSHI;
BICst.MAP_NAME["辽阳市"] = BICst.MAP_TYPE.LIAOYANGSHI;
BICst.MAP_NAME["达州市"] = BICst.MAP_TYPE.DAZHOUSHI;
BICst.MAP_NAME["运城市"] = BICst.MAP_TYPE.YUNCHENGSHI;
BICst.MAP_NAME["连云港市"] = BICst.MAP_TYPE.LIANYUNGANGSHI;
BICst.MAP_NAME["迪庆藏族自治州"] = BICst.MAP_TYPE.DIQINGCANGZUZIZHIZHOU;
BICst.MAP_NAME["通化市"] = BICst.MAP_TYPE.TONGHUASHI;
BICst.MAP_NAME["通辽市"] = BICst.MAP_TYPE.TONGLIAOSHI;
BICst.MAP_NAME["遂宁市"] = BICst.MAP_TYPE.SUININGSHI;
BICst.MAP_NAME["遵义市"] = BICst.MAP_TYPE.ZUNYISHI;
BICst.MAP_NAME["邢台市"] = BICst.MAP_TYPE.XINGTAISHI;
BICst.MAP_NAME["那曲地区"] = BICst.MAP_TYPE.NAQUDIQU;
BICst.MAP_NAME["邯郸市"] = BICst.MAP_TYPE.HANDANSHI;
BICst.MAP_NAME["邵阳市"] = BICst.MAP_TYPE.SHAOYANGSHI;
BICst.MAP_NAME["郑州市"] = BICst.MAP_TYPE.ZHENGZHOUSHI;
BICst.MAP_NAME["郴州市"] = BICst.MAP_TYPE.CHENZHOUSHI;
BICst.MAP_NAME["鄂尔多斯市"] = BICst.MAP_TYPE.EERDUOSISHI;
BICst.MAP_NAME["鄂州市"] = BICst.MAP_TYPE.EZHOUSHI;
BICst.MAP_NAME["酒泉市"] = BICst.MAP_TYPE.JIUQUANSHI;
BICst.MAP_NAME["重庆市"] = BICst.MAP_TYPE.CHONGQINGSHI;
BICst.MAP_NAME["金华市"] = BICst.MAP_TYPE.JINHUASHI;
BICst.MAP_NAME["金昌市"] = BICst.MAP_TYPE.JINCHANGSHI;
BICst.MAP_NAME["钦州市"] = BICst.MAP_TYPE.QINZHOUSHI;
BICst.MAP_NAME["铁岭市"] = BICst.MAP_TYPE.TIELINGSHI;
BICst.MAP_NAME["铜仁地区"] = BICst.MAP_TYPE.TONGRENDIQU;
BICst.MAP_NAME["铜川市"] = BICst.MAP_TYPE.TONGCHUANSHI;
BICst.MAP_NAME["铜陵市"] = BICst.MAP_TYPE.TONGLINGSHI;
BICst.MAP_NAME["银川市"] = BICst.MAP_TYPE.YINCHUANSHI;
BICst.MAP_NAME["锡林郭勒盟"] = BICst.MAP_TYPE.XILINGUOLEMENG;
BICst.MAP_NAME["锦州市"] = BICst.MAP_TYPE.JINZHOUSHI;
BICst.MAP_NAME["镇江市"] = BICst.MAP_TYPE.ZHENJIANGSHI;
BICst.MAP_NAME["长春市"] = BICst.MAP_TYPE.CHANGCHUNSHI;
BICst.MAP_NAME["长沙市"] = BICst.MAP_TYPE.CHANGSHASHI;
BICst.MAP_NAME["长治市"] = BICst.MAP_TYPE.CHANGZHISHI;
BICst.MAP_NAME["阜新市"] = BICst.MAP_TYPE.FUXINSHI;
BICst.MAP_NAME["阜阳市"] = BICst.MAP_TYPE.FUYANGSHI;
BICst.MAP_NAME["防城港市"] = BICst.MAP_TYPE.FANGCHENGGANGSHI;
BICst.MAP_NAME["阳江市"] = BICst.MAP_TYPE.YANGJIANGSHI;
BICst.MAP_NAME["阳泉市"] = BICst.MAP_TYPE.YANGQUANSHI;
BICst.MAP_NAME["阿克苏地区"] = BICst.MAP_TYPE.AKESUDIQU;
BICst.MAP_NAME["阿勒泰地区"] = BICst.MAP_TYPE.ALETAIDIQU;
BICst.MAP_NAME["阿坝藏族羌族自治州"] = BICst.MAP_TYPE.ABACANGZUQIANGZUZIZHIZHOU;
BICst.MAP_NAME["阿拉善盟"] = BICst.MAP_TYPE.ALASHANMENG;
BICst.MAP_NAME["阿里地区"] = BICst.MAP_TYPE.ALIDIQU;
BICst.MAP_NAME["陇南市"] = BICst.MAP_TYPE.LONGNANSHI;
BICst.MAP_NAME["陕西省"] = BICst.MAP_TYPE.SHANXISHENG1;
BICst.MAP_NAME["随州市"] = BICst.MAP_TYPE.SUIZHOUSHI;
BICst.MAP_NAME["雅安市"] = BICst.MAP_TYPE.YAANSHI;
BICst.MAP_NAME["青岛市"] = BICst.MAP_TYPE.QINGDAOSHI;
BICst.MAP_NAME["青海省"] = BICst.MAP_TYPE.QINGHAISHENG;
BICst.MAP_NAME["鞍山市"] = BICst.MAP_TYPE.ANSHANSHI;
BICst.MAP_NAME["韶关市"] = BICst.MAP_TYPE.SHAOGUANSHI;
BICst.MAP_NAME["香港特别行政区"] = BICst.MAP_TYPE.XIANGGANGTEBIEXINGZHENGQU;
BICst.MAP_NAME["马鞍山市"] = BICst.MAP_TYPE.MAANSHANSHI;
BICst.MAP_NAME["驻马店市"] = BICst.MAP_TYPE.ZHUMADIANSHI;
BICst.MAP_NAME["鸡西市"] = BICst.MAP_TYPE.JIXISHI;
BICst.MAP_NAME["鹤壁市"] = BICst.MAP_TYPE.HEBISHI;
BICst.MAP_NAME["鹤岗市"] = BICst.MAP_TYPE.HEGANGSHI;
BICst.MAP_NAME["鹰潭市"] = BICst.MAP_TYPE.YINGTANSHI;
BICst.MAP_NAME["黄冈市"] = BICst.MAP_TYPE.HUANGGANGSHI;
BICst.MAP_NAME["黄南藏族自治州"] = BICst.MAP_TYPE.HUANGNANCANGZUZIZHIZHOU;
BICst.MAP_NAME["黄山市"] = BICst.MAP_TYPE.HUANGSHANSHI;
BICst.MAP_NAME["黄石市"] = BICst.MAP_TYPE.HUANGSHISHI;
BICst.MAP_NAME["黑河市"] = BICst.MAP_TYPE.HEIHESHI;
BICst.MAP_NAME["黑龙江省"] = BICst.MAP_TYPE.HEILONGJIANGSHENG;
BICst.MAP_NAME["黔东南苗族侗族自治州"] = BICst.MAP_TYPE.QIANDONGNANMIAOZUDONGZUZIZHIZHOU;
BICst.MAP_NAME["黔南布依族苗族自治州"] = BICst.MAP_TYPE.QIANNANBUYIZUMIAOZUZIZHIZHOU;
BICst.MAP_NAME["黔西南布依族苗族自治州"] = BICst.MAP_TYPE.QIANXINANBUYIZUMIAOZUZIZHIZHOU;
BICst.MAP_NAME["齐齐哈尔市"] = BICst.MAP_TYPE.QIQIHAERSHI;
BICst.MAP_NAME["龙岩市"] = BICst.MAP_TYPE.LONGYANSHI;

BICst.MAP_TYPE_NAME = {};
BICst.MAP_TYPE_NAME[BICst.MAP_TYPE.WORLD] = "世界";
BICst.MAP_TYPE_NAME[BICst.MAP_TYPE.CHINA] = "中国";
BICst.MAP_TYPE_NAME[BICst.MAP_TYPE.QITAIHESHI] = "七台河市";
BICst.MAP_TYPE_NAME[BICst.MAP_TYPE.SANYASHI] = "三亚市";
BICst.MAP_TYPE_NAME[BICst.MAP_TYPE.SANMINGSHI] = "三明市";
BICst.MAP_TYPE_NAME[BICst.MAP_TYPE.SANMENXIASHI] = "三门峡市";
BICst.MAP_TYPE_NAME[BICst.MAP_TYPE.SHANGHAISHI] = "上海市";
BICst.MAP_TYPE_NAME[BICst.MAP_TYPE.SHANGRAOSHI] = "上饶市";
BICst.MAP_TYPE_NAME[BICst.MAP_TYPE.DONGGUANSHI] = "东莞市";
BICst.MAP_TYPE_NAME[BICst.MAP_TYPE.DONGYINGSHI] = "东营市";
BICst.MAP_TYPE_NAME[BICst.MAP_TYPE.ZHONGWEISHI] = "中卫市";
BICst.MAP_TYPE_NAME[BICst.MAP_TYPE.ZHONGSHANSHI] = "中山市";
BICst.MAP_TYPE_NAME[BICst.MAP_TYPE.LINXIAHUIZUZIZHIZHOU] = "临夏回族自治州";
BICst.MAP_TYPE_NAME[BICst.MAP_TYPE.LINFENSHI] = "临汾市";
BICst.MAP_TYPE_NAME[BICst.MAP_TYPE.LINYISHI] = "临沂市";
BICst.MAP_TYPE_NAME[BICst.MAP_TYPE.LINCANGSHI] = "临沧市";
BICst.MAP_TYPE_NAME[BICst.MAP_TYPE.DANDONGSHI] = "丹东市";
BICst.MAP_TYPE_NAME[BICst.MAP_TYPE.LISHUISHI] = "丽水市";
BICst.MAP_TYPE_NAME[BICst.MAP_TYPE.LIJIANGSHI] = "丽江市";
BICst.MAP_TYPE_NAME[BICst.MAP_TYPE.WULANCHABUSHI] = "乌兰察布市";
BICst.MAP_TYPE_NAME[BICst.MAP_TYPE.WUHAISHI] = "乌海市";
BICst.MAP_TYPE_NAME[BICst.MAP_TYPE.WULUMUQISHI] = "乌鲁木齐市";
BICst.MAP_TYPE_NAME[BICst.MAP_TYPE.LESHANSHI] = "乐山市";
BICst.MAP_TYPE_NAME[BICst.MAP_TYPE.JIUJIANGSHI] = "九江市";
BICst.MAP_TYPE_NAME[BICst.MAP_TYPE.YUNNANSHENG] = "云南省";
BICst.MAP_TYPE_NAME[BICst.MAP_TYPE.YUNFUSHI] = "云浮市";
BICst.MAP_TYPE_NAME[BICst.MAP_TYPE.YICHUNSHI] = "伊春市";
BICst.MAP_TYPE_NAME[BICst.MAP_TYPE.YILIHASAKEZIZHIZHOU] = "伊犁哈萨克自治州";
BICst.MAP_TYPE_NAME[BICst.MAP_TYPE.FOSHANSHI] = "佛山市";
BICst.MAP_TYPE_NAME[BICst.MAP_TYPE.JIAMUSISHI] = "佳木斯市";
BICst.MAP_TYPE_NAME[BICst.MAP_TYPE.BAODINGSHI] = "保定市";
BICst.MAP_TYPE_NAME[BICst.MAP_TYPE.BAOSHANSHI] = "保山市";
BICst.MAP_TYPE_NAME[BICst.MAP_TYPE.XINYANGSHI] = "信阳市";
BICst.MAP_TYPE_NAME[BICst.MAP_TYPE.KEZILESUKEERKEZIZIZHIZHOU] = "克孜勒苏柯尔克孜自治州";
BICst.MAP_TYPE_NAME[BICst.MAP_TYPE.KELAMAYISHI] = "克拉玛依市";
BICst.MAP_TYPE_NAME[BICst.MAP_TYPE.LUANSHI] = "六安市";
BICst.MAP_TYPE_NAME[BICst.MAP_TYPE.LIUPANSHUISHI] = "六盘水市";
BICst.MAP_TYPE_NAME[BICst.MAP_TYPE.LANZHOUSHI] = "兰州市";
BICst.MAP_TYPE_NAME[BICst.MAP_TYPE.XINGANMENG] = "兴安盟";
BICst.MAP_TYPE_NAME[BICst.MAP_TYPE.NEIJIANGSHI] = "内江市";
BICst.MAP_TYPE_NAME[BICst.MAP_TYPE.NEIMENGGUZIZHIQU] = "内蒙古自治区";
BICst.MAP_TYPE_NAME[BICst.MAP_TYPE.LIANGSHANYIZUZIZHIZHOU] = "凉山彝族自治州";
BICst.MAP_TYPE_NAME[BICst.MAP_TYPE.BAOTOUSHI] = "包头市";
BICst.MAP_TYPE_NAME[BICst.MAP_TYPE.BEIJINGSHI] = "北京市";
BICst.MAP_TYPE_NAME[BICst.MAP_TYPE.BEIHAISHI] = "北海市";
BICst.MAP_TYPE_NAME[BICst.MAP_TYPE.SHIYANSHI] = "十堰市";
BICst.MAP_TYPE_NAME[BICst.MAP_TYPE.NANJINGSHI] = "南京市";
BICst.MAP_TYPE_NAME[BICst.MAP_TYPE.NANCHONGSHI] = "南充市";
BICst.MAP_TYPE_NAME[BICst.MAP_TYPE.NANNINGSHI] = "南宁市";
BICst.MAP_TYPE_NAME[BICst.MAP_TYPE.NANPINGSHI] = "南平市";
BICst.MAP_TYPE_NAME[BICst.MAP_TYPE.NANCHANGSHI] = "南昌市";
BICst.MAP_TYPE_NAME[BICst.MAP_TYPE.NANTONGSHI] = "南通市";
BICst.MAP_TYPE_NAME[BICst.MAP_TYPE.NANYANGSHI] = "南阳市";
BICst.MAP_TYPE_NAME[BICst.MAP_TYPE.BOERTALAMENGGUZIZHIZHOU] = "博尔塔拉蒙古自治州";
BICst.MAP_TYPE_NAME[BICst.MAP_TYPE.XIAMENSHI] = "厦门市";
BICst.MAP_TYPE_NAME[BICst.MAP_TYPE.SHUANGYASHANSHI] = "双鸭山市";
BICst.MAP_TYPE_NAME[BICst.MAP_TYPE.TAIZHOUSHI] = "台州市";
BICst.MAP_TYPE_NAME[BICst.MAP_TYPE.TAIWANSHENG] = "台湾省";
BICst.MAP_TYPE_NAME[BICst.MAP_TYPE.HEFEISHI] = "合肥市";
BICst.MAP_TYPE_NAME[BICst.MAP_TYPE.JIANSHI] = "吉安市";
BICst.MAP_TYPE_NAME[BICst.MAP_TYPE.JILINSHI] = "吉林市";
BICst.MAP_TYPE_NAME[BICst.MAP_TYPE.JILINSHENG] = "吉林省";
BICst.MAP_TYPE_NAME[BICst.MAP_TYPE.TULUFANDIQU] = "吐鲁番地区";
BICst.MAP_TYPE_NAME[BICst.MAP_TYPE.LVLIANGSHI] = "吕梁市";
BICst.MAP_TYPE_NAME[BICst.MAP_TYPE.WUZHONGSHI] = "吴忠市";
BICst.MAP_TYPE_NAME[BICst.MAP_TYPE.ZHOUKOUSHI] = "周口市";
BICst.MAP_TYPE_NAME[BICst.MAP_TYPE.HULUNBEIERSHI] = "呼伦贝尔市";
BICst.MAP_TYPE_NAME[BICst.MAP_TYPE.HUHEHAOTESHI] = "呼和浩特市";
BICst.MAP_TYPE_NAME[BICst.MAP_TYPE.HETIANDIQU] = "和田地区";
BICst.MAP_TYPE_NAME[BICst.MAP_TYPE.XIANNINGSHI] = "咸宁市";
BICst.MAP_TYPE_NAME[BICst.MAP_TYPE.XIANYANGSHI] = "咸阳市";
BICst.MAP_TYPE_NAME[BICst.MAP_TYPE.HAMIDIQU] = "哈密地区";
BICst.MAP_TYPE_NAME[BICst.MAP_TYPE.HAERBINSHI] = "哈尔滨市";
BICst.MAP_TYPE_NAME[BICst.MAP_TYPE.TANGSHANSHI] = "唐山市";
BICst.MAP_TYPE_NAME[BICst.MAP_TYPE.SHANGQIUSHI] = "商丘市";
BICst.MAP_TYPE_NAME[BICst.MAP_TYPE.SHANGLUOSHI] = "商洛市";
BICst.MAP_TYPE_NAME[BICst.MAP_TYPE.KASHENDIQU] = "喀什地区";
BICst.MAP_TYPE_NAME[BICst.MAP_TYPE.JIAXINGSHI] = "嘉兴市";
BICst.MAP_TYPE_NAME[BICst.MAP_TYPE.JIAYUGUANSHI] = "嘉峪关市";
BICst.MAP_TYPE_NAME[BICst.MAP_TYPE.SICHUANSHENG] = "四川省";
BICst.MAP_TYPE_NAME[BICst.MAP_TYPE.SIPINGSHI] = "四平市";
BICst.MAP_TYPE_NAME[BICst.MAP_TYPE.GUYUANSHI] = "固原市";
BICst.MAP_TYPE_NAME[BICst.MAP_TYPE.TACHENGDIQU] = "塔城地区";
BICst.MAP_TYPE_NAME[BICst.MAP_TYPE.DAXINGANLINGDIQU] = "大兴安岭地区";
BICst.MAP_TYPE_NAME[BICst.MAP_TYPE.DATONGSHI] = "大同市";
BICst.MAP_TYPE_NAME[BICst.MAP_TYPE.DAQINGSHI] = "大庆市";
BICst.MAP_TYPE_NAME[BICst.MAP_TYPE.DALIBAIZUZIZHIZHOU] = "大理白族自治州";
BICst.MAP_TYPE_NAME[BICst.MAP_TYPE.DALIANSHI] = "大连市";
BICst.MAP_TYPE_NAME[BICst.MAP_TYPE.TIANSHUISHI] = "天水市";
BICst.MAP_TYPE_NAME[BICst.MAP_TYPE.TIANJINSHI] = "天津市";
BICst.MAP_TYPE_NAME[BICst.MAP_TYPE.TAIYUANSHI] = "太原市";
BICst.MAP_TYPE_NAME[BICst.MAP_TYPE.WEIHAISHI] = "威海市";
BICst.MAP_TYPE_NAME[BICst.MAP_TYPE.LOUDISHI] = "娄底市";
BICst.MAP_TYPE_NAME[BICst.MAP_TYPE.XIAOGANSHI] = "孝感市";
BICst.MAP_TYPE_NAME[BICst.MAP_TYPE.NINGXIAHUIZUZIZHIQU] = "宁夏回族自治区";
BICst.MAP_TYPE_NAME[BICst.MAP_TYPE.NINGDESHI] = "宁德市";
BICst.MAP_TYPE_NAME[BICst.MAP_TYPE.NINGBOSHI] = "宁波市";
BICst.MAP_TYPE_NAME[BICst.MAP_TYPE.ANQINGSHI] = "安庆市";
BICst.MAP_TYPE_NAME[BICst.MAP_TYPE.ANKANGSHI] = "安康市";
BICst.MAP_TYPE_NAME[BICst.MAP_TYPE.ANHUISHENG] = "安徽省";
BICst.MAP_TYPE_NAME[BICst.MAP_TYPE.ANYANGSHI] = "安阳市";
BICst.MAP_TYPE_NAME[BICst.MAP_TYPE.ANSHUNSHI] = "安顺市";
BICst.MAP_TYPE_NAME[BICst.MAP_TYPE.DINGXISHI] = "定西市";
BICst.MAP_TYPE_NAME[BICst.MAP_TYPE.YIBINSHI] = "宜宾市";
BICst.MAP_TYPE_NAME[BICst.MAP_TYPE.YICHANGSHI] = "宜昌市";
BICst.MAP_TYPE_NAME[BICst.MAP_TYPE.YICHUNSHI1] = "宜春市";
BICst.MAP_TYPE_NAME[BICst.MAP_TYPE.BAOJISHI] = "宝鸡市";
BICst.MAP_TYPE_NAME[BICst.MAP_TYPE.XUANCHENGSHI] = "宣城市";
BICst.MAP_TYPE_NAME[BICst.MAP_TYPE.SUZHOUSHI] = "宿州市";
BICst.MAP_TYPE_NAME[BICst.MAP_TYPE.SUQIANSHI] = "宿迁市";
BICst.MAP_TYPE_NAME[BICst.MAP_TYPE.SHANDONGSHENG] = "山东省";
BICst.MAP_TYPE_NAME[BICst.MAP_TYPE.SHANNANDIQU] = "山南地区";
BICst.MAP_TYPE_NAME[BICst.MAP_TYPE.SHANXISHENG] = "山西省";
BICst.MAP_TYPE_NAME[BICst.MAP_TYPE.YUEYANGSHI] = "岳阳市";
BICst.MAP_TYPE_NAME[BICst.MAP_TYPE.CHONGZUOSHI] = "崇左市";
BICst.MAP_TYPE_NAME[BICst.MAP_TYPE.BAZHONGSHI] = "巴中市";
BICst.MAP_TYPE_NAME[BICst.MAP_TYPE.BAYANNAOERSHI] = "巴彦淖尔市";
BICst.MAP_TYPE_NAME[BICst.MAP_TYPE.BAYINGUOLENGMENGGUZIZHIZHOU] = "巴音郭楞蒙古自治州";
BICst.MAP_TYPE_NAME[BICst.MAP_TYPE.CHANGZHOUSHI] = "常州市";
BICst.MAP_TYPE_NAME[BICst.MAP_TYPE.CHANGDESHI] = "常德市";
BICst.MAP_TYPE_NAME[BICst.MAP_TYPE.PINGLIANGSHI] = "平凉市";
BICst.MAP_TYPE_NAME[BICst.MAP_TYPE.PINGDINGSHANSHI] = "平顶山市";
BICst.MAP_TYPE_NAME[BICst.MAP_TYPE.GUANGDONGSHENG] = "广东省";
BICst.MAP_TYPE_NAME[BICst.MAP_TYPE.GUANGYUANSHI] = "广元市";
BICst.MAP_TYPE_NAME[BICst.MAP_TYPE.GUANGANSHI] = "广安市";
BICst.MAP_TYPE_NAME[BICst.MAP_TYPE.GUANGZHOUSHI] = "广州市";
BICst.MAP_TYPE_NAME[BICst.MAP_TYPE.GUANGXIZHUANGZUZIZHIQU] = "广西壮族自治区";
BICst.MAP_TYPE_NAME[BICst.MAP_TYPE.QINGYANGSHI] = "庆阳市";
BICst.MAP_TYPE_NAME[BICst.MAP_TYPE.LANGFANGSHI] = "廊坊市";
BICst.MAP_TYPE_NAME[BICst.MAP_TYPE.YANANSHI] = "延安市";
BICst.MAP_TYPE_NAME[BICst.MAP_TYPE.YANBIANCHAOXIANZUZIZHIZHOU] = "延边朝鲜族自治州";
BICst.MAP_TYPE_NAME[BICst.MAP_TYPE.KAIFENGSHI] = "开封市";
BICst.MAP_TYPE_NAME[BICst.MAP_TYPE.ZHANGJIAKOUSHI] = "张家口市";
BICst.MAP_TYPE_NAME[BICst.MAP_TYPE.ZHANGJIAJIESHI] = "张家界市";
BICst.MAP_TYPE_NAME[BICst.MAP_TYPE.ZHANGYESHI] = "张掖市";
BICst.MAP_TYPE_NAME[BICst.MAP_TYPE.XUZHOUSHI] = "徐州市";
BICst.MAP_TYPE_NAME[BICst.MAP_TYPE.DEHONGDAIZUJINGPOZUZIZHIZHOU] = "德宏傣族景颇族自治州";
BICst.MAP_TYPE_NAME[BICst.MAP_TYPE.DEZHOUSHI] = "德州市";
BICst.MAP_TYPE_NAME[BICst.MAP_TYPE.DEYANGSHI] = "德阳市";
BICst.MAP_TYPE_NAME[BICst.MAP_TYPE.XINZHOUSHI] = "忻州市";
BICst.MAP_TYPE_NAME[BICst.MAP_TYPE.HUAIHUASHI] = "怀化市";
BICst.MAP_TYPE_NAME[BICst.MAP_TYPE.NUJIANGLISUZUZIZHIZHOU] = "怒江傈僳族自治州";
BICst.MAP_TYPE_NAME[BICst.MAP_TYPE.ENSHITUJIAZUMIAOZUZIZHIZHOU] = "恩施土家族苗族自治州";
BICst.MAP_TYPE_NAME[BICst.MAP_TYPE.HUIZHOUSHI] = "惠州市";
BICst.MAP_TYPE_NAME[BICst.MAP_TYPE.CHENGDUSHI] = "成都市";
BICst.MAP_TYPE_NAME[BICst.MAP_TYPE.YANGZHOUSHI] = "扬州市";
BICst.MAP_TYPE_NAME[BICst.MAP_TYPE.CHENGDESHI] = "承德市";
BICst.MAP_TYPE_NAME[BICst.MAP_TYPE.FUZHOUSHI] = "抚州市";
BICst.MAP_TYPE_NAME[BICst.MAP_TYPE.FUSHUNSHI] = "抚顺市";
BICst.MAP_TYPE_NAME[BICst.MAP_TYPE.LASASHI] = "拉萨市";
BICst.MAP_TYPE_NAME[BICst.MAP_TYPE.JIEYANGSHI] = "揭阳市";
BICst.MAP_TYPE_NAME[BICst.MAP_TYPE.PANZHIHUASHI] = "攀枝花市";
BICst.MAP_TYPE_NAME[BICst.MAP_TYPE.WENSHANZHUANGZUMIAOZUZIZHIZHOU] = "文山壮族苗族自治州";
BICst.MAP_TYPE_NAME[BICst.MAP_TYPE.XINXIANGSHI] = "新乡市";
BICst.MAP_TYPE_NAME[BICst.MAP_TYPE.XINYUSHI] = "新余市";
BICst.MAP_TYPE_NAME[BICst.MAP_TYPE.XINJIANGWEIWUERZIZHIQU] = "新疆维吾尔自治区";
BICst.MAP_TYPE_NAME[BICst.MAP_TYPE.WUXISHI] = "无锡市";
BICst.MAP_TYPE_NAME[BICst.MAP_TYPE.RIKAZEDIQU] = "日喀则地区";
BICst.MAP_TYPE_NAME[BICst.MAP_TYPE.RIZHAOSHI] = "日照市";
BICst.MAP_TYPE_NAME[BICst.MAP_TYPE.KUNMINGSHI] = "昆明市";
BICst.MAP_TYPE_NAME[BICst.MAP_TYPE.CHANGJIHUIZUZIZHIZHOU] = "昌吉回族自治州";
BICst.MAP_TYPE_NAME[BICst.MAP_TYPE.CHANGDOUDIQU] = "昌都地区";
BICst.MAP_TYPE_NAME[BICst.MAP_TYPE.ZHAOTONGSHI] = "昭通市";
BICst.MAP_TYPE_NAME[BICst.MAP_TYPE.JINZHONGSHI] = "晋中市";
BICst.MAP_TYPE_NAME[BICst.MAP_TYPE.JINCHENGSHI] = "晋城市";
BICst.MAP_TYPE_NAME[BICst.MAP_TYPE.PUERSHI] = "普洱市";
BICst.MAP_TYPE_NAME[BICst.MAP_TYPE.JINGDEZHENSHI] = "景德镇市";
BICst.MAP_TYPE_NAME[BICst.MAP_TYPE.QUJINGSHI] = "曲靖市";
BICst.MAP_TYPE_NAME[BICst.MAP_TYPE.SHUOZHOUSHI] = "朔州市";
BICst.MAP_TYPE_NAME[BICst.MAP_TYPE.CHAOYANGSHI] = "朝阳市";
BICst.MAP_TYPE_NAME[BICst.MAP_TYPE.BENXISHI] = "本溪市";
BICst.MAP_TYPE_NAME[BICst.MAP_TYPE.LAIBINSHI] = "来宾市";
BICst.MAP_TYPE_NAME[BICst.MAP_TYPE.HANGZHOUSHI] = "杭州市";
BICst.MAP_TYPE_NAME[BICst.MAP_TYPE.SONGYUANSHI] = "松原市";
BICst.MAP_TYPE_NAME[BICst.MAP_TYPE.LINZHIDIQU] = "林芝地区";
BICst.MAP_TYPE_NAME[BICst.MAP_TYPE.GUOLUOCANGZUZIZHIZHOU] = "果洛藏族自治州";
BICst.MAP_TYPE_NAME[BICst.MAP_TYPE.ZAOZHUANGSHI] = "枣庄市";
BICst.MAP_TYPE_NAME[BICst.MAP_TYPE.LIUZHOUSHI] = "柳州市";
BICst.MAP_TYPE_NAME[BICst.MAP_TYPE.ZHUZHOUSHI] = "株洲市";
BICst.MAP_TYPE_NAME[BICst.MAP_TYPE.GUILINSHI] = "桂林市";
BICst.MAP_TYPE_NAME[BICst.MAP_TYPE.MEIZHOUSHI] = "梅州市";
BICst.MAP_TYPE_NAME[BICst.MAP_TYPE.WUZHOUSHI] = "梧州市";
BICst.MAP_TYPE_NAME[BICst.MAP_TYPE.CHUXIONGYIZUZIZHIZHOU] = "楚雄彝族自治州";
BICst.MAP_TYPE_NAME[BICst.MAP_TYPE.YULINSHI] = "榆林市";
BICst.MAP_TYPE_NAME[BICst.MAP_TYPE.WUWEISHI] = "武威市";
BICst.MAP_TYPE_NAME[BICst.MAP_TYPE.WUHANSHI] = "武汉市";
BICst.MAP_TYPE_NAME[BICst.MAP_TYPE.BIJIEDIQU] = "毕节地区";
BICst.MAP_TYPE_NAME[BICst.MAP_TYPE.HAOZHOUSHI] = "毫州市";
BICst.MAP_TYPE_NAME[BICst.MAP_TYPE.YONGZHOUSHI] = "永州市";
BICst.MAP_TYPE_NAME[BICst.MAP_TYPE.HANZHONGSHI] = "汉中市";
BICst.MAP_TYPE_NAME[BICst.MAP_TYPE.SHANTOUSHI] = "汕头市";
BICst.MAP_TYPE_NAME[BICst.MAP_TYPE.SHANWEISHI] = "汕尾市";
BICst.MAP_TYPE_NAME[BICst.MAP_TYPE.JIANGSUSHENG] = "江苏省";
BICst.MAP_TYPE_NAME[BICst.MAP_TYPE.JIANGXISHENG] = "江西省";
BICst.MAP_TYPE_NAME[BICst.MAP_TYPE.JIANGMENSHI] = "江门市";
BICst.MAP_TYPE_NAME[BICst.MAP_TYPE.CHIZHOUSHI] = "池州市";
BICst.MAP_TYPE_NAME[BICst.MAP_TYPE.SHENYANGSHI] = "沈阳市";
BICst.MAP_TYPE_NAME[BICst.MAP_TYPE.CANGZHOUSHI] = "沧州市";
BICst.MAP_TYPE_NAME[BICst.MAP_TYPE.HEBEISHENG] = "河北省";
BICst.MAP_TYPE_NAME[BICst.MAP_TYPE.HENANSHENG] = "河南省";
BICst.MAP_TYPE_NAME[BICst.MAP_TYPE.HECHISHI] = "河池市";
BICst.MAP_TYPE_NAME[BICst.MAP_TYPE.HEYUANSHI] = "河源市";
BICst.MAP_TYPE_NAME[BICst.MAP_TYPE.QUANZHOUSHI] = "泉州市";
BICst.MAP_TYPE_NAME[BICst.MAP_TYPE.TAIANSHI] = "泰安市";
BICst.MAP_TYPE_NAME[BICst.MAP_TYPE.TAIZHOUSHI1] = "泰州市";
BICst.MAP_TYPE_NAME[BICst.MAP_TYPE.LUZHOUSHI] = "泸州市";
BICst.MAP_TYPE_NAME[BICst.MAP_TYPE.LUOYANGSHI] = "洛阳市";
BICst.MAP_TYPE_NAME[BICst.MAP_TYPE.JINANSHI] = "济南市";
BICst.MAP_TYPE_NAME[BICst.MAP_TYPE.JININGSHI] = "济宁市";
BICst.MAP_TYPE_NAME[BICst.MAP_TYPE.ZHEJIANGSHENG] = "浙江省";
BICst.MAP_TYPE_NAME[BICst.MAP_TYPE.HAIDONGDIQU] = "海东地区";
BICst.MAP_TYPE_NAME[BICst.MAP_TYPE.HAIBEICANGZUZIZHIZHOU] = "海北藏族自治州";
BICst.MAP_TYPE_NAME[BICst.MAP_TYPE.HAINANSHENG] = "海南省";
BICst.MAP_TYPE_NAME[BICst.MAP_TYPE.HAINANCANGZUZIZHIZHOU] = "海南藏族自治州";
BICst.MAP_TYPE_NAME[BICst.MAP_TYPE.HAIKOUSHI] = "海口市";
BICst.MAP_TYPE_NAME[BICst.MAP_TYPE.HAIXIMENGGUZUCANGZUZIZHIZHOU] = "海西蒙古族藏族自治州";
BICst.MAP_TYPE_NAME[BICst.MAP_TYPE.ZIBOSHI] = "淄博市";
BICst.MAP_TYPE_NAME[BICst.MAP_TYPE.HUAIBEISHI] = "淮北市";
BICst.MAP_TYPE_NAME[BICst.MAP_TYPE.HUAINANSHI] = "淮南市";
BICst.MAP_TYPE_NAME[BICst.MAP_TYPE.HUAIANSHI] = "淮安市";
BICst.MAP_TYPE_NAME[BICst.MAP_TYPE.SHENZHENSHI] = "深圳市";
BICst.MAP_TYPE_NAME[BICst.MAP_TYPE.QINGYUANSHI] = "清远市";
BICst.MAP_TYPE_NAME[BICst.MAP_TYPE.WENZHOUSHI] = "温州市";
BICst.MAP_TYPE_NAME[BICst.MAP_TYPE.WEINANSHI] = "渭南市";
BICst.MAP_TYPE_NAME[BICst.MAP_TYPE.HUBEISHENG] = "湖北省";
BICst.MAP_TYPE_NAME[BICst.MAP_TYPE.HUNANSHENG] = "湖南省";
BICst.MAP_TYPE_NAME[BICst.MAP_TYPE.HUZHOUSHI] = "湖州市";
BICst.MAP_TYPE_NAME[BICst.MAP_TYPE.XIANGTANSHI] = "湘潭市";
BICst.MAP_TYPE_NAME[BICst.MAP_TYPE.XIANGXITUJIAZUMIAOZUZIZHIZHOU] = "湘西土家族苗族自治州";
BICst.MAP_TYPE_NAME[BICst.MAP_TYPE.ZHANJIANGSHI] = "湛江市";
BICst.MAP_TYPE_NAME[BICst.MAP_TYPE.CHUZHOUSHI] = "滁州市";
BICst.MAP_TYPE_NAME[BICst.MAP_TYPE.BINZHOUSHI] = "滨州市";
BICst.MAP_TYPE_NAME[BICst.MAP_TYPE.LUOHESHI] = "漯河市";
BICst.MAP_TYPE_NAME[BICst.MAP_TYPE.ZHANGZHOUSHI] = "漳州市";
BICst.MAP_TYPE_NAME[BICst.MAP_TYPE.WEIFANGSHI] = "潍坊市";
BICst.MAP_TYPE_NAME[BICst.MAP_TYPE.CHAOZHOUSHI] = "潮州市";
BICst.MAP_TYPE_NAME[BICst.MAP_TYPE.AOMENTEBIEXINGZHENGQU] = "澳门特别行政区";
BICst.MAP_TYPE_NAME[BICst.MAP_TYPE.PUYANGSHI] = "濮阳市";
BICst.MAP_TYPE_NAME[BICst.MAP_TYPE.YANTAISHI] = "烟台市";
BICst.MAP_TYPE_NAME[BICst.MAP_TYPE.JIAOZUOSHI] = "焦作市";
BICst.MAP_TYPE_NAME[BICst.MAP_TYPE.MUDANJIANGSHI] = "牡丹江市";
BICst.MAP_TYPE_NAME[BICst.MAP_TYPE.YULINSHI1] = "玉林市";
BICst.MAP_TYPE_NAME[BICst.MAP_TYPE.YUSHUCANGZUZIZHIZHOU] = "玉树藏族自治州";
BICst.MAP_TYPE_NAME[BICst.MAP_TYPE.YUXISHI] = "玉溪市";
BICst.MAP_TYPE_NAME[BICst.MAP_TYPE.ZHUHAISHI] = "珠海市";
BICst.MAP_TYPE_NAME[BICst.MAP_TYPE.GANNANCANGZUZIZHIZHOU] = "甘南藏族自治州";
BICst.MAP_TYPE_NAME[BICst.MAP_TYPE.GANZICANGZUZIZHIZHOU] = "甘孜藏族自治州";
BICst.MAP_TYPE_NAME[BICst.MAP_TYPE.GANSUSHENG] = "甘肃省";
BICst.MAP_TYPE_NAME[BICst.MAP_TYPE.BAICHENGSHI] = "白城市";
BICst.MAP_TYPE_NAME[BICst.MAP_TYPE.BAISHANSHI] = "白山市";
BICst.MAP_TYPE_NAME[BICst.MAP_TYPE.BAIYINSHI] = "白银市";
BICst.MAP_TYPE_NAME[BICst.MAP_TYPE.BAISESHI] = "百色市";
BICst.MAP_TYPE_NAME[BICst.MAP_TYPE.YIYANGSHI] = "益阳市";
BICst.MAP_TYPE_NAME[BICst.MAP_TYPE.YANCHENGSHI] = "盐城市";
BICst.MAP_TYPE_NAME[BICst.MAP_TYPE.PANJINSHI] = "盘锦市";
BICst.MAP_TYPE_NAME[BICst.MAP_TYPE.MEISHANSHI] = "眉山市";
BICst.MAP_TYPE_NAME[BICst.MAP_TYPE.SHIZUISHANSHI] = "石嘴山市";
BICst.MAP_TYPE_NAME[BICst.MAP_TYPE.SHIJIAZHUANGSHI] = "石家庄市";
BICst.MAP_TYPE_NAME[BICst.MAP_TYPE.FUZHOUSHI1] = "福州市";
BICst.MAP_TYPE_NAME[BICst.MAP_TYPE.FUJIANSHENG] = "福建省";
BICst.MAP_TYPE_NAME[BICst.MAP_TYPE.QINHUANGDAOSHI] = "秦皇岛市";
BICst.MAP_TYPE_NAME[BICst.MAP_TYPE.HONGHEHANIZUYIZUZIZHIZHOU] = "红河哈尼族彝族自治州";
BICst.MAP_TYPE_NAME[BICst.MAP_TYPE.SHAOXINGSHI] = "绍兴市";
BICst.MAP_TYPE_NAME[BICst.MAP_TYPE.SUIHUASHI] = "绥化市";
BICst.MAP_TYPE_NAME[BICst.MAP_TYPE.MIANYANGSHI] = "绵阳市";
BICst.MAP_TYPE_NAME[BICst.MAP_TYPE.LIAOCHENGSHI] = "聊城市";
BICst.MAP_TYPE_NAME[BICst.MAP_TYPE.ZHAOQINGSHI] = "肇庆市";
BICst.MAP_TYPE_NAME[BICst.MAP_TYPE.ZIGONGSHI] = "自贡市";
BICst.MAP_TYPE_NAME[BICst.MAP_TYPE.ZHOUSHANSHI] = "舟山市";
BICst.MAP_TYPE_NAME[BICst.MAP_TYPE.WUHUSHI] = "芜湖市";
BICst.MAP_TYPE_NAME[BICst.MAP_TYPE.SUZHOUSHI1] = "苏州市";
BICst.MAP_TYPE_NAME[BICst.MAP_TYPE.MAOMINGSHI] = "茂名市";
BICst.MAP_TYPE_NAME[BICst.MAP_TYPE.JINGZHOUSHI] = "荆州市";
BICst.MAP_TYPE_NAME[BICst.MAP_TYPE.JINGMENSHI] = "荆门市";
BICst.MAP_TYPE_NAME[BICst.MAP_TYPE.PUTIANSHI] = "莆田市";
BICst.MAP_TYPE_NAME[BICst.MAP_TYPE.LAIWUSHI] = "莱芜市";
BICst.MAP_TYPE_NAME[BICst.MAP_TYPE.HEZESHI] = "菏泽市";
BICst.MAP_TYPE_NAME[BICst.MAP_TYPE.PINGXIANGSHI] = "萍乡市";
BICst.MAP_TYPE_NAME[BICst.MAP_TYPE.YINGKOUSHI] = "营口市";
BICst.MAP_TYPE_NAME[BICst.MAP_TYPE.HULUDAOSHI] = "葫芦岛市";
BICst.MAP_TYPE_NAME[BICst.MAP_TYPE.BENGBUSHI] = "蚌埠市";
BICst.MAP_TYPE_NAME[BICst.MAP_TYPE.HENGSHUISHI] = "衡水市";
BICst.MAP_TYPE_NAME[BICst.MAP_TYPE.HENGYANGSHI] = "衡阳市";
BICst.MAP_TYPE_NAME[BICst.MAP_TYPE.QUZHOUSHI] = "衢州市";
BICst.MAP_TYPE_NAME[BICst.MAP_TYPE.XIANGFANSHI] = "襄樊市";
BICst.MAP_TYPE_NAME[BICst.MAP_TYPE.XISHUANGBANNADAIZUZIZHIZHOU] = "西双版纳傣族自治州";
BICst.MAP_TYPE_NAME[BICst.MAP_TYPE.XININGSHI] = "西宁市";
BICst.MAP_TYPE_NAME[BICst.MAP_TYPE.XIANSHI] = "西安市";
BICst.MAP_TYPE_NAME[BICst.MAP_TYPE.XICANGZIZHIQU] = "西藏自治区";
BICst.MAP_TYPE_NAME[BICst.MAP_TYPE.XUCHANGSHI] = "许昌市";
BICst.MAP_TYPE_NAME[BICst.MAP_TYPE.GUIZHOUSHENG] = "贵州省";
BICst.MAP_TYPE_NAME[BICst.MAP_TYPE.GUIGANGSHI] = "贵港市";
BICst.MAP_TYPE_NAME[BICst.MAP_TYPE.GUIYANGSHI] = "贵阳市";
BICst.MAP_TYPE_NAME[BICst.MAP_TYPE.HEZHOUSHI] = "贺州市";
BICst.MAP_TYPE_NAME[BICst.MAP_TYPE.ZIYANGSHI] = "资阳市";
BICst.MAP_TYPE_NAME[BICst.MAP_TYPE.GANZHOUSHI] = "赣州市";
BICst.MAP_TYPE_NAME[BICst.MAP_TYPE.CHIFENGSHI] = "赤峰市";
BICst.MAP_TYPE_NAME[BICst.MAP_TYPE.LIAONINGSHENG] = "辽宁省";
BICst.MAP_TYPE_NAME[BICst.MAP_TYPE.LIAOYUANSHI] = "辽源市";
BICst.MAP_TYPE_NAME[BICst.MAP_TYPE.LIAOYANGSHI] = "辽阳市";
BICst.MAP_TYPE_NAME[BICst.MAP_TYPE.DAZHOUSHI] = "达州市";
BICst.MAP_TYPE_NAME[BICst.MAP_TYPE.YUNCHENGSHI] = "运城市";
BICst.MAP_TYPE_NAME[BICst.MAP_TYPE.LIANYUNGANGSHI] = "连云港市";
BICst.MAP_TYPE_NAME[BICst.MAP_TYPE.DIQINGCANGZUZIZHIZHOU] = "迪庆藏族自治州";
BICst.MAP_TYPE_NAME[BICst.MAP_TYPE.TONGHUASHI] = "通化市";
BICst.MAP_TYPE_NAME[BICst.MAP_TYPE.TONGLIAOSHI] = "通辽市";
BICst.MAP_TYPE_NAME[BICst.MAP_TYPE.SUININGSHI] = "遂宁市";
BICst.MAP_TYPE_NAME[BICst.MAP_TYPE.ZUNYISHI] = "遵义市";
BICst.MAP_TYPE_NAME[BICst.MAP_TYPE.XINGTAISHI] = "邢台市";
BICst.MAP_TYPE_NAME[BICst.MAP_TYPE.NAQUDIQU] = "那曲地区";
BICst.MAP_TYPE_NAME[BICst.MAP_TYPE.HANDANSHI] = "邯郸市";
BICst.MAP_TYPE_NAME[BICst.MAP_TYPE.SHAOYANGSHI] = "邵阳市";
BICst.MAP_TYPE_NAME[BICst.MAP_TYPE.ZHENGZHOUSHI] = "郑州市";
BICst.MAP_TYPE_NAME[BICst.MAP_TYPE.CHENZHOUSHI] = "郴州市";
BICst.MAP_TYPE_NAME[BICst.MAP_TYPE.EERDUOSISHI] = "鄂尔多斯市";
BICst.MAP_TYPE_NAME[BICst.MAP_TYPE.EZHOUSHI] = "鄂州市";
BICst.MAP_TYPE_NAME[BICst.MAP_TYPE.JIUQUANSHI] = "酒泉市";
BICst.MAP_TYPE_NAME[BICst.MAP_TYPE.CHONGQINGSHI] = "重庆市";
BICst.MAP_TYPE_NAME[BICst.MAP_TYPE.JINHUASHI] = "金华市";
BICst.MAP_TYPE_NAME[BICst.MAP_TYPE.JINCHANGSHI] = "金昌市";
BICst.MAP_TYPE_NAME[BICst.MAP_TYPE.QINZHOUSHI] = "钦州市";
BICst.MAP_TYPE_NAME[BICst.MAP_TYPE.TIELINGSHI] = "铁岭市";
BICst.MAP_TYPE_NAME[BICst.MAP_TYPE.TONGRENDIQU] = "铜仁地区";
BICst.MAP_TYPE_NAME[BICst.MAP_TYPE.TONGCHUANSHI] = "铜川市";
BICst.MAP_TYPE_NAME[BICst.MAP_TYPE.TONGLINGSHI] = "铜陵市";
BICst.MAP_TYPE_NAME[BICst.MAP_TYPE.YINCHUANSHI] = "银川市";
BICst.MAP_TYPE_NAME[BICst.MAP_TYPE.XILINGUOLEMENG] = "锡林郭勒盟";
BICst.MAP_TYPE_NAME[BICst.MAP_TYPE.JINZHOUSHI] = "锦州市";
BICst.MAP_TYPE_NAME[BICst.MAP_TYPE.ZHENJIANGSHI] = "镇江市";
BICst.MAP_TYPE_NAME[BICst.MAP_TYPE.CHANGCHUNSHI] = "长春市";
BICst.MAP_TYPE_NAME[BICst.MAP_TYPE.CHANGSHASHI] = "长沙市";
BICst.MAP_TYPE_NAME[BICst.MAP_TYPE.CHANGZHISHI] = "长治市";
BICst.MAP_TYPE_NAME[BICst.MAP_TYPE.FUXINSHI] = "阜新市";
BICst.MAP_TYPE_NAME[BICst.MAP_TYPE.FUYANGSHI] = "阜阳市";
BICst.MAP_TYPE_NAME[BICst.MAP_TYPE.FANGCHENGGANGSHI] = "防城港市";
BICst.MAP_TYPE_NAME[BICst.MAP_TYPE.YANGJIANGSHI] = "阳江市";
BICst.MAP_TYPE_NAME[BICst.MAP_TYPE.YANGQUANSHI] = "阳泉市";
BICst.MAP_TYPE_NAME[BICst.MAP_TYPE.AKESUDIQU] = "阿克苏地区";
BICst.MAP_TYPE_NAME[BICst.MAP_TYPE.ALETAIDIQU] = "阿勒泰地区";
BICst.MAP_TYPE_NAME[BICst.MAP_TYPE.ABACANGZUQIANGZUZIZHIZHOU] = "阿坝藏族羌族自治州";
BICst.MAP_TYPE_NAME[BICst.MAP_TYPE.ALASHANMENG] = "阿拉善盟";
BICst.MAP_TYPE_NAME[BICst.MAP_TYPE.ALIDIQU] = "阿里地区";
BICst.MAP_TYPE_NAME[BICst.MAP_TYPE.LONGNANSHI] = "陇南市";
BICst.MAP_TYPE_NAME[BICst.MAP_TYPE.SHANXISHENG1] = "陕西省";
BICst.MAP_TYPE_NAME[BICst.MAP_TYPE.SUIZHOUSHI] = "随州市";
BICst.MAP_TYPE_NAME[BICst.MAP_TYPE.YAANSHI] = "雅安市";
BICst.MAP_TYPE_NAME[BICst.MAP_TYPE.QINGDAOSHI] = "青岛市";
BICst.MAP_TYPE_NAME[BICst.MAP_TYPE.QINGHAISHENG] = "青海省";
BICst.MAP_TYPE_NAME[BICst.MAP_TYPE.ANSHANSHI] = "鞍山市";
BICst.MAP_TYPE_NAME[BICst.MAP_TYPE.SHAOGUANSHI] = "韶关市";
BICst.MAP_TYPE_NAME[BICst.MAP_TYPE.XIANGGANGTEBIEXINGZHENGQU] = "香港特别行政区";
BICst.MAP_TYPE_NAME[BICst.MAP_TYPE.MAANSHANSHI] = "马鞍山市";
BICst.MAP_TYPE_NAME[BICst.MAP_TYPE.ZHUMADIANSHI] = "驻马店市";
BICst.MAP_TYPE_NAME[BICst.MAP_TYPE.JIXISHI] = "鸡西市";
BICst.MAP_TYPE_NAME[BICst.MAP_TYPE.HEBISHI] = "鹤壁市";
BICst.MAP_TYPE_NAME[BICst.MAP_TYPE.HEGANGSHI] = "鹤岗市";
BICst.MAP_TYPE_NAME[BICst.MAP_TYPE.YINGTANSHI] = "鹰潭市";
BICst.MAP_TYPE_NAME[BICst.MAP_TYPE.HUANGGANGSHI] = "黄冈市";
BICst.MAP_TYPE_NAME[BICst.MAP_TYPE.HUANGNANCANGZUZIZHIZHOU] = "黄南藏族自治州";
BICst.MAP_TYPE_NAME[BICst.MAP_TYPE.HUANGSHANSHI] = "黄山市";
BICst.MAP_TYPE_NAME[BICst.MAP_TYPE.HUANGSHISHI] = "黄石市";
BICst.MAP_TYPE_NAME[BICst.MAP_TYPE.HEIHESHI] = "黑河市";
BICst.MAP_TYPE_NAME[BICst.MAP_TYPE.HEILONGJIANGSHENG] = "黑龙江省";
BICst.MAP_TYPE_NAME[BICst.MAP_TYPE.QIANDONGNANMIAOZUDONGZUZIZHIZHOU] = "黔东南苗族侗族自治州";
BICst.MAP_TYPE_NAME[BICst.MAP_TYPE.QIANNANBUYIZUMIAOZUZIZHIZHOU] = "黔南布依族苗族自治州";
BICst.MAP_TYPE_NAME[BICst.MAP_TYPE.QIANXINANBUYIZUMIAOZUZIZHIZHOU] = "黔西南布依族苗族自治州";
BICst.MAP_TYPE_NAME[BICst.MAP_TYPE.QIQIHAERSHI] = "齐齐哈尔市";
BICst.MAP_TYPE_NAME[BICst.MAP_TYPE.LONGYANSHI] = "龙岩市";

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

//

//仪表盘自动样式
BICst.DASHBOARD_CHART_STYLE_AUTO = {
        FIRST: {
            color: "",
            from: 0,
            to: 100
        },
        SECOND: {
            color: "",
            from: 100,
            to: 200
        },
        THIRD: {
            color: "",
            from: 200,
            to: 300
        }
};

//单个指标、多个指标
BICst.POINTER = {
    ONE: 1,
    SOME: 2
};

//普通气泡图显示规则
BICst.DISPLAY_RULES = {
    DIMENSION: 1,
    FIXED: 2,
    GRADIENT: 3
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