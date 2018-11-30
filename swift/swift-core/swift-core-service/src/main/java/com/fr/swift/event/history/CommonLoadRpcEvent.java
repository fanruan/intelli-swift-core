package com.fr.swift.event.history;

import com.fr.swift.event.base.AbstractHistoryRpcEvent;
import com.fr.swift.segment.SegmentKey;
import com.fr.swift.source.SourceKey;
import com.fr.swift.structure.Pair;

import java.util.List;
import java.util.Map;

/**
 * @author yee
 * @date 2018/9/12
 */
public abstract class CommonLoadRpcEvent extends AbstractHistoryRpcEvent<Pair<SourceKey, Map<SegmentKey, List<String>>>> {
    /**
     * sourceKey->segmentKey->needUploadPaths
     */
    private Pair<SourceKey, Map<SegmentKey, List<String>>> content;

    public CommonLoadRpcEvent(Pair<SourceKey, Map<SegmentKey, List<String>>> content, String sourceClusterId) {
        this.content = content;
        this.sourceClusterId = sourceClusterId;
    }

    @Override
    public Pair<SourceKey, Map<SegmentKey, List<String>>> getContent() {
        return content;
    }
}
