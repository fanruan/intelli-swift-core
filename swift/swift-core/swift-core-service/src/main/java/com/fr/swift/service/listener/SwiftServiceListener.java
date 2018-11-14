package com.fr.swift.service.listener;

import com.fr.swift.service.SwiftServiceEvent;

/**
 * Created by pony on 2017/11/6.
 * 处理监听的类
 */
@Deprecated
public interface SwiftServiceListener<T> {
    /**
     * 如何处理事件
     *
     * @param event
     */
    void handle(SwiftServiceEvent<T> event);

    /**
     * 事件类型
     *
     * @return
     */
    EventType getType();

    /**
     * 事件顺序
     *
     * @return
     */
    EventOrder getOrder();

}
