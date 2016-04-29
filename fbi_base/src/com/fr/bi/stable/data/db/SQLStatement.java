package com.fr.bi.stable.data.db;

import com.fr.data.impl.Connection;
import com.fr.stable.StringUtils;

/**
 * Created by GUY on 2015/3/10.
 */
public class SQLStatement {

    private com.fr.data.impl.Connection conn;

    private String select;

    private String from;

    private String where;

    private java.sql.Connection sqlConn;

    public SQLStatement(Connection conn, String select, String from, String where) {
        this.conn = conn;
        this.select = select;
        this.from = from;
        this.where = where;
    }

    public SQLStatement(Connection conn) {
        this.conn = conn;
    }

    public Connection getConn() {
        return conn;
    }

    public java.sql.Connection getSqlConn() throws Exception{
        if (sqlConn == null){
            sqlConn = conn.createConnection();
        }
        return sqlConn;
    }

    public String getSelect() {
        return select;
    }

    public void setSelect(String select) {
        this.select = select;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getWhere() {
        return where;
    }

    public void setWhere(String where) {
        this.where = where;
    }

    @Override
    public String toString() {
        String query = "SELECT " + (StringUtils.isEmpty(select) ? "*" : select) + " FROM " + from;
        if (!StringUtils.isEmpty(where)) {
            query += " WHERE " + where;
        }
        return query;
    }
}