package com.fr.swift.event.base;

/**
 * @author yee
 * @date 2018/6/8
 */
public abstract class AbstractHistoryRpcEvent<T> implements SwiftRpcEvent<T> {

    @Override
    public EventType type() {
        return EventType.HISTORY;
    }

    public abstract Event subEvent();

    public enum Event implements SubEvent {
        QUERY, LOAD, LOAD_RELATION
    }
}
