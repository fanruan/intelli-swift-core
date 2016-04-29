package com.fr.bi.cluster;

/**
 * Created by FineSoft on 2015/6/17.
 */
public class ClusterConstant {
    public static final int SOCKETPORT = 11203;

    public static final class SERVICE_LIST {
        public static final byte BI_CONNECTION_SERVICE = 1;
        public static final byte BI_CUBE_SERVICE = 2;
        public static final byte BI_LOG_SERVICE = 3;
        public static final byte BI_REPORT_DAO_SERVICE = 4;
        public static final byte BI_TRANS_SERVICE = 5;
        public static final byte BI_BUSI_PACK_SERVICE = 6;
        public static final byte BI_DAO_ADHOC_REPORT_SERVICE = 7;
        public static final byte BI_DAO_COMPANY_SERVICE = 8;
        public static final byte BI_DAO_CUSTOM_SERVICE = 9;
        public static final byte BI_DAO_DEPARTMENT_SERVICE = 10;
        public static final byte BI_DAO_POST_SERVICE = 11;
        public static final byte BI_DAO_SHARED_REPORT_SERVICE = 12;
        public static final byte BI_DAO_TASK_INFO_USER = 13;
        public static final byte BI_DAO_USER_SERVICE = 14;
        public static final byte BI_DATA_SOURCE_SERVICE = 15;
        public static final byte BI_MATER_SERVICE = 16;
        public static final byte BI_SYSTEM_SERVICE = 17;
        public static final byte BI_FAVOURITE_SERVICE = 1;

    }

    /**
     * socket action list
     *
     * @author Daniel
     */

    /**
     * slave机器的状态
     *
     * @author Daniel
     */
    public static final class STATUS {

        /**
         * 断线
         */
        public static final byte OFFLINE = 0x0;

        /**
         * 在线
         */
        public static final byte ONLINE = 0x1;

        /**
         * 停止
         */
        public static final byte STOP = 0x2;

        /**
         * 正在生成表
         */
        public static final byte GENERATINGTABLE = 0x3;

        /**
         * 正在生成关联
         */
        public static final byte GENERATIONRELATION = 0x4;


        /**
         * 替换文件
         */
        public static final byte RENAMEFILE = 0x5;

        /**
         * 待命
         */
        public static final byte READY = 0x6;
    }

    /**
     * salve与master之间通信时需要传输空对象时使用值
     *
     * @author Daniel
     */
    public static final class TRANS {

        public static final String NULLOBJECT = "object_null";

    }

