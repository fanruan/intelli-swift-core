package com.fr.swift.config.conf.dao;

import com.fr.decision.authority.dao.impl.BaseDAO;
import com.fr.decision.base.data.DataList;
import com.fr.decision.base.db.session.DAOSession;
import com.fr.stable.query.QueryFactory;
import com.fr.stable.query.condition.QueryCondition;
import com.fr.stable.query.restriction.RestrictionFactory;
import com.fr.swift.config.conf.bean.SegmentBean;
import com.fr.swift.config.conf.entity.SegmentEntity;

import java.util.List;

/**
 * @author yee
 * @date 2018/5/24
 */
public class SegmentDAO extends BaseDAO<SegmentEntity> {
    public SegmentDAO(DAOSession session) {
        super(session);
    }

    @Override
    public void add(SegmentEntity segmentEntity) throws Exception {
        this.getSession().persist(segmentEntity);
    }

    @Override
    public SegmentEntity getById(String s) throws Exception {
        return this.getSession().getById(s, SegmentEntity.class);
    }

    @Override
    public void update(SegmentEntity segmentEntity) throws Exception {
        this.getSession().merge(segmentEntity);
    }

    @Override
    public void remove(String s) throws Exception {
        this.getSession().remove(QueryFactory.create().addRestriction(RestrictionFactory.eq(SegmentBean.COLUMN_ID, s)), SegmentEntity.class);
    }

    @Override
    public void remove(QueryCondition queryCondition) throws Exception {
        this.getSession().remove(queryCondition, SegmentEntity.class);
    }

    @Override
    public List<SegmentEntity> find(QueryCondition queryCondition) throws Exception {
        return this.getSession().find(queryCondition, SegmentEntity.class);
    }

    @Override
    public SegmentEntity findOne(QueryCondition queryCondition) throws Exception {
        return this.getSession().findOne(queryCondition, SegmentEntity.class);
    }

    @Override
    public DataList<SegmentEntity> findWithTotalCount(QueryCondition queryCondition) throws Exception {
        return this.getSession().findWithTotalCount(queryCondition, SegmentEntity.class);
    }
}
