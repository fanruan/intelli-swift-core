package com.fr.swift.segment;

import com.fr.swift.annotation.service.InnerService;
import com.fr.swift.config.entity.SwiftSegmentBucket;
import com.fr.swift.source.SourceKey;

import java.util.Collection;
import java.util.List;
import java.util.Set;

/**
 * @author lucifer
 * @date 2020/3/17
 * @description
 * @since swift 1.1
 */
@InnerService
public interface SegmentService {

    void addSegment(SegmentKey segmentKey);

    /**
     * mutableImport、slimImport、collate、historyBLock
     *
     * @param segmentKeys
     */
    void addSegments(List<SegmentKey> segmentKeys);

    Segment getSegment(SegmentKey key);

    List<Segment> getSegments(List<SegmentKey> keys);

    List<Segment> getSegments(SourceKey tableKey);

    List<Segment> getSegments(Set<String> segKeys);

    List<SegmentKey> getSegmentKeys(SourceKey tableKey);

    List<SegmentKey> getSegmentKeysByIds(SourceKey tableKey, Collection<String> segmentIds);

    boolean exist(SegmentKey segmentKey);

    boolean existAll(Collection<String> segmentIds);

    SegmentKey removeSegment(SegmentKey segmentKey);

    List<SegmentKey> removeSegments(List<SegmentKey> segmentKeys);

    SwiftSegmentBucket getBucketByTable(SourceKey sourceKey);

    void flushCache();
}