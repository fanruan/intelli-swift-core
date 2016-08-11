package com.fr.bi.data;

import com.fr.data.core.db.dialect.Dialect;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * This class created on 2016/8/11.
 *
 * @author Connery
 * @since Advanced FineBI Analysis 1.0
 */
public class HiveExtractor extends DBExtractorImpl {
    @Override
    public Statement createStatement(Connection conn, Dialect dialect) throws SQLException {
        Statement stmt = conn.createStatement();
        try {
            stmt.setFetchSize(dialect.getFetchSize());
        } catch (Exception e) {
        }
        return stmt;
    }
}
