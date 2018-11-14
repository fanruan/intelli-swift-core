package com.fr.swift.jdbc.proxy.invoke;

import com.fr.swift.api.rpc.ApiService;
import com.fr.swift.jdbc.proxy.JdbcExecutor;
import com.fr.swift.rpc.bean.RpcResponse;
import com.fr.swift.rpc.bean.impl.RpcRequest;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author yee
 * @date 2018/8/26
 */
public class ClientProxy {
    private JdbcExecutor remoteExecutor;
    private AtomicBoolean start = new AtomicBoolean(false);
    private Map<Class<? extends ApiService>, Object> services = new ConcurrentHashMap<Class<? extends ApiService>, Object>();

    public ClientProxy(JdbcExecutor remoteExecutor) {
        this.remoteExecutor = remoteExecutor;
    }

    public <T extends ApiService> T getProxy(Class<T> proxyClass) {
        if (null == services.get(proxyClass)) {
            services.put(proxyClass, Proxy.newProxyInstance(proxyClass.getClassLoader(), new Class[]{proxyClass}, new ProxyHandler(proxyClass, remoteExecutor)));
        }
        return (T) services.get(proxyClass);
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
        private Class<? extends ApiService> proxyClass;
        private JdbcExecutor remoteExecutor;

        public ProxyHandler(Class<? extends ApiService> proxyClass, JdbcExecutor remoteExecutor) {
            this.proxyClass = proxyClass;
            this.remoteExecutor = remoteExecutor;
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
    }
}
