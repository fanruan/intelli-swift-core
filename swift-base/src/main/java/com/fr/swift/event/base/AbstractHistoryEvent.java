package com.fr.swift.event.base;

/**
 * @author yee
 * @date 2018/6/8
 */
public abstract class AbstractHistoryEvent implements SwiftEvent {
    @Override
    public EventType type() {
        return EventType.HISTORY;
    }

    public abstract Event subEvent();

    public enum Event implements SubEvent {

    }
}
