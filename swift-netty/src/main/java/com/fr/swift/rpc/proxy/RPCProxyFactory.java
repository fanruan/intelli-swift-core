package com.fr.swift.rpc.proxy;

import com.fr.swift.Invoker;
import com.fr.swift.URL;
import com.fr.swift.invoker.InvokerInvocationHandler;
import com.fr.swift.proxy.AbstractProxyFactory;
import com.fr.swift.rpc.invoke.RPCInvoker;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;

/**
 * This class created on 2018/6/7
 *
 * @author Lucifer
 * @description
 * @since Advanced FineBI 5.0
 */
public class RPCProxyFactory extends AbstractProxyFactory {

    @Override
    public <T> T getProxy(Invoker<T> invoker) {
        InvocationHandler invocationHandler = new InvokerInvocationHandler(invoker);
        Class interfaceClass = invoker.getInterface();
        T t = (T) Proxy.newProxyInstance(interfaceClass.getClassLoader(), new Class<?>[]
                {interfaceClass}, invocationHandler);
        return t;
    }

    @Override
    public <T> Invoker<T> getInvoker(T proxy, Class<T> type, URL url) {
        return new RPCInvoker<T>(proxy, type, url);
    }

    @Override
    public <T> Invoker<T> getInvoker(T proxy, Class<T> type, URL url, boolean sync) {
        Invoker rpcInvoker = new RPCInvoker(proxy, type, url, sync);
        return rpcInvoker;
    }
}
