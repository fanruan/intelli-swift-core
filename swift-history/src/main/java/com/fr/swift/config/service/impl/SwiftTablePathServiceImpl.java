package com.fr.swift.config.service.impl;

import com.fr.swift.beans.annotation.SwiftBean;
import com.fr.swift.config.command.SwiftConfigCommandBus;
import com.fr.swift.config.command.impl.SwiftHibernateConfigCommandBus;
import com.fr.swift.config.condition.impl.SwiftConfigConditionImpl;
import com.fr.swift.config.entity.SwiftTablePathEntity;
import com.fr.swift.config.entity.key.SwiftTablePathKey;
import com.fr.swift.config.oper.ConfigQuery;
import com.fr.swift.config.oper.ConfigSession;
import com.fr.swift.config.oper.ConfigWhere;
import com.fr.swift.config.oper.impl.ConfigWhereImpl;
import com.fr.swift.config.query.SwiftConfigQuery;
import com.fr.swift.config.query.SwiftConfigQueryBus;
import com.fr.swift.config.query.impl.SwiftHibernateConfigQueryBus;
import com.fr.swift.config.service.SwiftTablePathService;
import com.fr.swift.event.ClusterEvent;
import com.fr.swift.event.ClusterEventListener;
import com.fr.swift.event.ClusterListenerHandler;
import com.fr.swift.property.SwiftProperty;
import com.fr.swift.util.function.Function;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author yee
 * @date 2018/7/18
 */
@SwiftBean
public class SwiftTablePathServiceImpl implements SwiftTablePathService {
    private ConcurrentHashMap<String, SwiftTablePathEntity> tablePath = new ConcurrentHashMap<String, SwiftTablePathEntity>();
    private SwiftConfigCommandBus<SwiftTablePathEntity> commandBus = new SwiftHibernateConfigCommandBus<>(SwiftTablePathEntity.class);
    private SwiftConfigQueryBus<SwiftTablePathEntity> queryBus = new SwiftHibernateConfigQueryBus<>(SwiftTablePathEntity.class);

    public SwiftTablePathServiceImpl() {
        ClusterListenerHandler.addExtraListener(new ClusterEventListener() {
            @Override
            public void handleEvent(ClusterEvent clusterEvent) {
                tablePath.clear();
            }
        });
    }

    @Override
    public List<SwiftTablePathEntity> find(final ConfigWhere... criterion) {
        return queryBus.get(new SwiftConfigQuery<List<SwiftTablePathEntity>>() {
            @Override
            public List<SwiftTablePathEntity> apply(ConfigSession p) {
                final ConfigQuery<SwiftTablePathEntity> entityQuery = p.createEntityQuery(SwiftTablePathEntity.class);
                entityQuery.where(criterion);
                return entityQuery.executeQuery();
            }
        });
    }

    @Override
    public boolean saveOrUpdate(final SwiftTablePathEntity entity) {
        entity.getId().setClusterId(SwiftProperty.getProperty().getClusterId());
        commandBus.merge(entity);
        tablePath.put(entity.getId().getTableKey().getId(), entity);
        return true;
    }

    @Override
    public boolean removePath(final String table) {
        final SwiftTablePathKey swiftTablePathKey = new SwiftTablePathKey(table, SwiftProperty.getProperty().getClusterId());
        commandBus.delete(SwiftConfigConditionImpl.newInstance().addWhere(ConfigWhereImpl.eq("id", swiftTablePathKey)));
        tablePath.remove(table);
        return true;
    }

    @Override
    public Integer getTablePath(final String table) {
        SwiftTablePathEntity path = tablePath.get(table);
        if (null == path) {
            return queryBus.select(new SwiftTablePathKey(table, SwiftProperty.getProperty().getClusterId()), new Function<SwiftTablePathEntity, Integer>() {
                @Override
                public Integer apply(SwiftTablePathEntity entity) {
                    if (null != entity) {
                        Integer path = entity.getTablePath();
                        if (null != path && path > -1) {
                            tablePath.put(table, entity);
                            return path;
                        }
                        return 0;
                    }
                    return 0;
                }
            });
        }
        return path.getTablePath() == null ? 0 : path.getTablePath();
    }

    @Override
    public Integer getLastPath(final String table) {
        return queryBus.select(new SwiftTablePathKey(table, SwiftProperty.getProperty().getClusterId()), new Function<SwiftTablePathEntity, Integer>() {
            @Override
            public Integer apply(SwiftTablePathEntity entity) {
                if (null != entity) {
                    return entity.getLastPath();
                }
                return 0;
            }
        });
    }

    @Override
    public SwiftTablePathEntity get(final String table) {
        final SwiftTablePathEntity select = queryBus.select(new SwiftTablePathKey(table, SwiftProperty.getProperty().getClusterId()));
        if (null != select) {
            tablePath.put(table, select);
        }
        return select;
    }
}
