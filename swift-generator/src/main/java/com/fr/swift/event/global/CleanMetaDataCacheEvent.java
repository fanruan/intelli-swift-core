package com.fr.swift.event.global;

import com.fr.swift.event.base.AbstractGlobalRpcEvent;

/**
 * @author yee
 * @date 2018/6/27
 */
public class CleanMetaDataCacheEvent extends AbstractGlobalRpcEvent<String[]> {

    private String[] needClean;

    public CleanMetaDataCacheEvent(String[] needClean) {
        this.needClean = needClean;
    }

    @Override
    public String[] getContent() {
        return needClean;
    }

    @Override
    public Event subEvent() {
        return Event.CLEAN;
    }
}
