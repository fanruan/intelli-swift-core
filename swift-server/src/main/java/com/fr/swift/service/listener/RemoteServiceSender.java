package com.fr.swift.service.listener;

import com.fr.swift.basics.annotation.ProxyService;
import com.fr.swift.beans.annotation.SwiftBean;
import com.fr.swift.event.base.SwiftRpcEvent;
import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.service.SwiftService;

import java.io.Serializable;
import java.util.Collection;

/**
 * @author pony
 * @date 2017/11/9
 * 待实现，向远程的serverService注册本地启动的服务，触发事件
 */
@SwiftBean
@ProxyService(value = RemoteSender.class)
public class RemoteServiceSender implements RemoteSender {

    @Override
    public Serializable appointTrigger(Collection<String> urls, SwiftRpcEvent event) {
        return RemoteServiceReceiver.getInstance().trigger(event);
    }

    @Override
    public Serializable trigger(SwiftRpcEvent event) {
        return RemoteServiceReceiver.getInstance().trigger(event);
    }

    @Override
    public void registerService(SwiftService service) {
        SwiftLoggers.getLogger().debug("RemoteServiceSender registerService");
        RemoteServiceReceiver.getInstance().registerService(service);
    }

    @Override
    public void unRegisterService(SwiftService service) {
        SwiftLoggers.getLogger().debug("RemoteServiceSender unRegisterService");
        RemoteServiceReceiver.getInstance().unRegisterService(service);
    }
}
