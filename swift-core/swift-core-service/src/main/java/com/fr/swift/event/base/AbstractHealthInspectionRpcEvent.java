package com.fr.swift.event.base;

import com.fr.swift.event.base.AbstractHealthInspectionRpcEvent.Event;

/**
 * @author Marvin
 * @version 1.1
 * Created by Marvin on 8/30/2019
 */
public abstract class AbstractHealthInspectionRpcEvent<T> implements SwiftRpcEvent<T, Event> {
    @Override
    public EventType type() {
        return EventType.HEALTH_INSPECT;
    }

    @Override
    public abstract Event subEvent();

    @Override
    public T getContent() {
        return null;
    }

    public enum Event implements SubEvent {
        /**
         * INSPECT_MASTER:master可达性
         * INSPECT_SLAVE:其它节点可达性
         */
        INSPECT_MASTER, INSPECT_SLAVE
    }
}
