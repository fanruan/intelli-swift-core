package com.fr.swift.jdbc.rpc;

import com.fr.swift.api.rpc.invoke.RpcSender;
import com.fr.swift.jdbc.exception.RpcException;
import com.fr.swift.jdbc.rpc.nio.RpcConnector;
import com.fr.swift.rpc.bean.RpcRequest;
import com.fr.swift.rpc.bean.RpcResponse;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author yee
 * @date 2018/8/26
 */
public class RpcExecutor implements RpcSender {
    protected int timeout = 10000;
    private RpcConnector connector;
    private AtomicInteger index = new AtomicInteger(10000);
    private RpcSyncObject sync;
    private ConcurrentHashMap<String, RpcCallBackSync> rpcCache = new ConcurrentHashMap<String, RpcCallBackSync>();


    public RpcExecutor(RpcConnector connector) {
        this.connector = connector;
        this.connector.registerExecutor(this);
        sync = new RpcSyncObject();
    }

    public RpcConnector getConnector() {
        return connector;
    }

    public void setConnector(RpcConnector connector) {
        this.connector = connector;
    }

    @Override
    public RpcResponse send(RpcRequest request, String address) throws Exception {
        int index = genIndex();
        RpcCallBackSync sync = new RpcCallBackSync(index, request);
        rpcCache.put(request.getRequestId(), sync);
        connector.sendRpcObject(request, timeout);
        this.sync.waitForResult(timeout, sync);
        rpcCache.remove(sync.getIndex());
        RpcResponse response = sync.getResponse();
        if (response == null) {
            throw new RpcException("null rpc response");
        }
        if (response.getException() != null) {
            throw new RpcException(response.getException());
        }
        return response;
    }

    public void start() {
        connector.connect();
    }

    public void stop() {
        connector.disConnect();
    }

    private int genIndex() {
        return index.incrementAndGet();
    }

    public void onRpcMessage(RpcResponse rpc) {
        RpcCallBackSync sync = rpcCache.get(rpc.getRequestId());
        if (sync != null && sync.getRequest().getRequestId().equals(rpc.getRequestId())) {
            this.sync.notifyResult(sync, rpc);
        }
    }
}
