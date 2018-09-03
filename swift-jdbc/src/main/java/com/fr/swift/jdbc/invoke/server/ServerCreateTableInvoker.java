package com.fr.swift.jdbc.invoke.server;

import com.fr.swift.jdbc.bean.CreateTableBean;
import com.fr.swift.jdbc.invoke.BaseCreateTableInvoker;
import com.fr.swift.jdbc.rpc.RpcCaller;

import java.sql.SQLException;

/**
 * @author yee
 * @date 2018/8/29
 */
public class ServerCreateTableInvoker extends BaseCreateTableInvoker {
    private RpcCaller.MaintenanceRpcCaller caller;

    public ServerCreateTableInvoker(CreateTableBean bean, RpcCaller.MaintenanceRpcCaller caller) {
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