    public static final class ACTIONLIST {
        public static final byte BI_BUZI_PACK_REMOVE_PACKAGE = 1;
        public static final byte BI_BUZI_PACK_REMOVE_PACKAGE_GROUP = 2;
        public static final byte BI_BUZI_PACK_RENAME_PACK = 3;
        public static final byte BI_BUZI_LOAD_AVAILABLE_PACK = 4;
        public static final byte BI_BUZI_HAS_AVAILABLE_ANALYSE_PACK = 5;
        public static final byte BI_BUZI_ADD_MINING_PACK = 6;
        public static final byte BI_BUZI_UPDATE_PACK_GROUP = 7;
        public static final byte BI_BUZI_ADD_EMPTY_PACK = 8;
        public static final byte BI_BUZI_PARSE_UPDATE_FREQUERY_HOUR = 9;
        public static final byte BI_BUZI_CREATE_UPDATE_FREQUERY_HOUR = 10;
        public static final byte BI_BUZI_AS_JSON_WITH_PACK_GROUP_COUNT = 11;
        public static final byte BI_BUZI_AS_JSON_WITH_PACK_GROUP = 12;
        public static final byte BI_BUZI_AS_JSON_WITH_PACK_COUNT = 13;
        public static final byte BI_BUZI_GET_FINAL_VERSION_OF_PACK_BY_NAME = 14;
        public static final byte BI_BUZI_UPDATE_PACK_BY_NAME = 15;
        public static final byte BI_BUZI_SAVE_PACK_BY_NAME = 16;
        public static final byte BI_BUZI_ADD_TABLE_TO_PACK_BY_NAME = 17;
        public static final byte BI_BUZI_REMOVE_TABLE_FROM_PACK_BY_NAME = 18;
        public static final byte BI_BUZI_GET_CURRENT_PACK_FOR_GENERATING = 19;
        public static final byte BI_BUZI_GET_LATEST_PACK = 20;
        public static final byte BI_BUZI_GET_ALL_ANALYSE_PACK_IN_USE = 21;
        public static final byte BI_BUZI_GET_CURRENT_PACK_GROUP_FOR_GENERATING = 22;
        public static final byte BI_BUZI_GET_LATEST_PACK_GROUP = 23;
        public static final byte BI_BUZI_GET_ANALYSE_PACK_GROUP_IN_USE = 24;
        public static final byte BI_BUZI_HAS_MINING_PACK = 25;
        public static final byte BI_BUZI_SEARCH_FOR_KEYWORD_AS_JSON_BY_CONNECTION = 26;
        public static final byte BI_BUZI_UPDATE_FREQUENCY_ITERATOR = 27;
        public static final byte BI_BUZI_GET_CUBE_PATH = 28;
        public static final byte BI_BUZI_SET_CUBE_PATH = 29;
        public static final byte BI_BUZI_CHECK_CUBE_PATH = 30;
        public static final byte BI_BUZI_ENV_CHANGE = 31;
        public static final byte BI_BUZI_GET_ANALYSIS_TABLE_PACK = 32;
        public static final byte BI_BUZI_GET_PACK_NAME_ITERATOR = 33;
        public static final byte BI_BUZI_GET_SINGLE_TABLE_UPDATE_ITERATOR = 34;
        public static final byte BI_BUZI_HAS_PACKAGE_ACCESSIBLE_PRIVILEGE = 35;
        public static final byte BI_BUZI_GET_ALL_USER_AVAILABLE_PACK = 36;
        public static final byte BI_BUZI_GET_USER_FILTER_VALUE_ITERATOR = 37;
        public static final byte BI_BUZI_AS_JSON_WITH_PACK_COUNT_WITH_INFO = 38;
        public static final byte BI_BUZI_AS_JSON_WITH_PACK_GROUP_COUNT_WITH_INFO = 39;
        public static final byte BI_BUZI_SEARCH_4_KEY_WORD_AS_JSON = 40;
        public static final byte BI_BUZI_SEARCH_MAIN_TABLE_4_KEYWORD_AS_JSON = 41;
        public static final byte BI_BUZI_GET_ANALYSIS_USE_PACK_BY_NAME = 42;
        public static final byte BI_BUZI_GET_SINGLE_TABLE_UPDATE_ACTION = 43;
        public static final byte BI_BUZI_SET_SINGLE_TABLE_UPDATE_ACTION = 44;
        public static final byte BI_BUZI_WRITE_RESOURCE = 45;
        public static final byte BI_BUZI_WRITE_RESOURCE_2 = 46;
        public static final byte BI_BUZI_SAVA_AUTHORITY_BY_JSON = 47;
        public static final byte BI_BUZI_ADD_TABLE_BY_JSONARRAY_WITHOUT_GENERATE_CUBE = 48;

        /**
         * 以下是重构后代码里面的bizipack里面的方法实现，上面是老版中的接口实现。
         */
        public static final byte BI_BUZI_GET_UPDATE_MANAGER = 49;
        public static final byte BI_BUZI_GET_PACKAGE_MANAGER = 50;
        public static final byte BI_BUZI_GET_SINGLE_TABLE_UPDATE_MANAGER = 51;
        public static final byte BI_BUZI_FINISH_GENERATE_CUBES = 52;


        public static final byte BI_TRANS_SET_TRANS_NAME = 1;
        public static final byte BI_TRANS_GET_TRANS_NAME = 2;
        public static final byte BI_TRANS_ENV_CHANGED = 3;

        public static final byte BI_LOG_CREATE_JSON = 1;

