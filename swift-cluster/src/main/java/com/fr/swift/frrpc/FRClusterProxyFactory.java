package com.fr.swift.frrpc;

import com.fr.swift.Invoker;
import com.fr.swift.ProxyFactory;
import com.fr.swift.URL;
import com.fr.swift.invoker.InvokerInvocationHandler;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;

/**
 * This class created on 2018/5/28
 *
 * @author Lucifer
 * @description
 * @since Advanced FineBI 5.0
 */
public class FRClusterProxyFactory implements ProxyFactory {

    @Override
    public <T> T getProxy(Invoker<T> invoker) throws Exception {

        InvocationHandler invocationHandler = new InvokerInvocationHandler(invoker);
        Class interfaceClass = invoker.getInterface();
        T t = (T) Proxy.newProxyInstance(interfaceClass.getClassLoader(), new Class<?>[]
                {interfaceClass}, invocationHandler);
        return t;
//        return (T) FRProxyCache.getProxy(invoker.getInterface());
    }

    @Override
    public <T> Invoker<T> getInvoker(T proxy, Class<T> type, URL url) {
        return new FRInvoker(proxy, type, url);
    }
}
