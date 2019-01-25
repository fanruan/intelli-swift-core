package com.fr.swift.config.dao;

import com.fr.swift.config.bean.SegLocationBean;
import com.fr.swift.config.oper.ConfigSession;
import com.fr.swift.converter.FindList;

import java.sql.SQLException;

/**
 * @author yee
 * @date 2018/7/3
 */
public interface SwiftSegmentLocationDao extends SwiftConfigDao<SegLocationBean> {
    boolean deleteBySourceKey(ConfigSession session, String sourceKey) throws SQLException;

    FindList<SegLocationBean> findByClusterId(ConfigSession session, String clusterId);

    FindList<SegLocationBean> findBySegmentId(ConfigSession session, String segmentId);

    FindList<SegLocationBean> findBySourceKey(ConfigSession session, String sourceKey);

    FindList<SegLocationBean> findAll(ConfigSession session);
}
