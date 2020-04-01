package com.fr.swift.config.service;

import com.fr.swift.annotation.service.DbService;
import com.fr.swift.cube.io.Types.StoreType;
import com.fr.swift.segment.SegmentKey;
import com.fr.swift.source.SourceKey;

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

    void delete(SegmentKey segKey);

    void delete(List<SegmentKey> segKeys);

    SegmentKey tryAppendSegment(SourceKey tableKey, StoreType storeType);

    List<SegmentKey> getTableSegKeys(SourceKey tableKey);

    Set<SegmentKey> getByIds(Set<String> segIds);

    List<SegmentKey> getOrderedRealtimeSegKeyOnNode(String nodeId, SourceKey tableKey);

    List<SegmentKey> getRealtimeSegKeyOnNode(String nodeId);

    List<SegmentKey> getSegKeyOnNode(String nodeId);

    Map<SourceKey, List<SegmentKey>> getTransferedSegments();

    Map<SourceKey, List<SegmentKey>> getOwnSegments(final String nodeId);

    List<SegmentKey> getOwnSegments(SourceKey tableKey);
}