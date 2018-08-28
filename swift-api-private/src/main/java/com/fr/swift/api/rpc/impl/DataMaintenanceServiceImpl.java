package com.fr.swift.api.rpc.impl;

import com.fr.swift.annotation.RpcService;
import com.fr.swift.annotation.RpcServiceType;
import com.fr.swift.api.rpc.DataMaintenanceService;
import com.fr.swift.api.rpc.SelectService;
import com.fr.swift.api.rpc.TableService;
import com.fr.swift.api.rpc.bean.Column;
import com.fr.swift.config.bean.MetaDataColumnBean;
import com.fr.swift.config.bean.SwiftMetaDataBean;
import com.fr.swift.config.service.SwiftMetaDataService;
import com.fr.swift.context.SwiftContext;
import com.fr.swift.db.SwiftDatabase;
import com.fr.swift.db.Where;
import com.fr.swift.exception.meta.SwiftMetaDataException;
import com.fr.swift.service.RealtimeService;
import com.fr.swift.source.Row;
import com.fr.swift.source.SourceKey;
import com.fr.swift.source.SwiftMetaData;
import com.fr.swift.source.SwiftMetaDataColumn;
import com.fr.swift.source.SwiftResultSet;
import com.fr.swift.source.core.MD5Utils;
import com.fr.swift.util.Crasher;
import com.fr.swift.util.ServiceBeanFactory;
import com.fr.third.springframework.beans.factory.annotation.Autowired;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

/**
 * @author yee
 * @date 2018/8/23
 */
@RpcService(value = DataMaintenanceService.class, type = RpcServiceType.EXTERNAL)
class DataMaintenanceServiceImpl implements DataMaintenanceService {
    @Autowired(required = false)
    private TableService tableService;
    @Autowired(required = false)
    private SwiftMetaDataService metaDataService;

    @Override
    public int insert(SwiftDatabase schema, String tableName, List<String> fields, List<Row> rows) throws SQLException {
        SwiftMetaDataBean metaData = (SwiftMetaDataBean) SwiftContext.get().getBean(TableService.class).detectiveMetaData(schema, tableName);
        insert(schema, tableName, new InsertResultSet(metaData, fields, rows));
        return rows.size();
    }

    @Override
    public int insert(SwiftDatabase schema, String tableName, List<Row> rows) throws SQLException {
        return insert(schema, tableName, null, rows);
    }

    @Override
    public int insert(SwiftDatabase schema, String tableName, String queryJson) throws SQLException {
        SwiftResultSet resultSet = SwiftContext.get().getBean(SelectService.class).query(schema, queryJson);
        return insert(schema, tableName, resultSet);
    }

    private int insert(SwiftDatabase schema, String tableName, SwiftResultSet resultSet) throws SQLException {
        SwiftMetaDataBean metaData = (SwiftMetaDataBean) SwiftContext.get().getBean(TableService.class).detectiveMetaData(schema, tableName);
        SourceKey sourceKey = new SourceKey(metaData.getId());
        try {
            getRealTimeService().insert(sourceKey, resultSet);
        } catch (SQLException e) {
            throw e;
        } catch (Exception e) {
            return -1;
        }
        return resultSet.getFetchSize();
    }

    @Override
    public int delete(SwiftDatabase schema, String tableName, Where where) {
        return 0;
    }

    @Override
    public int update(SwiftDatabase schema, String tableName, SwiftResultSet resultSet, Where where) {
        return 0;
    }

    @Override
    public int createTable(SwiftDatabase schema, String tableName, List<Column> columns) {
        if (tableService.isTableExists(schema, tableName)) {
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
        if (metaDataService.addMetaData(uniqueKey, swiftMetaDataBean)) {
            return 1;
        }
        return -1;
    }

    private RealtimeService getRealTimeService() throws SQLException {
        List<com.fr.swift.service.SwiftService> services = ServiceBeanFactory.getSwiftServiceByNames(Collections.singleton("realtime"));
        if (services.isEmpty()) {
            throw new SQLException("Can not find any RealTime service!");
        }
        return (RealtimeService) services.get(0);
    }

    private class InsertResultSet implements SwiftResultSet {

        private SwiftMetaData base;
        private SwiftMetaData insertMetaData;
        private int cursor = 0;
        private List<Row> rows;

        public InsertResultSet(SwiftMetaData base, List<String> fields, List<Row> rows) throws SQLException {
            this.base = base;
            this.rows = rows;
            initInsertMetaData(fields);
        }

        private void initInsertMetaData(List<String> fields) throws SwiftMetaDataException {
            if (null == fields || fields.isEmpty()) {
                insertMetaData = base;
            } else {
                List<SwiftMetaDataColumn> columns = new ArrayList<SwiftMetaDataColumn>();
                for (String field : fields) {
                    columns.add(base.getColumn(field));
                }
                SwiftMetaDataBean bean = (SwiftMetaDataBean) base;
                insertMetaData = new SwiftMetaDataBean(bean.getId(), bean.getSchemaName(), bean.getTableName(), bean.getRemark(), columns);
                ((SwiftMetaDataBean) insertMetaData).setSwiftDatabase(bean.getSwiftDatabase());
            }
        }

        @Override
        public int getFetchSize() {
            return rows.size();
        }

        @Override
        public SwiftMetaData getMetaData() throws SQLException {
            return insertMetaData;
        }

        @Override
        public boolean hasNext() throws SQLException {
            return cursor < rows.size();
        }

        @Override
        public Row getNextRow() throws SQLException {
            return rows.get(cursor++);
        }

        @Override
        public void close() throws SQLException {

        }
    }
}
