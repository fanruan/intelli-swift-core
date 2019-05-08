package com.fr.swift.api.rpc.impl;

import com.fr.swift.SwiftContext;
import com.fr.swift.annotation.SwiftApi;
import com.fr.swift.api.rpc.DataMaintenanceService;
import com.fr.swift.api.rpc.SelectService;
import com.fr.swift.api.rpc.TableService;
import com.fr.swift.base.meta.SwiftMetaDataBean;
import com.fr.swift.basics.annotation.ProxyService;
import com.fr.swift.basics.base.selector.ProxySelector;
import com.fr.swift.beans.annotation.SwiftBean;
import com.fr.swift.db.SwiftDatabase;
import com.fr.swift.db.Table;
import com.fr.swift.db.Where;
import com.fr.swift.event.global.DeleteEvent;
import com.fr.swift.exception.meta.SwiftMetaDataException;
import com.fr.swift.property.SwiftProperty;
import com.fr.swift.result.SwiftResultSet;
import com.fr.swift.service.RealtimeService;
import com.fr.swift.service.listener.RemoteSender;
import com.fr.swift.service.listener.SwiftServiceListenerManager;
import com.fr.swift.source.Row;
import com.fr.swift.source.SourceKey;
import com.fr.swift.source.SwiftMetaData;
import com.fr.swift.source.SwiftMetaDataColumn;
import com.fr.swift.structure.Pair;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author yee
 * @date 2018/8/23
 */
@ProxyService(value = DataMaintenanceService.class, type = ProxyService.ServiceType.EXTERNAL)
@SwiftApi
@SwiftBean
public class DataMaintenanceServiceImpl implements DataMaintenanceService {
    private TableService tableService = SwiftContext.get().getBean(TableService.class);

    @Override
    @SwiftApi
    public int insert(SwiftDatabase schema, String tableName, List<String> fields, List<Row> rows) throws SQLException {
        SwiftMetaDataBean metaData = (SwiftMetaDataBean) tableService.detectiveMetaData(schema, tableName);
        insert(schema, tableName, new InsertResultSet(metaData, fields, rows));
        return rows.size();
    }

    @Override
    @SwiftApi
    public int insert(SwiftDatabase schema, String tableName, List<Row> rows) throws SQLException {
        return insert(schema, tableName, null, rows);
    }

    @Override
    @SwiftApi
    public int insert(SwiftDatabase schema, String tableName, String queryJson) throws Exception {
        SwiftResultSet resultSet = SwiftContext.get().getBean(SelectService.class).query(schema, queryJson);
        return insert(schema, tableName, resultSet);
    }

    private int insert(SwiftDatabase schema, String tableName, SwiftResultSet resultSet) throws SQLException {
        SwiftMetaDataBean metaData = (SwiftMetaDataBean) tableService.detectiveMetaData(schema, tableName);
        SourceKey sourceKey = new SourceKey(metaData.getId());
        try {
            SwiftContext.get().getBean(RealtimeService.class).insert(sourceKey, resultSet);
        } catch (SQLException e) {
            throw e;
        } catch (Exception e) {
            return -1;
        }
        return resultSet.getFetchSize();
    }

    @Override
    @SwiftApi
    public int delete(SwiftDatabase schema, String tableName, Where where) throws SQLException {
        try {
            SwiftMetaDataBean metaData = (SwiftMetaDataBean) tableService.detectiveMetaData(schema, tableName);
            if (null == metaData) {
                return 0;
            }
            DeleteEvent event = new DeleteEvent(Pair.of(new SourceKey(metaData.getId()), where));
            if (SwiftProperty.getProperty().isCluster()) {
                ProxySelector.getInstance().getFactory().getProxy(RemoteSender.class).trigger(event);
            } else {
                SwiftServiceListenerManager.getInstance().triggerEvent(event);
            }
            return 0;
        } catch (Exception e) {
            throw new SQLException("Table which named " + tableName + " is not exists", e);
        }
    }

    @Override
    @SwiftApi(enable = false)
    public int update(SwiftDatabase schema, String tableName, SwiftResultSet resultSet, Where where) throws SQLException {

        try {
            SwiftMetaDataBean metaData = (SwiftMetaDataBean) tableService.detectiveMetaData(schema, tableName);
            if (null == metaData) {
                return 0;
            }
            Table table = com.fr.swift.db.impl.SwiftDatabase.getInstance().getTable(new SourceKey(metaData.getId()));
            throw new UnsupportedOperationException();
//            return table.update(where, resultSet);
        } catch (Exception e) {
            throw new SQLException("Table which named " + tableName + " is not exists", e);
        }
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
        public SwiftMetaData getMetaData() {
            return insertMetaData;
        }

        @Override
        public boolean hasNext() {
            return cursor < rows.size();
        }

        @Override
        public Row getNextRow() {
            return rows.get(cursor++);
        }

        @Override
        public void close() {

        }
    }
}
