package com.fr.swift.basics.base;

import com.fr.swift.basics.InvokerCreater;
import com.fr.swift.basics.InvokerHandler;
import com.fr.swift.basics.ProxyFactory;

/**
 * @author yee
 * @date 2018/10/24
 */
public abstract class BaseProxyFactory implements ProxyFactory {

    protected InvokerCreater invokerCreater;

    public BaseProxyFactory(InvokerCreater invokerCreater) {
        this.invokerCreater = invokerCreater;
    }

    protected abstract InvokerHandler createInvokerHandler();

}