        public static final byte BI_CONNECTION_ADD_ID_LINK = 80;
        public static final byte BI_CONNECTION_RESET_RELATION_MAPS = 81;
        public static final byte BI_CONNECTION_GET_TABLE_EXCEL = 82;
        public static final byte BI_CONNECTION_GET_PREV_CONNECTION_SET = 83;
        public static final byte BI_CONNECTION_GET_CONNECTION_SET = 84;
        public static final byte BI_CONNECTION_IS_RELATION_EXIST = 85;
        public static final byte BI_CONNECTION_REMOVE_REPORT_ID = 86;
        public static final byte BI_CONNECTION_GET_NEW_ID = 87;
        public static final byte BI_CONNECTION_ANALYSIS_USE_GET_LOGIN_USER_INFO = 88;
        public static final byte BI_CONNECTION_SETTING_USE_GET_LOGIN_USER_INFO = 89;
        public static final byte BI_CONNECTION_GENERATING_USE_GET_LOGIN_USER_INFO = 90;
        public static final byte BI_CONNECTION_PUSH_TABLE_EXCEL = 91;
        public static final byte BI_CONNECTION_SET_LATEST_SETTED_EXCEL = 92;
        public static final byte BI_CONNECTION_SAVE_EXCEL_VIEW = 93;
        public static final byte BI_CONNECTION_GET_EXCEL_NAME = 94;
        public static final byte BI_CONNECTION_GET_LAST_FIELD_EXCEL_COLUMN = 95;
        public static final byte BI_CONNECTION_REMOVE_TABLE_EXCEL = 96;
        public static final byte BI_CONNECTION_GET_FIELD_EXCEL_COLUMN = 97;
        public static final byte BI_CONNECTION_SETTING_USE_SET_LOGIN_USER_INFO = 98;
        public static final byte BI_CONNECTION_ADD_DISABLE_RELATIONS = 99;
        public static final byte BI_CONNECTION_REMOVE_DISABLE_RELATIONS = 100;
        public static final byte BI_CONNECTION_REMOVE_LAST_DISABLE_RELATIONS = 101;
        public static final byte BI_CONNECTION_HAS_DISABLE_RELATIONS = 102;
        public static final byte BI_CONNECTION_GET_DISABLED_RELATIONS = 103;
        public static final byte BI_CONNECTION_SETTING_USE_GET_TABLE_FIELD_RELATION_ITER = 104;
        public static final byte BI_CONNECTION_GENERATING_USE_TABLE_FIELD_RELATION = 105;
        public static final byte BI_CONNECTION_SETTING_USE_ADD_TABLE_FIELD_RELATION = 106;
        public static final byte BI_CONNECTION_SETTING_USE_IMPORT_DB_RELATION_BY_TABLE_NAME = 107;
        public static final byte BI_CONNECTION_ANALYSIS_USE_GET_IMPORT_SELF_CONNECTION_ITERATOR = 108;
        public static final byte BI_CONNECTION_ANALYSIS_GET_IMPORT_SELF_CONNECTION_ITERATOR = 109;
        public static final byte BI_CONNECTION_ANALYSIS_USE_GET_IMPORT_CONNECTION_ITERATOR = 110;
        public static final byte BI_CONNECTION_ANALYSIS_USE_GET_IMPORT_CONNECTION_ITERATOR_BY_BUSI_TABLE = 111;
        public static final byte BI_CONNECTION_ANALYSIS_USE_GET_FOREIGN_CONNECTION_ITERATOR_BY_BUSI_TABLE = 112;
        public static final byte BI_CONNECTION_ANALYSIS_USE_GET_IMPORT_CONNECTION_ITERATOR_3 = 113;
        public static final byte BI_CONNECTION_ANALYSIS_USE_GET_FOREIGN_TABLE = 114;
        public static final byte BI_CONNECTION_ANALYSIS_USE_IS_PRIMARY_KEY = 115;
        public static final byte BI_CONNECTION_ANALYSIS_USE_IS_FOREIGN_KEY = 116;
        public static final byte BI_CONNECTION_ANALYSIS_USE_GET_PRIMARY_CONNECTION_ITERATOR = 117;
        public static final byte BI_CONNECTION_ANALYSIS_USE_GET_PRIMARY_CONNECTION_ITERATOR_2 = 120;
        public static final byte BI_CONNECTION_ANALYSIS_USE_GET_FOREIGN_CONNECTION_ITERATOR = 118;
        public static final byte BI_CONNECTION_ANALYSIS_USE_GET_FOREIGN_CONNECTION_ITERATOR_2 = 119;
        public static final byte BI_CONNECTION_ANALYSIS_USE_GET_FOREIGN_CONNECTION_ITERATOR_3 = -2;
        public static final byte BI_CONNECTION_ANALYSIS_USE_GET_PRI_AND_FOR_KEYS_BY_TARGETS = 121;
        public static final byte BI_CONNECTION_ANALYSIS_USE_GET_ALL_KEYS_BY_TARGETS = 122;
        public static final byte BI_CONNECTION_SETTING_USE_CLEAR_RELATION_BY_FOREIGN_FIELD = 123;
        public static final byte BI_CONNECTION_GET_TABLE_TRANSLATER = 124;
        public static final byte BI_CONNECTION_SAVE_TABLE_TRANSLATER_BY_JSON = 125;
        public static final byte BI_CONNECTION_GET_ALL_CONNECTION_AND_FORMULA_CHANGES = 126;
        public static final byte BI_CONNECTION_ENV_CHANAGED = 127;
        public static final byte BI_CONNECTION_SAVE_TABLE_TRANSLATER_BY_JSON2 = -1;
        public static final byte BI_CONNECTION_ANALYSIS_USE_GET_IMPORT_CONNECTION_ITERATOR_2 = -36;
        public static final byte BI_CONNECTION_GET_RELATION_BY_KEYS = -4;
        public static final byte BI_CONNECTION_GET_REVERSE_RELATION_BY_KEYS = -5;
        public static final byte BI_CONNECTION_ADD_RELATION_BY_KEYS = -6;
        public static final byte BI_CONNECTION_ADD_REVERSE_RELATION_BY_KEYS = -7;
        public static final byte BI_CONNECTION_GET_BROTHER_RELATIONS = -8;
        public static final byte BI_CONNECTION_WRITE_RESOURCE = -9;

