package com.fr.swift.cloud.source.table;

import com.fr.swift.base.meta.MetaDataColumnBean;
import com.fr.swift.source.SwiftMetaDataColumn;

import java.sql.Types;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Created by lyon on 2019/2/28.
 */
@Deprecated
public class Execution extends CSVBaseTable {

    public static final String tableName = "execution";

    public static final SwiftMetaDataColumn id = new MetaDataColumnBean("id", Types.VARCHAR);
    public static final SwiftMetaDataColumn tName = new MetaDataColumnBean("tName", Types.VARCHAR);
    public static final SwiftMetaDataColumn displayName = new MetaDataColumnBean("displayName", Types.VARCHAR);
    public static final SwiftMetaDataColumn time = new MetaDataColumnBean("time", Types.DATE);
    public static final SwiftMetaDataColumn memory = new MetaDataColumnBean("memory", Types.BIGINT);
    public static final SwiftMetaDataColumn type = new MetaDataColumnBean("type", Types.BIGINT);
    public static final SwiftMetaDataColumn consume = new MetaDataColumnBean("consume", Types.BIGINT);
    public static final SwiftMetaDataColumn sqlTime = new MetaDataColumnBean("sqlTime", Types.BIGINT);
    public static final SwiftMetaDataColumn reportId = new MetaDataColumnBean("reportId", Types.VARCHAR);
    public static final SwiftMetaDataColumn coreConsume = new MetaDataColumnBean("coreConsume", Types.BIGINT);
    public static final SwiftMetaDataColumn appId = new MetaDataColumnBean("appId", Types.VARCHAR);
    public static final SwiftMetaDataColumn yearMonth = new MetaDataColumnBean("yearMonth", Types.VARCHAR);


    {
        rawColumns.addAll(Arrays.asList(id, tName, displayName, time, memory, type, consume, sqlTime, reportId));
        rawColumns = Collections.unmodifiableList(rawColumns);
    }

    public Execution(String appId, String yearMonth) {
        super(appId, yearMonth);
    }

    @Override
    public List<SwiftMetaDataColumn> getExtraColumns() {
        return Collections.singletonList(coreConsume);
    }

    @Override
    public String getTableName() {
        return tableName;
    }
}
