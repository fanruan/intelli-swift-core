package com.fr.swift.event.base;

import java.io.Serializable;

/**
 * @author yee
 * @date 2018/6/8
 */
public interface SwiftRpcEvent<C> extends Serializable {

    EventType type();

    <T extends SubEvent> T subEvent();

    C getContent();

    enum EventType {
        REAL_TIME, HISTORY, INDEXING, ANALYSE
    }

    interface SubEvent {
    }
}
