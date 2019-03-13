package com.fr.swift.cloud.source.table;

import com.fr.swift.base.meta.MetaDataColumnBean;
import com.fr.swift.source.SwiftMetaDataColumn;

import java.sql.Types;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
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

    private List<SwiftMetaDataColumn> rawColumns = new ArrayList<SwiftMetaDataColumn>();

    {
        rawColumns.addAll(Arrays.asList(id, time));
        rawColumns = Collections.unmodifiableList(rawColumns);
    }

    public FunctionUsageRate(String appId, String yearMonth) {
        super(appId, yearMonth);
    }

    @Override
    List<SwiftMetaDataColumn> getRawColumns() {
        return rawColumns;
    }

    @Override
    List<SwiftMetaDataColumn> getExtraColumns() {
        return new ArrayList<SwiftMetaDataColumn>();
    }

    @Override
    public String getTableName() {
        return tableName;
    }
}
