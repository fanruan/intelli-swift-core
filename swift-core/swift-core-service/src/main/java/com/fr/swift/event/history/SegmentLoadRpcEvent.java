package com.fr.swift.event.history;

import com.fr.swift.event.base.AbstractHistoryRpcEvent;

/**
 * @author yee
 * @date 2018/9/12
 */
public abstract class SegmentLoadRpcEvent<T> extends AbstractHistoryRpcEvent<T> {
    public SegmentLoadRpcEvent(String sourceClusterId) {
        this.sourceClusterId = sourceClusterId;
    }
}
