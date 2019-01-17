package com.fr.swift.config.dao.impl;

import com.fr.stable.StringUtils;
import com.fr.swift.config.SwiftConfigConstants;
import com.fr.swift.config.dao.BasicDao;
import com.fr.swift.config.dao.SwiftSegmentDao;
import com.fr.swift.config.entity.SwiftSegmentEntity;
import com.fr.swift.cube.io.Types;
import com.fr.swift.segment.SegmentKey;
import com.fr.third.org.hibernate.Session;
import com.fr.third.org.hibernate.criterion.Conjunction;
import com.fr.third.org.hibernate.criterion.Criterion;
import com.fr.third.org.hibernate.criterion.Order;
import com.fr.third.org.hibernate.criterion.Restrictions;
import com.fr.third.springframework.stereotype.Service;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


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
    public boolean addOrUpdateSwiftSegment(Session session, SegmentKey bean) throws SQLException {
        return saveOrUpdate(session, new SwiftSegmentEntity(bean));
    }

    @Override
    public List<SegmentKey> findBySourceKey(Session session, String sourceKey) {
        List<SwiftSegmentEntity> list = find(session, new Order[]{Order.asc(SwiftConfigConstants.SegmentConfig.COLUMN_SEGMENT_ORDER)},
                Restrictions.eq(SwiftConfigConstants.SegmentConfig.COLUMN_SEGMENT_OWNER, sourceKey));
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
    public List<SegmentKey> selectSelective(Session session, SegmentKey segmentKey) {
        List<Criterion> criterionList = new ArrayList<Criterion>();
        try {
            if (null != segmentKey.getTable()) {
                criterionList.add(Restrictions.eq(SwiftConfigConstants.SegmentConfig.COLUMN_SEGMENT_OWNER, segmentKey.getTable().getId()));
            }
            if (null != segmentKey.getUri()) {
                criterionList.add(Restrictions.eq(SwiftConfigConstants.SegmentConfig.COLUMN_SEGMENT_URI, segmentKey.getUri()));
            }
            if (null != segmentKey.getStoreType()) {
                criterionList.add(Restrictions.eq(SwiftConfigConstants.SegmentConfig.COLUMN_STORE_TYPE, segmentKey.getStoreType()));
            }
            if (null != segmentKey.getOrder()) {
                criterionList.add(Restrictions.eq(SwiftConfigConstants.SegmentConfig.COLUMN_SEGMENT_ORDER, segmentKey.getOrder()));
            }
            List<SwiftSegmentEntity> list = find(session, criterionList.toArray(new Criterion[criterionList.size()]));
            List<SegmentKey> result = new ArrayList<SegmentKey>();
            for (SwiftSegmentEntity entity : list) {
                result.add(entity.convert());
            }
            return result;
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }

    @Override
    public boolean deleteBySourceKey(Session session, final String sourceKey) throws SQLException {
        try {
            List<SwiftSegmentEntity> entities = find(session, Restrictions.eq(SwiftConfigConstants.SegmentConfig.COLUMN_SEGMENT_OWNER, sourceKey));
            for (SwiftSegmentEntity entity : entities) {
                session.delete(entity);
            }
            return true;
        } catch (Exception e) {
            throw new SQLException(e);
        }
    }

    @Override
    public boolean deleteByStoreType(Session session, final Types.StoreType storeType) throws SQLException {
        try {
            List<SwiftSegmentEntity> entities = find(session, Restrictions.eq(SwiftConfigConstants.SegmentConfig.COLUMN_STORE_TYPE, storeType));
            for (SwiftSegmentEntity entity : entities) {
                session.delete(entity);
            }
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

    @Override
    public List<SegmentKey> findSegmentKey(Session session, Criterion... criterions) {
        try {
            List<SwiftSegmentEntity> list = find(session, criterions);
            List<SegmentKey> result = new ArrayList<SegmentKey>();
            for (SwiftSegmentEntity entity : list) {
                result.add(entity.convert());
            }
            return result;
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }

    @Override
    public Map<String, List<SegmentKey>> findSegmentKeyWithSourceKey(Session session, Criterion... criterions) {
        try {
            List<SwiftSegmentEntity> list = find(session, criterions);
            Map<String, List<SegmentKey>> result = new HashMap<String, List<SegmentKey>>();
            for (SwiftSegmentEntity entity : list) {
                String sourceKey = entity.getSegmentOwner();
                if (!result.containsKey(sourceKey)) {
                    result.put(sourceKey, new ArrayList<SegmentKey>());
                }
                result.get(sourceKey).add(entity.convert());
            }
            return result;
        } catch (Exception e) {
            return Collections.emptyMap();
        }
    }
}
