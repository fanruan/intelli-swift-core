package com.fr.bi.stable.constant;

/**
 * Created by GUY on 2015/3/11.
 */
public class CubeConstant {

    public final static String CODE = "UTF-8";

    public final static int LICMAXROW = 100000;

    public final static byte NULLVALUE = -1;

    public final static byte[] NULLSTRING = new byte[]{NULLVALUE};

    public final static byte[] NULLBYTES = new String("8c80c6db-4038-49dc-880b-fdb4fba04716").getBytes();

    public final static byte ENDBYTE = 0;
    /**
     * 表示位图索引的空值，现在字符串的null与空值是当作同一个值来处理的
     * 以后可能需要分开
     */
    public final static int NULLINDEX = 0xffffffff;

    public final static String PART = ".part";

    public final static int ROWS = 0x7;

    public final static int INDEXLENGTH = 0x40;

    public static final String READ_FROM_DB = "Read From Database finish";

    public static final int LOG_ROW_COUNT = 0xFFFF;

    public static final int TEMP_LOG_ROW_COUNT = 0x7F;

    /**
     * cube版本控制
     */
    /**
     * 2015/12/30 调整index 删除rowcount 删除不用的index 并支持andnot操作
     */
    public static final int CUBEVERSION = 388;

    /**
     * linked index版本控制
     */
    public static final int LINKEDINDEXVERSION = 388;

    public static final int PERCENT_ROW = 10000;

    public static final int PERCENT_ROW_D = 100;

    public static final int PERCENT_ROT_SEVENTY = 70;

    public static final int PERCENT_ROT_THIRTY = 30;


    public static final int LOG_ROW = 0x3FFFF;

    public static final class CUBEUPDATE {
        public static final int NONE = 0x0;

        public static final int ALLTASK = 0x1;

        public static final int CHECKTASK = 0x2;
    }
//    public static final class CUBE_TASK_TYPE {
//        public static final String GLOBAL_FULL_ = "GLOBAL_FULL";
//        public static final String GLOBAL_PART = "GLOBAL_PART";
//        public static final String SINGLE_TABLE_PART = "SINGLE_TABLE_PART";
//        public static final String SINGLE_TABLE_FULL = "SINGLE_TABLE_FULL";
//    }
    public static final String CUBE_PROPERTY = "property";

    public static final class CUBE_UPDATE_TYPE {
        public static final String GLOBAL_UPDATE = "__global_update__";
        public static final String SINGLETABLE_UPDATE = "__singleTable_update__";
    }
}
