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
@Service
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
    public List<Segment> getSegment(SourceKey sourceKey) {
        return manager.getSegment(sourceKey);
    }

    @Override
    public boolean isSegmentsExist(SourceKey key) {
        return manager.isSegmentsExist(key);
    }
}