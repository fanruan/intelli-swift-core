package com.fr.swift.jdbc.session;

import com.fr.swift.api.rpc.session.SwiftApiSession;

import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * @author yee
 * @date 2018/8/27
 */
public interface SwiftJdbcSession extends SwiftApiSession {
    Statement createStatement() throws SQLException;

    PreparedStatement preparedStatement(String sql) throws SQLException;

    DatabaseMetaData getDatabaseMetaData();
}
