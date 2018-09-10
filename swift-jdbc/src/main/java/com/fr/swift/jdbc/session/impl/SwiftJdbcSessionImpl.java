package com.fr.swift.jdbc.session.impl;

import com.fr.swift.db.SwiftDatabase;
import com.fr.swift.jdbc.metadata.server.SwiftServerDatabaseMetadata;
import com.fr.swift.jdbc.proxy.invoke.JdbcCaller;
import com.fr.swift.jdbc.session.SwiftJdbcSession;
import com.fr.swift.jdbc.statement.SwiftPreparedStatement;
import com.fr.swift.jdbc.statement.SwiftStatement;

import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * @author yee
 * @date 2018/8/27
 */
public class SwiftJdbcSessionImpl implements SwiftJdbcSession {

    private JdbcCaller.SelectJdbcCaller selectRpcCaller;
    private JdbcCaller.MaintenanceJdbcCaller maintenanceRpcCaller;
    private SwiftDatabase schema;

    SwiftJdbcSessionImpl(SwiftDatabase schema, JdbcCaller.SelectJdbcCaller selectRpcCaller, JdbcCaller.MaintenanceJdbcCaller maintenanceRpcCaller) {
        this.selectRpcCaller = selectRpcCaller;
        this.maintenanceRpcCaller = maintenanceRpcCaller;
        this.schema = schema;
    }

    @Override
    public Statement createStatement() throws SQLException {
        return new SwiftStatement(schema, selectRpcCaller, maintenanceRpcCaller);
    }

    @Override
    public PreparedStatement preparedStatement(String sql) throws SQLException {
        return new SwiftPreparedStatement(sql, new SwiftStatement(schema, selectRpcCaller, maintenanceRpcCaller));
    }

    @Override
    public DatabaseMetaData getDatabaseMetaData() {
        return new SwiftServerDatabaseMetadata(schema, selectRpcCaller);
    }

    @Override
    public void close() throws Exception {

    }
}
