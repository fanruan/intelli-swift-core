package com.fr.swift.source.db;

import com.fr.data.core.db.DBUtils;
import com.fr.swift.source.ListBasedRow;
import com.fr.swift.source.Row;
import com.fr.swift.source.SwiftMetaData;
import com.fr.swift.source.SwiftResultSet;
import com.fr.swift.source.db.dbdealer.DBDealer;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by pony on 2017/12/6.
 */
public class JDBCResultSet implements SwiftResultSet{
    private ResultSet rs;
    private Statement stmt;
    private Connection conn;
    private SwiftMetaData metaData;
    private DBDealer[] dealers;

    public JDBCResultSet(ResultSet rs, Statement stmt, Connection conn, SwiftMetaData metaData, DBDealer[] dealers) {
        this.rs = rs;
        this.stmt = stmt;
        this.conn = conn;
        this.metaData = metaData;
        this.dealers = dealers;
    }

    @Override
    public void close() throws SQLException {
        DBUtils.closeResultSet(rs);
        DBUtils.closeStatement(stmt);
        DBUtils.closeConnection(conn);
    }

    @Override
    public boolean next() throws SQLException {
        return rs.next();
    }

    @Override
    public SwiftMetaData getMetaData() throws SQLException {
        return metaData;
    }

    @Override
    public Row getRowData() throws SQLException{
        List list = new ArrayList();
        for (int i = 0; i < dealers.length; i++) {
            list.add(dealers[i].dealWithResultSet(rs));
        }
        return new ListBasedRow(list);
    }
}
