package com.fr.swift.event.base;

/**
 * @author yee
 * @date 2018/6/29
 */
public abstract class AbstractGlobalRpcEvent<T> implements SwiftRpcEvent<T> {
    @Override
    public EventType type() {
        return EventType.GLOBAL;
    }

    @Override
    public abstract Event subEvent();

    public enum Event implements SubEvent {
        CLEAN, TASK_DONE, PUSH_SEG
    }
}
