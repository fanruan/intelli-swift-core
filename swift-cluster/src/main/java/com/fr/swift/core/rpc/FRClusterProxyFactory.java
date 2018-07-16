package com.fr.swift.core.rpc;

import com.fr.swift.Invoker;
import com.fr.swift.URL;
import com.fr.swift.proxy.AbstractProxyFactory;

/**
 * This class created on 2018/5/28
 *
 * @author Lucifer
 * @description
 * @since Advanced FineBI 5.0
 */
public class FRClusterProxyFactory extends AbstractProxyFactory {

    @Override
    public <T> Invoker<T> getInvoker(T proxy, Class<T> type, URL url) {
        return new FRInvoker(proxy, type, url);
    }
}
