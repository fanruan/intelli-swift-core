package com.fr.swift.source.resultset;

import com.fr.data.core.db.DBUtils;
import com.fr.swift.result.SwiftResultSet;
import com.fr.swift.source.ListBasedRow;
import com.fr.swift.source.Row;
import com.fr.swift.source.SwiftMetaData;
import com.fr.swift.source.db.dbdealer.DBDealer;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * @author pony
 * @date 2017/12/6
 */
public class JdbcResultSet implements SwiftResultSet {
    private ResultSet rs;
    private Statement stmt;
    private Connection conn;
    private SwiftMetaData metaData;
    private DBDealer[] dealers;

    private Row current;

    public JdbcResultSet(ResultSet rs, Statement stmt, Connection conn, SwiftMetaData metaData, DBDealer[] dealers) throws SQLException {
        this.rs = rs;
        this.stmt = stmt;
        this.conn = conn;
        this.metaData = metaData;
        this.dealers = dealers;
        nextRow();
    }

    @Override
    public void close() throws SQLException {
        DBUtils.closeResultSet(rs);
        DBUtils.closeStatement(stmt);
        DBUtils.closeConnection(conn);
    }

    @Override
    public boolean hasNext() throws SQLException {
        return current != null;
    }

    @Override
    public int getFetchSize() {
        return 0;
    }

    @Override
    public SwiftMetaData getMetaData() throws SQLException {
        return metaData;
    }

    @Override
    public Row getNextRow() throws SQLException {
        Row prev = current;
        nextRow();
        return prev;
    }

    private void nextRow() throws SQLException {
        if (rs.next()) {
            List<Object> list = new ArrayList<Object>();
            for (DBDealer dealer : dealers) {
                list.add(dealer.dealWithResultSet(rs));
            }
            current = new ListBasedRow(list);
        } else {
            current = null;
        }
    }
}
