package com.fr.swift.segment;

import com.fr.swift.config.service.SwiftSegmentService;
import com.fr.swift.config.service.SwiftTablePathService;
import com.fr.swift.context.SwiftContext;
import com.fr.swift.source.SourceKey;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by pony on 2017/10/16.
 */
public abstract class AbstractSegmentManager implements SwiftSegmentManager {
    protected SwiftSegmentService segmentService = SwiftContext.get().getBean("segmentServiceProvider", SwiftSegmentService.class);
    protected SwiftTablePathService tablePathService = SwiftContext.get().getBean(SwiftTablePathService.class);

    @Override
    public synchronized List<Segment> getSegment(SourceKey tableKey) {
        // 并发地拿，比如多个column indexer同时进行索引， 要同步下
        List<Segment> segments = new ArrayList<Segment>();
        List<SegmentKey> keys = getSegmentKeys(tableKey);
        Integer currentFolder = getCurrentFolder(tablePathService, tableKey);
        if (null != keys && !keys.isEmpty()) {
            for (SegmentKey key : keys) {
                try {
                    Segment segment = getSegment(key, currentFolder);
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
    public Segment getSegment(SegmentKey key) {
        Integer currentFolder = getCurrentFolder(tablePathService, key.getTable());
        return getSegment(key, currentFolder);
    }

    @Override
    public boolean isSegmentsExist(SourceKey tableKey) {
        return !getSegmentKeys(tableKey).isEmpty();
    }

    protected abstract Integer getCurrentFolder(SwiftTablePathService service, SourceKey sourceKey);

    protected abstract Segment getSegment(SegmentKey segmentKey, Integer currentFolder);
}