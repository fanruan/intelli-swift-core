package com.fr.swift.jdbc.invoke.impl;

import com.fr.swift.jdbc.bean.InsertBean;
import com.fr.swift.jdbc.invoke.BaseInsertInvoker;
import com.fr.swift.jdbc.proxy.invoke.JdbcCaller;

import java.sql.SQLException;

/**
 * @author yee
 * @date 2018/8/29
 */
public class InsertInvokerImpl extends BaseInsertInvoker {
    private JdbcCaller.MaintenanceJdbcCaller caller;

    public InsertInvokerImpl(InsertBean insertBean, JdbcCaller.MaintenanceJdbcCaller caller) {
        super(insertBean);
        this.caller = caller;
    }

    @Override
    public Integer invoke() throws SQLException {
        return executeInsert(caller);
    }
}
