package com.fr.swift.event.history;

import com.fr.swift.segment.SegmentKey;
import com.fr.swift.source.SourceKey;
import com.fr.swift.structure.Pair;

import java.util.List;
import java.util.Map;

/**
 * @author yee
 * @date 2018/6/29
 */
public class HistoryCommonLoadRpcEvent extends CommonLoadRpcEvent {

    private static final long serialVersionUID = -6628972279664707378L;

    public HistoryCommonLoadRpcEvent(Pair<SourceKey, Map<SegmentKey, List<String>>> relation, String sourceClusterId) {
        super(relation, sourceClusterId);
    }

    @Override
    public Event subEvent() {
        return Event.COMMON_LOAD;
    }
}
