package com.fr.bi.stable.constant;

/**
 * This class created on 16-12-9.
 *
 * @author Kary
 * @since Advanced FineBI Analysis 1.0
 */
public class BILogConstant {
    /**
     * table log 日志类型
     */
    public static final class TABLE_LOG_TYPE {
        public static final int CORRECT = 0;

        public static final int ERROR = 1;

        public static final int RUNNING = 2;
    }

    /**
     * path log
     */
    public static final class PATH_LOG_TYPE {
        public static final int CORRECT = 0;

        public static final int ERROR = 1;

    }

    public static final class LOG_CACHE_TAG {
        public static final String CUBE_GENERATE_INFO = "CUBE_GENERATE_INFO";
        public static final String CUBE_GENERATE_EXCEPTION_INFO = "CUBE_GENERATE_EXCEPTION_INFO";
    }

    public static final class LOG_CACHE_SUB_TAG {
        public static final String CUBE_GENERATE_START = "CUBE_GENERATE_START";
        public static final String CUBE_GENERATE_END = "CUBE_GENERATE_END";
        public static final String CUBE_GENERATE_TABLE_NORMAL_INFO = "CUBE_GENERATE_TABLE_NORMAL_INFO";
        public static final String CUBE_GENERATE_FIELD_NORMAL_INFO = "CUBE_GENERATE_FIELD_NORMAL_INFO";
        public static final String READ_ONLY_BUSINESS_TABLES_OF_TABLE_SOURCE_MAP = "READ_ONLY_BUSINESS_TABLES_OF_TABLE_SOURCE_MAP";
    }


    public static final class LOG_CACHE_TIME_TYPE {
        public static final String TRANSPORT_EXECUTE_START = "TRANSPORT_EXECUTE_START";
        public static final String TRANSPORT_EXECUTE_END = "TRANSPORT_EXECUTE_END";
        public static final String FIELD_INDEX_EXECUTE_START = "FIELD_INDEX_EXECUTE_START";
        public static final String FIELD_INDEX_EXECUTE_END = "FIELD_INDEX_EXECUTE_END";
        public static final String RELATION_INDEX_EXECUTE_START = "RELATION_INDEX_EXECUTE_START";
        public static final String RELATION_INDEX_EXECUTE_END = "RELATION_INDEX_EXECUTE_END";
    }


}
