package com.fr.swift.api.rpc.session.impl;

import com.fr.swift.api.result.SwiftApiResultSet;
import com.fr.swift.api.rpc.Api;
import com.fr.swift.api.rpc.DataMaintenanceService;
import com.fr.swift.api.rpc.SelectService;
import com.fr.swift.api.rpc.bean.Column;
import com.fr.swift.db.SwiftDatabase;
import com.fr.swift.db.Where;
import com.fr.swift.exception.meta.SwiftMetaDataAbsentException;
import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.query.info.bean.query.AbstractSingleTableQueryInfoBean;
import com.fr.swift.query.query.QueryBean;
import com.fr.swift.result.serialize.SerializableDetailResultSet;
import com.fr.swift.source.Row;
import com.fr.swift.source.SwiftMetaData;
import com.fr.swift.source.SwiftResultSet;

import java.util.List;

/**
 * @author yee
 * @date 2018/8/27
 */
public class SwiftApiSessionImpl implements SwiftPublicApiSession, DataMaintenanceService, SelectService {

    private Api.SelectApi selectApi;
    private Api.DataMaintenanceApi dataMaintenanceApi;

    SwiftApiSessionImpl(Api.SelectApi selectApi, Api.DataMaintenanceApi dataMaintenanceApi) {
        this.selectApi = selectApi;
        this.dataMaintenanceApi = dataMaintenanceApi;
    }

    @Override
    public int insert(SwiftDatabase schema, String tableName, List<String> fields, List<Row> rows) throws Exception {
        return dataMaintenanceApi.insert(schema, tableName, fields, rows);
    }

    @Override
    public int insert(SwiftDatabase schema, String tableName, List<Row> rows) throws Exception {
        return dataMaintenanceApi.insert(schema, tableName, rows);
    }

    @Override
    public int insert(SwiftDatabase schema, String tableName, String queryJson) throws Exception {
        return dataMaintenanceApi.insert(schema, tableName, queryJson);
    }

    @Override
    public int delete(SwiftDatabase schema, String tableName, Where where) throws Exception {
        return dataMaintenanceApi.delete(schema, tableName, where);
    }

    @Override
    public int update(SwiftDatabase schema, String tableName, SwiftResultSet resultSet, Where where) throws Exception {
        return dataMaintenanceApi.update(schema, tableName, resultSet, where);
    }

    @Override
    public SwiftResultSet query(SwiftDatabase database, String queryJson) {
        try {
            SwiftResultSet result = selectApi.query(database, queryJson);
            if (result instanceof SerializableDetailResultSet) {
                return new SwiftApiResultSet((SerializableDetailResultSet) result, database, this);
            }
            return result;
        } catch (Exception e) {
            SwiftLoggers.getLogger().error(e);
            return null;
        }
    }

    @Override
    public void close() {
    }

    public boolean createTable(SwiftDatabase schema, String tableName, List<Column> columns) throws Exception {
        return dataMaintenanceApi.createTable(schema, tableName, columns) >= 0;
    }

    public void dropTable(SwiftDatabase schema, String tableName) throws Exception {
        dataMaintenanceApi.dropTable(schema, tableName);
    }

    public void truncateTable(SwiftDatabase schema, String tableName) throws Exception {
        dataMaintenanceApi.truncateTable(schema, tableName);
    }

    @Override
    public SwiftResultSet query(SwiftDatabase database, QueryBean queryBean) {
        if (queryBean instanceof AbstractSingleTableQueryInfoBean) {
            String tableName = findUniqueTableName(database, ((AbstractSingleTableQueryInfoBean) queryBean).getTableName());
            ((AbstractSingleTableQueryInfoBean) queryBean).setTableName(tableName);
        }
        return query(database, queryBean.toString());
    }

    private String findUniqueTableName(SwiftDatabase database, String tableName) {
        try {
            SwiftMetaData metaData = selectApi.detectiveMetaData(database, tableName);
            if (null != metaData) {
                return metaData.getId();
            }
        } catch (SwiftMetaDataAbsentException ignore) {
        }
        return tableName;
    }
}
