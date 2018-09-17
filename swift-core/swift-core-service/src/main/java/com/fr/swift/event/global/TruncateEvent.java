package com.fr.swift.event.global;

import com.fr.swift.event.base.AbstractGlobalRpcEvent;

/**
 * @author yee
 * @date 2018/9/13
 */
public class TruncateEvent extends AbstractGlobalRpcEvent<String> {
    private String sourceKey;

    public TruncateEvent(String sourceKey) {
        this.sourceKey = sourceKey;
    }

    @Override
    public Event subEvent() {
        return Event.TRUNCATE;
    }

    @Override
    public String getContent() {
        return sourceKey;
    }
}
