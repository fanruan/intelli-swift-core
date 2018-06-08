package com.fr.swift.service.handler.base;

import com.fr.swift.event.base.SwiftEvent;

/**
 * @author yee
 * @date 2018/6/8
 */
public interface Handler<T extends SwiftEvent> {
    void handle(T event);
}
