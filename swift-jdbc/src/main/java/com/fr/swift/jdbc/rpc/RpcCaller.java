package com.fr.swift.jdbc.rpc;

import com.fr.swift.api.rpc.DataMaintenanceService;
import com.fr.swift.api.rpc.SelectService;
import com.fr.swift.api.rpc.bean.Column;
import com.fr.swift.db.Schema;
import com.fr.swift.db.Where;
import com.fr.swift.jdbc.rpc.holder.AddressHolder;
import com.fr.swift.jdbc.rpc.invoke.ClientProxy;
import com.fr.swift.jdbc.rpc.nio.RpcConnector;
import com.fr.swift.source.Row;
import com.fr.swift.source.SwiftResultSet;

import java.sql.SQLException;
import java.util.Collections;
import java.util.List;

/**
 * @author yee
 * @date 2018/8/26
 */
public class RpcCaller implements DataMaintenanceService, SelectService {

    protected AddressHolder holder;

    private RpcCaller(String address) {
        holder = AddressHolder.getHolder(address);
    }

    private RpcCaller(String host, int port) {
        holder = AddressHolder.getHolder(host, port);
    }

    public static RpcCaller connect(String address) {
        return new RpcCaller(address);
    }

    public static RpcCaller connect(String host, int port) {
        return new RpcCaller(host, port);
    }

    @Override
    public int insert(String tableName, List<String> fields, List<Row> rows) throws SQLException {
        String address = holder.nextRealTimeAddress();
        ClientProxy proxy = new ClientProxy(new RpcConnector(address));
        try {
            proxy.start();
            return proxy.getProxy(DataMaintenanceService.class).insert(tableName, fields, rows);
        } finally {
            proxy.stop();
        }
    }

    @Override
    public int insert(String tableName, List<Row> rows) throws SQLException {
        return insert(tableName, Collections.<String>emptyList(), rows);
    }

    @Override
    public int delete(String tableName, Where where) {
        String address = holder.nextRealTimeAddress();
        ClientProxy proxy = new ClientProxy(new RpcConnector(address));
        try {
            proxy.start();
            return proxy.getProxy(DataMaintenanceService.class).delete(tableName, where);
        } finally {
            proxy.stop();
        }
    }

    @Override
    public int update(String tableName, SwiftResultSet resultSet, Where where) {
        String address = holder.nextRealTimeAddress();
        ClientProxy proxy = new ClientProxy(new RpcConnector(address));
        try {
            proxy.start();
            return proxy.getProxy(DataMaintenanceService.class).update(tableName, resultSet, where);
        } finally {
            proxy.stop();
        }
    }

    @Override
    public int createTable(Schema schema, String tableName, List<Column> columns) {
        String address = holder.nextRealTimeAddress();
        ClientProxy proxy = new ClientProxy(new RpcConnector(address));
        try {
            proxy.start();
            return proxy.getProxy(DataMaintenanceService.class).createTable(schema, tableName, columns);
        } finally {
            proxy.stop();
        }
    }

    @Override
    public SwiftResultSet query(String queryJson) {
        String address = holder.nextRealTimeAddress();
        ClientProxy proxy = new ClientProxy(new RpcConnector(address));
        try {
            proxy.start();
            return proxy.getProxy(SelectService.class).query(queryJson);
        } finally {
            proxy.stop();
        }
    }
}
