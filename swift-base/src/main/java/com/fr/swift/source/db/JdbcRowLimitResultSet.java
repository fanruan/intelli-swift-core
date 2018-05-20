package com.fr.swift.source.db;

import com.fr.swift.source.SwiftMetaData;
import com.fr.swift.source.db.dbdealer.DBDealer;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * @author pony
 * @date 2017/12/6
 */
public class JdbcRowLimitResultSet extends JdbcResultSet {
    private int row = -1;
    private int limit;

    public JdbcRowLimitResultSet(ResultSet rs, Statement stmt, Connection conn, SwiftMetaData metaData, DBDealer[] dealers, int limit) {
        super(rs, stmt, conn, metaData, dealers);
        this.limit = limit;
    }

    @Override
    public boolean next() throws SQLException {
        return super.next() && row++ < limit;
    }
}
