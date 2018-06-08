package com.fr.swift.service.event.base;

/**
 * @author yee
 * @date 2018/6/8
 */
public abstract class AbstractIndexingEvent implements SwiftEvent {
    @Override
    public EventType type() {
        return EventType.INDEXING;
    }

    public abstract Event subEvent();

    public enum Event implements SubEvent {

    }
}
