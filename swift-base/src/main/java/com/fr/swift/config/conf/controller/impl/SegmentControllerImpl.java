package com.fr.swift.config.conf.controller.impl;

import com.fr.decision.base.data.DataList;
import com.fr.decision.base.util.CollectionUtil;
import com.fr.stable.query.QueryFactory;
import com.fr.stable.query.condition.QueryCondition;
import com.fr.stable.query.restriction.RestrictionFactory;
import com.fr.swift.config.conf.bean.SegmentBean;
import com.fr.swift.config.conf.controller.SegmentController;
import com.fr.swift.config.conf.entity.SegmentEntity;
import com.fr.swift.config.conf.session.SwiftConfigSessionController;

import java.util.List;

/**
 * @author yee
 * @date 2018/5/24
 */
public class SegmentControllerImpl implements SegmentController {

    private SwiftConfigSessionController controller;

    public SegmentControllerImpl(SwiftConfigSessionController controller) {
        this.controller = controller;
    }

    @Override
    public void add(SegmentBean segmentBean) throws Exception {
        this.controller.getSegmentDAO().add(segmentBean.convert());
    }

    @Override
    public SegmentBean getById(String s) throws Exception {
        return this.controller.getSegmentDAO().getById(s).convert();
    }

    @Override
    public void update(SegmentBean segmentBean) throws Exception {
        this.controller.getSegmentDAO().update(segmentBean.convert());
    }

    @Override
    public void remove(String s) throws Exception {
        this.controller.getSegmentDAO().remove(s);
    }

    @Override
    public void remove(QueryCondition queryCondition) throws Exception {
        this.controller.getSegmentDAO().remove(queryCondition);
    }

    @Override
    public List<SegmentBean> find(QueryCondition queryCondition) throws Exception {
        List<SegmentEntity> entities = this.controller.getSegmentDAO().find(queryCondition);
        return CollectionUtil.map(entities, new CollectionUtil.MapIteratee<SegmentEntity, SegmentBean>() {
            @Override
            public SegmentBean convert(SegmentEntity item) throws Exception {
                return item.convert();
            }
        });
    }

    @Override
    public SegmentBean findOne(QueryCondition queryCondition) throws Exception {
        return this.controller.getSegmentDAO().findOne(queryCondition).convert();
    }

    @Override
    public DataList<SegmentBean> findWithTotalCount(QueryCondition queryCondition) throws Exception {
        List<SegmentBean> list = find(queryCondition);
        return new DataList<SegmentBean>().list(list);
    }

    @Override
    public List<SegmentBean> findBySourceKey(String sourceKey) throws Exception {
        return find(QueryFactory.create().addRestriction(RestrictionFactory.eq(SegmentBean.COLUMN_SOURCE_KEY, sourceKey)));
    }
}
