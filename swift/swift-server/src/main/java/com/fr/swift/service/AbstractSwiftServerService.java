package com.fr.swift.service;

import com.fr.swift.event.base.SwiftRpcEvent;
import com.fr.swift.service.listener.SwiftServiceListenerHandler;
import com.fr.swift.service.listener.SwiftServiceListenerManager;
import com.fr.swift.task.impl.SchedulerTaskPool;

import javax.annotation.PostConstruct;
import java.io.Serializable;


/**
 * @author pony
 * @date 2017/10/10
 */
public abstract class AbstractSwiftServerService extends AbstractSwiftService implements SwiftServiceListenerHandler {

    @PostConstruct
    @Override
    public boolean start() {
        initListener();
        SwiftServiceListenerManager.getInstance().registerHandler(this);
        return true;
    }

    @Override
    public ServiceType getServiceType() {
        return ServiceType.SERVER;
    }

    @Override
    public Serializable trigger(SwiftRpcEvent event) {
        return null;
    }

    protected void initListener() {
        SchedulerTaskPool.getInstance().initListener();
    }
}
