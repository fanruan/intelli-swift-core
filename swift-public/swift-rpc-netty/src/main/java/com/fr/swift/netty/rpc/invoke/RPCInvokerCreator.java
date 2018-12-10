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
    public Invoker createAsyncInvoker(Class clazz, URL url) {
        Invoker invoker = super.createAsyncInvoker(clazz, url);
        return invoker != null ? invoker : new RPCInvoker(ProxyServiceRegistry.get().getService(clazz.getName()), clazz, url, false);
    }

    @Override
    public Invoker createSyncInvoker(Class clazz, URL url) {
        Invoker invoker = super.createSyncInvoker(clazz, url);
        return invoker != null ? invoker : new RPCInvoker(ProxyServiceRegistry.get().getService(clazz.getName()), clazz, url);
    }

    @Override
    public InvokerType getType() {
        return InvokerType.REMOTE;
    }
}
