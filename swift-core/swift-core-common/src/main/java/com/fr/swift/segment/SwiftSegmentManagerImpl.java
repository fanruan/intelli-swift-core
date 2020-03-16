package com.fr.swift.segment;

import com.fr.swift.SwiftContext;
import com.fr.swift.beans.annotation.SwiftBean;
import com.fr.swift.config.service.SwiftSegmentService;
import com.fr.swift.source.SourceKey;

import java.util.Collection;
import java.util.List;

/**
 * @author lucifer
 * @date 2020/3/14
 * @description
 * @since swift-log 10.0.5
 */
@SwiftBean(name = "swiftSegmentManager")
public class SwiftSegmentManagerImpl implements SwiftSegmentManager {

    private SwiftSegmentService swiftSegmentService = SwiftContext.get().getBean(SwiftSegmentService.class);

    @Override
    public Segment getSegment(SegmentKey key) {
        return null;
    }

    @Override
    public boolean existsSegment(SegmentKey segKey) {
        return false;
    }

    @Override
    public List<Segment> getSegment(SourceKey tableKey) {
        return null;
    }

    @Override
    public List<SegmentKey> getSegmentKeys(SourceKey tableKey) {
        return null;
    }

    @Override
    public boolean isSegmentsExist(SourceKey tableKey) {
        return false;
    }

    @Override
    public List<Segment> getSegmentsByIds(SourceKey table, Collection<String> segmentIds) {
        return null;
    }

    @Override
    public void remove(SourceKey sourceKey) {

    }
}
