package com.fr.swift.service.listener;

import com.fr.swift.basic.URL;
import com.fr.swift.event.base.SwiftRpcEvent;
import com.fr.swift.exception.SwiftServiceException;
import com.fr.swift.exception.SwiftServiceListenerHandlerAbsentException;
import com.fr.swift.service.SwiftService;
import com.fr.swift.util.Util;

import java.util.Set;

/**
 * @author pony
 * @date 2017/11/6
 * 管理service向server注册与触发事件的类。
 */
public class SwiftServiceListenerManager {
    private static final SwiftServiceListenerManager INSTANCE = new SwiftServiceListenerManager();

    private SwiftServiceEventHandler handler;

    public static SwiftServiceListenerManager getInstance() {
        return INSTANCE;
    }

    /**
     * 单机模式就注册单机的handler, 多台就注册RemoteServiceListenerHandler
     *
     * @param handler
     * @see SwiftServiceListenerHandler
     */
    public void registerHandler(SwiftServiceEventHandler handler) {
        Util.requireNonNull(handler);
        this.handler = handler;
    }

    /**
     * 注册服务
     *
     * @param service
     * @throws SwiftServiceException
     */
    public void registerService(SwiftService service) throws SwiftServiceException {
        checkIfHandlerRegistered();
        this.handler.registerService(service);
    }

    /**
     * 注销服务
     *
     * @param service
     * @throws SwiftServiceException
     */
    public void unRegisterService(SwiftService service) throws SwiftServiceException {
        checkIfHandlerRegistered();
        this.handler.unRegisterService(service);
    }

    /**
     * 通知服务，触发事件
     *
     * @param event
     * @throws SwiftServiceException
     */
    public void triggerEvent(SwiftRpcEvent event) throws SwiftServiceException {
        checkIfHandlerRegistered();
        handler.trigger(event);
    }

    public Set<URL> getNodeUrls(Class<?> proxyIface) {
        return handler.getNodeUrls(proxyIface);
    }

    private void checkIfHandlerRegistered() throws SwiftServiceListenerHandlerAbsentException {
        if (this.handler == null) {
            throw new SwiftServiceListenerHandlerAbsentException("can not register without handler");
        }
    }
}