package com.fr.swift.service.listener;

import com.fr.swift.event.base.SwiftRpcEvent;
import com.fr.swift.exception.SwiftServiceException;
import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.service.SwiftService;
import com.fr.swift.service.handler.SwiftServiceHandlerManager;

import java.io.Serializable;

/**
 * @author pony
 * @date 2017/11/10
 * 待实现，接收远程serverService的注册，触发事件
 */
public class RemoteServiceReceiver implements RemoteReceiver {

    private static final RemoteServiceReceiver INSTANCE = new RemoteServiceReceiver();

    private RemoteServiceReceiver() {
    }

    public static RemoteServiceReceiver getInstance() {
        return INSTANCE;
    }

    @Override
    public Serializable trigger(SwiftRpcEvent event) {
        try {
            return SwiftServiceHandlerManager.getManager().handle(event);
        } catch (Exception e) {
            SwiftLoggers.getLogger().error(e);
            return null;
        }
    }

    @Override
    public void registerService(SwiftService service) {
        try {
            SwiftLoggers.getLogger().debug("RemoteServiceReceiver registerService");
            SwiftServiceListenerManager.getInstance().registerService(service);
        } catch (SwiftServiceException e) {
            SwiftLoggers.getLogger().error(e);
        }
    }

    @Override
    public void unRegisterService(SwiftService service) {
        try {
            SwiftLoggers.getLogger().debug("RemoteServiceReceiver unRegisterService");
            SwiftServiceListenerManager.getInstance().unRegisterService(service);
        } catch (SwiftServiceException e) {
            SwiftLoggers.getLogger().error(e);
        }
    }
}
