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
public class RealTimeUsage extends BaseTable {

    public static final String tableName = "real_time_usage";

    private SwiftMetaDataColumn time = new MetaDataColumnBean("time", Types.DATE);
    private SwiftMetaDataColumn cpu = new MetaDataColumnBean("cpu", Types.DOUBLE);
    private SwiftMetaDataColumn memory = new MetaDataColumnBean("memory", Types.BIGINT);
    private SwiftMetaDataColumn appId = new MetaDataColumnBean("appId", Types.VARCHAR);
    private SwiftMetaDataColumn yearMonth = new MetaDataColumnBean("yearMonth", Types.VARCHAR);

    private List<SwiftMetaDataColumn> columnList = new ArrayList<SwiftMetaDataColumn>();

    {
        columnList.addAll(Arrays.asList(time, cpu, memory, appId, yearMonth));
    }

    public RealTimeUsage() {
    }

    String getTableName() {
        return tableName;
    }

    @Override
    List<SwiftMetaDataColumn> getColumnList() {
        return columnList;
    }
}
