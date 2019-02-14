package com.fr.swift.netty.rpc.invoke;

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
public class RPCInvokerCreator extends AbstractInvokerCreator {

    @Override
    public <T> Invoker<T> createAsyncInvoker(Class<T> clazz, URL url) {
        Invoker<T> invoker = super.createAsyncInvoker(clazz, url);
        T service = clazz.cast(ProxyServiceRegistry.get().getService(clazz.getName()));
        return invoker != null ? invoker : new RPCInvoker<T>(service, clazz, url, false);
    }

    @Override
    public <T> Invoker<T> createSyncInvoker(Class<T> clazz, URL url) {
        Invoker<T> invoker = super.createSyncInvoker(clazz, url);
        T service = clazz.cast(ProxyServiceRegistry.get().getService(clazz.getName()));
        return invoker != null ? invoker : new RPCInvoker<T>(service, clazz, url);
    }

    @Override
    public InvokerType getType() {
        return InvokerType.REMOTE;
    }
}
