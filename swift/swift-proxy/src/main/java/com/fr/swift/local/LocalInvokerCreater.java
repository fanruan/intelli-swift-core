package com.fr.swift.local;

import com.fr.swift.basics.Invoker;
import com.fr.swift.basics.InvokerType;
import com.fr.swift.basics.URL;

/**
 * This class created on 2018/11/1
 *
 * @author Lucifer
 * @description
 * @since Advanced FineBI 5.0
 */
public class LocalInvokerCreater extends AbstractInvokerCreater {

    @Override
    public Invoker createAsyncInvoker(Class clazz, URL url) {
        return super.createAsyncInvoker(clazz, url);
    }

    @Override
    public Invoker createSyncInvoker(Class clazz, URL url) {
        return super.createSyncInvoker(clazz, url);
    }

    @Override
    public InvokerType getType() {
        return InvokerType.LOCAL;
    }
}
