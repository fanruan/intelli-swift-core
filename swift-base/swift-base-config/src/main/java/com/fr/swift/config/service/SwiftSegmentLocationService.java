package com.fr.swift.config.service;

import com.fr.swift.config.entity.SwiftSegmentLocationEntity;
import com.fr.swift.source.SourceKey;

import java.util.List;
import java.util.Map;

/**
 * @author yee
 * @date 2018/7/24
 */
public interface SwiftSegmentLocationService extends ConfigService<SwiftSegmentLocationEntity> {
    boolean delete(String table, String clusterId);

    Map<String, List<SwiftSegmentLocationEntity>> findAll();

    List<SwiftSegmentLocationEntity> findBySourceKey(SourceKey sourceKey);
}
