package com.fr.swift.config.dao;

import com.fr.swift.config.entity.SwiftSegmentLocationEntity;
import com.fr.third.org.hibernate.Session;

import java.sql.SQLException;
import java.util.List;

/**
 * @author yee
 * @date 2018/7/3
 */
public interface SwiftSegmentLocationDao extends SwiftConfigDao<SwiftSegmentLocationEntity> {
    boolean deleteBySourceKey(Session session, String sourceKey) throws SQLException;

    List<SwiftSegmentLocationEntity> findByClusterId(Session session, String clusterId);

    List<SwiftSegmentLocationEntity> findBySegmentId(Session session, String segmentId);

    List<SwiftSegmentLocationEntity> findAll(Session session);
}
