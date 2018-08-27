package com.fr.swift.api.rpc.invoke;

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
    public static <T> T getProxy(final Class<T> proxyClass, final String address) {
        return (T) Proxy.newProxyInstance(proxyClass.getClassLoader(), new Class[]{proxyClass}, new InvocationHandler() {
            @Override
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                RpcRequest request = new RpcRequest();
                request.setRequestId(UUID.randomUUID().toString());
                request.setInterfaceName(proxyClass.getName());
                request.setMethodName(method.getName());
                request.setParameterTypes(method.getParameterTypes());
                request.setParameters(args);
                RpcResponse response = new CallClient(address).send(request);
                if (null != response.getException()) {
                    throw response.getException();
                }
                return response.getResult();
            }
        });
    }
}
