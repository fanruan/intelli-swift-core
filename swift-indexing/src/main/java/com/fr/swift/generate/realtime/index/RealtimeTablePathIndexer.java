package com.fr.swift.generate.realtime.index;


import com.fr.swift.cube.io.Releasable;
import com.fr.swift.generate.BaseTablePathIndexer;
import com.fr.swift.segment.Segment;
import com.fr.swift.segment.SwiftSegmentManager;
import com.fr.swift.segment.relation.CubeMultiRelationPath;
import com.fr.swift.source.SourceKey;

import java.util.List;

/**
 * @author yee
 * @date 2018/1/17
 */
public class RealtimeTablePathIndexer extends BaseTablePathIndexer {
    public RealtimeTablePathIndexer(CubeMultiRelationPath relationPath, SwiftSegmentManager provider) {
        super(relationPath, provider);
    }

    @Override
    protected List<Segment> getSegments(SourceKey key) {
        return provider.getSegment(key);
    }

    @Override
    protected void releaseIfNeed(Releasable releasable) {
    }
}
