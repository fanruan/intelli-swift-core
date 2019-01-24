package com.fr.swift.event.base;


import com.fr.swift.event.base.AbstractHistoryRpcEvent.Event;

/**
 * @author yee
 * @date 2018/6/8
 */
public abstract class AbstractHistoryRpcEvent<T> implements SwiftRpcEvent<T, Event> {

    protected String sourceClusterId;

    @Override
    public EventType type() {
        return EventType.HISTORY;
    }

    @Override
    public abstract Event subEvent();

    public String getSourceClusterId() {
        return sourceClusterId;
    }

    public void setSourceClusterId(String sourceClusterId) {
        this.sourceClusterId = sourceClusterId;
    }

    public enum Event implements SubEvent {
        LOAD_SEGMENT, TRANS_COLLATE_LOAD, COMMON_LOAD, CHECK_LOAD, MODIFY_LOAD, HISTORY_REMOVE
    }
}
