package com.fr.swift.event.base;

/**
 * @author yee
 * @date 2018/6/8
 */
public interface SwiftRpcEvent<C, T extends SubEvent> {

    EventType type();

    T subEvent();

    C getContent();

    enum EventType {
        REAL_TIME, HISTORY, INDEXING, ANALYSE, GLOBAL
    }


}

interface SubEvent {
}