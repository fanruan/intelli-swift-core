package com.fr.swift.jdbc.rpc.invoke;

import com.fr.swift.jdbc.exception.Exceptions;
import com.fr.swift.jdbc.rpc.JdbcConnector;
import com.fr.swift.jdbc.rpc.JdbcExecutor;
import com.fr.swift.rpc.bean.RpcResponse;
import com.fr.swift.rpc.bean.impl.RpcRequest;

import java.util.concurrent.ConcurrentHashMap;

/**
 * @author yee
 * @date 2018/8/26
 */
public class SimpleExecutor implements JdbcExecutor {
    /**
     * 默认超时30秒
     */
    private static final int DEFAULT_TIMEOUT = 30000;
    protected int timeout;
    private JdbcConnector connector;
    private SyncObject sync;
    private ConcurrentHashMap<String, CallBackSync> rpcCache = new ConcurrentHashMap<String, CallBackSync>();


    public SimpleExecutor(JdbcConnector connector) {
        this(connector, DEFAULT_TIMEOUT);
    }

    public SimpleExecutor(JdbcConnector connector, int timeout) {
        this.timeout = timeout;
        this.connector = connector;
        this.connector.registerExecutor(this);
        sync = new SyncObject();
    }

    @Override
    public RpcResponse send(RpcRequest request) {
        CallBackSync sync = new CallBackSync(request.getRequestId(), request);
        rpcCache.put(request.getRequestId(), sync);
        connector.sendRpcObject(request, timeout);
        this.sync.waitForResult(timeout, sync);
        rpcCache.remove(sync.getRpcId());
        RpcResponse response = sync.getResponse();
        if (response == null) {
            throw Exceptions.runtime("null rpc response");
        }
        if (response.getException() != null) {
            throw Exceptions.runtime(response.getException().getMessage(), response.getException());
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
        CallBackSync sync = rpcCache.get(rpc.getRequestId());
        if (sync != null && sync.getRpcId().equals(rpc.getRequestId())) {
            this.sync.notifyResult(sync, rpc);
        }
    }
}
