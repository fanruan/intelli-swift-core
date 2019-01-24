package com.fr.swift.event.history;

import com.fr.swift.event.base.AbstractHistoryRpcEvent;
import com.fr.swift.segment.SegmentKey;
import com.fr.swift.source.SourceKey;
import com.fr.swift.structure.Pair;

import java.util.List;

/**
 * This class created on 2019/1/16
 *
 * @author Lucifer
 * @description
 */
public class HistoryRemoveEvent extends AbstractHistoryRpcEvent {

    private static final long serialVersionUID = 5778321156623768337L;

    private Pair<SourceKey, List<SegmentKey>> content;

    public HistoryRemoveEvent(List<SegmentKey> needRemoveList, SourceKey sourceKey, String clusterId) {
        content = new Pair<SourceKey, List<SegmentKey>>(sourceKey, needRemoveList);
        this.sourceClusterId = clusterId;
    }

    @Override
    public Event subEvent() {
        return Event.HISTORY_REMOVE;
    }

    @Override
    public Pair<SourceKey, List<SegmentKey>> getContent() {
        return content;
    }
}
