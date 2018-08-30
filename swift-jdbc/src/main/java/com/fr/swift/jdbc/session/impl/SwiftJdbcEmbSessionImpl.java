package com.fr.swift.jdbc.session.impl;

import com.fr.swift.db.SwiftDatabase;
import com.fr.swift.jdbc.session.SwiftJdbcSession;
import com.fr.swift.jdbc.statement.SwiftPreparedStatement;
import com.fr.swift.jdbc.statement.SwiftStatement;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * @author yee
 * @date 2018/8/29
 */
public class SwiftJdbcEmbSessionImpl implements SwiftJdbcSession {
    private SwiftDatabase database;

    public SwiftJdbcEmbSessionImpl(SwiftDatabase database) {
        this.database = database;
    }

    @Override
    public Statement createStatement() throws SQLException {
        return new SwiftStatement(database);
    }

    @Override
    public PreparedStatement preparedStatement(String sql) throws SQLException {
        return new SwiftPreparedStatement(sql, new SwiftStatement(database));
    }
}
