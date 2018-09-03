package com.fr.swift.jdbc.invoke.server;

import com.fr.swift.db.SwiftDatabase;
import com.fr.swift.jdbc.invoke.BaseSelectInvoker;
import com.fr.swift.jdbc.rpc.RpcCaller;
import com.fr.swift.query.query.QueryBean;
import com.fr.swift.source.SwiftResultSet;

import java.sql.SQLException;

/**
 * @author yee
 * @date 2018/8/29
 */
public class ServerSelectInvoker extends BaseSelectInvoker {
    private SwiftDatabase database;
    private RpcCaller.SelectRpcCaller caller;

    public ServerSelectInvoker(QueryBean queryBean, SwiftDatabase database, RpcCaller.SelectRpcCaller caller) {
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
