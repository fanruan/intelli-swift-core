package com.fr.swift.event.base;

import com.fr.swift.event.base.AbstractGlobalRpcEvent.Event;

/**
 * @author yee
 * @date 2018/6/29
 */
public abstract class AbstractGlobalRpcEvent<T> implements SwiftRpcEvent<T, Event> {
    @Override
    public EventType type() {
        return EventType.GLOBAL;
    }

    @Override
    public abstract Event subEvent();

    public enum Event implements SubEvent {
        //
        CLEAN, TASK_DONE, PUSH_SEG, REMOVE_SEG, GET_ANALYSE_REAL_TIME, DELETE, TRUNCATE, CHECK_MASTER
    }
}
