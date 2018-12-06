package com.fr.swift.event.global;

import com.fr.swift.event.base.AbstractGlobalRpcEvent;

import java.io.Serializable;

/**
 * @author yee
 * @date 2018/9/13
 */
public class TruncateEvent extends AbstractGlobalRpcEvent<String> implements Serializable {
    private static final long serialVersionUID = -7776848856352325564L;
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
