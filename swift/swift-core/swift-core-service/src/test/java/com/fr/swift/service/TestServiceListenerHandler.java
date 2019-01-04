package com.fr.swift.service;

import com.fr.swift.event.base.SwiftRpcEvent;
import com.fr.swift.service.listener.SwiftServiceListenerHandler;

import java.io.Serializable;

/**
 * This class created on 2019/1/4
 *
 * @author Lucifer
 * @description
 */
public class TestServiceListenerHandler implements SwiftServiceListenerHandler {

    @Override
    public Serializable trigger(SwiftRpcEvent event) {
        return null;
    }

    @Override
    public void registerService(SwiftService service) {
    }

    @Override
    public void unRegisterService(SwiftService service) {

    }
}
