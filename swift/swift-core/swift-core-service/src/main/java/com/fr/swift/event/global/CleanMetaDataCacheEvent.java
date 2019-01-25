package com.fr.swift.event.global;

import com.fr.swift.event.base.AbstractGlobalRpcEvent;
import com.fr.swift.source.SourceKey;

/**
 * @author yee
 * @date 2018/6/27
 */
public class CleanMetaDataCacheEvent extends AbstractGlobalRpcEvent<SourceKey[]> {

    private static final long serialVersionUID = 2355178141491253387L;

    private SourceKey[] needClean;

    public CleanMetaDataCacheEvent(SourceKey[] needClean) {
        this.needClean = needClean;
    }

    @Override
    public SourceKey[] getContent() {
        return needClean;
    }

    @Override
    public Event subEvent() {
        return Event.CLEAN;
    }
}
