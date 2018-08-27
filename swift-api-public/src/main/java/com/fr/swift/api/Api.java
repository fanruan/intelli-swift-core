package com.fr.swift.api;

import com.fr.swift.api.rpc.DataMaintenanceService;
import com.fr.swift.api.rpc.SelectService;
import com.fr.swift.api.rpc.bean.Column;
import com.fr.swift.api.rpc.holder.InternalServiceAddressHolder;
import com.fr.swift.api.rpc.invoke.ApiProxyFactory;
import com.fr.swift.db.Schema;
import com.fr.swift.db.Where;
import com.fr.swift.source.Row;
import com.fr.swift.source.SwiftResultSet;

import java.sql.SQLException;
import java.util.Collections;
import java.util.List;

/**
 * @author yee
 * @date 2018/8/24
 */
public class API implements DataMaintenanceService, SelectService {
    protected InternalServiceAddressHolder holder;

    private API(String address) {
        holder = InternalServiceAddressHolder.getHolder(address);
    }

    public static API connect(String address) {
        return new API(address);
    }

    @Override
    public int insert(Schema schema, String tableName, List<String> fields, List<Row> rows) throws SQLException {
        String address = holder.nextRealTimeAddress();
        DataMaintenanceService service = ApiProxyFactory.getProxy(DataMaintenanceService.class, address);
        return service.insert(schema, tableName, fields, rows);
    }

    @Override
    public int insert(Schema schema, String tableName, List<Row> rows) throws SQLException {
        return insert(schema, tableName, Collections.<String>emptyList(), rows);
    }

    @Override
    public int insert(Schema schema, String tableName, String queryJson) throws SQLException {
        String address = holder.nextRealTimeAddress();
        DataMaintenanceService service = ApiProxyFactory.getProxy(DataMaintenanceService.class, address);
        return service.insert(schema, tableName, queryJson);
    }

    @Override
    public int delete(Schema schema, String tableName, Where where) {
        String address = holder.nextRealTimeAddress();
        DataMaintenanceService service = ApiProxyFactory.getProxy(DataMaintenanceService.class, address);
        return service.delete(schema, tableName, where);
    }

    @Override
    public int update(Schema schema, String tableName, SwiftResultSet resultSet, Where where) {
        String address = holder.nextRealTimeAddress();
        DataMaintenanceService service = ApiProxyFactory.getProxy(DataMaintenanceService.class, address);
        return service.update(schema, tableName, resultSet, where);
    }

    @Override
    public int createTable(Schema schema, String tableName, List<Column> columns) {
        String address = holder.nextRealTimeAddress();
        DataMaintenanceService service = ApiProxyFactory.getProxy(DataMaintenanceService.class, address);
        return service.createTable(schema, tableName, columns);
    }

    @Override
    public SwiftResultSet query(String queryJson) {
        String address = holder.nextAnalyseAddress();
        SelectService service = ApiProxyFactory.getProxy(SelectService.class, address);
        return service.query(queryJson);
    }


}
