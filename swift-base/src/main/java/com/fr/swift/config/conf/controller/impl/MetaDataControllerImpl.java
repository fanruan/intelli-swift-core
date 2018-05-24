package com.fr.swift.config.conf.controller.impl;

import com.fr.decision.base.data.DataList;
import com.fr.decision.base.util.CollectionUtil;
import com.fr.stable.query.QueryFactory;
import com.fr.stable.query.condition.QueryCondition;
import com.fr.stable.query.restriction.RestrictionFactory;
import com.fr.swift.config.conf.controller.MetaDataController;
import com.fr.swift.config.conf.entity.MetaDataEntity;
import com.fr.swift.config.conf.session.SwiftConfigSessionController;
import com.fr.swift.config.conf.bean.SwiftMetaDataBean;

import java.util.List;

/**
 * @author yee
 * @date 2018/5/24
 */
public class MetaDataControllerImpl implements MetaDataController {

    private SwiftConfigSessionController controller;

    public MetaDataControllerImpl(SwiftConfigSessionController controller) {
        this.controller = controller;
    }

    @Override
    public void add(SwiftMetaDataBean metaDataBean) throws Exception {
        this.controller.getMetaDataDAO().add(metaDataBean.convert());
    }

    @Override
    public SwiftMetaDataBean getById(String s) throws Exception {
        return this.controller.getMetaDataDAO().getById(s).convert();
    }

    @Override
    public void update(SwiftMetaDataBean metaDataBean) throws Exception {
        this.controller.getMetaDataDAO().update(metaDataBean.convert());
    }

    @Override
    public void remove(String s) throws Exception {
        this.controller.getMetaDataDAO().remove(s);
    }

    @Override
    public void remove(QueryCondition queryCondition) throws Exception {
        this.controller.getMetaDataDAO().remove(queryCondition);
    }

    @Override
    public List<SwiftMetaDataBean> find(QueryCondition queryCondition) throws Exception {
        List<MetaDataEntity> entities = this.controller.getMetaDataDAO().find(queryCondition);
        return CollectionUtil.map(entities, new CollectionUtil.MapIteratee<MetaDataEntity, SwiftMetaDataBean>() {
            @Override
            public SwiftMetaDataBean convert(MetaDataEntity item) throws Exception {
                return item.convert();
            }
        });
    }

    @Override
    public SwiftMetaDataBean findOne(QueryCondition queryCondition) throws Exception {
        return this.controller.getMetaDataDAO().findOne(queryCondition).convert();
    }

    @Override
    public DataList<SwiftMetaDataBean> findWithTotalCount(QueryCondition queryCondition) throws Exception {
        List<SwiftMetaDataBean> list = find(queryCondition);
        return new DataList<SwiftMetaDataBean>().list(list);
    }

    @Override
    public List<SwiftMetaDataBean> findByTableName(String tableName) throws Exception {
        return find(QueryFactory.create().addRestriction(RestrictionFactory.eq(SwiftMetaDataBean.COLUMN_TABLE_NAME, tableName)));
    }
}
