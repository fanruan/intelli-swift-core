package com.fr.swift.cloud.basics.base;


import com.fr.swift.cloud.basics.InvokerCreator;
import com.fr.swift.cloud.basics.InvokerHandler;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;

/**
 * @author yee
 * @date 2018/10/24
 */
public class JdkProxyFactory extends BaseProxyFactory {

    private InvocationHandler handler;

    public JdkProxyFactory(InvokerCreator invokerCreator) {
        super(invokerCreator);
        this.handler = createInvokerHandler();
    }

    @Override
    protected InvokerHandler createInvokerHandler() {
        return new JdkProxyInvokeHandler(invokerCreator);
    }

    @Override
    public <T> T getProxy(Class<T> proxyIface) {
        return proxyIface.cast(Proxy.newProxyInstance(proxyIface.getClassLoader(), new Class[]{proxyIface}, handler));
    }
}