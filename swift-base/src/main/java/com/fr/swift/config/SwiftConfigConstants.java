package com.fr.swift.config;

/**
 * @author yee
 * @date 2018/5/28
 */
public class SwiftConfigConstants {
    public static final int LONG_TEXT_LENGTH = 65536;

    public static class SegmentConfig {
        public static final String COLUMN_SEGMENT_OWNER = "segmentOwner";
        public static final String USABLE = "usable";
        public static final String COLUMN_SEGMENT_ORDER = "segmentOrder";
        public static final String COLUMN_STORE_TYPE = "storeType";
        public static final String COLUMN_SEGMENT_URI = "segmentUri";
    }

    public static class MetaDataConfig {
        public static final String COLUMN_SWIFT_SCHEMA = "swiftSchema";
        public static final String COLUMN_SCHEMA = "schemaName";
        public static final String COLUMN_TABLE_NAME = "tableName";
        public static final String COLUMN_REMARK = "remark";
        public static final String COLUMN_FIELDS = "fields";
    }

    public static class FRConfiguration {
        public static final String CUBE_PATH_NAMESPACE = "swift_cube_path";
        public static final String SERVICE_ADDRESS_NAMESPACE = "SWIFT_RPC_SERVICE_CONFIG";
        public static final String ZIP_NAMESPACE = "SWIFT_USE_ZIP";
        public static final String REPOSITORY_CONF_NAMESPACE = "SWIFT_REPOSITORY_CONF";
        public static final String SWIFT_DB_CONF_NAMESPACE = "SWIFT_DB_CONF";
    }
}
