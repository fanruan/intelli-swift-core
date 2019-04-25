package com.fr.swift.config.dao;

import com.fr.swift.config.entity.SwiftSegmentLocationEntity;
import com.fr.swift.config.oper.ConfigSession;

import java.sql.SQLException;
import java.util.List;

/**
 * @author yee
 * @date 2018/7/3
 */
public interface SwiftSegmentLocationDao extends SwiftConfigDao<SwiftSegmentLocationEntity> {
    boolean deleteBySourceKey(ConfigSession session, String sourceKey) throws SQLException;

    List<SwiftSegmentLocationEntity> findByClusterId(ConfigSession session, String clusterId);

    List<SwiftSegmentLocationEntity> findBySegmentId(ConfigSession session, String segmentId);

    List<SwiftSegmentLocationEntity> findBySourceKey(ConfigSession session, String sourceKey);

    List<SwiftSegmentLocationEntity> findAll(ConfigSession session);
}