        public static final byte BI_CONNECTION_GET_USER_INFO_MANAGER = -10;
        public static final byte BI_CONNECTION_GET_CONNECTION_MANAGER = -11;
        public static final byte BI_CONNECTION_FINISH_GENERATE_CUBES = -12;
        public static final byte BI_CONNECTION_GET_CURRENT_LOGIN_INFO_4_GENERATING = -13;
        public static final byte BI_CONNECTION_ADDDISABLERELATION = -14;
        public static final byte BI_CONNECTION_REMOVEDISABLERELATION = -15;


        public static final byte BI_DATA_SOURCE_GET_MD5_TABLE_BY_ID = 1;
        public static final byte BI_DATA_SOURCE_ADD_MD5_TABLE = 2;
        public static final byte BI_DATA_SOURCE_EDIT_MD5_TABLE = 3;
        public static final byte BI_DATA_SOURCE_GET_MD5_NAME_BY_ID = 4;
        public static final byte BI_DATA_SOURCE_GET_MD5_TABLE_BY_MD5 = 5;
        public static final byte BI_DATA_SOURCE_ENV_CHANGE = 6;


        public static final byte BI_CUBE_SET_STATUS = 2;
        public static final byte BI_CUBE_ENV_CHANGED = 3;
        public static final byte BI_CUBE_RESET_CUBE_GENERATION_HOUR = 4;
        public static final byte BI_CUBE_GENERATE_CUBES = 5;
        public static final byte BI_CUBE_IS_CUBE_GENERATING = 6;
        public static final byte BI_CUBE_DELETE_TEMP_FOLDER = 7;
        public static final byte BI_CUBE_CHECK_VERSION = 8;
        public static final byte BI_CUBE_ADD_SINGLE_TASK_FROM_BUSI_PACK_MANAGER = 10;
        public static final byte BI_CUBE_ADD_GET_GENERATRING_OBJECT = 11;
        public static final byte BI_CUBE_RESET_CUBE_PATH = 12;
        public static final byte BI_CUBE_REMOVE_TASK = 13;
        public static final byte BI_CUBE_GENERATED_TASK = 15;
        public static final byte BI_CUBE_GENERATING_TASK = 16;
        public static final byte BI_CUBE_WATING_TASK_ITERATOR = 17;
        public static final byte BI_CUBE_HAS_ALL_TASK = 18;
        public static final byte BI_CUBE_HAS_CHECK_TASK = 19;
        public static final byte BI_CUBE_HAS_WAITING_CHECK_TASK = 20;
        public static final byte BI_CUBE_CHECK_CUBE_STATUS = 21;
        public static final byte BI_CUBE_HAS_TASK_2 = 22;
        public static final byte BI_CUBE_ADD_TASK = 23;
        public static final byte BI_CUBE_HAS_TASK = 24;
        public static final byte BI_CUBE_GET_STATUS = 25;
        public static final byte BI_CUBE_HAS_TASK_WITHOUTTASK = 26;


