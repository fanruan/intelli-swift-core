package com.fr.swift.config.service.impl;

import com.fr.swift.beans.annotation.SwiftBean;
import com.fr.swift.config.command.SwiftConfigCommandBus;
import com.fr.swift.config.command.impl.SwiftHibernateConfigCommandBus;
import com.fr.swift.config.condition.SwiftConfigCondition;
import com.fr.swift.config.condition.impl.SwiftConfigConditionImpl;
import com.fr.swift.config.entity.SwiftServiceInfoEntity;
import com.fr.swift.config.oper.ConfigQuery;
import com.fr.swift.config.oper.ConfigSession;
import com.fr.swift.config.oper.ConfigWhere;
import com.fr.swift.config.oper.impl.ConfigWhereImpl;
import com.fr.swift.config.query.SwiftConfigQuery;
import com.fr.swift.config.query.SwiftConfigQueryBus;
import com.fr.swift.config.query.impl.SwiftHibernateConfigQueryBus;
import com.fr.swift.config.service.SwiftServiceInfoService;

import java.util.List;

/**
 * This class created on 2018/5/29
 *
 * @author Lucifer
 * @description
 * @since Advanced FineBI 5.0
 */
@SwiftBean(name = "swiftServiceInfoService")
class SwiftServiceInfoServiceImpl implements SwiftServiceInfoService {

    private SwiftConfigCommandBus<SwiftServiceInfoEntity> commandBus = new SwiftHibernateConfigCommandBus<>(SwiftServiceInfoEntity.class);
    private SwiftConfigQueryBus<SwiftServiceInfoEntity> queryBus = new SwiftHibernateConfigQueryBus<>(SwiftServiceInfoEntity.class);

    @Override
    public boolean saveOrUpdate(final SwiftServiceInfoEntity serviceInfoBean) {
        commandBus.merge(serviceInfoBean);
        return true;
    }

    @Override
    public boolean removeServiceInfo(final SwiftServiceInfoEntity serviceInfoBean) {
        final SwiftConfigCondition configCondition = SwiftConfigConditionImpl.newInstance().addWhere(ConfigWhereImpl.eq("id", serviceInfoBean.getId()));
        return commandBus.delete(configCondition) > 0;
    }

    @Override
    public SwiftServiceInfoEntity getServiceInfo(final SwiftServiceInfoEntity serviceInfoBean) {
        return queryBus.select(serviceInfoBean.getId());
    }

    @Override
    public List<SwiftServiceInfoEntity> getAllServiceInfo() {
        return queryBus.get(SwiftConfigConditionImpl.newInstance());
    }

    @Override
    public List<SwiftServiceInfoEntity> getServiceInfoByService(final String service) {
        return queryBus.get(SwiftConfigConditionImpl.newInstance().addWhere(ConfigWhereImpl.eq("service_info", service)));
    }

    @Override
    public List<SwiftServiceInfoEntity> find(final ConfigWhere... criterion) {
        return queryBus.get(new SwiftConfigQuery<List<SwiftServiceInfoEntity>>() {
            @Override
            public List<SwiftServiceInfoEntity> apply(ConfigSession p) {
                final ConfigQuery<SwiftServiceInfoEntity> entityQuery = p.createEntityQuery(SwiftServiceInfoEntity.class);
                entityQuery.where(criterion);
                return entityQuery.executeQuery();
            }
        });
    }
}
