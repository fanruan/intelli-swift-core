package com.fr.swift.config.service.impl;

import com.fr.swift.beans.annotation.SwiftBean;
import com.fr.swift.config.command.impl.SwiftHibernateConfigCommandBus;
import com.fr.swift.config.condition.SwiftConfigCondition;
import com.fr.swift.config.condition.impl.SwiftConfigConditionImpl;
import com.fr.swift.config.entity.SwiftJdbcServerInfo;
import com.fr.swift.config.oper.ConfigWhere;
import com.fr.swift.config.query.impl.SwiftHibernateConfigQueryBus;
import com.fr.swift.config.service.SwiftJdbcServerInfoService;
import com.fr.swift.util.function.Function;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author yee
 * @version 1.1
 * Created by yee on 2019-09-03
 */
@SwiftBean
public class SwiftJdbcServerInfoServiceImpl implements SwiftJdbcServerInfoService {
    private SwiftHibernateConfigQueryBus<SwiftJdbcServerInfo> queryBus = new SwiftHibernateConfigQueryBus<>(SwiftJdbcServerInfo.class);
    private SwiftHibernateConfigCommandBus<SwiftJdbcServerInfo> commandBus = new SwiftHibernateConfigCommandBus<>(SwiftJdbcServerInfo.class);

    @Override
    public List<SwiftJdbcServerInfo> find(ConfigWhere... criterion) {
        SwiftConfigCondition swiftConfigCondition = SwiftConfigConditionImpl.newInstance();
        for (ConfigWhere configWhere : criterion) {
            swiftConfigCondition = swiftConfigCondition.addWhere(configWhere);
        }
        return queryBus.get(swiftConfigCondition);
    }

    @Override
    public boolean saveOrUpdate(SwiftJdbcServerInfo obj) {
        commandBus.merge(obj);
        return true;
    }

    @Override
    public Map<String, SwiftJdbcServerInfo> getAllServerInfo() {
        return queryBus.get(SwiftConfigConditionImpl.newInstance(), new Function<Collection<SwiftJdbcServerInfo>, Map<String, SwiftJdbcServerInfo>>() {
            @Override
            public Map<String, SwiftJdbcServerInfo> apply(Collection<SwiftJdbcServerInfo> info) {
                Map<String, SwiftJdbcServerInfo> result = new HashMap<>(info.size());
                for (SwiftJdbcServerInfo item : info) {
                    result.put(item.getClusterId(), item);
                }
                return result;
            }
        });
    }
}
