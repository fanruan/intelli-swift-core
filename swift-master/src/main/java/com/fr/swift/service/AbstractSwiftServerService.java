package com.fr.swift.service;

import com.fr.swift.service.listener.SwiftServiceEventHandler;
import com.fr.swift.service.listener.SwiftServiceListenerManager;


/**
 * @author pony
 * @date 2017/10/10
 */
public abstract class AbstractSwiftServerService extends AbstractSwiftService implements SwiftServiceEventHandler {

    @Override
    public boolean start() {
        SwiftServiceListenerManager.getInstance().registerHandler(this);
        return true;
    }

    @Override
    public ServiceType getServiceType() {
        return ServiceType.SERVER;
    }
}
