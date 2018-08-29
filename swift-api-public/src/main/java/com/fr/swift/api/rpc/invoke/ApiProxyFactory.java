package com.fr.swift.api.rpc.invoke;

import com.fr.swift.api.Api;
import com.fr.swift.api.rpc.pool.CallClientPool;
import com.fr.swift.rpc.bean.RpcResponse;
import com.fr.swift.rpc.bean.impl.RpcRequest;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.UUID;

/**
 * @author yee
 * @date 2018/8/24
 */
public class ApiProxyFactory {

    @SuppressWarnings("unchecked")
    public static <T> T getProxy(final Class<T> proxyClass, final String address, final int maxFrameSize) {
        return (T) Proxy.newProxyInstance(proxyClass.getClassLoader(), new Class[]{proxyClass}, new InvocationHandler() {
            @Override
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                RpcRequest request = new RpcRequest();
                request.setRequestId(UUID.randomUUID().toString());
                request.setInterfaceName(proxyClass.getName());
                request.setMethodName(method.getName());
                request.setParameterTypes(method.getParameterTypes());
                request.setParameters(args);
                CallClient client = CallClientPool.getInstance(maxFrameSize).borrowObject(address);
                if (!client.isActive()) {
                    CallClientPool.getInstance(maxFrameSize).returnObject(address, client);
                    CallClientPool.getInstance(maxFrameSize).invalidateObject(address, client);
                    client = CallClientPool.getInstance(maxFrameSize).borrowObject(address);
                }
                try {
                    RpcResponse response = client.send(request);
                    if (null != response.getException()) {
                        throw response.getException();
                    }
                    return response.getResult();
                } finally {
                    CallClientPool.getInstance(maxFrameSize).returnObject(address, client);
                }
            }
        });
    }

    @SuppressWarnings("unchecked")
    public static <T> T getProxy(final Class<T> proxyClass, final String address) {
        return getProxy(proxyClass, address, Api.DEFAULT_MAX_FRAME_SIZE);
    }
}
