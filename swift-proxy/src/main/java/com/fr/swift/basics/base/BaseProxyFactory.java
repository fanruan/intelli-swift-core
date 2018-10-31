package com.fr.swift.basics.base;

import com.fr.swift.basics.InvokerHandler;
import com.fr.swift.basics.ProcessHandlerRegistry;
import com.fr.swift.basics.ProxyFactory;

/**
 * @author yee
 * @date 2018/10/24
 */
public abstract class BaseProxyFactory implements ProxyFactory {

    protected ProcessHandlerRegistry registry;

    public BaseProxyFactory(ProcessHandlerRegistry registry) {
        this.registry = registry;
    }

    protected abstract InvokerHandler createInvokerHandler();

    @Override
    public ProcessHandlerRegistry getRegistry() {
        return registry;
    }
}
