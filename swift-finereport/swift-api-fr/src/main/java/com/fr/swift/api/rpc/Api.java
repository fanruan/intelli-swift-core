package com.fr.swift.api.rpc;

import com.fr.swift.api.rpc.invoke.CallClient;
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
public class Api {

    public static final int DEFAULT_MAX_FRAME_SIZE = 1000000000;

    private CallClient client;

    public Api(CallClient client) {
        this.client = client;
    }

    public <T> T getProxy(final Class<T> proxyClass) {
        return (T) Proxy.newProxyInstance(proxyClass.getClassLoader(), new Class[]{proxyClass}, new InvocationHandler() {
            @Override
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                RpcRequest request = new RpcRequest();
                request.setRequestId(UUID.randomUUID().toString());
                request.setInterfaceName(proxyClass.getName());
                request.setMethodName(method.getName());
                request.setParameterTypes(method.getParameterTypes());
                request.setParameters(args);
                RpcResponse response = client.send(request);
                if (null != response.getException()) {
                    throw response.getException();
                }
                return response.getResult();
            }
        });
    }

}
