package com.fr.swift.basics.base.selector;

import com.fr.swift.basics.ProxyFactory;
import com.fr.swift.basics.Selector;
import com.fr.swift.basics.base.JdkProxyFactory;
import com.fr.swift.local.LocalInvokerCreator;

/**
 * This class created on 2018/5/28
 *
 * @author Lucifer
 * @description
 * @since Advanced FineBI 5.0
 */
public class ProxySelector implements Selector<ProxyFactory> {

    private ProxyFactory proxyFactory;

    private ProxySelector() {
        this.proxyFactory = new JdkProxyFactory(new LocalInvokerCreator());
    }

    private static final ProxySelector INSTANCE = new ProxySelector();

    public static ProxySelector getInstance() {
        return INSTANCE;
    }

    @Override
    public ProxyFactory getFactory() {
        synchronized (ProxySelector.class) {
            return proxyFactory;
        }
    }

    @Override
    public void switchFactory(ProxyFactory proxyFactory) {
        synchronized (ProxySelector.class) {
            this.proxyFactory = proxyFactory;
        }
    }

    /**
     * 简化拿代理
     *
     * @param proxy
     * @param <T>
     * @return
     */
    public static <T> T getProxy(Class<T> proxy) {
        return getInstance().getFactory().getProxy(proxy);
    }
}
