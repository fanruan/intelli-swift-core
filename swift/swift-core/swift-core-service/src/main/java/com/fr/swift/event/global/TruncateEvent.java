package com.fr.swift.event.global;

import com.fr.swift.event.base.AbstractGlobalRpcEvent;
import com.fr.swift.source.SourceKey;

import java.io.Serializable;

/**
 * @author yee
 * @date 2018/9/13
 */
public class TruncateEvent extends AbstractGlobalRpcEvent<SourceKey> implements Serializable {
    private static final long serialVersionUID = -7776848856352325564L;
    private SourceKey content;

    public TruncateEvent(String sourceKey) {
        this.content = new SourceKey(sourceKey);
    }

    @Override
    public Event subEvent() {
        return Event.TRUNCATE;
    }

    @Override
    public SourceKey getContent() {
        return content;
    }
}
