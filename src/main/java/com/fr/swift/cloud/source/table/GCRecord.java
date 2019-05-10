package com.fr.swift.cloud.source.table;

import com.fr.swift.base.meta.MetaDataColumnBean;
import com.fr.swift.cloud.source.CloudTableType;
import com.fr.swift.source.SwiftMetaDataColumn;

import java.io.File;
import java.sql.Types;
import java.util.Arrays;
import java.util.Collections;

/**
 * This class created on 2019/4/26
 *
 * @author Lucifer
 * @description
 */
public class GCRecord extends GCBaseTable {

    public static final String tableName = "gc_record";

    private SwiftMetaDataColumn gcTime = new MetaDataColumnBean("gcTime", Types.BIGINT);
    private SwiftMetaDataColumn gcType = new MetaDataColumnBean("gcType", Types.VARCHAR);
    private SwiftMetaDataColumn duration = new MetaDataColumnBean("duration", Types.BIGINT);

    private SwiftMetaDataColumn appId = new MetaDataColumnBean("appId", Types.VARCHAR);
    private SwiftMetaDataColumn yearMonth = new MetaDataColumnBean("yearMonth", Types.VARCHAR);

    {
        rawColumns.addAll(Arrays.asList(gcTime, gcType, duration));
        rawColumns = Collections.unmodifiableList(rawColumns);
    }

    public GCRecord(String appId, String yearMonth, File[] files) {
        super(appId, yearMonth, files);
    }

    @Override
    public CloudTableType getTableType() {
        return CloudTableType.GC;
    }

    @Override
    public String getTableName() {
        return tableName;
    }
}
