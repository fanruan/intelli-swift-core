package com.fr.swift.event.base;

/**
 * @author yee
 * @date 2018/6/8
 */
public abstract class AbstractAnalyseRpcEvent<T> implements SwiftRpcEvent<T> {

    @Override
    public EventType type() {
        return EventType.ANALYSE;
    }

    public abstract Event subEvent();

    public enum Event implements SubEvent {
        SEGMENT_LOCATION
    }
}
