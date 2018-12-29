package com.fr.swift.jdbc.rpc;

import com.fr.swift.rpc.bean.RpcResponse;
import com.fr.swift.rpc.bean.impl.RpcRequest;

/**
 * @author yee
 * @date 2018/9/6
 */
public interface JdbcConnector extends JdbcComponent {
    /**
     * 发送响应
     *
     * @param object
     */
    void fireRpcResponse(RpcResponse object);

    /**
     * 注册执行器
     *
     * @param executor
     */
    void registerExecutor(JdbcExecutor executor);

    /**
     * 发送对象
     *
     * @param rpc
     * @param timeout
     * @return
     */
    boolean sendRpcObject(RpcRequest rpc, int timeout);

    void notifySend();
}
