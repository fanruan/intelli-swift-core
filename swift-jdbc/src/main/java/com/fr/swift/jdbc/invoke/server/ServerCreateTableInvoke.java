package com.fr.swift.jdbc.invoke.server;

import com.fr.swift.jdbc.bean.CreateTableBean;
import com.fr.swift.jdbc.invoke.BaseCreateTableInvoke;
import com.fr.swift.jdbc.rpc.RpcCaller;

import java.sql.SQLException;

/**
 * @author yee
 * @date 2018/8/29
 */
public class ServerCreateTableInvoke extends BaseCreateTableInvoke {
    private RpcCaller.MaintenanceRpcCaller caller;

    public ServerCreateTableInvoke(CreateTableBean bean, RpcCaller.MaintenanceRpcCaller caller) {
        super(bean);
        this.caller = caller;
    }

    @Override
    public Integer invoke() throws SQLException {
        return caller.createTable(bean.getDatabase(), bean.getTableName(), bean.getColumns());
    }
}
