package com.swift.jdbc;

import com.swift.jdbc.response.JdbcResponse;
import com.swift.jdbc.sql.BaseSwiftConnection;
import com.swift.jdbc.sql.ConnectionConfig;
import com.swift.jdbc.sql.UnregisteredDriver;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * @author yee
 * @date 2018/11/16
 */
public class Driver extends UnregisteredDriver {
    static {
        new Driver().register();
    }

    @Override
    protected String getConnectionSchema() {
        return JdbcProperty.get().getConnectionSchema();
    }

    @Override
    public Connection connect(String url, Properties info) throws SQLException {
        BaseSwiftConnection connection = (BaseSwiftConnection) super.connect(url, info);
        if (null != connection) {
            testConnection(connection);
        }
        return connection;
    }

    /**
     * test connection
     *
     * @param connection
     * @throws SQLException
     */
    private void testConnection(BaseSwiftConnection connection) throws SQLException {
        ConnectionConfig config = connection.getConfig();
        JdbcResponse response = holder.getRequestService().applyWithRetry(config.requestSender(), config.swiftUser(), config.swiftPassword(), 3);
        if (null != response.exception()) {
            throw response.exception();
        }
    }

    private void register() {
        try {
            DriverManager.registerDriver(this);
        } catch (SQLException ignore) {

        }
    }
}
