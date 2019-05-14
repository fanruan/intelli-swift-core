package com.fr.swift.config.dao.impl;

import com.fr.swift.beans.annotation.SwiftBean;
import com.fr.swift.config.dao.BasicDao;
import com.fr.swift.config.dao.SwiftSegmentLocationDao;
import com.fr.swift.config.entity.SwiftSegmentLocationEntity;
import com.fr.swift.config.oper.ConfigSession;
import com.fr.swift.config.oper.impl.ConfigWhereImpl;

import java.sql.SQLException;
import java.util.List;

/**
 * @author yee
 * @date 2018/7/3
 */
@SwiftBean
public class SwiftSegmentLocationDaoImpl extends BasicDao<SwiftSegmentLocationEntity> implements SwiftSegmentLocationDao {
    public SwiftSegmentLocationDaoImpl() {
        super(SwiftSegmentLocationEntity.class);
    }


    @Override
    public boolean deleteBySourceKey(final ConfigSession session, String sourceKey) throws SQLException {
        try {
            for (SwiftSegmentLocationEntity key : find(session, ConfigWhereImpl.eq("sourceKey", sourceKey))) {
                session.delete(key);
            }
            return true;
        } catch (Exception e) {
            throw new SQLException(e);
        }
    }

    @Override
    public List<SwiftSegmentLocationEntity> findByClusterId(ConfigSession session, String clusterId) {
        return find(session, ConfigWhereImpl.eq("id.clusterId", clusterId));
    }

    @Override
    public List<SwiftSegmentLocationEntity> findBySegmentId(ConfigSession session, String segmentId) {
        return find(session, ConfigWhereImpl.eq("id.segmentId", segmentId));
    }

    @Override
    public List<SwiftSegmentLocationEntity> findAll(ConfigSession session) {
        return find(session);
    }

    @Override
    public List<SwiftSegmentLocationEntity> findBySourceKey(ConfigSession session, String sourceKey) {
        return find(session, ConfigWhereImpl.eq("sourceKey", sourceKey));
    }
}
