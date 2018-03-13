package com.fr.swift.source.etl.utils;

/**
 * Created by Handsome on 2018/1/18 0018 09:30
 */
public class ETLConstant {

    public static final class CONF {

        public static final class DATEDELTRA {

            public static final int DAY = 1000 * 60 * 60 * 24;

            public static final int WEEK = DAY * 7;

            public static final int MONTH_OF_YEAR = 12;

            public static final int MONTH_OF_SEASON = 3;
        }


        public static final class COLUMN {
            public static final int STRING = 16;
            public static final int NUMBER = 32;
            public static final int DATE = 48;
            public static final int COUNTER = 64;
            public static final int ROW = 80;
        }

        public static final class FIELD_ID {

            public static final String HEAD = "81c48028-1401-11e6-a148-3e1d05defe78";
            public static final String SYSTEM_TIME = "__system_time-3e1d05defe78__";
        }


        public static final class MENU {
            public static final int DATA_LINK = 0x1;
            public static final int DATA_MANAGER = 0x2;
            public static final int MUILT_PATH = 0x3;
            public static final int PERMISSION_CONFIG = 0x4;
            public static final int UPDATE_SETTING = 0x5;
        }

        public static final String NO_GROUPED = "__no_group__";
        public static final String MY_ANALYSIS = "__my_analysis__";
        public static final String NEW_TABLE_ID = "__new_table_id__";
        public static final String CURRENT_TABLE = "__current_table__";
        public static final String EMPTY_FIELD = "__empty_field__";

        public static final class ANALYSIS {
            public static final class OPERATOR {
                public static final int SELECT_FIELD = 0x1;
                public static final int JOIN = 0x2;
                public static final int UNION = 0x3;
                public static final int SORT = 0x4;
                public static final int FILTER = 0x5;
                public static final int GROUP = 0x6;
                public static final int ADD_COLUMN = 0x7;
                public static final int FIELD_SETTING = 0x8;
            }
        }

        public static final class TABLE_DETAIL {
            public static final int PREVIEW = 0x1;
            public static final int USAGE = 0x2;
            public static final int RELATION = 0x3;
            public static final int UPDATE = 0x4;
        }

        public static final class PREVIEW_TABLE {
            public static final int DETAIL_TABLE = 0x1;
            public static final int STRUCT_TABLE = 0x2;
        }

        public static final class SORT_TYPE {
            public static final int ASCEND = 0x1;
            public static final int DESCEND = 0x2;
        }

        public static final class CONNECTION {
            public static final String SERVER_CONNECTION = "__FR_BI_SERVER__";
            public static final String SQL_CONNECTION = "__FR_BI_SQL__";
            public static final String EXCEL_CONNECTION = "__FR_BI_EXCEL__";
        }

        public static final class DB_OPERATOR {
            public static final int CIRCULATE = 0xa;
            public static final int SETTING = 0xc;
            public static final int CONVERT = 0xb;
        }

        public static final class GROUP {
            // 用于标识Dimension中的Bean类型
            public static final class TYPE {
                // 单个简单value
                public static final int SINGLE = 0x1;
                // 含有childValue的结构
                public static final int DOUBLE = 0x2;
                // 分组情况中的结构
                public static final int GROUP = 0x3;
            }

            // 区间分组类型
            public static final class NUMBER_GROUP_TYPE {
                // 自动分组
                public static final int AUTO = 0x1;
                // 自定义分组
                public static final int CUSTOM = 0x2;
            }

            // 文本字段操作类型
            public static final class STRING {
                // 相同值为一组
                public static final int GROUP_BY_VALUE = 0x1;
                // 自定义分组
                public static final int GROUP_BY_CUSTOM = 0x2;

                // 以下是汇总
                // 去重计数
                public static final int COUNT = 0x3;
                // 记录个数
                public static final int RECORD_COUNT = 0x4;
                // 字符串拼接
                public static final int APPEND = 0x5;
                // 重命名
                public static final int RENAME = 0x6;
                // 删除
                public static final int DELETE = 0x7;
            }

            // 数值字段操作类型
            public static final class NUMBER {
                // 相同值为一组
                public static final int GROUP_BY_VALUE = 0x1;
                // 区间分组
                public static final int CUSTOM_NUMBER_GROUP = 0x2;

