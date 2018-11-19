package com.fr.swift.jdbc;

import com.fr.swift.jdbc.response.JdbcResponse;
import com.fr.swift.jdbc.sql.BaseSwiftConnection;
import com.fr.swift.jdbc.sql.ConnectionConfig;
import com.fr.swift.jdbc.sql.UnregisteredDriver;

import java.io.File;
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
        File keytab = config.kerberosKeytab();
        if (null != keytab) {
            // TODO kerberos 验证
            return;
        }
        JdbcResponse response = holder.getRequestService().applyWithRetry(config.requestExecutor(), config.swiftUser(), config.swiftPassword(), 3);
        if (null != response.exception()) {
            throw response.exception();
        }
        // 结果应该包括用户校验码以及 realtime和analyse服务的地址
        Object result = response.result();
        // TODO holder 保存下authCode以及 realtime和analyse地址
    }

    private void register() {
        try {
            DriverManager.registerDriver(this);
        } catch (SQLException ignore) {
        }
    }
}
