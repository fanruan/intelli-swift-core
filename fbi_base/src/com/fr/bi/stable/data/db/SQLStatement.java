package com.fr.bi.stable.data.db;

import com.fr.data.impl.Connection;
import com.fr.stable.StringUtils;

/**
 * TODO 删除这个类，都由tableSource自己处理
 * @deprecated  由于执行SQL的类用到这个，暂时删不掉。这个对象除了toString方法外，
 * 没有太多作用，反倒限制太多，强行将sql语句转为子查询的结构，带来很多问题。
 * http://www.finedevelop.com/pages/viewpage.action?pageId=12974253
 * Created by GUY on 2015/3/10.
 */
public class SQLStatement {

    private com.fr.data.impl.Connection conn;

    private String select;

    private String schema;

    private String tableName;

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

    public void createConn() throws Exception {
        sqlConn = conn.createConnection();
    }

    public java.sql.Connection getSqlConn() throws Exception {
        if (sqlConn == null) {
            createConn();
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


    public String getSchema() {
        return schema;
    }

    public void setSchema(String schema) {
        this.schema = schema;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
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