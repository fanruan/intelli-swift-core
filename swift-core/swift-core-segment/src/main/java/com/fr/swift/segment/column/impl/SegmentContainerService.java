package com.fr.swift.segment.column.impl;

import com.fr.swift.beans.annotation.SwiftBean;
import com.fr.swift.segment.Segment;
import com.fr.swift.segment.SegmentContainer;
import com.fr.swift.segment.SegmentKey;
import com.fr.swift.segment.SegmentService;
import com.fr.swift.source.SourceKey;

import java.util.List;
import java.util.Set;

/**
 * @author lucifer
 * @date 2020/3/17
 * @description
 * @since swift 1.1
 */
@SwiftBean(name = "segmentService")
public class SegmentContainerService implements SegmentService {

    @Override
    public void addSegment(SegmentKey segmentKey) {
        SegmentContainer.LOCAL.addSegment(segmentKey);
    }

    @Override
    public void addSegments(List<SegmentKey> segmentKeys) {
        SegmentContainer.LOCAL.addSegments(segmentKeys);

    }

    @Override
    public Segment getSegment(SegmentKey key) {
        return SegmentContainer.LOCAL.getSegment(key);
    }

    @Override
    public List<Segment> getSegments(SourceKey tableKey) {
        return SegmentContainer.LOCAL.getSegments(tableKey);
    }

    @Override
    public List<Segment> getSegments(Set<String> segKeys) {
        return SegmentContainer.LOCAL.getSegments(segKeys);
    }

    @Override
    public List<SegmentKey> getSegmentKeys(SourceKey tableKey) {
        return SegmentContainer.LOCAL.getSegmentKeys(tableKey);
    }

    @Override
    public boolean exist(SegmentKey segmentKey) {
        return SegmentContainer.LOCAL.exist(segmentKey);
    }

    @Override
    public SegmentKey removeSegment(SegmentKey segmentKey) {
        return SegmentContainer.LOCAL.removeSegment(segmentKey);
    }

    @Override
    public List<SegmentKey> removeSegments(List<SegmentKey> segmentKeys) {
        return SegmentContainer.LOCAL.removeSegments(segmentKeys);
    }
}
