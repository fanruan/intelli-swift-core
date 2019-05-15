package com.fr.swift.cloud.source.table;

import com.fr.swift.base.meta.MetaDataColumnBean;
import com.fr.swift.source.SwiftMetaDataColumn;

import java.sql.Types;
import java.util.Arrays;
import java.util.Collections;

/**
 * Created by lyon on 2019/2/28.
 */
@Deprecated
public class ExecutionSql extends CSVBaseTable {

    public static final String tableName = "execution_sql";

    public static final SwiftMetaDataColumn time = new MetaDataColumnBean("time", Types.DATE);
    public static final SwiftMetaDataColumn executionId = new MetaDataColumnBean("executionId", Types.VARCHAR);
    public static final SwiftMetaDataColumn dsName = new MetaDataColumnBean("dsName", Types.VARCHAR);
    public static final SwiftMetaDataColumn sqlTime = new MetaDataColumnBean("sqlTime", Types.BIGINT);
    public static final SwiftMetaDataColumn rows = new MetaDataColumnBean("rows", Types.BIGINT);
    public static final SwiftMetaDataColumn columns = new MetaDataColumnBean("columns", Types.BIGINT);
    public static final SwiftMetaDataColumn appId = new MetaDataColumnBean("appId", Types.VARCHAR);
    public static final SwiftMetaDataColumn yearMonth = new MetaDataColumnBean("yearMonth", Types.VARCHAR);

    {
        rawColumns.addAll(Arrays.asList(time, executionId, dsName, sqlTime, rows, columns));
        rawColumns = Collections.unmodifiableList(rawColumns);
    }

    public ExecutionSql(String appId, String yearMonth) {
        super(appId, yearMonth);
    }

    @Override
    public String getTableName() {
        return tableName;
    }
}
