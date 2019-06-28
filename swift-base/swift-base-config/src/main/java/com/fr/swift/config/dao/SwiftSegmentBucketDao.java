package com.fr.swift.config.dao;

import com.fr.swift.config.entity.SwiftSegmentBucketElement;
import com.fr.swift.config.oper.ConfigSession;

import java.sql.SQLException;

/**
 * @author lucifer
 * @date 2019-06-25
 * @description
 * @since advanced swift 1.0
 */
public interface SwiftSegmentBucketDao extends SwiftConfigDao<SwiftSegmentBucketElement> {

    boolean deleteBySourceKey(ConfigSession session, String sourceKey) throws SQLException;

    boolean deleteBySegmentKey(ConfigSession session, String segmentKey) throws SQLException;
}