                // 以下是汇总
                // 求和
                public static final int SUM = 0x3;
                // 求平均
                public static final int AVG = 0x4;
                // 求中位数
                public static final int MEDIAN = 0x5;
                // 求最大值
                public static final int MAX = 0x6;
                // 求最小值
                public static final int MIN = 0x7;
                // 求标准差
                public static final int STANDARD_DEVIATION = 0x8;
                // 求方差
                public static final int VARIANCE = 0x9;
                // 去重计数
                public static final int COUNT = 0xa;
                // 记录个数
                public static final int RECORD_COUNT = 0xb;
                // 无操作
                public static final int NONE = 0xc;
                // 求同期，此为父项，同期的单位下面会标出
                public static final int PERIOD = 0xd;
                // 求环期
                public static final int RING = 0xe;
                // 求同比，此为父项，同比的单位下面会标出
                public static final int PERIOD_RATE = 0xf;
                // 求环比
                public static final int RING_RATE = 0x10;
                // 以下为子项，为同期、同比的单位
                // 年
                public static final int YEAR = 0x11;
                // 季度
                public static final int QUARTER = 0x12;
                // 月
                public static final int MONTH = 0x13;
                // 周
                public static final int WEEK = 0x14;
                // 星期
                public static final int WEEKDAY = 0x15;
                // 重命名
                public static final int RENAME = 0x16;
                // 删除
                public static final int DELETE = 0x17;
            }

            public static final class DATE {
                // 年月日
                public static final int DATE = 0x1;
                // 年份
                public static final int YEAR = 0x2;
                // 季度
                public static final int QUARTER = 0x3;
                // 月份
                public static final int MONTH = 0x4;
                // 星期
                public static final int WEEKDAY = 0x5;
                // 日
                public static final int DAY = 0x6;
                // 更多分组，此为父项
                public static final int MORE_GROUP = 0x7;
                // 此为子项
                // 周数
                public static final int WEEK_COUNT = 0x8;
                // 时
                public static final int HOUR = 0x9;
                // 分
                public static final int MINUTE = 0xa;
                // 秒
                public static final int SECOND = 0xb;
                // 年季度
                public static final int YQ = 0xc;
                // 年月
                public static final int YM = 0xd;
                // 年周数
                public static final int YW = 0xe;
                // 年月时
                public static final int YMDH = 0xf;
                // 年月时分
                public static final int YMDHM = 0x10;
                // 年月时分秒
                public static final int YMDHMS = 0x11;
                // 汇总
                // 去重计数
                public static final int COUNT = 0x12;
                // 记录个数
                public static final int RECORD_COUNT = 0x13;
                // 最早时间
                public static final int ET = 0x14;
                // 最晚时间
                public static final int LT = 0x15;
                // 重命名
                public static final int RENAME = 0x16;
                // 删除
                public static final int DELETE = 0x17;
            }
        }

        public static final class ADD_COLUMN {
            public static final int NOT_IN_GROUP = 0x0;
            public static final int IN_GROUP = 0x1;

