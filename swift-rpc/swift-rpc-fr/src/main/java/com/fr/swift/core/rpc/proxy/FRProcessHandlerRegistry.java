package com.fr.swift.core.rpc.proxy;

import com.fr.swift.basics.ProcessHandler;
import com.fr.swift.basics.base.BaseProcessHandlerRegistry;

/**
 * @author yee
 * @date 2018/10/25
 */
public class FRProcessHandlerRegistry extends BaseProcessHandlerRegistry {
    @Override
    protected ProcessHandler[] registerProcessHandlers() {
        return new ProcessHandler[]{
                new FRMasterProcessHandler()
        };
    }
}
