package com.fr.swift.event.global;

import com.fr.swift.event.base.AbstractGlobalRpcEvent;
import com.fr.swift.segment.SegmentKey;
import com.fr.swift.service.ServiceType;
import com.fr.swift.structure.Pair;

import java.util.ArrayList;
import java.util.List;

/**
 * @author yee
 * @date 2018/7/11
 */
public class PushSegLocationRpcEvent extends AbstractGlobalRpcEvent<Pair<ServiceType, List<String>>> {

    private Pair<ServiceType, List<String>> sourceKeys;

    private PushSegLocationRpcEvent(Pair<ServiceType, List<String>> sourceKeys) {
        this.sourceKeys = sourceKeys;
    }

    public static PushSegLocationRpcEvent fromSourceKey(ServiceType type, List<String> sourceKeys) {
        return new PushSegLocationRpcEvent(Pair.of(type, sourceKeys));
    }

    public static PushSegLocationRpcEvent fromSegmentKey(ServiceType type, List<SegmentKey> segmentKeys) {
        List<String> sourceKeys = new ArrayList<String>() {
            @Override
            public boolean add(String s) {
                return contains(s) ? false : super.add(s);
            }
        };
        for (SegmentKey segmentKey : segmentKeys) {
            sourceKeys.add(segmentKey.getTable().getId());
        }
        return new PushSegLocationRpcEvent(Pair.of(type, sourceKeys));
    }

    public Event subEvent() {
        return Event.PUSH_SEG;
    }

    public Pair<ServiceType, List<String>> getContent() {
        return sourceKeys;
    }
}
