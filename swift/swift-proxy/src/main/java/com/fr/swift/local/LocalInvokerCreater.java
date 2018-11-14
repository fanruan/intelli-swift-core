package com.fr.swift.local;

import com.fr.swift.basics.Invoker;
import com.fr.swift.basics.InvokerCreater;
import com.fr.swift.basics.URL;
import com.fr.swift.basics.base.ProxyServiceRegistry;

/**
 * This class created on 2018/11/1
 *
 * @author Lucifer
 * @description
 * @since Advanced FineBI 5.0
 */
public class LocalInvokerCreater implements InvokerCreater {

    @Override
    public Invoker createAsyncInvoker(Class clazz, URL url) {
        return new LocalInvoker(ProxyServiceRegistry.get().getService(clazz), clazz, url, false);
    }

    @Override
    public Invoker createSyncInvoker(Class clazz, URL url) {
        return new LocalInvoker(ProxyServiceRegistry.get().getService(clazz), clazz, url);
    }

}
