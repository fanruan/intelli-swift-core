package com.fr.swift.service;

import com.fr.swift.service.listener.SwiftServiceListenerHandler;
import com.fr.swift.service.listener.SwiftServiceListenerManager;
import com.fr.swift.task.impl.SchedulerTaskPool;


/**
 * @author pony
 * @date 2017/10/10
 */
public abstract class AbstractSwiftServerService extends AbstractSwiftService implements SwiftServiceListenerHandler {

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

    protected void initListener() {
        SchedulerTaskPool.getInstance().initListener();
    }
}
