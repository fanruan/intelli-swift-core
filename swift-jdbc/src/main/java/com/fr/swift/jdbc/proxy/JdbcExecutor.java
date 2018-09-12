package com.fr.swift.jdbc.proxy;

import com.fr.swift.api.rpc.invoke.RpcSender;
import com.fr.swift.rpc.bean.RpcResponse;

/**
 * @author yee
 * @date 2018/9/6
 */
public interface JdbcExecutor extends RpcSender, JdbcComponent {
    void onRpcResponse(RpcResponse rpcResponse);
}
