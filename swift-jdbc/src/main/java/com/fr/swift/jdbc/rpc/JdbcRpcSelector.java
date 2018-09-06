package com.fr.swift.jdbc.rpc;

import com.fr.swift.rpc.bean.RpcResponse;

/**
 * @author yee
 * @date 2018/9/6
 */
public interface JdbcRpcSelector<T extends JdbcRpcConnector> extends JdbcRpcComponent {
    void register(T connector);

    void notifySend();

    void fireRpcResponse(T connector, RpcResponse object);

    void fireRpcException(T connector, Exception object);
}
