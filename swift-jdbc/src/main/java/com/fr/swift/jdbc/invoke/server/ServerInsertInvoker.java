package com.fr.swift.jdbc.invoke.server;

import com.fr.swift.jdbc.bean.InsertBean;
import com.fr.swift.jdbc.invoke.BaseInsertInvoker;
import com.fr.swift.jdbc.rpc.RpcCaller;

import java.sql.SQLException;

/**
 * @author yee
 * @date 2018/8/29
 */
public class ServerInsertInvoker extends BaseInsertInvoker {
    private RpcCaller.MaintenanceRpcCaller caller;

    public ServerInsertInvoker(InsertBean insertBean, RpcCaller.MaintenanceRpcCaller caller) {
        super(insertBean);
        this.caller = caller;
    }

    @Override
    public Integer invoke() throws SQLException {
        return executeInsert(caller);
    }
}
