package com.fr.swift.cloud.basics.base;

import com.fr.swift.cloud.basics.InvokerCreator;
import com.fr.swift.cloud.basics.InvokerHandler;
import com.fr.swift.cloud.basics.ProxyFactory;

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
