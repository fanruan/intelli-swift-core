package com.fr.swift.service;

import com.fr.swift.service.listener.EventType;

/**
 * @author pony
 * @date 2017/11/7
 * 触发的事件
 */
public interface SwiftServiceEvent<T> {
    /**
     * 事件的内容
     *
     * @return 事件的内容
     */
    T getContent();

    /**
     * 事件的类型
     *
     * @return 事件的类型
     */
    EventType getEventType();
}
