package com.fr.swift.jdbc.rpc.selector;

import com.fr.swift.SwiftContext;
import com.fr.swift.annotation.SwiftApi;
import com.fr.swift.jdbc.exception.Exceptions;
import com.fr.swift.jdbc.rpc.connection.EmbJdbcConnector;
import com.fr.swift.jdbc.rpc.invoke.BaseSelector;
import com.fr.swift.rpc.bean.RpcResponse;
import com.fr.swift.rpc.bean.impl.RpcRequest;

import java.lang.reflect.Method;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author yee
 * @date 2018/9/8
 */
public class EmbJdbcSelector extends BaseSelector<EmbJdbcConnector> {
    private AtomicBoolean stop = new AtomicBoolean(true);
    private EmbJdbcConnector connector;
    private Semaphore semaphore = new Semaphore(1);
    private SelectionThread selectionThread;

    public EmbJdbcSelector() {
    }

    @Override
    protected void setUpSelector() {
        stop.set(false);
        selectionThread = new SelectionThread();
        selectionThread.start();
    }

    @Override
    protected void shutdownSelector() {
        stop.set(true);
        semaphore.release();
    }

    @Override
    public void register(EmbJdbcConnector connector) {
        this.connector = connector;
    }

    @Override
    public void notifySend() {
        semaphore.release();
    }

    private class SelectionThread extends Thread {

        public SelectionThread() {
            super("swift-emb-selection-thread");
        }

        @Override
        public void run() {
            while (!stop.get()) {
                while (connector.isNeedToSend()) {
                    RpcRequest request = connector.getRequest();
                    String requestId = request.getRequestId();
                    RpcResponse response = new RpcResponse();
                    response.setRequestId(requestId);
                    try {
                        Object proxyObject = SwiftContext.get().getBean(Class.forName(request.getInterfaceName()));
                        if (null == proxyObject) {
                            response.setException(Exceptions.notSupported(request.getInterfaceName() + " is invalid service"));
                        } else {
                            Method method = proxyObject.getClass().getDeclaredMethod(request.getMethodName(), request.getParameterTypes());
                            SwiftApi api = method.getAnnotation(SwiftApi.class);
                            if (null != api && api.enable()) {
                                method.setAccessible(true);
                                response.setResult(method.invoke(proxyObject, request.getParameters()));
                            } else {
                                response.setException(Exceptions.notSupported(method.getName() + " is invalid service"));
                            }
                        }
                    } catch (Exception e) {
                        response.setException(Exceptions.notSupported("Service invalid. ", e));
                    }
                    fireRpcResponse(connector, response);
                }
                try {
                    semaphore.tryAcquire(10, TimeUnit.SECONDS);
                } catch (InterruptedException e) {
                    fireRpcException(connector, e);
                }
            }
        }

    }
}
