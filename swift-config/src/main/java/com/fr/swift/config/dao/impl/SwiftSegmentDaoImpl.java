package com.fr.swift.config.dao.impl;

import com.fr.stable.StringUtils;
import com.fr.swift.config.SwiftConfigConstants;
import com.fr.swift.config.bean.SegmentKeyBean;
import com.fr.swift.config.dao.BasicDao;
import com.fr.swift.config.dao.SwiftSegmentDao;
import com.fr.swift.config.entity.SwiftSegmentEntity;
import com.fr.swift.cube.io.Types;
import com.fr.swift.segment.SegmentKey;
import com.fr.third.org.hibernate.Session;
import com.fr.third.org.hibernate.criterion.Conjunction;
import com.fr.third.org.hibernate.criterion.Restrictions;
import com.fr.third.springframework.stereotype.Service;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


/**
 * @author yee
 * @date 2018/5/24
 */
@Service
public class SwiftSegmentDaoImpl extends BasicDao<SwiftSegmentEntity> implements SwiftSegmentDao {

    public SwiftSegmentDaoImpl() {
        super(SwiftSegmentEntity.class);
    }

    @Override
    public boolean addOrUpdateSwiftSegment(Session session, SegmentKeyBean bean) throws SQLException {
        return saveOrUpdate(session, bean.convert());
    }

    @Override
    public List<SegmentKey> findBySourceKey(Session session, String sourceKey) {
        List<SwiftSegmentEntity> list = find(session, Restrictions.eq(SwiftConfigConstants.SegmentConfig.COLUMN_SEGMENT_OWNER, sourceKey));
        List<SegmentKey> result = new ArrayList<SegmentKey>();
        for (SwiftSegmentEntity entity : list) {
            result.add(entity.convert());
        }
        return Collections.unmodifiableList(result);
    }

    @Override
    public List<SegmentKey> findBeanByStoreType(Session session, String sourceKey, Types.StoreType type) throws SQLException {
        if (StringUtils.isEmpty(sourceKey) || null == type) {
            throw new SQLException();
        }
        List<SwiftSegmentEntity> list = find(session, Restrictions.eq(SwiftConfigConstants.SegmentConfig.COLUMN_SEGMENT_OWNER, sourceKey),
                Restrictions.eq(SwiftConfigConstants.SegmentConfig.COLUMN_STORE_TYPE, type));
        List<SegmentKey> result = new ArrayList<SegmentKey>();
        for (SwiftSegmentEntity entity : list) {
            result.add(entity.convert());
        }
        return result;
    }

    @Override
    public boolean deleteBySourceKey(Session session, final String sourceKey) throws SQLException {
        try {
            SwiftSegmentEntity entity = new SwiftSegmentEntity();
            entity.setSegmentOwner(sourceKey);
            session.delete(entity);
            return true;
        } catch (Exception e) {
            throw new SQLException(e);
        }
    }

    @Override
    public boolean deleteByStoreType(Session session, final Types.StoreType storeType) throws SQLException {
        try {
            SwiftSegmentEntity entity = new SwiftSegmentEntity();
            entity.setStoreType(storeType);
            session.delete(entity);
            return true;
        } catch (Exception e) {
            throw new SQLException(e);
        }
    }

    @Override
    public List<SegmentKey> findAll(Session session) {
        List<SwiftSegmentEntity> list = find(session, new Conjunction[]{});
        List<SegmentKey> result = new ArrayList<SegmentKey>();
        for (SwiftSegmentEntity entity : list) {
            result.add(entity.convert());
        }
        return Collections.unmodifiableList(result);
    }

}
