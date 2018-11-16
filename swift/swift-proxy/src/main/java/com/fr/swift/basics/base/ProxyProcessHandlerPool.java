package com.fr.swift.basics.base;

import com.fr.swift.basics.InvokerCreater;
import com.fr.swift.basics.InvokerType;
import com.fr.swift.basics.ProcessHandler;
import com.fr.swift.basics.ProcessHandlerPool;

import java.lang.reflect.Constructor;
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
    public ProcessHandler getProcessHandler(Class<? extends ProcessHandler> aClass, InvokerCreater invokerCreater) throws Exception {
        if (!handlerMap.containsKey(invokerCreater.getType())) {
            handlerMap.put(invokerCreater.getType(), new HashMap<Class<? extends ProcessHandler>, ProcessHandler>());
        }
        Map<Class<? extends ProcessHandler>, ProcessHandler> processHandlerMap = handlerMap.get(invokerCreater.getType());
        if (!processHandlerMap.containsKey(aClass)) {
            synchronized (ProxyProcessHandlerPool.class) {
                if (!processHandlerMap.containsKey(aClass)) {
                    Constructor<? extends ProcessHandler> cons = aClass.getDeclaredConstructor(InvokerCreater.class);
                    ProcessHandler handler = cons.newInstance(invokerCreater);
                    processHandlerMap.put(aClass, handler);
                }
            }
        }
        return processHandlerMap.get(aClass);
    }
}
