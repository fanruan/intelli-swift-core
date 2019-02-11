package com.fr.swift.event.history;

import com.fr.swift.segment.SegmentKey;
import com.fr.swift.source.SourceKey;
import com.fr.swift.structure.Pair;

import java.util.List;

/**
 * @author yee
 * @date 2018/9/12
 */
public class TransCollateLoadEvent extends SegmentLoadRpcEvent<Pair<SourceKey, List<SegmentKey>>> {

    private static final long serialVersionUID = -937933243124511444L;

    private Pair<SourceKey, List<SegmentKey>> segmentKey;

    public TransCollateLoadEvent(Pair<SourceKey, List<SegmentKey>> segmentKey, String sourceClusterId) {
        super(sourceClusterId);
        this.segmentKey = segmentKey;
    }

    @Override
    public Event subEvent() {
        return Event.TRANS_COLLATE_LOAD;
    }

    @Override
    public Pair<SourceKey, List<SegmentKey>> getContent() {
        return segmentKey;
    }
}
