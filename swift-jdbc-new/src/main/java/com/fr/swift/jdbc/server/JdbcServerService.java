package com.fr.swift.jdbc.server;

import com.fr.swift.rpc.bean.RpcResponse;

/**
 * @author yee
 * @date 2018/11/20
 */
public interface JdbcServerService {
    RpcResponse dispatchRequest(String request);
}
