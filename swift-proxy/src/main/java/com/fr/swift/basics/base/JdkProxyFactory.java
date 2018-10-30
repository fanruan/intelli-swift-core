package com.fr.swift.basics.base;


import com.fr.swift.basics.InvokerHandler;
import com.fr.swift.basics.ProcessHandlerRegistry;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;

/**
 * @author yee
 * @date 2018/10/24
 */
public class JdkProxyFactory extends BaseProxyFactory {

    private InvocationHandler handler;

    public JdkProxyFactory(ProcessHandlerRegistry registry) {
        super(registry);
        this.handler = (InvocationHandler) createInvokerHandler();
    }

    @Override
    protected InvokerHandler createInvokerHandler() {
        return new JdkProxyInvokeHandler(registry);
    }

    @Override
    public <T> T getProxy(Class<T> proxy) {
        return (T) Proxy.newProxyInstance(proxy.getClassLoader(), new Class[]{proxy}, handler);
    }
}
