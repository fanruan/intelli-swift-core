package com.fr.swift.config.service;

import com.fr.swift.config.entity.SwiftSegmentLocationEntity;
import com.fr.swift.segment.SegmentKey;
import com.fr.swift.source.SourceKey;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author yee
 * @date 2018/7/24
 */
public interface SwiftSegmentLocationService extends ConfigService<SwiftSegmentLocationEntity> {
    boolean delete(String table, String clusterId);

    /**
     * @param segKeys seg keys
     * @return seg key -> 所有出现的node id
     */
    Map<SegmentKey, Set<String>> findLocationsBySegKeys(Set<SegmentKey> segKeys);

    Map<String, List<SwiftSegmentLocationEntity>> findAll();

    List<SwiftSegmentLocationEntity> findBySourceKey(SourceKey sourceKey);

    void saveOrUpdateLocal(Set<SegmentKey> segKeys);

    void delete(Set<SegmentKey> segKeys);

    boolean containsLocal(SegmentKey segKey);

    Map<SourceKey, List<SwiftSegmentLocationEntity>> getAllLocal();
}
