package com.fr.swift.local;

import com.fr.swift.basic.URL;
import com.fr.swift.basics.Invoker;
import com.fr.swift.basics.InvokerCreator;
import com.fr.swift.basics.base.ProxyServiceRegistry;
import com.fr.swift.property.SwiftProperty;

/**
 * This class created on 2018/11/15
 *
 * @author Lucifer
 * @description
 * @since Advanced FineBI 5.0
 */
public abstract class AbstractInvokerCreator implements InvokerCreator {
    @Override
    public Invoker createAsyncInvoker(Class clazz, URL url) {
        if (urlIsNull(url) || url.getDestination().getId().equals(SwiftProperty.getProperty().getClusterId())) {
            return new LocalInvoker(ProxyServiceRegistry.get().getService(clazz.getName()), clazz, url, false);
        }
        return null;
    }

    @Override
    public Invoker createSyncInvoker(Class clazz, URL url) {
        if (urlIsNull(url) || url.getDestination().getId().equals(SwiftProperty.getProperty().getClusterId())) {
            return new LocalInvoker(ProxyServiceRegistry.get().getService(clazz.getName()), clazz, url);
        }
        return null;
    }

    private boolean urlIsNull(URL url) {
        return url == null || url.getDestination() == null || url.getDestination().getId() == null;
    }
}
