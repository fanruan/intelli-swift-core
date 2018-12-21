package com.fr.swift.service;

import com.fr.swift.exception.SwiftServiceException;
import com.fr.swift.log.SwiftLogger;
import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.service.listener.SwiftServiceListenerManager;

import java.io.Serializable;

/**
 * Created by pony on 2017/10/10.
 */
public abstract class AbstractSwiftService implements SwiftService, Serializable {

    private static final long serialVersionUID = -7878341721352591837L;
    protected transient SwiftLogger logger = SwiftLoggers.getLogger(getClass());
    //远程机器id，没有表示本地
    private String id;

    public AbstractSwiftService(String id) {
        this.id = id;
    }

    public AbstractSwiftService() {
    }

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
    public String getId() {
        return id;
    }

    @Override
    public void setId(String id) {
        this.id = id;
    }
}
