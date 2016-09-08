package com.fr.bi.sql.analysis;

import com.fr.bi.stable.constant.BIReportConstant;

/**
 * Created by Daniel on 2016/1/21.
 */
public class Constants {

    public final static String PARENTS ="parents";
    public final static String DB_NAME ="db_name";
    public final static String TABLE_NAME ="table_name";
    public final static String FIELDS ="sql_fields";
    public final static String ITEMS ="items";
    public final static String PACK_ID ="mysqlidbuxudonga";
    public final static String TABLE_ID ="table_id";
    public final static String ETLTYPE ="etlType";
    public final static String SYSTEM_TIME = BIReportConstant.SYSTEM_TIME;

    public final static class BUSINESS_TABLE_TYPE {
        public final static int ANALYSIS_SQL_TYPE = 0x9;
    }

    public static final class TABLE_TYPE {
        public static final int SQL_TEMP = 0x9;

        public static final int SQL_BASE = 0xa;

        public static final int SQL_ID = 0xb;

        public static final int SQL_ETL = 0xc;
    }

    public static final class ETL_TYPE {
        public static final int SELECT_DATA = 0x1;

        public static final int SELECT_TABLE = 0x2;

        public static final int FILTER = 0x3;

        public static final int GROUP_SUMMARY = 0x4;

        public static final int ADD_COLUMN = 0x5;

        public static final int USE_PART_FIELDS = 0x6;

        public static final int MERGE_SHEET = 0x7;
    }

}