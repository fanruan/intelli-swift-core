package com.fr.swift.local;

import com.fr.swift.basics.Invoker;
import com.fr.swift.basics.ProcessHandler;
import com.fr.swift.basics.URL;
import com.fr.swift.basics.annotation.Process;
import com.fr.swift.basics.base.handler.BaseMasterProcessHandler;
import com.fr.swift.basics.handler.MasterProcessHandler;
import com.fr.swift.context.SwiftContext;

/**
 * @author yee
 * @date 2018/10/25
 */
@Process(inf = {MasterProcessHandler.class, ProcessHandler.class})
public class LocalProcessHandler extends BaseMasterProcessHandler {

    @Override
    protected Invoker createInvoker(Class tClass, URL url) {
        return new LocalInvoker(SwiftContext.get().getBean(tClass), tClass, url);
    }
}
