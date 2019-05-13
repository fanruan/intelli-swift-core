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
public class RealTimeUsage extends CSVBaseTable {

    public static final String tableName = "real_time_usage";

    private SwiftMetaDataColumn time = new MetaDataColumnBean("time", Types.DATE);
    //    private SwiftMetaDataColumn node = new MetaDataColumnBean("node", Types.VARCHAR);
    private SwiftMetaDataColumn cpu = new MetaDataColumnBean("cpu", Types.DOUBLE);
    private SwiftMetaDataColumn memory = new MetaDataColumnBean("memory", Types.BIGINT);
//    private SwiftMetaDataColumn sessionNum = new MetaDataColumnBean("sessionNum", Types.BIGINT);
//    private SwiftMetaDataColumn onlineNum = new MetaDataColumnBean("onlineNum", Types.BIGINT);
//    private SwiftMetaDataColumn pid = new MetaDataColumnBean("pid", Types.BIGINT);

    private SwiftMetaDataColumn appId = new MetaDataColumnBean("appId", Types.VARCHAR);
    private SwiftMetaDataColumn yearMonth = new MetaDataColumnBean("yearMonth", Types.VARCHAR);

    {
//        rawColumns.addAll(Arrays.asList(time, node, cpu, memory, sessionNum, onlineNum, pid));
        rawColumns.addAll(Arrays.asList(time, cpu, memory));
        rawColumns = Collections.unmodifiableList(rawColumns);
    }

    public RealTimeUsage(String appId, String yearMonth) {
        super(appId, yearMonth);
    }

    @Override
    public String getTableName() {
        return tableName;
    }
}
