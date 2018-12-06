package com.fr.swift.event.global;

import com.fr.swift.event.base.AbstractGlobalRpcEvent;
import com.fr.swift.segment.SegmentLocationInfo;

import java.io.Serializable;

/**
 * @author yee
 * @date 2018/7/11
 */
public class PushSegLocationRpcEvent extends AbstractGlobalRpcEvent<SegmentLocationInfo> implements Serializable {

    private static final long serialVersionUID = -4672025049895674130L;
    private SegmentLocationInfo sourceKeys;

    public PushSegLocationRpcEvent(SegmentLocationInfo sourceKeys) {
        this.sourceKeys = sourceKeys;
    }


    @Override
    public Event subEvent() {
        return Event.PUSH_SEG;
    }

    @Override
    public SegmentLocationInfo getContent() {
        return sourceKeys;
    }
}
