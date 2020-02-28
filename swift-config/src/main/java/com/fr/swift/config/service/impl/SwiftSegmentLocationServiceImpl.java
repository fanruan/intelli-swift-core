package com.fr.swift.config.service.impl;

import com.fr.swift.beans.annotation.SwiftBean;
import com.fr.swift.config.command.SwiftConfigCommand;
import com.fr.swift.config.command.impl.SwiftHibernateConfigCommandBus;
import com.fr.swift.config.condition.SwiftConfigCondition;
import com.fr.swift.config.condition.impl.SwiftConfigConditionImpl;
import com.fr.swift.config.entity.SwiftSegmentLocationEntity;
import com.fr.swift.config.entity.key.SwiftSegLocationEntityId;
import com.fr.swift.config.oper.ConfigQuery;
import com.fr.swift.config.oper.ConfigSession;
import com.fr.swift.config.oper.ConfigWhere;
import com.fr.swift.config.oper.impl.ConfigWhereImpl;
import com.fr.swift.config.query.SwiftConfigQuery;
import com.fr.swift.config.query.impl.SwiftHibernateConfigQueryBus;
import com.fr.swift.config.service.SwiftSegmentLocationService;
import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.property.SwiftProperty;
import com.fr.swift.segment.SegmentKey;
import com.fr.swift.source.SourceKey;
import com.fr.swift.util.function.Function;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author yee
 * @date 2018/7/24
 */
@SwiftBean
public class SwiftSegmentLocationServiceImpl implements SwiftSegmentLocationService {

    private SwiftHibernateConfigCommandBus<SwiftSegmentLocationEntity> commandBus = new SwiftHibernateConfigCommandBus<>(SwiftSegmentLocationEntity.class);
    private SwiftHibernateConfigQueryBus<SwiftSegmentLocationEntity> queryBus = new SwiftHibernateConfigQueryBus<>(SwiftSegmentLocationEntity.class);

    @Override
    public boolean delete(final String table, final String clusterId) {
        return commandBus.delete(SwiftConfigConditionImpl.newInstance()
                .addWhere(ConfigWhereImpl.eq("id.clusterId", clusterId))
                .addWhere(ConfigWhereImpl.eq("sourceKey", table))) >= 0;
    }

    @Override
    public Map<SegmentKey, Set<String>> findLocationsBySegKeys(final Set<SegmentKey> segKeys) {
        return queryBus.get(new SwiftConfigQuery<Map<SegmentKey, Set<String>>>() {
            @Override
            public Map<SegmentKey, Set<String>> apply(ConfigSession session) {
                Map<SegmentKey, Set<String>> segKeyToNodeIds = new HashMap<>();
                for (SegmentKey segKey : segKeys) {
                    if (!segKeyToNodeIds.containsKey(segKey)) {
                        segKeyToNodeIds.put(segKey, new HashSet<String>());
                    }

                    List<SwiftSegmentLocationEntity> locationEntities =
                            queryBus.get(SwiftConfigConditionImpl.newInstance().addWhere(ConfigWhereImpl.eq("id.segmentId", segKey.getId())));
                    for (SwiftSegmentLocationEntity locationEntity : locationEntities) {
                        segKeyToNodeIds.get(segKey).add(locationEntity.getClusterId());
                    }
                }
                return segKeyToNodeIds;
            }
        });
    }

    @Override
    public Map<String, List<SwiftSegmentLocationEntity>> findAll() {
        return queryBus.get(SwiftConfigConditionImpl.newInstance(), new Function<Collection<SwiftSegmentLocationEntity>, Map<String, List<SwiftSegmentLocationEntity>>>() {
            @Override
            public Map<String, List<SwiftSegmentLocationEntity>> apply(Collection<SwiftSegmentLocationEntity> collection) {
                final Map<String, List<SwiftSegmentLocationEntity>> result = new HashMap<String, List<SwiftSegmentLocationEntity>>();
                try {
                    for (SwiftSegmentLocationEntity item : collection) {
                        String sourceKey = item.getSourceKey();
                        if (null == result.get(sourceKey)) {
                            result.put(sourceKey, new ArrayList<SwiftSegmentLocationEntity>());
                        }
                        result.get(sourceKey).add(item);
                    }
                } catch (Exception e) {
                    SwiftLoggers.getLogger().warn(e);
                }
                return result;
            }
        });
    }

