package com.fr.swift.netty.rpc.proxy;

import com.fr.swift.basics.Invoker;
import com.fr.swift.basics.URL;
import com.fr.swift.basics.base.AbstractProxyFactory;
import com.fr.swift.netty.rpc.invoke.RPCInvoker;

/**
 * This class created on 2018/6/7
 *
 * @author Lucifer
 * @description
 * @since Advanced FineBI 5.0
 */
public class RPCProxyFactory extends AbstractProxyFactory {

    @Override
    public <T> Invoker<T> getInvoker(T proxy, Class<T> type, URL url) {
        return new RPCInvoker<T>(proxy, type, url);
    }

    @Override
    public <T> Invoker<T> getInvoker(T proxy, Class<T> type, URL url, boolean sync) {
        return (Invoker) new RPCInvoker(proxy, type, url, sync);
    }
}
