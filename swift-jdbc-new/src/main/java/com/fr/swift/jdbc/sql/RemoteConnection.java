package com.fr.swift.jdbc.sql;

import com.fr.swift.jdbc.rpc.JdbcExecutor;
import com.fr.swift.jdbc.rpc.connection.RpcNioConnector;
import com.fr.swift.jdbc.rpc.invoke.SimpleExecutor;

import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.util.Properties;

/**
 * @author yee
 * @date 2018/11/16
 */
public class RemoteConnection extends BaseSwiftConnection {
    RemoteConnection(UnregisteredDriver driver, Properties properties) {
        super(driver, properties);
    }

    @Override
    protected JdbcExecutor createJdbcExecutor(String host, int port) {
        return new SimpleExecutor(new RpcNioConnector(host, port), connectionTimeout());
    }

    @Override
    protected JdbcExecutor createJdbcExecutor(String address) {
        return new SimpleExecutor(new RpcNioConnector(address), connectionTimeout());
    }

    @Override
    public DatabaseMetaData getMetaData() throws SQLException {
        return new SwiftDataBaseMetaData(this);
    }
}