    @Override
    public List<SwiftSegmentLocationEntity> find(final ConfigWhere... criterion) {
        return queryBus.get(new SwiftConfigQuery<List<SwiftSegmentLocationEntity>>() {
            @Override
            public List<SwiftSegmentLocationEntity> apply(ConfigSession p) {
                final ConfigQuery<SwiftSegmentLocationEntity> entityQuery = p.createEntityQuery(SwiftSegmentLocationEntity.class);
                entityQuery.where(criterion);
                return entityQuery.executeQuery();
            }
        });
    }

    @Override
    public boolean saveOrUpdate(final SwiftSegmentLocationEntity obj) {
        commandBus.merge(obj);
        return true;
    }

    @Override
    public List<SwiftSegmentLocationEntity> findBySourceKey(final SourceKey sourceKey) {
        return queryBus.get(SwiftConfigConditionImpl.newInstance().addWhere(ConfigWhereImpl.eq("sourceKey", sourceKey.getId())));
    }

    @Override
    public void delete(final Set<SegmentKey> segKeys) {
        Set<String> segIds = new HashSet<>();
        for (SegmentKey segKey : segKeys) {
            segIds.add(segKey.getId());
        }
        commandBus.delete(SwiftConfigConditionImpl.newInstance().addWhere(ConfigWhereImpl.in("id.segmentId", segIds)));
    }

    private static SwiftSegmentLocationEntity toLocalSegLocation(SegmentKey segKey) {
        return new SwiftSegmentLocationEntity(SwiftProperty.getProperty().getClusterId(), segKey.getId(), segKey.getTable().getId());
    }

    @Override
    public boolean containsLocal(final SegmentKey segKey) {
        SwiftSegLocationEntityId id = new SwiftSegLocationEntityId(SwiftProperty.getProperty().getClusterId(), segKey.getId());
        return null != queryBus.select(id);
    }

    @Override
    public void saveOrUpdateLocal(final Set<SegmentKey> segKeys) {

        try {
            commandBus.transaction(new SwiftConfigCommand<Object>() {
                @Override
                public Object apply(ConfigSession p) {
                    for (SegmentKey segKey : segKeys) {
                        p.merge(toLocalSegLocation(segKey));
                    }
                    return null;
                }
            });
        } catch (SQLException e) {
            SwiftLoggers.getLogger().error(e);
        }
    }

    @Override
    public Map<SourceKey, List<SwiftSegmentLocationEntity>> getAllLocal() {
        final SwiftConfigCondition condition = SwiftConfigConditionImpl.newInstance()
                .addWhere(ConfigWhereImpl.eq("id.clusterId", SwiftProperty.getProperty().getClusterId()));
        return queryBus.get(condition, new Function<Collection<SwiftSegmentLocationEntity>, Map<SourceKey, List<SwiftSegmentLocationEntity>>>() {
            @Override
            public Map<SourceKey, List<SwiftSegmentLocationEntity>> apply(Collection<SwiftSegmentLocationEntity> p) {
                Map<SourceKey, List<SwiftSegmentLocationEntity>> tableToLocations = new HashMap<>();
                for (SwiftSegmentLocationEntity localSegLocation : p) {
                    SourceKey tableKey = new SourceKey(localSegLocation.getSourceKey());
                    if (!tableToLocations.containsKey(tableKey)) {
                        tableToLocations.put(tableKey, new ArrayList<SwiftSegmentLocationEntity>());
                    }
                    tableToLocations.get(tableKey).add(localSegLocation);
                }
                return tableToLocations;
            }
        });
    }

    @Override
    public boolean updateSegmentTable(final Map<String, Set<SegmentKey>> segmentTable) {
        try {
            return commandBus.transaction(new SwiftConfigCommand<Boolean>() {
                @Override
                public Boolean apply(ConfigSession p) {
                    for (Map.Entry<String, Set<SegmentKey>> entry : segmentTable.entrySet()) {
                        for (SegmentKey segmentKey : entry.getValue()) {
                            SwiftSegmentLocationEntity locationEntity = new SwiftSegmentLocationEntity(entry.getKey(), segmentKey.getId(), segmentKey.getTable().getId());
                            p.merge(locationEntity);
                        }
                    }
                    return true;
                }
            });
        } catch (SQLException e) {
            SwiftLoggers.getLogger().warn("Update table error!", e);
            return false;
        }
    }
}
