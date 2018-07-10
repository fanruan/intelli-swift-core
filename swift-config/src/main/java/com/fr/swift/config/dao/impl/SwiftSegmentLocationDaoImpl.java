package com.fr.swift.config.dao.impl;

import com.fr.swift.config.dao.BasicDao;
import com.fr.swift.config.dao.SwiftSegmentLocationDao;
import com.fr.swift.config.entity.SwiftSegmentLocationEntity;
import com.fr.third.org.hibernate.Session;
import com.fr.third.org.hibernate.criterion.Restrictions;
import com.fr.third.springframework.stereotype.Service;

import java.sql.SQLException;
import java.util.List;

/**
 * @author yee
 * @date 2018/7/3
 */
@Service
public class SwiftSegmentLocationDaoImpl extends BasicDao<SwiftSegmentLocationEntity> implements SwiftSegmentLocationDao {
    public SwiftSegmentLocationDaoImpl() {
        super(SwiftSegmentLocationEntity.class);
    }

    @Override
    public boolean deleteBySourceKey(Session session, String sourceKey) throws SQLException {
        try {
            List<SwiftSegmentLocationEntity> entities = find(session, Restrictions.eq("sourceKey", sourceKey));
            if (null != entities) {
                for (SwiftSegmentLocationEntity entity : entities) {
                    session.delete(entity);
                }
            }
            return true;
        } catch (Exception e) {
            throw new SQLException(e);
        }
    }

    @Override
    public List<SwiftSegmentLocationEntity> findByClusterId(Session session, String clusterId) {
        return find(session, Restrictions.eq("id.clusterId", clusterId));
    }

    @Override
    public List<SwiftSegmentLocationEntity> findBySegmentId(Session session, String segmentId) {
        return find(session, Restrictions.eq("id.segmentId", segmentId));
    }

    @Override
    public List<SwiftSegmentLocationEntity> findAll(Session session) {
        return find(session);
    }
}
