package com.fr.swift.basics.base;

import com.fr.swift.basics.ProcessHandler;
import com.fr.swift.basics.ProcessHandlerRegistry;
import com.fr.swift.structure.lru.ConcurrentCacheHashMap;

import java.util.Map;

/**
 * This class created on 2018/11/6
 *
 * @author Lucifer
 * @description
 * @since Advanced FineBI 5.0
 */
public class ProxyProcessHandlerRegistry implements ProcessHandlerRegistry {

    private Map<Class<? extends ProcessHandler>, Class<? extends ProcessHandler>> handlerMap = new ConcurrentCacheHashMap<Class<? extends ProcessHandler>, Class<? extends ProcessHandler>>();

    public static final ProcessHandlerRegistry INSTANCE = new ProxyProcessHandlerRegistry();

    @Override
    public void addHandler(Class<? extends ProcessHandler> iProcessHandler, Class<? extends ProcessHandler> cProcessHandler) {
        handlerMap.put(iProcessHandler, cProcessHandler);
    }

    @Override
    public Class<? extends ProcessHandler> getHandler(Class<? extends ProcessHandler> iProcessHandler) {
        return handlerMap.get(iProcessHandler);
    }
}
