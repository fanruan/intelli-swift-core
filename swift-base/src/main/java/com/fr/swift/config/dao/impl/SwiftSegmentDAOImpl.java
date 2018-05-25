package com.fr.swift.config.dao.impl;

import com.fr.config.dao.HibernateTemplate;
import com.fr.stable.db.DBSession;
import com.fr.store.access.AccessActionCallback;
import com.fr.store.access.ResourceHolder;
import com.fr.swift.config.bean.SegmentBean;
import com.fr.swift.config.dao.BaseDAO;
import com.fr.swift.config.dao.SwiftSegmentDAO;
import com.fr.swift.config.entity.SegmentEntity;
import com.fr.third.org.hibernate.Query;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


/**
 * @author yee
 * @date 2018/5/24
 */
public class SwiftSegmentDAOImpl extends BaseDAO<SegmentEntity> implements SwiftSegmentDAO {

    private static final String FIND_BY_SOURCE_KEY = String.format("from SegmentEntity entity where entity.%s = ", SegmentBean.COLUMN_SOURCE_KEY);
    private static final String DELETE_BY_SOURCE_KEY = String.format("delete from SegmentEntity entity where entity.%s = ", SegmentBean.COLUMN_SOURCE_KEY);

    public SwiftSegmentDAOImpl() {
        super(SegmentEntity.class);
    }

    @Override
    public boolean addOrUpdateSwiftSegment(SegmentBean bean) {
        return saveOrUpdate(bean.convert());
    }

    @Override
    public List<SegmentBean> findBySourceKey(String sourceKey) {
        List<SegmentEntity> list = find(String.format("%s'%s' order by entity.%s", FIND_BY_SOURCE_KEY, sourceKey, SegmentBean.COLUMN_ORDER));
        List<SegmentBean> result = new ArrayList<SegmentBean>();
        for (SegmentEntity entity : list) {
            result.add(entity.convert());
        }
        return Collections.unmodifiableList(result);
    }

    @Override
    public boolean deleteBySourceKey(final String sourceKey) {
        return HibernateTemplate.getInstance().doQuery(new AccessActionCallback<Boolean>() {
            public Boolean doInServer(ResourceHolder holder) {
                DBSession session = (DBSession) holder.getResource();

                try {
                    Query query = session.createHibernateQuery(String.format("%s'%s'", DELETE_BY_SOURCE_KEY, sourceKey));
                    query.executeUpdate();
                } catch (Exception e) {
                    LOGGER.error("deleteById failed", e);
                    throw new RuntimeException(e);
                }

                return true;
            }
        });
    }

    @Override
    public List<SegmentBean> findAll() {
        List<SegmentEntity> list = find();
        List<SegmentBean> result = new ArrayList<SegmentBean>();
        for (SegmentEntity entity : list) {
            result.add(entity.convert());
        }
        return Collections.unmodifiableList(result);
    }
}
