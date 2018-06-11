package com.fr.swift.rpc.proxy;

import com.fr.swift.Invoker;
import com.fr.swift.URL;
import com.fr.swift.proxy.AbstractProxyFactory;
import com.fr.swift.rpc.invoke.RPCInvoker;

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
}
