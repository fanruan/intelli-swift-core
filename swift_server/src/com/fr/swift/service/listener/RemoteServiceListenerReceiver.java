package com.fr.swift.service.listener;

import com.fr.swift.service.SwiftService;
import com.fr.swift.service.SwiftServiceEvent;

/**
 * Created by pony on 2017/11/10.
 * 待实现，接收远程serverService的注册，触发事件
 */
public class RemoteServiceListenerReceiver implements SwiftServiceListenerHandler {
    @Override
    public void addListener(SwiftServiceListener listener) {

    }

    @Override
    public void trigger(SwiftServiceEvent event) {
    }

    @Override
    public void registerService(SwiftService service) {

    }

    @Override
    public void unRegisterService(SwiftService service) {

    }
}
