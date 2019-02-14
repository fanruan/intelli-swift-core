package com.fr.swift.event.base;

import com.fr.swift.event.base.AbstractAnalyseRpcEvent.Event;

/**
 * @author yee
 * @date 2018/6/8
 */
public abstract class AbstractAnalyseRpcEvent<T> implements SwiftRpcEvent<T, Event> {

    @Override
    public EventType type() {
        return EventType.ANALYSE;
    }

    public abstract Event subEvent();

    /**
     * @author yee
     * @date 2018/9/13
     */
    public enum Event implements SubEvent {
        SEGMENT_LOCATION, REQUEST_SEG_LOCATION
    }
}
