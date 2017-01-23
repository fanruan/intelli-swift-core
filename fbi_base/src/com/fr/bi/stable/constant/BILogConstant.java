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

    public static final class LOG_CACHE_TIME_TYPE {
        public static final String START = "START";
        public static final String END = "END";
    }


    public static final class LOG_CACHE_SUB_TAG {
        public static final String TRANSPORT_EXECUTE_TIME = "TRANSPORT_EXECUTE_TIME";
        public static final String FIELD_INDEX_EXECUTE_TIME = "FIELD_INDEX_EXECUTE_TIME";
        public static final String RELATION_INDEX_EXECUTE_TIME = "RELATION_INDEX_EXECUTE_TIME";
    }

}
