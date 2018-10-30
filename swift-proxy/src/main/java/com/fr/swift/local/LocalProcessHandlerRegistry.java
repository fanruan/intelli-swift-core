package com.fr.swift.local;

import com.fr.swift.basics.ProcessHandler;
import com.fr.swift.basics.base.BaseProcessHandlerRegistry;

/**
 * @author yee
 * @date 2018/10/25
 */
public class LocalProcessHandlerRegistry extends BaseProcessHandlerRegistry {
    @Override
    protected ProcessHandler[] registerProcessHandlers() {
        return new ProcessHandler[]{
                new LocalProcessHandler()
        };
    }
}
