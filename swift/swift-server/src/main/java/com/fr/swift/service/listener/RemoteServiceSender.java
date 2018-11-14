package com.fr.swift.service.listener;

import com.fr.swift.basics.annotation.ProxyService;
import com.fr.swift.event.base.SwiftRpcEvent;
import com.fr.swift.log.SwiftLogger;
import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.service.SwiftService;
import com.fr.swift.service.SwiftServiceEvent;
import com.fr.third.springframework.stereotype.Service;

import java.io.Serializable;

/**
 * Created by pony on 2017/11/9.
 * 待实现，向远程的serverService注册本地启动的服务，触发事件
 */
// TODO: 2018/11/1 临时加service
@Service
@ProxyService(value = RemoteSender.class)
public class RemoteServiceSender implements RemoteSender {

    private static final SwiftLogger LOGGER = SwiftLoggers.getLogger(RemoteServiceSender.class);

    @Override
    public void addListener(SwiftServiceListener listener) {

    }

    @Override
    public void trigger(SwiftServiceEvent event) {

    }

    @Override
    public Serializable trigger(SwiftRpcEvent event) {
        return RemoteServiceReceiver.getInstance().trigger(event);
    }

    @Override
    public void registerService(SwiftService service) {
        LOGGER.debug("RemoteServiceSender registerService");
        RemoteServiceReceiver.getInstance().registerService(service);
    }

    @Override
    public void unRegisterService(SwiftService service) {
        LOGGER.debug("RemoteServiceSender unRegisterService");
        RemoteServiceReceiver.getInstance().unRegisterService(service);
    }
}