            public static final class FORMULA {
                public static final int TYPE = 0x1;
            }
            public static final class TIME_GAP {
                public static final int TYPE = 0x2;
                public static final String SYSTEM_TIME = "";
                public static final class UNITS {
                    public static final int YEAR = 0x1;
                    public static final int QUARTER = 0x2;
                    public static final int MONTH = 0x3;
                    public static final int WEEK = 0x4;
                    public static final int DAY = 0x5;
                    public static final int HOUR = 0x6;
                    public static final int MINUTE = 0x7;
                    public static final int SECOND = 0x8;
                }
            }
            public static final class TIME {
                public static final int TYPE = 0x3;
                public static final class UNITS {
                    public static final int YEAR = 0x1;
                    public static final int QUARTER = 0x2;
                    public static final int MONTH = 0x3;
                    public static final int WEEKDAY = 0x4;
                    public static final int DAY = 0x5;
                    public static final int WEEK_COUNT = 0x6;
                    public static final int HOUR = 0x7;
                    public static final int MINUTE = 0x8;
                    public static final int SECOND = 0x9;
                    public static final int YQ = 0xa;
                    public static final int YM = 0xb;
                    public static final int YW = 0xc;
                    public static final int YMDH = 0xd;
                    public static final int YMDHM = 0xe;
                    public static final int YMDHMS = 0xf;
                }
            }
            public static final class ALL_VALUES {
                public static final int TYPE = 0x4;
                public static final class SUMMARY_TYPE {
                    public static final int SUM = 0x1;
                    public static final int AVG = 0x2;
                    public static final int MAX = 0x3;
                    public static final int MIN = 0x4;
                    public static final int COUNT = 6;
                }
            }
            public static final class ACCUMULATIVE_VALUE {
                public static final int TYPE = 0x5;
            }
            public static final class RANKING {
                public static final int TYPE = 0x6;
                public static final int ASC = 0xa;
                public static final int DSC = 0xb;
                public static final int ASC_IN_GROUP = 0xc;
                public static final int DSC_IN_GROUP = 0xd;
            }
            public static final class GROUP {
                public static final int TYPE = 0x7;
                public static final int TYPE_STRING = 0x8;
                public static final int TYPE_NUMBER_CUSTOM = 0x9;
                public static final int TYPE_NUMBER_AUTO = 0xa;
            }
        }

        public static final class CIRCULATE {
            public static final int CONDITION_TYPE_NOT_HAS_PARENT = 0x1;
            public static final int CONDITION_TYPE_HAS_PARENT = 0x2;
        }

        public static final class SINGLE_TABLE_UPDATE_TYPE {
            public static final int ALL = 0x1;
            public static final int PART = 0x2;
            public static final int NEVER = 0x3;
        }

        public static final class SINGLE_TABLE_UPDATE_START_TIME_TYPE {
            public static final int IMMEDIATELY = 0x1;
            public static final int SET = 0x2;
        }

        public static final class SINGLE_TABLE_UPDATE_FREQUENCY {
            public static final class TYPE {
                public static final int ONCE = 0x1;
                public static final int REPEAT = 0x2;
                public static final int DETAIL = 0x3;
                public static final int FORMULA = 0x4;
            }

            public static final class REPEAT_TYPE {
                public static final int MINUTE = 0x1;
                public static final int HOUR = 0x2;
                public static final int DAY = 0x3;
                public static final int WEEK = 0x4;
            }

            public static final class RADIO_TYPE {
                public static final int DAY = 0x1;
                public static final int WEEK = 0x2;
                public static final int MONTH = 0x3;
            }

            public static final class WEEK_TYPE {
                public static final int MONDAY = 0x1;
                public static final int TUESDAY = 0x2;
                public static final int WEDNESDAY = 0x3;
                public static final int THURSDAY = 0x4;
                public static final int FRIDAY = 0x5;
                public static final int SATURDAY = 0x6;
                public static final int SUNDAY = 0x7;
            }

            public static final class MONTH_DAY_TYPE {
                public static final int ONE = 0x1;
                public static final int TWO = 0x2;
                public static final int THREE = 0x3;
                public static final int FOUR = 0x4;
                public static final int FIVE = 0x5;
                public static final int SIX = 0x6;
                public static final int SEVEN = 0x7;
                public static final int EIGHT = 0x8;
                public static final int NINE = 0x9;
                public static final int TEN = 0xa;
                public static final int ELEVEN = 0xb;
                public static final int TWELVE = 0xc;
                public static final int THIRTEEN = 0xd;
                public static final int FOURTEEN = 0xe;
                public static final int FIFTEEN = 0xf;
                public static final int SIXTEEN = 0x10;
                public static final int SEVENTEEN = 0x11;
                public static final int EIGHTEEN = 0x12;
                public static final int NINETEEN = 0x13;
                public static final int TWENTY = 0x14;
                public static final int TWENTY_ONE = 0x15;
                public static final int TWENTY_TWO = 0x16;
                public static final int TWENTY_THREE = 0x17;
                public static final int TWENTY_FOUR = 0x18;
                public static final int TWENTY_FIVE = 0x19;
                public static final int TWENTY_SIX = 0x1a;
                public static final int TWENTY_SEVEN = 0x1b;
                public static final int TWENTY_EIGHT = 0x1c;
                public static final int TWENTY_NINE = 0x1d;
                public static final int THIRTY = 0x1e;
                public static final int THIRTY_ONE = 0x1f;
            }

