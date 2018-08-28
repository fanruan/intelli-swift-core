package com.fr.swift.api.rpc.session.impl;

import com.fr.swift.api.Api;
import com.fr.swift.api.result.SwiftApiResultSet;
import com.fr.swift.api.rpc.DataMaintenanceService;
import com.fr.swift.api.rpc.SelectService;
import com.fr.swift.api.rpc.TableService;
import com.fr.swift.api.rpc.bean.Column;
import com.fr.swift.api.rpc.session.SwiftApiSession;
import com.fr.swift.db.SwiftDatabase;
import com.fr.swift.db.Where;
import com.fr.swift.exception.meta.SwiftMetaDataAbsentException;
import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.query.info.bean.query.QueryInfoBeanFactory;
import com.fr.swift.result.serialize.SerializableDetailResultSet;
import com.fr.swift.source.Row;
import com.fr.swift.source.SwiftMetaData;
import com.fr.swift.source.SwiftResultSet;

import java.sql.SQLException;
import java.util.List;

/**
 * @author yee
 * @date 2018/8/27
 */
public class SwiftApiSessionImpl implements SwiftApiSession, DataMaintenanceService, SelectService, TableService {

    private Api.SelectApi selectApi;
    private Api.DataMaintenanceApi dataMaintenanceApi;
    private QueryInfoBeanFactory queryInfoBeanFactory = new QueryInfoBeanFactory();

    SwiftApiSessionImpl(Api.SelectApi selectApi, Api.DataMaintenanceApi dataMaintenanceApi) {
        this.selectApi = selectApi;
        this.dataMaintenanceApi = dataMaintenanceApi;
    }

    @Override
    public int insert(SwiftDatabase schema, String tableName, List<String> fields, List<Row> rows) throws SQLException {
        return dataMaintenanceApi.insert(schema, tableName, fields, rows);
    }

    @Override
    public int insert(SwiftDatabase schema, String tableName, List<Row> rows) throws SQLException {
        return dataMaintenanceApi.insert(schema, tableName, rows);
    }

    @Override
    public int insert(SwiftDatabase schema, String tableName, String queryJson) throws SQLException {
        return dataMaintenanceApi.insert(schema, tableName, queryJson);
    }

    @Override
    public int delete(SwiftDatabase schema, String tableName, Where where) {
        return dataMaintenanceApi.delete(schema, tableName, where);
    }

    @Override
    public int update(SwiftDatabase schema, String tableName, SwiftResultSet resultSet, Where where) {
        return dataMaintenanceApi.update(schema, tableName, resultSet, where);
    }

    @Override
    public int createTable(SwiftDatabase schema, String tableName, List<Column> columns) {
        return dataMaintenanceApi.createTable(schema, tableName, columns);
    }

    @Override
    public SwiftResultSet query(SwiftDatabase database, String queryJson) {
        try {
            SwiftResultSet result = selectApi.query(database, queryJson);
            return new SwiftApiResultSet((SerializableDetailResultSet) result, database, this);
        } catch (Exception e) {
            SwiftLoggers.getLogger().error(e);
            return null;
        }
    }

    @Override
    public SwiftMetaData detectiveMetaData(SwiftDatabase schema, String tableName) throws SwiftMetaDataAbsentException {
        return selectApi.detectiveMetaData(schema, tableName);
    }

    @Override
    public boolean isTableExists(SwiftDatabase schema, String tableName) throws SwiftMetaDataAbsentException {
        return selectApi.isTableExists(schema, tableName);
    }
}
