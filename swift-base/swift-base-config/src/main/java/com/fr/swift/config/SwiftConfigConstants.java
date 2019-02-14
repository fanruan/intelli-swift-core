package com.fr.swift.config;

import com.fr.swift.config.bean.SegLocationBean;
import com.fr.swift.config.bean.SegmentKeyBean;
import com.fr.swift.config.bean.SwiftColumnIdxConfBean;
import com.fr.swift.config.bean.SwiftConfigBean;
import com.fr.swift.base.meta.SwiftMetaDataBean;
import com.fr.swift.config.bean.SwiftServiceInfoBean;
import com.fr.swift.config.bean.SwiftTableAllotConfBean;
import com.fr.swift.config.bean.SwiftTablePathBean;

import java.util.Arrays;
import java.util.List;

/**
 * @author yee
 * @date 2018/5/28
 */
public class SwiftConfigConstants {
    public static final Class<?>[] ENTITIES = {
            SwiftMetaDataBean.TYPE,
            SegmentKeyBean.TYPE,
            SwiftConfigBean.TYPE,
            SegLocationBean.TYPE,
            SwiftServiceInfoBean.TYPE,
            SwiftTableAllotConfBean.TYPE,
            SwiftColumnIdxConfBean.TYPE,
            SwiftTablePathBean.TYPE
    };

    public static final String LOCALHOST = "LOCAL";


    public static class KeyWords {
        public static final List<String> COLUMN_KEY_WORDS = Arrays.asList("row_count", "all_show_index");
    }

    public static final int LONG_TEXT_LENGTH = 65536;

    public static class SegmentConfig {
        public static final String TABLE_NAME = "fine_swift_segments";
        public static final String COLUMN_SEGMENT_OWNER = "segmentOwner";
        public static final String COLUMN_SEGMENT_ORDER = "segmentOrder";
        public static final String COLUMN_STORE_TYPE = "storeType";
        public static final String COLUMN_SEGMENT_URI = "segmentUri";
    }

    public static class MetaDataConfig {
        public static final String TABLE_NAME = "fine_swift_metadata";
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
    }
}
