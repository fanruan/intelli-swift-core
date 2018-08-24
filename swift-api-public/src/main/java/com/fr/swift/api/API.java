package com.fr.swift.api;

import com.fr.swift.api.rpc.DataMaintenanceService;
import com.fr.swift.api.rpc.SelectService;
import com.fr.swift.api.rpc.holder.InternalServiceAddressHolder;
import com.fr.swift.api.rpc.invoke.ApiProxyFactory;
import com.fr.swift.db.Where;
import com.fr.swift.source.Row;
import com.fr.swift.source.SwiftResultSet;

import java.sql.SQLException;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * @author yee
 * @date 2018/8/24
 */
class API implements DataMaintenanceService, SelectService {
    protected InternalServiceAddressHolder holder;

    private API() {
        holder = InternalServiceAddressHolder.getHolder();
    }

    public static API sync() {
        return new API() {
        };
    }

    @Override
    public int insert(String tableName, List<String> fields, List<Row> rows) throws SQLException {
        String address = holder.nextRealTimeAddress();
        DataMaintenanceService service = ApiProxyFactory.getProxy(DataMaintenanceService.class, address);
        return service.insert(tableName, fields, rows);
    }

    @Override
    public int insert(String tableName, List<Row> rows) throws SQLException {
        return insert(tableName, Collections.<String>emptyList(), rows);
    }

    @Override
    public int insert(String tableName, SwiftResultSet resultSet) throws SQLException {
        String address = holder.nextRealTimeAddress();
        DataMaintenanceService service = ApiProxyFactory.getProxy(DataMaintenanceService.class, address);
        return service.insert(tableName, resultSet);
    }

    @Override
    public int delete(String tableName, Where where) {
        String address = holder.nextRealTimeAddress();
        DataMaintenanceService service = ApiProxyFactory.getProxy(DataMaintenanceService.class, address);
        return service.delete(tableName, where);
    }

    @Override
    public int update(String tableName, Map<String, Object> fieldValues, Where where) {
        String address = holder.nextRealTimeAddress();
        DataMaintenanceService service = ApiProxyFactory.getProxy(DataMaintenanceService.class, address);
        return service.update(tableName, fieldValues, where);
    }

    @Override
    public SwiftResultSet query(String queryJson) {
        String address = holder.nextAnalyseAddress();
        SelectService service = ApiProxyFactory.getProxy(SelectService.class, address);
        return service.query(queryJson);
    }


}
