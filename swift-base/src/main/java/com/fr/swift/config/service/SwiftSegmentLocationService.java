package com.fr.swift.config.service;

import com.fr.swift.config.entity.SwiftSegmentLocationEntity;

/**
 * @author yee
 * @date 2018/7/24
 */
public interface SwiftSegmentLocationService extends ConfigService<SwiftSegmentLocationEntity> {
    boolean delete(String table, String clusterId);
}
