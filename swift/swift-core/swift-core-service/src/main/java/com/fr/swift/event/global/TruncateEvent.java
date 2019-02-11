package com.fr.swift.event.global;

import com.fr.swift.event.base.AbstractGlobalRpcEvent;
import com.fr.swift.source.SourceKey;

/**
 * @author yee
 * @date 2018/9/13
 */
public class TruncateEvent extends AbstractGlobalRpcEvent<SourceKey> {
    private static final long serialVersionUID = -7915655548354335399L;
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
