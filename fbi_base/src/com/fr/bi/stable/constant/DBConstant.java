package com.fr.bi.stable.constant;

import java.util.Calendar;

/**
 * Created by 小灰灰 on 2015/9/7.
 */
public class DBConstant {

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

    public static final class CUBE_UPDATE_TYPE {
        public static final String GLOBAL_UPDATE = "__global_update__";
        public static final String SINGLETABLE_UPDATE = "__singleTable_update__";
    }

    public static final class GLOBAL_UPDATE_TYPE {
        public static final String PART_UPDATE = "_part_update_";
        public static final String COMPLETE_UPDATE = "_complete_update_";
        public static final String META_UPDATE = "_meta_update_";
    }

    public static final String SYSTEM_USER_NAME = "__system_user_name__";

    public static final String LAST_UPDATE_TIME = "__last_update_time__";
    public static final String CURRENT_UPDATE_TIME = "__current_update_time__";


    //数据配置权限分配的基本节点常量 都作为id、parentId使用
    public static final class DATA_CONFIG_AUTHORITY {
        public static final class DATA_CONNECTION {
            public static final String NODE = "__data_connection_node__";   //节点
            public static final String PAGE = "__data_connection_page__";   //页面展示
        }

        public static final class PACKAGE_MANAGER {
            public static final String NODE = "__package_manager_node__";
            public static final String PAGE = "__package_manager_page__";
            public static final String SERVER_CONNECTION = "__package_server_connection__";   //业务包选表控制服务器数据集权限
            public static final String DATA_CONNECTION = "__package_data_connection__";
        }

        public static final String MULTI_PATH_SETTING = "__multi_path_setting__";     //多路径设置
        public static final String PACKAGE_AUTHORITY = "__package_authority__";       //权限配置
        public static final String FINE_INDEX_UPDATE = "__fine_index_update__";       //FineIndex更新

    }

    //是否授权节点
    public static final class DATA_CONFIG_DESIGN {
        public static final int NO = 0;
        public static final int YES = 1;
    }

    //单表定时更新设置
    public static final class SINGLE_TABLE_UPDATE_START_TIME {
        public static final int IMMEDIATE = 0x1;
        public static final int SET = 0x2;
    }
    public static final class SINGLE_TABLE_UPDATE_FREQUENCY {
        public static final int ONCE = 0x1;
        public static final int REPEAT = 0x2;
        public static final int SET = 0x3;
        public static final int FORMULA = 0x4;
    }
    public static final class UPDATE_FREQUENCY_REPEAT {
        public static final int MINUTE = 0x1;
        public static final int HOUR = 0x2;
        public static final int DAY = 0x3;
        public static final int WEEK = 0x4;
    }
    public static final class SINGLE_TABLE_UPDATE_END_TIME {
        public static final int ONCE = 0x1;
        public static final int UNLIMITED = 0x2;
        public static final int SET = 0x3;
        public static final int REPEAT = 0x4;
    }

    public static final class UPDATE_STATE {
        public static final int NOT_START = 0x1;
        public static final int RUNNING = 0x2;
        public static final int FINSIH = 0x3;
        public static final int FAIL = 0x4;
    }

    //业务包类型
    public static final class PACKAGE_TYPE {
        public static final int FINE_INDEX = 1;
        public static final int FINE_DIRECT = 2;
    }

}