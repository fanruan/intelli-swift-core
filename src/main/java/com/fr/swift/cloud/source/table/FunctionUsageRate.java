package com.fr.swift.cloud.source.table;

import com.fr.swift.base.meta.MetaDataColumnBean;
import com.fr.swift.source.SwiftMetaDataColumn;

import java.sql.Types;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by lyon on 2019/2/28.
 */
public class FunctionUsageRate extends BaseTable {

    public static final String tableName = "function_usage_rate";

    private SwiftMetaDataColumn id = new MetaDataColumnBean("id", Types.VARCHAR);
    private SwiftMetaDataColumn time = new MetaDataColumnBean("time", Types.DATE);
    private SwiftMetaDataColumn appId = new MetaDataColumnBean("appId", Types.VARCHAR);
    private SwiftMetaDataColumn yearMonth = new MetaDataColumnBean("yearMonth", Types.VARCHAR);

    private List<SwiftMetaDataColumn> columnList = new ArrayList<SwiftMetaDataColumn>();

    {
        columnList.addAll(Arrays.asList(id, time, appId, yearMonth));
    }

    public FunctionUsageRate() {
    }

    String getTableName() {
        return tableName;
    }

    @Override
    List<SwiftMetaDataColumn> getColumnList() {
        return columnList;
    }
}
