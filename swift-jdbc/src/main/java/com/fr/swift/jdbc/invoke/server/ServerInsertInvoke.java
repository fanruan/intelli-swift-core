package com.fr.swift.jdbc.invoke.server;

import com.fr.swift.jdbc.bean.InsertBean;
import com.fr.swift.jdbc.invoke.BaseInsertInvoke;
import com.fr.swift.jdbc.rpc.RpcCaller;

import java.sql.SQLException;

/**
 * @author yee
 * @date 2018/8/29
 */
public class ServerInsertInvoke extends BaseInsertInvoke {
    private RpcCaller.MaintenanceRpcCaller caller;

    public ServerInsertInvoke(InsertBean insertBean, RpcCaller.MaintenanceRpcCaller caller) {
        super(insertBean);
        this.caller = caller;
    }

    @Override
    public Integer invoke() throws SQLException {
        if (null != insertBean.getQueryJson()) {
            return caller.insert(insertBean.getSchema(), insertBean.getTableName(), insertBean.getQueryJson());
        } else {
            return caller.insert(insertBean.getSchema(), insertBean.getTableName(), insertBean.getColumnNames(), insertBean.getDatas());
        }
    }
}
