package com.fr.swift.generate.realtime.index;

import com.fr.swift.cube.io.Releasable;
import com.fr.swift.generate.BaseMultiRelationIndexer;
import com.fr.swift.segment.Segment;
import com.fr.swift.segment.SwiftSegmentManager;
import com.fr.swift.segment.relation.CubeMultiRelation;
import com.fr.swift.source.SourceKey;

import java.util.List;

/**
 * @author yee
 * @date 2018/1/29
 */
public class RealtimeMultiRelationIndexer extends BaseMultiRelationIndexer {
    public RealtimeMultiRelationIndexer(CubeMultiRelation relation, SwiftSegmentManager provider) {
        super(relation, provider);
    }

    @Override
    protected List<Segment> getSegments(SourceKey key) {
        return provider.getSegment(key);
    }

    @Override
    protected void releaseIfNeed(Releasable releasable) {
    }
}
