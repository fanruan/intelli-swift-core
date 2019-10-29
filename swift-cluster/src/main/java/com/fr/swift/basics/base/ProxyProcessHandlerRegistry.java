package com.fr.swift.basics.base;

import com.fr.swift.basics.ProcessHandler;
import com.fr.swift.basics.ProcessHandlerRegistry;
import com.fr.swift.util.Assert;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * This class created on 2018/11/6
 *
 * @author Lucifer
 * @description
 * @since Advanced FineBI 5.0
 */
public class ProxyProcessHandlerRegistry implements ProcessHandlerRegistry {

    private static final ProxyProcessHandlerRegistry INSTANCE = new ProxyProcessHandlerRegistry();

    private Map<Class<? extends ProcessHandler>, Class<? extends ProcessHandler>> handlerMap = new ConcurrentHashMap<Class<? extends ProcessHandler>, Class<? extends ProcessHandler>>();

    private ProxyProcessHandlerRegistry() {
    }

    public static ProcessHandlerRegistry get() {
        return INSTANCE;
    }

    @Override
    public void addHandler(Class<? extends ProcessHandler> iProcessHandler, Class<? extends ProcessHandler> cProcessHandler) {
        Assert.isAssignable(iProcessHandler, cProcessHandler);
        handlerMap.put(iProcessHandler, cProcessHandler);
    }

    @Override
    public Class getHandler(Class<? extends ProcessHandler> iProcessHandler) {
        return handlerMap.get(iProcessHandler);
    }
}
