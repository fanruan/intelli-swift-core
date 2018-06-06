package com.fr.swift.config.dao.impl;

import com.fr.config.dao.HibernateTemplate;
import com.fr.stable.StringUtils;
import com.fr.stable.db.DBSession;
import com.fr.store.access.AccessActionCallback;
import com.fr.store.access.ResourceHolder;
import com.fr.swift.config.SwiftConfigConstants;
import com.fr.swift.config.bean.SegmentKeyBean;
import com.fr.swift.config.dao.BaseDAO;
import com.fr.swift.config.dao.SwiftSegmentDAO;
import com.fr.swift.config.entity.SwiftSegmentEntity;
import com.fr.swift.cube.io.Types;
import com.fr.third.org.hibernate.Query;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


/**
 * @author yee
 * @date 2018/5/24
 */
public class SwiftSegmentDAOImpl extends BaseDAO<SwiftSegmentEntity> implements SwiftSegmentDAO {

    private static final String FIND_BY_SOURCE_KEY = String.format("from SwiftSegmentEntity entity where entity.%s = ", SwiftConfigConstants.SegmentConfig.COLUMN_SEGMENT_OWNER);
    private static final String FIND_BY_STORE_TYPE = String.format("from SwiftSegmentEntity entity where entity.%s = ", SwiftConfigConstants.SegmentConfig.COLUMN_STORE_TYPE);
    private static final String DELETE_BY_SOURCE_KEY = String.format("delete from SwiftSegmentEntity entity where entity.%s = ", SwiftConfigConstants.SegmentConfig.COLUMN_SEGMENT_OWNER);
    private static final String DELETE_BY_STORE_TYPE = String.format("delete from SwiftSegmentEntity entity where entity.%s = ", SwiftConfigConstants.SegmentConfig.COLUMN_STORE_TYPE);

    public SwiftSegmentDAOImpl() {
        super(SwiftSegmentEntity.class);
    }

    @Override
    public boolean addOrUpdateSwiftSegment(SegmentKeyBean bean) throws SQLException {
        return saveOrUpdate(bean.convert());
    }

    @Override
    public List<SegmentKeyBean> findBySourceKey(String sourceKey) {
        List<SwiftSegmentEntity> list = find(String.format("%s'%s' order by entity.%s", FIND_BY_SOURCE_KEY, sourceKey, SwiftConfigConstants.SegmentConfig.COLUMN_SEGMENT_ORDER));
        List<SegmentKeyBean> result = new ArrayList<SegmentKeyBean>();
        for (SwiftSegmentEntity entity : list) {
            result.add(entity.convert());
        }
        return Collections.unmodifiableList(result);
    }

    @Override
    public List<SegmentKeyBean> findBeanByStoreType(String sourceKey, Types.StoreType type) throws SQLException {
        if (StringUtils.isEmpty(sourceKey) || null == type) {
            throw new SQLException();
        }
        StringBuilder hqlBuilder = new StringBuilder(String.format("%s'%s'", FIND_BY_STORE_TYPE, type.name()));
        hqlBuilder.append(String.format(" and entity.%s = '%s'", SwiftConfigConstants.SegmentConfig.COLUMN_SEGMENT_OWNER, sourceKey));
        List<SwiftSegmentEntity> list = find(hqlBuilder.toString());
        List<SegmentKeyBean> result = new ArrayList<SegmentKeyBean>();
        for (SwiftSegmentEntity entity : list) {
            result.add(entity.convert());
        }
        return Collections.unmodifiableList(result);
    }

    @Override
    public boolean contains(SegmentKeyBean bean) throws SQLException {
        return null != select(bean.getId());
    }

    @Override
    public boolean deleteBySourceKey(final String sourceKey) throws SQLException {
        try {
            return HibernateTemplate.getInstance().doQuery(new AccessActionCallback<Boolean>() {
                public Boolean doInServer(ResourceHolder holder) {
                    DBSession session = (DBSession) holder.getResource();

                    try {
                        Query query = session.createHibernateQuery(String.format("%s'%s'", DELETE_BY_SOURCE_KEY, sourceKey));
                        query.executeUpdate();
                    } catch (Exception e) {
                        LOGGER.error("deleteBySourceKey failed", e);
                        throw new RuntimeException(e);
                    }

                    return true;
                }
            });
        } catch (Exception e) {
            throw new SQLException(e);
        }
    }

    @Override
    public boolean deleteByStoreType(final Types.StoreType storeType) throws SQLException {
        try {
            return HibernateTemplate.getInstance().doQuery(new AccessActionCallback<Boolean>() {
                public Boolean doInServer(ResourceHolder holder) {
                    DBSession session = (DBSession) holder.getResource();

                    try {
                        Query query = session.createHibernateQuery(String.format("%s'%s'", DELETE_BY_STORE_TYPE, storeType.name()));
                        query.executeUpdate();
                    } catch (Exception e) {
                        LOGGER.error("deleteById failed", e);
                        throw new RuntimeException(e);
                    }

                    return true;
                }
            });
        } catch (Exception e) {
            throw new SQLException(e);
        }
    }

    @Override
    public List<SegmentKeyBean> findAll() {
        List<SwiftSegmentEntity> list = find();
        List<SegmentKeyBean> result = new ArrayList<SegmentKeyBean>();
        for (SwiftSegmentEntity entity : list) {
            result.add(entity.convert());
        }
        return Collections.unmodifiableList(result);
    }
}
