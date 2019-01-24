package com.fr.swift.event.history;

import com.fr.swift.segment.SegmentKey;
import com.fr.swift.source.SourceKey;
import com.fr.swift.structure.Pair;

import java.util.List;
import java.util.Map;

/**
 * @author yee
 * @date 2018/9/12
 */
public class ModifyLoadRpcEvent extends CommonLoadRpcEvent {

    private static final long serialVersionUID = -3000346969764634426L;

    public ModifyLoadRpcEvent(Pair<SourceKey, Map<SegmentKey, List<String>>> content, String sourceClusterId) {
        super(content, sourceClusterId);
    }

    @Override
    public Event subEvent() {
        return Event.MODIFY_LOAD;
    }
}
