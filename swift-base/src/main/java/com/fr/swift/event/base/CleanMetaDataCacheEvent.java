package com.fr.swift.event.base;

/**
 * @author yee
 * @date 2018/6/27
 */
public class CleanMetaDataCacheEvent implements SwiftRpcEvent<String[]> {

    private String[] needClean;

    public CleanMetaDataCacheEvent(String[] needClean) {
        this.needClean = needClean;
    }

    @Override
    public EventType type() {
        return EventType.GLOBAL;
    }

    @Override
    public Event subEvent() {
        return Event.CLEAN;
    }

    @Override
    public String[] getContent() {
        return needClean;
    }

    public enum Event implements SubEvent {
        CLEAN
    }
}
