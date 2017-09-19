package com.fr.bi.cal.report.db;

import com.fr.data.core.db.dialect.PostgreSQLDialect;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Created by Roy on 2017/9/15.
 */
public class PostgreDialect extends PostgreSQLDialect {
    private int DEFAULT_FETCH_SIZE = 10000;

    @Override
    public Statement createStatement(Connection connection, String sql) throws SQLException {
        connection.setAutoCommit(false);
        Statement statement = super.createStatement(connection, sql);
        return statement;
    }

    @Override
    public int getFetchSize() {
        int fetchSize = super.getFetchSize();
        if (fetchSize < 0) {
            fetchSize = DEFAULT_FETCH_SIZE;
        }
        return fetchSize;
    }
}
