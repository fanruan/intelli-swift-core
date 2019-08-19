package com.fr.swift.event.base;

import com.fr.swift.event.base.AbstractExceptionRpcEvent.Event;

/**
 * @author Marvin
 * @date 8/14/2019
 * @description
 * @since swift 1.1
 */
public abstract class AbstractExceptionRpcEvent<T> implements SwiftRpcEvent<T, Event> {
    @Override
    public EventType type() {
        return EventType.EXCEPTION;
    }

    @Override
    public abstract Event subEvent();

    @Override
    public T getContent() {
        return null;
    }

    public enum Event implements SubEvent {
        SOLVED, UNSOLVED
    }
}
