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

    @Override
    public abstract Event subEvent();

    public enum Event implements SubEvent {
        LOAD_SEGMENT, TRANS_COLLATE_LOAD, COMMON_LOAD, MODIFY_LOAD
    }
}
