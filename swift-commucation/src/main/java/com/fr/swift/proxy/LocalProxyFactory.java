package com.fr.swift.proxy;

import com.fr.swift.Invoker;
import com.fr.swift.ProxyFactory;
import com.fr.swift.URL;
import com.fr.swift.invoker.InvokerInvocationHandler;
import com.fr.swift.invoker.SwiftInvoker;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;

/**
 * This class created on 2018/5/26
 *
 * @author Lucifer
 * @description
 * @since Advanced FineBI 5.0
 */
public class LocalProxyFactory implements ProxyFactory {

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
        return new SwiftInvoker<T>(proxy, type, url);
    }

    @Override
    public <T> T getProxy(T proxy, Class<T> type, URL url) {
        Invoker invoker = getInvoker(proxy, type, url);
        T t = (T)getProxy(invoker);
        return t;
    }
}
