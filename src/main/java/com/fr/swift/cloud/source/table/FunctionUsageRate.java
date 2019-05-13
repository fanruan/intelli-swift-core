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
public class FunctionUsageRate extends CSVBaseTable {

    public static final String tableName = "function_usage_rate";

    private SwiftMetaDataColumn id = new MetaDataColumnBean("id", Types.VARCHAR);
    private SwiftMetaDataColumn time = new MetaDataColumnBean("time", Types.DATE);
    private SwiftMetaDataColumn appId = new MetaDataColumnBean("appId", Types.VARCHAR);
    private SwiftMetaDataColumn yearMonth = new MetaDataColumnBean("yearMonth", Types.VARCHAR);

    {
        rawColumns.addAll(Arrays.asList(id, time));
        rawColumns = Collections.unmodifiableList(rawColumns);
    }

    public FunctionUsageRate(String appId, String yearMonth) {
        super(appId, yearMonth);
    }

    @Override
    public String getTableName() {
        return tableName;
    }
}