            public static final class MONTH_TYPE {
                public static final int JANUARY = 0x1;
                public static final int FEBRUARY = 0x2;
                public static final int MARCH = 0x3;
                public static final int APRIL = 0x4;
                public static final int MAY = 0x5;
                public static final int JUNE = 0x6;
                public static final int JULY = 0x7;
                public static final int AUGUST = 0x8;
                public static final int SEPTEMBER = 0x9;
                public static final int OCTOBER = 0xa;
                public static final int NOVEMBER = 0xb;
                public static final int DECEMBER = 0xc;
            }
        }

        public static final class SINGLE_TABLE_UPDATE_END_TIME_TYPE {
            public static final int IMMEDIATELY = 0x1;
            public static final int INFINITE = 0x2;
            public static final int SET = 0x3;
            public static final int EXTRA = 0x4;
        }

        public static final class RELATION_TYPE {
            public static final int ONE_TO_ONE = 0x1;
            public static final int ONE_TO_N = 0x2;
            public static final int N_TO_ONE = 0x3;
        }

        public static final class TABLE_USAGE_TYPE {
            public static final int TEMPLATE = 0x1;
            public static final int ANALYSIS_TABLE = 0x2;
        }

        public static final class DATE_TYPE {
            public static final int MULTI_DATE_YEAR_PREV = 0x1;
            public static final int MULTI_DATE_YEAR_AFTER = 0x2;
            public static final int MULTI_DATE_YEAR_BEGIN = 0x3;
            public static final int MULTI_DATE_YEAR_END = 0x4;
            public static final int MULTI_DATE_MONTH_PREV = 0x5;
            public static final int MULTI_DATE_MONTH_AFTER = 0x6;
            public static final int MULTI_DATE_MONTH_BEGIN = 0x7;
            public static final int MULTI_DATE_MONTH_END = 0x8;
            public static final int MULTI_DATE_QUARTER_PREV = 0x9;
            public static final int MULTI_DATE_QUARTER_AFTER = 0xa;
            public static final int MULTI_DATE_QUARTER_BEGIN = 0xb;
            public static final int MULTI_DATE_QUARTER_END = 0xc;
            public static final int MULTI_DATE_WEEK_PREV = 0xd;
            public static final int MULTI_DATE_WEEK_AFTER = 0xe;
            public static final int MULTI_DATE_DAY_PREV = 0xf;
            public static final int MULTI_DATE_DAY_AFTER = 0x10;
            public static final int MULTI_DATE_DAY_TODAY = 0x11;
            public static final int MULTI_DATE_PARAM = 0x12;
            public static final int MULTI_DATE_CALENDAR = 0x13;
            public static final int YEAR_QUARTER = 0x14;
            public static final int YEAR_MONTH = 0x15;
            public static final int YEAR_WEEK = 0x16;
            public static final int YEAR_DAY = 0x17;
            public static final int MONTH_WEEK = 0x18;
            public static final int MONTH_DAY = 0x19;
            public static final int YEAR = 0x1a;
            public static final int SAME_PERIOD = 0x1b;
            public static final int LAST_SAME_PERIOD = 0x1c;
        }

        public static final class JOIN {
            public static final int OUTER = 0x1;
            public static final int INNER = 0x2;
            public static final int LEFT = 0x3;
            public static final int RIGHT = 0x4;
        }

        public static final class PACK_UPDATE_TYPE {
            public static final int UPDATE = 0x1;
            public static final int NOT_UPDATE = 0x2;
        }

        public static final class PACK_UPDATE_TASK_STATE {
            public static final int NOT_START = 0x1;
            public static final int RUNNING = 0x2;
            public static final int END = 0x3;
            public static final int FAIL = 0x4;
        }

        public static final class CUBE_UPDATE_TYPE {
            public static final String GLOBAL_UPDATE = "__global_update__";
            public static final String PACK_UPDATE = "__pack_update__";
            public static final String TABLE_UPDATE = "__table_update__";
        }

        public static final class BROADCAST {
            public static final String PACKAGE_EDIT_PREFIX = "__package_edit__";
        }
    }
}