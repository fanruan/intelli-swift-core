package com.fr.swift.api.request;

import com.fr.swift.rpc.bean.RpcResponse;
import com.fr.swift.rpc.bean.impl.RpcRequest;

/**
 * @author yee
 * @date 2018/8/26
 */
public interface RpcSender {
    RpcResponse send(RpcRequest request) throws Exception;
}
