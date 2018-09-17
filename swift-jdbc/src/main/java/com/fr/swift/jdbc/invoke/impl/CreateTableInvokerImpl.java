package com.fr.swift.jdbc.invoke.impl;

import com.fr.swift.jdbc.bean.CreateTableBean;
import com.fr.swift.jdbc.invoke.BaseCreateTableInvoker;
import com.fr.swift.jdbc.proxy.invoke.JdbcCaller;

import java.sql.SQLException;

/**
 * @author yee
 * @date 2018/8/29
 */
public class CreateTableInvokerImpl extends BaseCreateTableInvoker {
    private JdbcCaller.MaintenanceJdbcCaller caller;

    public CreateTableInvokerImpl(CreateTableBean bean, JdbcCaller.MaintenanceJdbcCaller caller) {
        super(bean);
        this.caller = caller;
    }

    @Override
    public Integer invoke() throws SQLException {
        try {
            return caller.createTable(bean.getDatabase(), bean.getTableName(), bean.getColumns());
        } catch (Exception e) {
            throw new SQLException(e);
        }
    }
}
