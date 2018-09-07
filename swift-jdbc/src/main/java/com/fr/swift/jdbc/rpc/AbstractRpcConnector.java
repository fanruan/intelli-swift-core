package com.fr.swift.jdbc.rpc;

import com.fr.swift.jdbc.exception.RpcException;
import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.rpc.bean.RpcResponse;
import com.fr.swift.rpc.bean.impl.RpcRequest;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * @author yee
 * @date 2018/9/6
 */
public abstract class AbstractRpcConnector implements JdbcRpcConnector {
    protected String host = "localhost";
    protected int port = 7000;
    private ConcurrentLinkedQueue<RpcRequest> sendQueueCache = new ConcurrentLinkedQueue<RpcRequest>();
    private List<JdbcRpcExecutor> rpcExecutors;

    public AbstractRpcConnector(String host, int port) {
        this.host = host;
        this.port = port == -1 ? 7000 : port;
        this.rpcExecutors = new ArrayList<JdbcRpcExecutor>();
    }

    public AbstractRpcConnector(String address) {
        String[] array = address.split(":");
        host = array[0];
        if (array.length > 1) {
            port = Integer.parseInt(array[1]);
        }
        rpcExecutors = new ArrayList<JdbcRpcExecutor>();
    }

    public AbstractRpcConnector() {
    }

    @Override
    public void fireRpcResponse(RpcResponse object) {
        for (JdbcRpcExecutor rpcExecutor : rpcExecutors) {
            rpcExecutor.onRpcResponse(object);
        }
    }

    @Override
    public void registerExecutor(JdbcRpcExecutor executor) {
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
                throw new RpcException(e);
            }
            if (timeout > 0 && cost > timeout) {
                throw new RpcException("request time out");
            }
        }
        this.notifySend();
        return true;
    }

    @Override
    public void handlerException(Exception e) {
        SwiftLoggers.getLogger().error(e);
        stop();
    }
}
