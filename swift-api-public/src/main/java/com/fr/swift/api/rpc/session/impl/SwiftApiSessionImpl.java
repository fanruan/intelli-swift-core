package com.fr.swift.api.rpc.session.impl;

import com.fr.swift.api.Api;
import com.fr.swift.api.result.SwiftApiResultSet;
import com.fr.swift.api.rpc.DataMaintenanceService;
import com.fr.swift.api.rpc.SelectService;
import com.fr.swift.api.rpc.bean.Column;
import com.fr.swift.api.rpc.session.SwiftApiSession;
import com.fr.swift.db.Schema;
import com.fr.swift.db.Where;
import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.result.serialize.SerializableDetailResultSet;
import com.fr.swift.source.Row;
import com.fr.swift.source.SwiftResultSet;

import java.sql.SQLException;
import java.util.List;

/**
 * @author yee
 * @date 2018/8/27
 */
public class SwiftApiSessionImpl implements SwiftApiSession, DataMaintenanceService, SelectService {

    private Api.SelectApi selectApi;
    private Api.DataMaintenanceApi dataMaintenanceApi;

    SwiftApiSessionImpl(Api.SelectApi selectApi, Api.DataMaintenanceApi dataMaintenanceApi) {
        this.selectApi = selectApi;
        this.dataMaintenanceApi = dataMaintenanceApi;
    }

    @Override
    public int insert(Schema schema, String tableName, List<String> fields, List<Row> rows) throws SQLException {
        return dataMaintenanceApi.insert(schema, tableName, fields, rows);
    }

    @Override
    public int insert(Schema schema, String tableName, List<Row> rows) throws SQLException {
        return dataMaintenanceApi.insert(schema, tableName, rows);
    }

    @Override
    public int insert(Schema schema, String tableName, String queryJson) throws SQLException {
        return dataMaintenanceApi.insert(schema, tableName, queryJson);
    }

    @Override
    public int delete(Schema schema, String tableName, Where where) {
        return dataMaintenanceApi.delete(schema, tableName, where);
    }

    @Override
    public int update(Schema schema, String tableName, SwiftResultSet resultSet, Where where) {
        return dataMaintenanceApi.update(schema, tableName, resultSet, where);
    }

    @Override
    public int createTable(Schema schema, String tableName, List<Column> columns) {
        return dataMaintenanceApi.createTable(schema, tableName, columns);
    }

    @Override
    public SwiftResultSet query(String queryJson) {
        SwiftResultSet result = selectApi.query(queryJson);
        try {
            return new SwiftApiResultSet((SerializableDetailResultSet) result, this);
        } catch (SQLException e) {
            SwiftLoggers.getLogger().error(e);
            return null;
        }
    }
}
