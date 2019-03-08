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
public class RealTimeUsage extends BaseTable {

    public static final String tableName = "real_time_usage";

    private SwiftMetaDataColumn time = new MetaDataColumnBean("time", Types.DATE);
    private SwiftMetaDataColumn cpu = new MetaDataColumnBean("cpu", Types.DOUBLE);
    private SwiftMetaDataColumn memory = new MetaDataColumnBean("memory", Types.BIGINT);
    private SwiftMetaDataColumn appId = new MetaDataColumnBean("appId", Types.VARCHAR);
    private SwiftMetaDataColumn yearMonth = new MetaDataColumnBean("yearMonth", Types.VARCHAR);

    private List<SwiftMetaDataColumn> rawColumns = new ArrayList<SwiftMetaDataColumn>();

    {
        rawColumns.addAll(Arrays.asList(time, cpu, memory));
        rawColumns = Collections.unmodifiableList(rawColumns);
    }

    public RealTimeUsage(String appId, String yearMonth) {
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
