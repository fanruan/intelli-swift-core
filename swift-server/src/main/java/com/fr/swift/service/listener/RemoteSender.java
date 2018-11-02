package com.fr.swift.service.listener;

import com.fr.swift.basics.annotation.InvokeMethod;
import com.fr.swift.basics.base.handler.SwiftMasterProcessHandler;
import com.fr.swift.event.base.SwiftRpcEvent;
import com.fr.swift.service.SwiftService;
import com.fr.swift.service.SwiftServiceEvent;

import java.io.Serializable;

/**
 * This class created on 2018/11/1
 *
 * @author Lucifer
 * @description
 * @since Advanced FineBI 5.0
 */
public interface RemoteSender extends SwiftServiceListenerHandler {

    @Override
    void addListener(SwiftServiceListener listener);

    @Override
    void trigger(SwiftServiceEvent event);

    @Override
    @InvokeMethod(SwiftMasterProcessHandler.class)
    Serializable trigger(SwiftRpcEvent event);

    @Override
    @InvokeMethod(SwiftMasterProcessHandler.class)
    void registerService(SwiftService service);

    @Override
    @InvokeMethod(SwiftMasterProcessHandler.class)
    void unRegisterService(SwiftService service);
}
