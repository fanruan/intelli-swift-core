package com.fr.swift.service;

import com.fr.swift.exception.SwiftServiceException;

import java.io.Serializable;

/**
 * @author pony
 * @date 2017/10/10
 */
public abstract class AbstractSwiftService implements SwiftService, Serializable {

    private static final long serialVersionUID = -7878341721352591837L;
    /**
     * 远程机器id，没有表示本地
     */
    private String id;

    public AbstractSwiftService(String id) {
        this.id = id;
    }

    public AbstractSwiftService() {
    }

    @Override
    public boolean start() throws SwiftServiceException {
//        SwiftServiceListenerManager.getInstance().registerService(this);
        return true;
    }

    @Override
    public boolean shutdown() throws SwiftServiceException {
//        SwiftServiceListenerManager.getInstance().unRegisterService(this);
        return true;
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