        public static final byte BI_SYSTEM_FIND_SYS_BI_REPORT_NODES = 1;
        public static final byte BI_SYSTEM_FIND_SYS_BI_REPORT_NODE_BY_ID = 2;
        public static final byte BI_SYSTEM_SAVE_OR_UPDATE_BI_NODE = 3;

        public static final byte BI_COMPANY_ROLE_DAO_SAVE = 1;
        public static final byte BI_COMPANY_ROLE_DAO_DELETE = 2;
        public static final byte BI_COMPANY_ROLE_DAO_DELETE_BY_ID = 3;
        public static final byte BI_COMPANY_ROLE_DAO_UPDATE = 4;
        public static final byte BI_COMPANY_ROLE_DAO_FIND_BY_ID = 5;
        public static final byte BI_COMPANY_ROLE_DAO_FIND_ALL = 6;
        public static final byte BI_COMPANY_ROLE_DAO_FIND_BY_POST = 7;
        public static final byte BI_COMPANY_ROLE_DAO_FIND_BY_DEPARTMENT = 8;
        public static final byte BI_COMPANY_ROLE_DAO_FIND_BY_POST_AND_DEPARTMENT = 9;
        public static final byte BI_COMPANY_ROLE_DAO_GET_ENTRY_PRIVILEGES = 10;
        public static final byte BI_COMPANY_ROLE_DAO_GET_DEP_AND_CROLE_PRIVILEGES = 11;
        public static final byte BI_COMPANY_ROLE_DAO_GET_TEMPLATE_PRIVILEGES = 12;
        public static final byte BI_COMPANY_ROLE_DAO_GET_DATA_CONNECTION_PRIVILEGES = 13;
        public static final byte BI_COMPANY_ROLE_DAO_GET_ES_PRIVILEGES = 14;
        public static final byte BI_COMPANY_ROLE_DAO_GET_MODULE_PRIVILEGES = 15;
        public static final byte BI_COMPANY_ROLE_DAO_GET_HOME_PAGE_PRIVILEGES = 16;
        public static final byte BI_COMPANY_ROLE_DAO_GET_PRIVILEGES_WITH_PLATE_NAME = 17;
        public static final byte BI_COMPANY_ROLE_DAO_UPDATE_ENTRY_PRIVILEGES = 18;
        public static final byte BI_COMPANY_ROLE_DAO_UPDATE_DEP_AND_CROLE_PRIVILEGE = 19;
        public static final byte BI_COMPANY_ROLE_DAO_UPDATE_ES_PRIVILEGES = 20;
        public static final byte BI_COMPANY_ROLE_DAO_UPDATE_MODULE_PRIVILEGES = 21;
        public static final byte BI_COMPANY_ROLE_DAO_UPDATE_HOME_PAGE_PRIVILEGES = 22;
        public static final byte BI_COMPANY_ROLE_DAO_UPDATE_TEMPLATE_PRIVILEGES = 23;
        public static final byte BI_COMPANY_ROLE_DAO_UPDATE_DATA_CONNECTION_PRIVILEGES = 24;
        public static final byte BI_COMPANY_ROLE_DAO_UPDATE_PLATE_PRIVILEGES = 25;
        public static final byte BI_COMPANY_ROLE_DAO_SORT_BY_ENTRY_ID = 26;

        public static final byte BI_REPORT_SAVA_OR_UPDATE = 1;
        public static final byte BI_REPORT_FIND_BY_USER_ID = 2;
        public static final byte BI_REPORT_FIND_BY_ID = 3;
        public static final byte BI_REPORT_FIND_BY_NAME = 4;
        public static final byte BI_REPORT_DELETE = 5;
        public static final byte BI_REPORT_DELETE_BY_ID = 6;
        public static final byte BI_REPORT_LIST_ALL = 7;

