package com.fr.swift.core.rpc;

import com.fr.swift.basics.Invoker;
import com.fr.swift.basics.URL;
import com.fr.swift.basics.base.AbstractProxyFactory;
import com.fr.swift.netty.rpc.invoke.RPCInvoker;

/**
 * This class created on 2018/5/28
 *
 * @author Lucifer
 * @description
 * @since Advanced FineBI 5.0
 */
public class FRProxyFactory extends AbstractProxyFactory {

    @Override
    public <T> Invoker<T> getInvoker(T proxy, Class<T> type, URL url) {
        return new FRInvoker(proxy, type, url);
    }

    @Override
    public <T> Invoker<T> getInvoker(T proxy, Class<T> type, URL url, boolean sync) {
        return (Invoker) new FRInvoker(proxy, type, url, sync);
    }
}
