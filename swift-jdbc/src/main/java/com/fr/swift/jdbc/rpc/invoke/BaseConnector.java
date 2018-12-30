package com.fr.swift.jdbc.rpc.invoke;

import com.fr.swift.jdbc.exception.Exceptions;
import com.fr.swift.jdbc.rpc.JdbcConnector;
import com.fr.swift.jdbc.rpc.JdbcExecutor;
import com.fr.swift.rpc.bean.RpcResponse;
import com.fr.swift.rpc.bean.impl.RpcRequest;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * @author yee
 * @date 2018/9/6
 */
public abstract class BaseConnector implements JdbcConnector {
    protected String host = "localhost";
    protected int port = 7000;
    private ConcurrentLinkedQueue<RpcRequest> sendQueueCache = new ConcurrentLinkedQueue<RpcRequest>();
    private List<JdbcExecutor> rpcExecutors;

    public BaseConnector(String host, int port) {
        this.host = host;
        this.port = port == -1 ? 7000 : port;
        this.rpcExecutors = new ArrayList<JdbcExecutor>();
    }

    public BaseConnector(String address) {
        String[] array = address.split(":");
        host = array[0];
        if (array.length > 1) {
            port = Integer.parseInt(array[1]);
        }
        rpcExecutors = new ArrayList<JdbcExecutor>();
    }

    public BaseConnector() {
        rpcExecutors = new ArrayList<JdbcExecutor>();
    }

    @Override
    public void fireRpcResponse(RpcResponse object) {
        for (JdbcExecutor rpcExecutor : rpcExecutors) {
            rpcExecutor.onRpcResponse(object);
        }
    }

    @Override
    public void registerExecutor(JdbcExecutor executor) {
        rpcExecutors.add(executor);
    }

    @Override
    public boolean sendRpcObject(RpcRequest rpc, int timeout) {
        int cost = 0;
        while (!sendQueueCache.offer(rpc)) {
            cost += 3;
            try {
                Thread.sleep(3);
            } catch (InterruptedException e) {
                throw Exceptions.runtime("", e);
            }
            if (timeout > 0 && cost > timeout) {
                throw Exceptions.timeout();
            }
        }
        this.notifySend();
        return true;
    }

    @Override
    public void handlerException(Exception e) {
//        SwiftLoggers.getLogger().error(e);
        stop();
    }

    public RpcRequest getRequest() {
        return sendQueueCache.poll();
    }

    public boolean isNeedToSend() {
        return null != sendQueueCache.peek();
    }
}
