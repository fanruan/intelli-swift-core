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

    private SwiftMetaDataColumn time = new MetaDataColumnBean("time", Types.DATE);
    private SwiftMetaDataColumn executionId = new MetaDataColumnBean("executionId", Types.VARCHAR);
    private SwiftMetaDataColumn dsName = new MetaDataColumnBean("dsName", Types.VARCHAR);
    private SwiftMetaDataColumn sqlTime = new MetaDataColumnBean("sqlTime", Types.BIGINT);
    private SwiftMetaDataColumn rows = new MetaDataColumnBean("rows", Types.BIGINT);
    private SwiftMetaDataColumn columns = new MetaDataColumnBean("columns", Types.BIGINT);
    private SwiftMetaDataColumn appId = new MetaDataColumnBean("appId", Types.VARCHAR);
    private SwiftMetaDataColumn yearMonth = new MetaDataColumnBean("yearMonth", Types.VARCHAR);

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
