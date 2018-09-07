package com.fr.swift.jdbc.rpc;

import com.fr.swift.jdbc.exception.RpcException;
import com.fr.swift.rpc.bean.RpcResponse;
import com.fr.swift.rpc.bean.impl.RpcRequest;

import java.util.concurrent.ConcurrentHashMap;

/**
 * @author yee
 * @date 2018/8/26
 */
public class SimpleRpcExecutor implements JdbcRpcExecutor {
    private static final int DEFAULT_TIMEOUT = 10000;
    protected int timeout;
    private JdbcRpcConnector connector;
    private RpcSyncObject sync;
    private ConcurrentHashMap<String, RpcCallBackSync> rpcCache = new ConcurrentHashMap<String, RpcCallBackSync>();


    public SimpleRpcExecutor(JdbcRpcConnector connector) {
        this(connector, DEFAULT_TIMEOUT);
    }

    public SimpleRpcExecutor(JdbcRpcConnector connector, int timeout) {
        this.timeout = timeout;
        this.connector = connector;
        this.connector.registerExecutor(this);
        sync = new RpcSyncObject();
    }

    @Override
    public RpcResponse send(RpcRequest request) throws Exception {
        RpcCallBackSync sync = new RpcCallBackSync(request.getRequestId(), request);
        rpcCache.put(request.getRequestId(), sync);
        connector.sendRpcObject(request, timeout);
        this.sync.waitForResult(timeout, sync);
        rpcCache.remove(sync.getRpcId());
        RpcResponse response = sync.getResponse();
        if (response == null) {
            throw new RpcException("null rpc response");
        }
        if (response.getException() != null) {
            throw new RpcException(response.getException());
        }
        return response;
    }

    @Override
    public void start() {
        connector.start();
    }

    @Override
    public void stop() {
        connector.stop();
    }

    @Override
    public void handlerException(Exception e) {
        connector.handlerException(e);
    }

    @Override
    public void onRpcResponse(RpcResponse rpc) {
        RpcCallBackSync sync = rpcCache.get(rpc.getRequestId());
        if (sync != null && sync.getRpcId().equals(rpc.getRequestId())) {
            this.sync.notifyResult(sync, rpc);
        }
    }
}
