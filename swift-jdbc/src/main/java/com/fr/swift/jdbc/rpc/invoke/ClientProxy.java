package com.fr.swift.jdbc.rpc.invoke;

import com.fr.swift.jdbc.rpc.RpcExecutor;
import com.fr.swift.jdbc.rpc.nio.RpcConnector;
import com.fr.swift.rpc.bean.RpcRequest;
import com.fr.swift.rpc.bean.RpcResponse;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author yee
 * @date 2018/8/26
 */
public class ClientProxy implements InvocationHandler {
    private RpcExecutor remoteExecutor;
    private Class proxyClass;
    private AtomicBoolean start = new AtomicBoolean(false);

    public ClientProxy(RpcExecutor remoteExecutor) {
        this.remoteExecutor = remoteExecutor;
    }

    public ClientProxy(RpcConnector connector) {
        remoteExecutor = new RpcExecutor(connector);
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        RpcRequest request = new RpcRequest();
        request.setRequestId(UUID.randomUUID().toString());
        request.setInterfaceName(proxyClass.getName());
        request.setParameters(args);
        request.setMethodName(method.getName());
        request.setParameterTypes(method.getParameterTypes());
        RpcResponse response = remoteExecutor.send(request);
        return response.getResult();
    }

    public <T> T getProxy(Class<T> proxyClass) {
        this.proxyClass = proxyClass;
        return (T) Proxy.newProxyInstance(proxyClass.getClassLoader(), new Class[]{proxyClass}, this);
    }

    public void start() {
        if (!start.get()) {
            remoteExecutor.start();
            start.set(true);
        }
    }

    public void stop() {
        if (start.get()) {
            remoteExecutor.stop();
            start.set(false);
        }
    }
}
