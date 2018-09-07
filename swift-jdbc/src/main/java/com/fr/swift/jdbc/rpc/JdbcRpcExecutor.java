package com.fr.swift.jdbc.rpc;

import com.fr.swift.api.rpc.invoke.RpcSender;
import com.fr.swift.rpc.bean.RpcResponse;

/**
 * @author yee
 * @date 2018/9/6
 */
public interface JdbcRpcExecutor extends RpcSender, JdbcRpcComponent {
    void onRpcResponse(RpcResponse rpcResponse);
}
