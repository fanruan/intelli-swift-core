package com.fr.swift.event.history;

import com.fr.swift.structure.Pair;

import java.util.List;

/**
 * @author yee
 * @date 2018/9/12
 */
public class TransCollateLoadEvent extends SegmentLoadRpcEvent<Pair<String, List<String>>> {

    private static final long serialVersionUID = -8128616454294243512L;
    private Pair<String, List<String>> segmentKey;

    public TransCollateLoadEvent(Pair<String, List<String>> segmentKey) {
        this.segmentKey = segmentKey;
    }

    @Override
    public Event subEvent() {
        return Event.TRANS_COLLATE_LOAD;
    }

    @Override
    public Pair<String, List<String>> getContent() {
        return segmentKey;
    }
}
