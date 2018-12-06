package com.fr.swift.event.global;

import com.fr.swift.event.base.AbstractGlobalRpcEvent;
import com.fr.swift.source.SourceKey;

import java.io.Serializable;

/**
 * @author yee
 * @date 2018/6/27
 */
public class CleanMetaDataCacheEvent extends AbstractGlobalRpcEvent<SourceKey[]> implements Serializable {

    private static final long serialVersionUID = -9103583502609660412L;
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
