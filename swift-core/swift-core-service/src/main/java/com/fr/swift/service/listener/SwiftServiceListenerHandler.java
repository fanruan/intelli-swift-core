package com.fr.swift.service.listener;

import com.fr.swift.event.base.SwiftRpcEvent;
import com.fr.swift.service.SwiftService;

import java.io.Serializable;

/**
 * @author pony
 * @date 2017/11/7
 * <p>
 * 处理服务事件监听的类
 */
public interface SwiftServiceListenerHandler {

    /**
     * Rpc
     *
     * @param event
     * @return
     */
    Serializable trigger(SwiftRpcEvent event);

    /**
     * 注册服务
     *
     * @param service
     */
    void registerService(SwiftService service);

    /**
     * 注销服务
     *
     * @param service
     */
    void unRegisterService(SwiftService service);
}