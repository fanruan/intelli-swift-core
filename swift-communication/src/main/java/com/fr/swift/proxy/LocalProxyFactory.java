package com.fr.swift.proxy;

import com.fr.swift.Invoker;
import com.fr.swift.URL;
import com.fr.swift.invoker.SwiftInvoker;

/**
 * This class created on 2018/5/26
 *
 * @author Lucifer
 * @description
 * @since Advanced FineBI 5.0
 */
public class LocalProxyFactory extends AbstractProxyFactory {

    @Override
    public <T> Invoker<T> getInvoker(T proxy, Class<T> type, URL url) {
        return new SwiftInvoker<T>(proxy, type, url);
    }

}
