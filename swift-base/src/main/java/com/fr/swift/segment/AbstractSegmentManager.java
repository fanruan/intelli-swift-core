package com.fr.swift.segment;

import com.fr.swift.config.service.SwiftSegmentService;
import com.fr.swift.config.service.SwiftSegmentServiceProvider;
import com.fr.swift.source.SourceKey;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by pony on 2017/10/16.
 */
public abstract class AbstractSegmentManager implements SwiftSegmentManager {
    protected SwiftSegmentService segmentService = SwiftSegmentServiceProvider.getProvider();

    @Override
    public synchronized List<Segment> getSegment(SourceKey tableKey) {
        // 并发地拿，比如多个column indexer同时进行索引， 要同步下
        List<Segment> segments = new ArrayList<Segment>();
        List<SegmentKey> keys = getSegmentKeys(tableKey);
        if (null != keys && !keys.isEmpty()) {
            for (SegmentKey key : keys) {
                try {
                    Segment segment = getSegment(key);
                    if (null != segment) {
                        segments.add(segment);
                    }
                } catch (Exception e) {
                    return segments;
                }
            }
        }
        return segments;
    }

    @Override
    public boolean existsSegment(SegmentKey segKey) {
        return segmentService.containsSegment(segKey);
    }

    @Override
    public List<SegmentKey> getSegmentKeys(SourceKey tableKey) {
        return segmentService.getSegmentByKey(tableKey.getId());
    }

    @Override
    public boolean isSegmentsExist(SourceKey tableKey) {
        return !getSegmentKeys(tableKey).isEmpty();
    }
}