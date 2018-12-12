package com.fr.swift.jdbc;

import com.fr.swift.api.server.response.ApiResponse;
import com.fr.swift.api.server.response.AuthResponse;
import com.fr.swift.jdbc.exception.Exceptions;
import com.fr.swift.jdbc.sql.BaseSwiftConnection;
import com.fr.swift.jdbc.sql.ConnectionConfig;
import com.fr.swift.jdbc.sql.UnregisteredDriver;

import java.io.File;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;
import java.util.concurrent.LinkedBlockingQueue;

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
        ApiResponse response = holder.getRequestService().applyWithRetry(config.requestExecutor(), config.swiftUser(), config.swiftPassword(), 3);
        if (response.isError()) {
            throw Exceptions.sql(response.statusCode(), response.description());
        }
        // 结果应该包括用户校验码以及 realtime和analyse服务的地址
        AuthResponse result = (AuthResponse) response.result();
        holder.setAuthCode(result.getAuthCode());
        holder.setAnalyseAddresses(new LinkedBlockingQueue<String>(result.getAnalyseAddresses()));
        holder.setRealtimeAddresses(new LinkedBlockingQueue<String>(result.getRealTimeAddresses()));
    }
}
