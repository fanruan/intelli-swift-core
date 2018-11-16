package com.fr.swift.local;

import com.fr.swift.basics.Invoker;
import com.fr.swift.basics.InvokerCreater;
import com.fr.swift.basics.URL;
import com.fr.swift.basics.base.ProxyServiceRegistry;
import com.fr.swift.property.SwiftProperty;

/**
 * This class created on 2018/11/15
 *
 * @author Lucifer
 * @description
 * @since Advanced FineBI 5.0
 */
public abstract class AbstractInvokerCreater implements InvokerCreater {
    @Override
    public Invoker createAsyncInvoker(Class clazz, URL url) {
        if (url == null || url.getDestination().getId().equals(SwiftProperty.getProperty().getClusterId())) {
            return new LocalInvoker(ProxyServiceRegistry.get().getService(clazz), clazz, url, false);
        }
        return null;
    }

    @Override
    public Invoker createSyncInvoker(Class clazz, URL url) {
        if (url == null || url.getDestination().getId().equals(SwiftProperty.getProperty().getClusterId())) {
            return new LocalInvoker(ProxyServiceRegistry.get().getService(clazz), clazz, url);
        }
        return null;
    }
}
