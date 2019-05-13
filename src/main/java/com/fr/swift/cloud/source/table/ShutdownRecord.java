package com.fr.swift.cloud.source.table;

import com.fr.swift.base.meta.MetaDataColumnBean;
import com.fr.swift.source.SwiftMetaDataColumn;

import java.sql.Types;
import java.util.Arrays;
import java.util.Collections;

/**
 * This class created on 2019/4/26
 *
 * @author Lucifer
 * @description
 */
// TODO: 2019/5/10 by lucifer gc demo待重构
public class ShutdownRecord extends CSVBaseTable {

    public static final String tableName = "shutdown_record";

    private SwiftMetaDataColumn time = new MetaDataColumnBean("time", Types.BIGINT);
    private SwiftMetaDataColumn node = new MetaDataColumnBean("node", Types.VARCHAR);
    private SwiftMetaDataColumn pid = new MetaDataColumnBean("pid", Types.BIGINT);
    private SwiftMetaDataColumn startTime = new MetaDataColumnBean("startTime", Types.DATE);
    private SwiftMetaDataColumn upTime = new MetaDataColumnBean("upTime", Types.DATE);
    private SwiftMetaDataColumn signalName = new MetaDataColumnBean("signalName", Types.VARCHAR);

    private SwiftMetaDataColumn appId = new MetaDataColumnBean("appId", Types.VARCHAR);
    private SwiftMetaDataColumn yearMonth = new MetaDataColumnBean("yearMonth", Types.VARCHAR);

    {
        rawColumns.addAll(Arrays.asList(time, node, pid, startTime, upTime, signalName));
        rawColumns = Collections.unmodifiableList(rawColumns);
    }

    public ShutdownRecord(String appId, String yearMonth) {
        super(appId, yearMonth);
    }

    @Override
    public String getTableName() {
        return tableName;
    }
}
