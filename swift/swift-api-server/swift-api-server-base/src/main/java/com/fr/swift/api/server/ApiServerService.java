package com.fr.swift.api.server;

import com.fr.swift.rpc.bean.RpcResponse;

/**
 * @author yee
 * @date 2018/11/20
 */
public interface ApiServerService {
    RpcResponse dispatchRequest(String request);
}
