package com.fr.swift.jdbc.invoke.impl;

import com.fr.swift.db.SwiftDatabase;
import com.fr.swift.jdbc.invoke.BaseSelectInvoker;
import com.fr.swift.jdbc.proxy.invoke.JdbcCaller;
import com.fr.swift.query.query.QueryBean;
import com.fr.swift.source.SwiftResultSet;

import java.sql.SQLException;

/**
 * @author yee
 * @date 2018/8/29
 */
public class SelectInvokerImpl extends BaseSelectInvoker {
    private SwiftDatabase database;
    private JdbcCaller.SelectJdbcCaller caller;

    public SelectInvokerImpl(QueryBean queryBean, SwiftDatabase database, JdbcCaller.SelectJdbcCaller caller) {
        super(queryBean);
        this.database = database;
        this.caller = caller;
    }

    @Override
    public SwiftResultSet invoke() throws SQLException {
        try {
            return caller.query(database, queryBean.toString());
        } catch (Exception e) {
            throw new SQLException(e);
        }
    }
}
