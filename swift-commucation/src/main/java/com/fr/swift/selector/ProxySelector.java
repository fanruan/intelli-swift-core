package com.fr.swift.selector;

import com.fr.swift.ProxyFactory;
import com.fr.swift.Selector;
import com.fr.swift.proxy.LocalProxyFactory;

/**
 * This class created on 2018/5/28
 *
 * @author Lucifer
 * @description
 * @since Advanced FineBI 5.0
 */
public class ProxySelector implements Selector {

    private ProxyFactory proxyFactory;

    private ProxySelector() {
        proxyFactory = new LocalProxyFactory();
    }

    private static final Selector INSTANCE = new ProxySelector();

    public static Selector getInstance() {
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
}
