package com.fr.swift.manager;

import com.fr.swift.beans.annotation.SwiftBean;
import com.fr.swift.segment.Segment;
import com.fr.swift.segment.SegmentKey;
import com.fr.swift.segment.SwiftSegmentManager;
import com.fr.swift.source.SourceKey;

import java.util.Collection;
import java.util.List;

/**
 * Created by pony on 2017/12/14.
 */
@SwiftBean(name = "localSegmentProvider")
public class LocalSegmentProvider implements SwiftSegmentManager {

    private SwiftSegmentManager manager;

    private LocalSegmentProvider() {
        manager = new LineSegmentManager();
    }

    @Override
    public Segment getSegment(SegmentKey key) {
        return manager.getSegment(key);
    }

    @Override
    public boolean existsSegment(SegmentKey segKey) {
        return manager.existsSegment(segKey);
    }

    @Override
    public List<Segment> getSegment(SourceKey tableKey) {
        return manager.getSegment(tableKey);
    }

    @Override
    public List<SegmentKey> getSegmentKeys(SourceKey tableKey) {
        return manager.getSegmentKeys(tableKey);
    }

    @Override
    public boolean isSegmentsExist(SourceKey tableKey) {
        return manager.isSegmentsExist(tableKey);
    }

    @Override
    public List<Segment> getSegmentsByIds(SourceKey table, Collection<String> segmentIds) {
        return manager.getSegmentsByIds(table, segmentIds);
    }

    @Override
    public void remove(SourceKey sourceKey) {
        manager.remove(sourceKey);
    }
}