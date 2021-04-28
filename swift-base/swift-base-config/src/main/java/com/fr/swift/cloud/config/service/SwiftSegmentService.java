package com.fr.swift.cloud.config.service;

import com.fr.swift.cloud.annotation.service.DbService;
import com.fr.swift.cloud.config.entity.SwiftSegmentBucket;
import com.fr.swift.cloud.config.entity.SwiftSegmentBucketElement;
import com.fr.swift.cloud.cube.io.Types;
import com.fr.swift.cloud.segment.SegmentKey;
import com.fr.swift.cloud.segment.SegmentSource;
import com.fr.swift.cloud.source.SourceKey;
import com.fr.swift.cloud.source.alloter.impl.hash.HashIndexRange;

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

    SegmentKey tryAppendSegment(SourceKey tableKey, Types.StoreType storeType);

    SegmentKey tryAppendSegment(SourceKey tableKey, Types.StoreType storeType, SegmentSource segmentSource);

    SegmentKey tryAppendSegment(SourceKey tableKey, Types.StoreType storeType, SegmentSource segmentSource, String segmentUri);

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