package com.fr.swift.api.request;

import com.fr.swift.rpc.bean.RpcResponse;
import com.fr.swift.rpc.bean.impl.RpcRequest;

/**
 * @author yee
 * @date 2018/8/26
 */
public interface RpcSender {
    /**
     * send rpc request
     *
     * @param request rpc request
     * @return rpc response
     * @throws Exception the method might be throw exception
     * @see RpcRequest
     * @see RpcResponse
     */
    RpcResponse send(RpcRequest request) throws Exception;
}
