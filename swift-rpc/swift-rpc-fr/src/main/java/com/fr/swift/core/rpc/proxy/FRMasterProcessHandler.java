package com.fr.swift.core.rpc.proxy;

import com.fr.swift.basics.Invoker;
import com.fr.swift.basics.URL;
import com.fr.swift.basics.base.handler.BaseMasterProcessHandler;
import com.fr.swift.context.SwiftContext;
import com.fr.swift.core.rpc.FRInvoker;

/**
 * @author yee
 * @date 2018/10/25
 */
public class FRMasterProcessHandler extends BaseMasterProcessHandler {
    @Override
    protected Invoker createInvoker(Class tClass, URL url) {
        return new FRInvoker(SwiftContext.get().getBean(tClass), tClass, url);
    }
}
