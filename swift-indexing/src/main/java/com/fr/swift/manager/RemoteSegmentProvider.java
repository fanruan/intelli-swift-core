package com.fr.swift.manager;

import com.fr.swift.segment.Segment;
import com.fr.swift.segment.SegmentKey;
import com.fr.swift.segment.SwiftSegmentManager;
import com.fr.swift.source.SourceKey;

import java.util.Collection;
import java.util.List;

/**
 * @author yee
 * @date 2017/12/19
 */
public class RemoteSegmentProvider implements SwiftSegmentManager {

    private static RemoteSegmentProvider ourInstance = new RemoteSegmentProvider();

    public static RemoteSegmentProvider getInstance() {
        return ourInstance;
    }

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

}
