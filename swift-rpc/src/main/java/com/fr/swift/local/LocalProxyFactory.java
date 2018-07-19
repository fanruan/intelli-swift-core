package com.fr.swift.local;

import com.fr.swift.basics.Invoker;
import com.fr.swift.basics.URL;
import com.fr.swift.basics.base.AbstractProxyFactory;

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
        return new LocalInvoker<T>(proxy, type, url);
    }

}
