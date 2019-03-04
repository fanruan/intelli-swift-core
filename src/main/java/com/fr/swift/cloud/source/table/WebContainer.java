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
public class WebContainer extends BaseTable {

    public static final String tableName = "web_container";

    private SwiftMetaDataColumn time = new MetaDataColumnBean("time", Types.DATE);
    private SwiftMetaDataColumn containerType = new MetaDataColumnBean("containerType", Types.VARCHAR);
    private SwiftMetaDataColumn containerMemory = new MetaDataColumnBean("containerMemory", Types.BIGINT);
    private SwiftMetaDataColumn cpu = new MetaDataColumnBean("cpu", Types.BIGINT);
    private SwiftMetaDataColumn disk = new MetaDataColumnBean("disk", Types.VARCHAR);
    private SwiftMetaDataColumn appId = new MetaDataColumnBean("appId", Types.VARCHAR);
    private SwiftMetaDataColumn yearMonth = new MetaDataColumnBean("yearMonth", Types.VARCHAR);

    private List<SwiftMetaDataColumn> columnList = new ArrayList<SwiftMetaDataColumn>();

    {
        columnList.addAll(Arrays.asList(time, containerType, containerMemory, cpu, disk, appId, yearMonth));
    }

    public WebContainer() {
    }

    String getTableName() {
        return tableName;
    }

    @Override
    List<SwiftMetaDataColumn> getColumnList() {
        return columnList;
    }
}
