package com.fr.swift.cloud.source.table;

import com.fr.swift.base.meta.MetaDataColumnBean;
import com.fr.swift.base.meta.SwiftMetaDataBean;
import com.fr.swift.db.SwiftDatabase;
import com.fr.swift.source.SwiftMetaDataColumn;

import java.sql.Types;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by lyon on 2019/2/28.
 */
public class Execution extends BaseTable {

    public static final String tableName = "execution";

    public static final SwiftMetaDataColumn id = new MetaDataColumnBean("id", Types.VARCHAR);
    public static final SwiftMetaDataColumn tName = new MetaDataColumnBean("tName", Types.VARCHAR);
    public static final SwiftMetaDataColumn displayName = new MetaDataColumnBean("displayName", Types.VARCHAR);
    public static final SwiftMetaDataColumn time = new MetaDataColumnBean("time", Types.DATE);
    public static final SwiftMetaDataColumn memory = new MetaDataColumnBean("memory", Types.BIGINT);
    public static final SwiftMetaDataColumn type = new MetaDataColumnBean("type", Types.BIGINT);
    public static final SwiftMetaDataColumn consume = new MetaDataColumnBean("consume", Types.BIGINT);
    public static final SwiftMetaDataColumn sqlTime = new MetaDataColumnBean("sqlTime", Types.BIGINT);
    public static final SwiftMetaDataColumn reportId = new MetaDataColumnBean("reportId", Types.VARCHAR);
    public static final SwiftMetaDataColumn appId = new MetaDataColumnBean("appId", Types.VARCHAR);
    public static final SwiftMetaDataColumn yearMonth = new MetaDataColumnBean("yearMonth", Types.VARCHAR);

    private List<SwiftMetaDataColumn> columnList = new ArrayList<SwiftMetaDataColumn>();

    {
        columnList.addAll(Arrays.asList(id, tName, displayName, time, memory, type, consume, sqlTime, reportId, appId, yearMonth));
    }

    public Execution() {
    }

    public SwiftMetaDataBean createBean(SwiftDatabase db) {
        SwiftMetaDataBean bean = new SwiftMetaDataBean();
        bean.setSwiftDatabase(db);
        bean.setId(tableName);
        bean.setTableName(tableName);
        bean.setFields(columnList);
        return bean;
    }

    String getTableName() {
        return tableName;
    }

    @Override
    List<SwiftMetaDataColumn> getColumnList() {
        return columnList;
    }
}
