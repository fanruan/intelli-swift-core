package com.fr.swift.api.rpc.impl;

import com.fr.swift.annotation.RpcService;
import com.fr.swift.annotation.SwiftApi;
import com.fr.swift.api.rpc.TableService;
import com.fr.swift.api.rpc.bean.Column;
import com.fr.swift.config.SwiftConfigConstants;
import com.fr.swift.config.bean.MetaDataColumnBean;
import com.fr.swift.config.bean.SwiftMetaDataBean;
import com.fr.swift.config.service.SwiftMetaDataService;
import com.fr.swift.db.AlterTableAction;
import com.fr.swift.db.SwiftDatabase;
import com.fr.swift.db.Table;
import com.fr.swift.db.impl.AddColumnAction;
import com.fr.swift.db.impl.DropColumnAction;
import com.fr.swift.exception.meta.SwiftMetaDataAbsentException;
import com.fr.swift.source.SourceKey;
import com.fr.swift.source.SwiftMetaData;
import com.fr.swift.source.SwiftMetaDataColumn;
import com.fr.swift.source.core.MD5Utils;
import com.fr.swift.util.Crasher;
import com.fr.third.org.hibernate.criterion.Restrictions;
import com.fr.third.springframework.beans.factory.annotation.Autowired;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

/**
 * @author yee
 * @date 2018/8/27
 */
@RpcService(value = TableService.class, type = RpcService.RpcServiceType.EXTERNAL)
@SwiftApi
class TableServiceImpl implements TableService {
    @Autowired(required = false)
    SwiftMetaDataService swiftMetaDataService;

    @Override
    @SwiftApi
    public SwiftMetaData detectiveMetaData(SwiftDatabase schema, String tableName) throws SwiftMetaDataAbsentException {
        List<SwiftMetaData> metaDataList = swiftMetaDataService.find(Restrictions.eq(SwiftConfigConstants.MetaDataConfig.COLUMN_TABLE_NAME, tableName),
                Restrictions.eq(SwiftConfigConstants.MetaDataConfig.COLUMN_SWIFT_SCHEMA, schema));
        if (metaDataList.isEmpty()) {
            throw new SwiftMetaDataAbsentException(tableName);
        }
        return metaDataList.get(0);
    }

    @Override
    @SwiftApi
    public List<String> detectiveAllTableNames(SwiftDatabase schema) {
        List<SwiftMetaData> metaDataList = swiftMetaDataService.find(Restrictions.eq(SwiftConfigConstants.MetaDataConfig.COLUMN_SWIFT_SCHEMA, schema));
        if (metaDataList.isEmpty()) {
            return Collections.emptyList();
        } else {
            List<String> tableNames = new ArrayList<String>();
            try {
                for (SwiftMetaData metaData : metaDataList) {
                    tableNames.add(metaData.getTableName());
                }
            } catch (Exception ignore) {
            }
            return tableNames;
        }
    }

    @Override
    @SwiftApi
    public boolean isTableExists(SwiftDatabase schema, String tableName) {
        try {
            return null != detectiveMetaData(schema, tableName);
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    @SwiftApi(enable = false)
    synchronized
    public int createTable(SwiftDatabase schema, String tableName, List<Column> columns) {
        if (isTableExists(schema, tableName)) {
            Crasher.crash("Table " + tableName + " is already exists");
        }
        if (columns.isEmpty()) {
            Crasher.crash("Table " + tableName + " must contain at lease one column.");
        }
        SwiftMetaDataBean swiftMetaDataBean = new SwiftMetaDataBean();
        swiftMetaDataBean.setSwiftDatabase(schema);
        swiftMetaDataBean.setTableName(tableName);
        String uniqueKey = MD5Utils.getMD5String(new String[]{UUID.randomUUID().toString(), tableName});
        swiftMetaDataBean.setId(uniqueKey);
        List<SwiftMetaDataColumn> columnList = new ArrayList<SwiftMetaDataColumn>();
        for (Column column : columns) {
            columnList.add(new MetaDataColumnBean(column.getColumnName(), column.getColumnType()));
        }
        swiftMetaDataBean.setFields(columnList);
        if (swiftMetaDataService.addMetaData(uniqueKey, swiftMetaDataBean)) {
            return 1;
        }
        return -1;
    }

    @Override
    @SwiftApi(enable = false)
    public boolean addColumn(SwiftDatabase schema, String tableName, Column column) throws SQLException {
        SwiftMetaData metaData = detectiveMetaData(schema, tableName);
        if (metaData.getFieldNames().contains(column.getColumnName())) {
            throw new SQLException("Column " + column.getColumnName() + " is already exists!");
        }
        Table table = com.fr.swift.db.impl.SwiftDatabase.getInstance().getTable(new SourceKey(metaData.getId()));
        SwiftMetaDataColumn metaDataColumn = new MetaDataColumnBean(column.getColumnName(), column.getColumnType());
        AlterTableAction action = new AddColumnAction(metaDataColumn);
        return alterTable(table, action);
    }

    @Override
    @SwiftApi(enable = false)
    public boolean dropColumn(SwiftDatabase schema, String tableName, String columnName) throws SQLException {
        SwiftMetaData metaData = detectiveMetaData(schema, tableName);
        SwiftMetaDataColumn metaDataColumn = metaData.getColumn(columnName);
        Table table = com.fr.swift.db.impl.SwiftDatabase.getInstance().getTable(new SourceKey(metaData.getId()));
        AlterTableAction action = new DropColumnAction(metaDataColumn);
        return alterTable(table, action);
    }

    private boolean alterTable(Table table, AlterTableAction action) throws SQLException {
        action.alter(table);
        return true;
    }
}