        public static final byte BI_SHARED_REPORT_REST_SHARED_BY_REPORT_ID_AND_USERS = 1;
        public static final byte BI_SHARED_REPORT_FIND_USERS_BY_REPORT_ID = 2;
        public static final byte BI_SHARED_REPORT_FIND_TEMPLATE_IDS_BY_USER_ID = 3;

        public static final byte BI_CUSTOM_ROLE_DAO_SAVE = 1;
        public static final byte BI_CUSTOM_ROLE_DAO_DELETE = 2;
        public static final byte BI_CUSTOM_ROLE_DAO_DELETE_BY_ID = 3;
        public static final byte BI_CUSTOM_ROLE_DAO_UPDATE_ROLE_NAME = 4;
        public static final byte BI_CUSTOM_ROLE_DAO_FIND_ALL = 5;
        public static final byte BI_CUSTOM_ROLE_DAO_FIND_BY_ROLE_NAME = 6;
        public static final byte BI_CUSTOM_ROLE_DAO_GET_USER_SET = 7;
        public static final byte BI_CUSTOM_ROLE_DAO_ADD_USER = 8;
        public static final byte BI_CUSTOM_ROLE_DAO_REMOVE_USER = 9;
        public static final byte BI_CUSTOM_ROLE_DAO_GET_ENTRY_PRIVILEGES = 10;
        public static final byte BI_CUSTOM_ROLE_DAO_GET_DEP_AND_CROLE_PRIVILEGES = 11;
        public static final byte BI_CUSTOM_ROLE_DAO_GET_TEMPLATE_PRIVILEGES = 12;
        public static final byte BI_CUSTOM_ROLE_DAO_GET_DATA_CONNECTION_PRIVILEGES = 13;
        public static final byte BI_CUSTOM_ROLE_DAO_GET_ES_PRIVILEGES = 14;
        public static final byte BI_CUSTOM_ROLE_DAO_GET_MODULE_PRIVILEGES = 15;
        public static final byte BI_CUSTOM_ROLE_DAO_GET_HOME_PAGE_PRIVILEGES = 16;
        public static final byte BI_CUSTOM_ROLE_DAO_GET_PRIVILEGES_WITH_PLATE_NAME = 17;
        public static final byte BI_CUSTOM_ROLE_DAO_UPDATE_ENTRY_PRIVILEGES = 18;
        public static final byte BI_CUSTOM_ROLE_DAO_UPDATE_DEP_AND_CROLE_PRIVILEGES = 19;
        public static final byte BI_CUSTOM_ROLE_DAO_UPDATE_ESP_PRIVILEGES = 20;
        public static final byte BI_CUSTOM_ROLE_DAO_UPDATE_MODULE_PRIVILEGES = 21;
        public static final byte BI_CUSTOM_ROLE_DAO_UPDATE_HOME_PRIVILEGES = 22;
        public static final byte BI_CUSTOM_ROLE_DAO_UPDATE_TEMPLATE_PRIVILEGES = 23;
        public static final byte BI_CUSTOM_ROLE_DAO_UPDATE_DATA_CONNECTION_PRIVILEGES = 24;
        public static final byte BI_CUSTOM_ROLE_DAO_UPDATE_PLATE_PRIVILEGES = 25;
        public static final byte BI_CUSTOM_ROLE_DAO_SORT_BY_ENTRY_ID = 26;
        public static final byte BI_CUSTOM_ROLE_DAO_FIND_BY_ID = 27;


