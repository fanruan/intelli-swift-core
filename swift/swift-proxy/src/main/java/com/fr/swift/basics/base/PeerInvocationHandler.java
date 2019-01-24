package com.fr.swift.basics.base;

import com.fr.swift.basic.URL;
import com.fr.swift.basics.InvokerCreator;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * @author anchore
 * @date 2019/1/21
 */
public class PeerInvocationHandler implements InvocationHandler {

    private Class<?> proxyIface;

    private URL url;

    private InvokerCreator invokerCreator;

    public PeerInvocationHandler(Class<?> proxyIface, URL url, InvokerCreator invokerCreator) {
        this.proxyIface = proxyIface;
        this.url = url;
        this.invokerCreator = invokerCreator;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        return invokerCreator.createSyncInvoker(proxyIface, url).invoke(new SwiftInvocation(method, args)).recreate();
    }
}