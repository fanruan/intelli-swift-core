package com.fr.swift.manager;

import com.fr.swift.segment.Segment;
import com.fr.swift.segment.SegmentKey;
import com.fr.swift.segment.SwiftSegmentManager;
import com.fr.swift.source.SourceKey;
import com.fr.third.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by pony on 2017/12/14.
 */
@Service("LocalSegmentProvider")
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
}