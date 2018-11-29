package com.fr.swift.jdbc.rpc.invoke;

import com.fr.swift.jdbc.rpc.JdbcExecutor;
import com.fr.swift.rpc.bean.RpcResponse;
import com.fr.swift.rpc.bean.impl.RpcRequest;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author yee
 * @date 2018/8/26
 */
public class ClientProxy {
    private JdbcExecutor remoteExecutor;
    private AtomicBoolean start = new AtomicBoolean(false);

    public ClientProxy(JdbcExecutor remoteExecutor) {
        this.remoteExecutor = remoteExecutor;
    }

    public <T> T getProxy(Class<T> proxyClass) {
        return (T) Proxy.newProxyInstance(proxyClass.getClassLoader(), new Class[]{proxyClass}, new ProxyHandler(proxyClass, remoteExecutor));
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

    private class ProxyHandler implements InvocationHandler {
        private Class proxyClass;
        private JdbcExecutor remoteExecutor;

        public ProxyHandler(Class proxyClass, JdbcExecutor remoteExecutor) {
            this.proxyClass = proxyClass;
            this.remoteExecutor = remoteExecutor;
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) {
            RpcRequest request = new RpcRequest();
            request.setRequestId(UUID.randomUUID().toString());
            request.setInterfaceName(proxyClass.getName());
            request.setParameters(args);
            request.setMethodName(method.getName());
            request.setParameterTypes(method.getParameterTypes());
            RpcResponse response = remoteExecutor.send(request);
            return response.getResult();
        }
    }
}
