package com.fr.swift.event.base;

/**
 * @author yee
 * @date 2018/6/8
 */
public interface SwiftEvent {

    EventType type();

    <T extends SubEvent> T subEvent();

    enum EventType {
        REAL_TIME, HISTORY, INDEXING, ANALYSE
    }

    interface SubEvent {
    }
}
