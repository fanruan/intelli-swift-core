package com.fr.swift.config.service;

import com.fr.swift.annotation.service.DbService;
import com.fr.swift.config.entity.SwiftSegmentLocationEntity;
import com.fr.swift.segment.SegmentKey;
import com.fr.swift.source.SourceKey;

import java.util.Collection;
import java.util.List;

/**
 * @author yee
 * @date 2018/7/24
 */
@DbService
public interface SwiftSegmentLocationService {
    /**
     * mutableImport、slimImport、collate、historyBLock
     *
     * @param nodeId
     * @param segKeys
     */
    void saveOnNode(String nodeId, Collection<SegmentKey> segKeys);

    void deleteOnNode(String nodeId, Collection<SegmentKey> segKeys);

    void deleteOnNode(String nodeId, SourceKey tableKey);

    boolean existsOnNode(String nodeId, SegmentKey segKey);

    void updateBelongs(String newNodeId, Collection<SegmentKey> segKeys);

    List<SwiftSegmentLocationEntity> getTableMatchedSegOnNode(String nodeId, SourceKey tableKey, String segIdStartsWith);

    List<SwiftSegmentLocationEntity> getTableMatchedSegOnNode(String nodeId, SourceKey tableKey, Collection<String> inSegIds);

    List<SwiftSegmentLocationEntity> getTableSegsByClusterId(SourceKey tableKey, String clusterId);

    List<SwiftSegmentLocationEntity> getSegLocations(String clusterId, SourceKey tableKey);
}
