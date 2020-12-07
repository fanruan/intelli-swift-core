package com.fr.swift.config.service;

import com.fr.swift.annotation.service.DbService;
import com.fr.swift.config.entity.SwiftSegmentBucket;
import com.fr.swift.config.entity.SwiftSegmentBucketElement;
import com.fr.swift.cube.io.Types.StoreType;
import com.fr.swift.segment.SegmentKey;
import com.fr.swift.segment.SegmentSource;
import com.fr.swift.source.SourceKey;
import com.fr.swift.source.alloter.impl.hash.HashIndexRange;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author yee
 * @date 2018/6/6
 * <p>
 */
@DbService
public interface SwiftSegmentService {
    void save(SegmentKey segKey);

    void save(Collection<SegmentKey> segKeys);

    void update(SegmentKey segKey);

    void update(Collection<SegmentKey> segKeys);

    void delete(SegmentKey segKey);

    void delete(List<SegmentKey> segKeys);

    SegmentKey tryAppendSegment(SourceKey tableKey, StoreType storeType);

    SegmentKey tryAppendSegment(SourceKey tableKey, StoreType storeType, SegmentSource segmentSource);

    SegmentKey tryAppendSegment(SourceKey tableKey, StoreType storeType, SegmentSource segmentSource, String segmentUri);

    List<SegmentKey> getTableSegKeys(SourceKey tableKey);

    Set<SegmentKey> getByIds(Set<String> segIds);

    List<SegmentKey> getOrderedRealtimeSegKeyOnNode(String nodeId, SourceKey tableKey);

    List<SegmentKey> getRealtimeSegKeyOnNode(String nodeId);

    List<SegmentKey> getSegKeyOnNode(String nodeId);

    Map<SourceKey, List<SegmentKey>> getTransferedSegments();

    Map<SourceKey, List<SegmentKey>> getOwnSegments(final String nodeId);

    Map<SourceKey, List<SegmentKey>> getOwnSegmentsByRange(HashIndexRange range);

    List<SegmentKey> getOwnSegments(SourceKey tableKey);

    SwiftSegmentBucket getBucketByTable(SourceKey sourceKey);

    List<SwiftSegmentBucketElement> getBucketElementsByKeys(Collection<SegmentKey> segmentKeys);

    void saveBucket(SwiftSegmentBucketElement element);

    void deleteBucket(SwiftSegmentBucketElement element);
}