package com.fr.swift.core.rpc;

import com.fr.swift.basic.URL;
import com.fr.swift.basics.Invoker;
import com.fr.swift.basics.InvokerType;
import com.fr.swift.basics.base.ProxyServiceRegistry;
import com.fr.swift.local.AbstractInvokerCreator;

/**
 * This class created on 2018/11/1
 *
 * @author Lucifer
 * @description
 * @since Advanced FineBI 5.0
 */
public class FRInvokerCreater extends AbstractInvokerCreator {

    @Override
    public Invoker createAsyncInvoker(Class clazz, URL url) {
        Invoker invoker = super.createAsyncInvoker(clazz, url);
        return invoker != null ? invoker : new FRInvoker(ProxyServiceRegistry.get().getService(clazz.getName()), clazz, url, false);
    }

    @Override
    public Invoker createSyncInvoker(Class clazz, URL url) {
        Invoker invoker = super.createSyncInvoker(clazz, url);
        return invoker != null ? invoker : new FRInvoker(ProxyServiceRegistry.get().getService(clazz.getName()), clazz, url);
    }

    @Override
    public InvokerType getType() {
        return InvokerType.REMOTE;
    }
}
