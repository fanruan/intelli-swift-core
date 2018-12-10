package com.fr.swift.basics.base;

import com.fr.swift.basics.InvokerCreator;
import com.fr.swift.basics.InvokerHandler;
import com.fr.swift.basics.ProxyFactory;

/**
 * @author yee
 * @date 2018/10/24
 */
public abstract class BaseProxyFactory implements ProxyFactory {

    protected InvokerCreator invokerCreator;

    public BaseProxyFactory(InvokerCreator invokerCreator) {
        this.invokerCreator = invokerCreator;
    }

    protected abstract InvokerHandler createInvokerHandler();

}
