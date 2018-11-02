package com.fr.swift.core.rpc;

import com.fr.swift.basics.Invoker;
import com.fr.swift.basics.InvokerCreater;
import com.fr.swift.basics.URL;
import com.fr.swift.context.SwiftContext;

/**
 * This class created on 2018/11/1
 *
 * @author Lucifer
 * @description
 * @since Advanced FineBI 5.0
 */
public class FRInvokerCreater implements InvokerCreater {

    @Override
    public Invoker createInvoker(Class clazz, URL url, boolean sync) {
        return new FRInvoker(SwiftContext.get().getBean(clazz), clazz, url, sync);
    }

    @Override
    public Invoker createInvoker(Class clazz, URL url) {
        return new FRInvoker(SwiftContext.get().getBean(clazz), clazz, url);
    }
}
