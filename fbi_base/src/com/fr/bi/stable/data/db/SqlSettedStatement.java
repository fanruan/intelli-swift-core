package com.fr.bi.stable.data.db;


import com.fr.data.impl.Connection;

/**
 * Created by 小灰灰 on 2015/10/20.
 */
public class SqlSettedStatement extends SQLStatement {
    private String sql;

    public SqlSettedStatement(Connection conn) {
        super(conn);
    }

    public void setSql(String sql) {
        this.sql = sql;
    }

    @Override
    public String toString() {
        return sql;
    }
}