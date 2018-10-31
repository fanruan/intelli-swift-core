package com.fr.swift.basics.base;

import com.fr.swift.basics.ProcessHandler;
import com.fr.swift.basics.ProcessHandlerRegistry;
import com.fr.swift.basics.annotation.Process;
import com.fr.swift.basics.exception.ClassIsNotInterfaceException;
import com.fr.swift.basics.exception.ProcessHandlerNotRegisterException;
import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.structure.lru.ConcurrentCacheHashMap;

import java.lang.reflect.Modifier;
import java.util.Map;

/**
 * @author yee
 * @date 2018/10/24
 */
public abstract class BaseProcessHandlerRegistry implements ProcessHandlerRegistry {

    private Map<Class<? extends ProcessHandler>, ProcessHandler> handlerMap = new ConcurrentCacheHashMap<Class<? extends ProcessHandler>, ProcessHandler>();

    public BaseProcessHandlerRegistry() {
        init();
    }

    private void init() {
        ProcessHandler[] handlers = registerProcessHandlers();
        if (null != handlers) {
            for (ProcessHandler handler : handlers) {
                Class<? extends ProcessHandler> pClass = handler.getClass();
                if (pClass.isAnnotationPresent(Process.class)) {
                    Process process = pClass.getAnnotation(Process.class);
                    if (null != process) {
                        Class<? extends ProcessHandler>[] classes = process.inf();
                        for (Class<? extends ProcessHandler> aClass : classes) {
                            if (!Modifier.isInterface(aClass.getModifiers())) {
                                throw new ClassIsNotInterfaceException(aClass);
                            }
                            addHandler(aClass, handler);
                        }
                    }
                } else {
                    SwiftLoggers.getLogger().warn("Ignore because of {} is not wrapped by {}.", pClass.getName(), Process.class.getClasses());
                }
            }
        }
    }

    @Override
    public void addHandler(Class<? extends ProcessHandler> cProcessHandler, ProcessHandler processHandler) {
        handlerMap.put(cProcessHandler, checkProcessHandler(cProcessHandler, processHandler));
    }

    @Override
    public ProcessHandler getHandler(Class<? extends ProcessHandler> cProcessHandler) {
        return checkProcessHandler(cProcessHandler, handlerMap.get(cProcessHandler));
    }

    private ProcessHandler checkProcessHandler(Class<? extends ProcessHandler> cProcessHandler, ProcessHandler handler) {
        if (null == handler) {
            throw new ProcessHandlerNotRegisterException(cProcessHandler);
        }
        return handler;
    }

    /**
     * 注册ProcessHandler
     *
     * @return
     */
    protected abstract ProcessHandler[] registerProcessHandlers();
}
