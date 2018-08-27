package com.fr.swift.jdbc.session;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * @author yee
 * @date 2018/8/27
 */
public interface SwiftJdbcSession {
    Statement createStatement() throws SQLException;

    PreparedStatement preparedStatement(String sql) throws SQLException;
}