        public static final byte BI_USER_DAO_SAVE = 1;
        public static final byte BI_USER_DAO_DELETE = 2;
        public static final byte BI_USER_DAO_DELETE_BY_ID = 3;
        public static final byte BI_USER_DAO_FIND_BY_ID = 4;
        public static final byte BI_USER_DAO_FIND_ALL = 5;
        public static final byte BI_USER_DAO_FIND_ALL_SORT_BY_USER_NAME = 6;
        public static final byte BI_USER_DAO_UPDATE = 7;
        public static final byte BI_USER_DAO_UPDATE_PARAM_5 = 8;
        public static final byte BI_USER_DAO_UPDATE_PASSWORD = 9;
        public static final byte BI_USER_DAO_FIND_BY_USER_NAME = 10;
        public static final byte BI_USER_DAO_FIND_BY_USER_NAME_AND_PASSWORD = 11;
        public static final byte BI_USER_DAO_GET_ROLE_SET = 12;
        public static final byte BI_USER_DAO_GET_JOB_SET = 13;
        public static final byte BI_USER_DAO_GET_USER_SET = 14;
        public static final byte BI_USER_DAO_ADD_JOB = 15;
        public static final byte BI_USER_DAO_REMOVE_JOB = 16;
        public static final byte BI_USER_DAO_REMOVE_JOB_2 = 20;
        public static final byte BI_USER_DAO_UPDATE_JOB = 17;
        public static final byte BI_USER_DAO_ADD_ROLE = 18;
        public static final byte BI_USER_DAO_REMOVE_ROLE = 19;
        public static final byte BI_USER_DAO_UPDATE_PARAM_6 = 20;


        public static final byte BI_DEPARTMENT_DAO_SAVE = 1;
        public static final byte BI_DEPARTMENT_DAO_DELETE = 2;
        public static final byte BI_DEPARTMENT_DAO_DELETE_BY_ID = 3;
        public static final byte BI_DEPARTMENT_DAO_FIND_BY_ID = 4;
        public static final byte BI_DEPARTMENT_DAO_FIND_ALL = 5;
        public static final byte BI_DEPARTMENT_DAO_UPDATE_DEPARTMENT_NAME = 6;
        public static final byte BI_DEPARTMENT_DAO_FIND_BY_NAME_AND_PID = 7;
        public static final byte BI_DEPARTMENT_DAO_GET_USER_AND_POST_ITERATOR = 8;

        public static final byte BI_POST_DAO_SAVE = 1;
        public static final byte BI_POST_DAO_DELETE = 2;
        public static final byte BI_POST_DAO_DELETE_BY_ID = 3;
        public static final byte BI_POST_DAO_FIND_BY_ID = 4;
        public static final byte BI_POST_DAO_FIND_ALL = 5;
        public static final byte BI_POST_DAO_FIND_BY_POST_NAME = 6;

        public static final byte BI_FAVORITE_DAO_SAVE = 1;
        public static final byte BI_FAVORITE_DAO_DELETE = 2;
        public static final byte BI_FAVORITE_DAO_DELETE_BY_ID = 3;
        public static final byte BI_FAVORITE_DAO_DELETE_BY_USER_AND_ENTRY = 4;
        public static final byte BI_FAVORITE_DAO_FIND_BY_USER_ID = 5;

        public static final byte BI_ADHOCREPORT_DAO_SAVE = 1;
        public static final byte BI_ADHOCREPORT_FIND_USER_BY_ID = 2;
        public static final byte BI_ADHOCREPORT_DELETE_BY_ID = 3;
        public static final byte BI_ADHOCREPORT_DELETE = 4;
        public static final byte BI_ADHOCREPORT_FIND_BY_ID = 5;

        public static final byte BI_TASK_INFO_DAO_SAVE = 1;
        public static final byte BI_TASK_INFO_DAO_UPDATE = 2;
        public static final byte BI_TASK_INFO_DAO_REMOVE = 3;
        public static final byte BI_TASK_INFO_DAO_FIND_BY_USER_ID = 4;
        public static final byte BI_TASK_INFO_DAO_FIND_BY_TASK_ID = 5;
        public static final byte BI_TASK_INFO_DAO_FIND_ALL = 6;

        public static final byte BI_VALIDATE_PASSWORD = 1;
        public static final byte BI_VALIDATE_PASSWORD_PARAM_3 = 3;
        public static final byte BI_ENCODE_PASSWORD = 2;
        public static final byte BI_ENCODE_PASSWORD_2 = 4;
        public static final byte BI_ENCODE_SHOULD_INGNORE_USERNAME = 5;
        public static final byte BI_ENCODE_TO_STRING = 6;
        public static final byte BI_ENCODE_CLONE = 7;


    }
}