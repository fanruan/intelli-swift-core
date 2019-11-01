package com.fr.swift.service.listener;

import com.fr.swift.basics.annotation.InvokeMethod;
import com.fr.swift.basics.handler.AppointProcessHandler;
import com.fr.swift.basics.handler.MasterProcessHandler;
import com.fr.swift.event.base.SwiftRpcEvent;
import com.fr.swift.service.SwiftService;

import java.io.Serializable;
import java.util.Collection;

/**
 * This class created on 2018/11/1
 *
 * @author Lucifer
 * @description
 * @since Advanced FineBI 5.0
 */
public interface RemoteSender extends SwiftServiceListenerHandler {

    @InvokeMethod(AppointProcessHandler.class)
    Serializable appointTrigger(Collection<String> urls, SwiftRpcEvent event);

    @Override
    @InvokeMethod(MasterProcessHandler.class)
    Serializable trigger(SwiftRpcEvent event);

    @Override
    @InvokeMethod(MasterProcessHandler.class)
    void registerService(SwiftService service);

    @Override
    @InvokeMethod(MasterProcessHandler.class)
    void unRegisterService(SwiftService service);
}
