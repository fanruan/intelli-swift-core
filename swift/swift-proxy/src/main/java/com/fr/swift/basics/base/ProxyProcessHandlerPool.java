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

    private Map<InvokerType, Map<Class<? extends ProcessHandler>, ProcessHandler>> handlerMap = new HashMap<InvokerType, Map<Class<? extends ProcessHandler>, ProcessHandler>>();

    private ProxyProcessHandlerPool() {
    }

    public static ProcessHandlerPool get() {
        return INSTANCE;
    }

    @Override
    public ProcessHandler getProcessHandler(Class<? extends ProcessHandler> aClass, InvokerCreator invokerCreator) throws Exception {
        if (!handlerMap.containsKey(invokerCreator.getType())) {
            synchronized (this) {
                if (!handlerMap.containsKey(invokerCreator.getType())) {
                    handlerMap.put(invokerCreator.getType(), new HashMap<Class<? extends ProcessHandler>, ProcessHandler>());
                }
            }
        }
        Map<Class<? extends ProcessHandler>, ProcessHandler> processHandlerMap = handlerMap.get(invokerCreator.getType());
        if (!processHandlerMap.containsKey(aClass)) {
            synchronized (this) {
                if (!processHandlerMap.containsKey(aClass)) {
                    ProcessHandler handler = SwiftContext.get().getBean(aClass, invokerCreator);
                    processHandlerMap.put(aClass, handler);
                }
            }
        }
        return processHandlerMap.get(aClass);
    }
}
