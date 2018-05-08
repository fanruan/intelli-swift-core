package com.fr.swift.service;

import com.fr.swift.exception.SwiftServiceException;
import com.fr.swift.service.listener.SwiftServiceListenerManager;

/**
 * Created by pony on 2017/10/10.
 */
public abstract class AbstractSwiftService implements SwiftService {
    private String id;
    @Override
    public boolean start() throws SwiftServiceException {
        SwiftServiceListenerManager.getInstance().registerService(this);
        return true;
    }

    @Override
    public boolean shutdown() throws SwiftServiceException {
        SwiftServiceListenerManager.getInstance().unRegisterService(this);
        return false;
    }

    @Override
    public String getID() {
        return id;
    }
}
