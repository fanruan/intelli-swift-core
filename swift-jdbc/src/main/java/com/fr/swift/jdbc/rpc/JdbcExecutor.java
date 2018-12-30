package com.fr.swift.jdbc.rpc;

import com.fr.swift.api.request.RpcSender;
import com.fr.swift.rpc.bean.RpcResponse;
import com.fr.swift.rpc.bean.impl.RpcRequest;

/**
 * @author yee
 * @date 2018/9/6
 */
public interface JdbcExecutor extends JdbcComponent, RpcSender {
    void onRpcResponse(RpcResponse rpcResponse);

    @Override
    RpcResponse send(RpcRequest rpcRequest);
}
