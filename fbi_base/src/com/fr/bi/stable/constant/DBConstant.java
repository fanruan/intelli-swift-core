package com.fr.bi.stable.constant;

import java.util.Calendar;

/**
 * Created by 小灰灰 on 2015/9/7.
 */
public class DBConstant {

public static final class CUBE_UPDATE_TYPE {
    public static final String GLOBAL_UPDATE = "__global_update__";
    public static final String SINGLETABLE_UPDATE = "__singleTable_update__";
}
    /**
     * 数据库返回的数据类型
     *
     * @author frank
     */
    public static final class CLASS {
        public static final int INTEGER = 0x0;

        public static final int LONG = 0x1;

        public static final int DOUBLE = 0x2;

        public static final int FLOAT = 0x3;

        public static final int DATE = 0x4;

        public static final int STRING = 0x5;

        public static final int BOOLEAN = 0x6;

        public static final int TIMESTAMP = 0x7;

        public static final int DECIMAL = 0x8;

        public static final int TIME = 0x9;

        public static final int BYTE = 0xa;

        /**
         * 行号类型，逻辑对象
         */
        public static final int ROW = 0x10;
    }

    /**
     * 字段类型
     *
     * @author frank
     */
    public static final class COLUMN {
        public static final int NUMBER = 0x20;

        public static final int STRING = 0x10;

        public static final int DATE = 0x30;

        public static final int COUNTER = 0x40;

        /**
         * 行号类型，逻辑对象
         */
        public static final int ROW = 0x50;

    }

    public static final class CONNECTION {

        public final static String ETL_CONNECTION = "__FR_BI_ETL__";
        public final static String SERVER_CONNECTION = "__FR_BI_SERVER__";
        public final static String SQL_CONNECTION = "__FR_BI_SQL__";
        public final static String EXCEL_CONNECTION = "__FR_BI_EXCEL__";
    }

    public static final class TRANS_TYPE {
        public static final String READ_FROM_DB = "db";
        public static final String READ_FROM_TABLEDATA = "tabledata";
        public static final String CHOOSE = "choose";
    }

    public static final class REQ_DATA_TYPE {
        public static final int REQ_GET_ALL_DATA = -1;
        public static final int REQ_GET_DATA_LENGTH = 0;
    }

    public static class UPDATE_FREQUENCY {

        public static final int EVER_DAY = 0;

        public final static int EVER_SUNDAY = Calendar.SUNDAY;

        public final static int EVER_MONDAY = Calendar.MONDAY;

        public final static int EVER_TUESDAY = Calendar.TUESDAY;

        public final static int EVER_WEDNESDAY = Calendar.WEDNESDAY;

        public final static int EVER_THURSDAY = Calendar.THURSDAY;

        public final static int EVER_FRIDAY = Calendar.FRIDAY;

        public final static int EVER_SATURDAY = Calendar.SATURDAY;

        public static final int EVER_MONTH = 10;
    }

    public static final class SINGLE_TABLE_UPDATE_TYPE {

        public static final int ALL = 0x0;

        public static final int PART = 0x1;

        public static final int NEVER = 0x2;


    }

    public static final class SINGLE_TABLE_UPDATE {

        public static final int TOGETHER = 0x0;

        public static final int NEVER = 0x1;
    }
}