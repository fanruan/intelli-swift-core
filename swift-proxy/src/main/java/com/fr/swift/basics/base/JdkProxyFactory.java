package com.fr.swift.basics.base;


import com.fr.swift.basic.URL;
import com.fr.swift.basics.InvokerCreator;
import com.fr.swift.basics.InvokerHandler;

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

    @Override
    public <T> T getPeerProxy(URL url, Class<?> serviceClass, Class<T> proxyIface) {
        return proxyIface.cast(Proxy.newProxyInstance(proxyIface.getClassLoader(), new Class[]{proxyIface}, new PeerInvocationHandler(proxyIface, url, invokerCreator)));
    }
}
