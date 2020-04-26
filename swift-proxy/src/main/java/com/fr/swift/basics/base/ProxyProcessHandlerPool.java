package com.fr.swift.basics.base;

import com.fr.swift.SwiftContext;
import com.fr.swift.basics.InvokerCreator;
import com.fr.swift.basics.InvokerType;
import com.fr.swift.basics.ProcessHandler;
import com.fr.swift.basics.ProcessHandlerPool;

import java.util.HashMap;
import java.util.Map;

/**
 * This class created on 2018/11/15
 *
 * @author Lucifer
 * @description
 * @since Advanced FineBI 5.0
 */
public class ProxyProcessHandlerPool implements ProcessHandlerPool {

    private static final ProcessHandlerPool INSTANCE = new ProxyProcessHandlerPool();

    private Map<InvokerType, Map<Class<? extends ProcessHandler>, ProcessHandler>> handlerMap = new HashMap<>();

    private ProxyProcessHandlerPool() {
    }

    public static ProcessHandlerPool get() {
        return INSTANCE;
    }

    @Override
    public ProcessHandler getProcessHandler(Class<? extends ProcessHandler> aClass, InvokerCreator invokerCreator) throws Exception {
        if (!handlerMap.containsKey(invokerCreator.getType())) {
            synchronized (this) {
                handlerMap.computeIfAbsent(invokerCreator.getType(), n -> new HashMap<>());
            }
        }
        Map<Class<? extends ProcessHandler>, ProcessHandler> processHandlerMap = handlerMap.get(invokerCreator.getType());
        if (!processHandlerMap.containsKey(aClass)) {
            synchronized (this) {
                processHandlerMap.computeIfAbsent(aClass, n -> SwiftContext.get().getBean(n, invokerCreator));
            }
        }
        return processHandlerMap.get(aClass);
    }
}
