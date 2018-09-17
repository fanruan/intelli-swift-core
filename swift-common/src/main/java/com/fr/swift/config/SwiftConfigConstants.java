package com.fr.swift.config;

import com.fr.swift.config.entity.SwiftColumnIndexingConf;
import com.fr.swift.config.entity.SwiftConfigEntity;
import com.fr.swift.config.entity.SwiftMetaDataEntity;
import com.fr.swift.config.entity.SwiftSegmentEntity;
import com.fr.swift.config.entity.SwiftSegmentLocationEntity;
import com.fr.swift.config.entity.SwiftServiceInfoEntity;
import com.fr.swift.config.entity.SwiftTableIndexingConf;
import com.fr.swift.config.entity.SwiftTablePathEntity;

import java.util.Arrays;
import java.util.List;

/**
 * @author yee
 * @date 2018/5/28
 */
public class SwiftConfigConstants {
    public static final Class<?>[] ENTITIES = {
            SwiftMetaDataEntity.class,
            SwiftSegmentEntity.class,
            SwiftServiceInfoEntity.class,
            SwiftSegmentLocationEntity.class,
            SwiftConfigEntity.class,
            SwiftTableIndexingConf.class,
            SwiftColumnIndexingConf.class,
            SwiftTablePathEntity.class
    };


    public static class KeyWords {
        public static final List<String> COLUMN_KEY_WORDS = Arrays.asList("row_count", "all_show_index");
    }

    public static final int LONG_TEXT_LENGTH = 65536;

    public static class SegmentConfig {
        public static final String COLUMN_SEGMENT_OWNER = "segmentOwner";
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
        public static final String SERVICE_ADDRESS_NAMESPACE = "SWIFT_RPC_SERVICE_ADDRESS";
        public static final String ZIP_NAMESPACE = "SWIFT_USE_ZIP";
        public static final String REPOSITORY_CONF_NAMESPACE = "SWIFT_REPOSITORY_CONF";
        public static final String SWIFT_DB_CONF_NAMESPACE = "SWIFT_DB_CONF";
    }
}
