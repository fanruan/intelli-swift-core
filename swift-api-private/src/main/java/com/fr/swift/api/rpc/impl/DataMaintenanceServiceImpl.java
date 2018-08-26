package com.fr.swift.api.rpc.impl;

import com.fr.swift.annotation.RpcService;
import com.fr.swift.annotation.RpcServiceType;
import com.fr.swift.api.rpc.DataMaintenanceService;
import com.fr.swift.api.rpc.DetectService;
import com.fr.swift.api.rpc.bean.Column;
import com.fr.swift.config.bean.SwiftMetaDataBean;
import com.fr.swift.context.SwiftContext;
import com.fr.swift.db.Where;
import com.fr.swift.db.impl.SwiftDatabase;
import com.fr.swift.exception.meta.SwiftMetaDataException;
import com.fr.swift.service.RealtimeService;
import com.fr.swift.source.Row;
import com.fr.swift.source.SourceKey;
import com.fr.swift.source.SwiftMetaData;
import com.fr.swift.source.SwiftMetaDataColumn;
import com.fr.swift.source.SwiftResultSet;
import com.fr.swift.util.ServiceBeanFactory;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author yee
 * @date 2018/8/23
 */
@RpcService(value = DataMaintenanceService.class, type = RpcServiceType.EXTERNAL)
class DataMaintenanceServiceImpl implements DataMaintenanceService {

    @Override
    public int insert(String tableName, List<String> fields, List<Row> rows) throws SQLException {
        SwiftMetaDataBean metaData = (SwiftMetaDataBean) SwiftContext.get().getBean(DetectService.class).detectiveMetaData(tableName);
        insert(tableName, new InsertResultSet(metaData, fields, rows));
        return rows.size();
    }

    @Override
    public int insert(String tableName, List<Row> rows) throws SQLException {
        return insert(tableName, null, rows);
    }

    @Override
    public int insert(String tableName, SwiftResultSet resultSet) throws SQLException {
        SwiftMetaDataBean metaData = (SwiftMetaDataBean) SwiftContext.get().getBean(DetectService.class).detectiveMetaData(tableName);
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
    public int delete(String tableName, Where where) {
        return 0;
    }

    @Override
    public int update(String tableName, SwiftResultSet resultSet, Where where) {
        return 0;
    }

    @Override
    public int createTable(SwiftDatabase.Schema schema, String tableName, List<Column> columns) {
        return 0;
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
                ((SwiftMetaDataBean) insertMetaData).setSwiftSchema(bean.getSwiftSchema());
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
