package com.fr.swift.config.conf.dao;

import com.fr.decision.authority.dao.impl.BaseDAO;
import com.fr.decision.base.data.DataList;
import com.fr.decision.base.db.session.DAOSession;
import com.fr.stable.query.QueryFactory;
import com.fr.stable.query.condition.QueryCondition;
import com.fr.stable.query.restriction.RestrictionFactory;
import com.fr.swift.config.conf.entity.MetaDataEntity;

import java.util.List;

/**
 * @author yee
 * @date 2018/5/24
 */
public class MetaDataDAO extends BaseDAO<MetaDataEntity> {
    public MetaDataDAO(DAOSession session) {
        super(session);
    }

    @Override
    public void add(MetaDataEntity metaDataEntity) throws Exception {
        this.getSession().persist(metaDataEntity);
    }

    @Override
    public MetaDataEntity getById(String s) throws Exception {
        return this.getSession().getById(s, MetaDataEntity.class);
    }

    @Override
    public void update(MetaDataEntity metaDataEntity) throws Exception {
        this.getSession().merge(metaDataEntity);
    }

    @Override
    public void remove(String id) throws Exception {
        this.getSession().remove(QueryFactory.create().addRestriction(RestrictionFactory.eq("id", id)), MetaDataEntity.class);
    }

    @Override
    public void remove(QueryCondition queryCondition) throws Exception {
        this.getSession().remove(queryCondition, MetaDataEntity.class);
    }

    @Override
    public List<MetaDataEntity> find(QueryCondition queryCondition) throws Exception {
        return this.getSession().find(queryCondition, MetaDataEntity.class);
    }

    @Override
    public MetaDataEntity findOne(QueryCondition queryCondition) throws Exception {
        return this.getSession().findOne(queryCondition, MetaDataEntity.class);

    }

    @Override
    public DataList<MetaDataEntity> findWithTotalCount(QueryCondition queryCondition) throws Exception {
        return this.getSession().findWithTotalCount(queryCondition, MetaDataEntity.class);
    }
}
