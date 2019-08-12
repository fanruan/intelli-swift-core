package com.fr.swift.config.service.impl;

import com.fr.swift.beans.annotation.SwiftBean;
import com.fr.swift.config.SwiftConfigConstants;
import com.fr.swift.config.command.SwiftSegmentCommandBus;
import com.fr.swift.config.command.impl.SwiftSegmentCommandBusImpl;
import com.fr.swift.config.condition.SwiftConfigCondition;
import com.fr.swift.config.condition.impl.SwiftConfigConditionImpl;
import com.fr.swift.config.entity.SwiftSegmentEntity;
import com.fr.swift.config.entity.SwiftSegmentLocationEntity;
import com.fr.swift.config.oper.ConfigQuery;
import com.fr.swift.config.oper.ConfigSession;
import com.fr.swift.config.oper.ConfigWhere;
import com.fr.swift.config.oper.Page;
import com.fr.swift.config.oper.exception.SwiftConstraintViolationException;
import com.fr.swift.config.oper.exception.SwiftEntityExistsException;
import com.fr.swift.config.oper.exception.SwiftNonUniqueObjectException;
import com.fr.swift.config.oper.impl.ConfigWhereImpl;
import com.fr.swift.config.oper.impl.OrderImpl;
import com.fr.swift.config.query.SwiftConfigQuery;
import com.fr.swift.config.query.impl.SwiftHibernateConfigQueryBus;
import com.fr.swift.config.service.SwiftClusterSegmentService;
import com.fr.swift.config.service.SwiftSegmentService;
import com.fr.swift.cube.io.Types.StoreType;
import com.fr.swift.db.SwiftSchema;
import com.fr.swift.db.impl.SwiftDatabase;
import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.property.SwiftProperty;
import com.fr.swift.segment.SegmentKey;
import com.fr.swift.segment.container.SegmentContainer;
import com.fr.swift.source.SourceKey;
import com.fr.swift.util.Crasher;
import com.fr.swift.util.function.Function;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author yee
 * @date 2018/6/7
 */
@SwiftBean(name = "swiftClusterSegmentService")
public class SwiftSegmentServiceImpl implements SwiftClusterSegmentService, SwiftSegmentService {

    private SwiftSegmentCommandBus commandBus = new SwiftSegmentCommandBusImpl();
    private SwiftHibernateConfigQueryBus<SwiftSegmentEntity> queryBus = new SwiftHibernateConfigQueryBus<>(SwiftSegmentEntity.class);

    @Override
    public boolean saveOrUpdate(SegmentKey obj) {
        commandBus.merge(obj);
        return true;
    }

    @Override
    public boolean addSegments(final List<SegmentKey> segments) {
        commandBus.merge(segments);
        return true;
    }

    @Override
    public boolean removeSegments(final String... sourceKey) {
        final List<String> value = Arrays.asList(sourceKey);
        if (value.isEmpty()) {
            return false;
        }
        final SwiftConfigCondition condition = SwiftConfigConditionImpl.newInstance()
                .addWhere(ConfigWhereImpl.in(SwiftConfigConstants.SegmentConfig.COLUMN_SEGMENT_OWNER, value));
        commandBus.deleteCascade(condition);
        return true;
    }

    @Override
    public boolean removeSegments(final List<SegmentKey> segmentKeys) {

        if (null == segmentKeys || segmentKeys.isEmpty()) {
            return false;
        }
        Set<String> segmentId = new HashSet<>(segmentKeys.size());
        for (SegmentKey segmentKey : segmentKeys) {
            segmentId.add(segmentKey.getId());
        }
        return commandBus.deleteCascade(SwiftConfigConditionImpl.newInstance().addWhere(ConfigWhereImpl.in("id", segmentId))) >= 0;
    }

    @Override
    public Map<SourceKey, List<SegmentKey>> getAllSegments() {
        return queryBus.get(SwiftConfigConditionImpl.newInstance(), new Function<Collection<SwiftSegmentEntity>, Map<SourceKey, List<SegmentKey>>>() {
            @Override
            public Map<SourceKey, List<SegmentKey>> apply(Collection<SwiftSegmentEntity> collection) {
                final Map<SourceKey, List<SegmentKey>> result = new HashMap<SourceKey, List<SegmentKey>>();
                try {
                    for (SegmentKey segmentKey : collection) {
                        SourceKey sourceKey = segmentKey.getTable();
                        if (!result.containsKey(sourceKey)) {
                            result.put(sourceKey, new ArrayList<SegmentKey>());
                        }
                        result.get(sourceKey).add(segmentKey);
                    }
                } catch (Exception e) {
                    SwiftLoggers.getLogger().warn("find segments failed", e);
                }
                return result;
            }
        });
    }

    @Override
    public List<SegmentKey> getSegmentByKey(String sourceKey) {
        List<SegmentKey> result = getOwnSegments().get(new SourceKey(sourceKey));
        if (null == result) {
            return Collections.emptyList();
        }
        return result;
    }

    @Override
    public boolean containsSegment(final SegmentKey segmentKey) {
        return null != queryBus.select(segmentKey.getId());
    }

    @Override
    public SegmentKey tryAppendSegment(final SourceKey tableKey, final StoreType storeType) {
        do {
            try {
                final SwiftConfigCondition condition = SwiftConfigConditionImpl.newInstance()
                        .addWhere(ConfigWhereImpl.eq(SwiftConfigConstants.SegmentConfig.COLUMN_SEGMENT_OWNER, tableKey.getId()))
                        .addSort(OrderImpl.desc(SwiftConfigConstants.SegmentConfig.COLUMN_SEGMENT_ORDER));
                final List<SwiftSegmentEntity> list = queryBus.get(condition);
                int appendOrder;
                if (list.isEmpty()) {
                    appendOrder = 0;
                } else {
                    appendOrder = list.get(0).getOrder() + 1;
                }
                SwiftSegmentEntity segKeyEntity = new SwiftSegmentEntity(tableKey, appendOrder, storeType,
                        SwiftDatabase.getInstance().getTable(tableKey).getMetadata().getSwiftSchema());
                commandBus.save(segKeyEntity);
                return segKeyEntity;
            } catch (SQLException e) {
                Throwable cause = e.getCause();
                if (cause instanceof SwiftConstraintViolationException
                        || cause instanceof SwiftNonUniqueObjectException
                        || cause instanceof SwiftEntityExistsException) {
                    // 主键冲突，继续尝试
                    continue;
                }
                return Crasher.crash(e);
            }
        } while (true);
    }

    @Override
    public Page<SegmentKey> selectSelective(SegmentKey segmentKey, final int page, final int size) {
        final SwiftConfigCondition configCondition = SwiftConfigConditionImpl.newInstance();
        SourceKey sourceKey = segmentKey.getTable();
        if (null != sourceKey) {
            configCondition.addWhere(ConfigWhereImpl.eq(SwiftConfigConstants.SegmentConfig.COLUMN_SEGMENT_OWNER, sourceKey.getId()));
        }
        StoreType storeType = segmentKey.getStoreType();
        if (null != storeType) {
            configCondition.addWhere(ConfigWhereImpl.eq(SwiftConfigConstants.SegmentConfig.COLUMN_STORE_TYPE, storeType));
        }
        SwiftSchema swiftSchema = segmentKey.getSwiftSchema();
        if (null != swiftSchema) {
            configCondition.addWhere(ConfigWhereImpl.eq("swiftSchema", swiftSchema));
        }

        return queryBus.get(new SwiftConfigQuery<Page<SegmentKey>>() {
            @Override
            public Page<SegmentKey> apply(ConfigSession p) {
                ConfigQuery<SwiftSegmentLocationEntity> entityQuery = p.createEntityQuery(SwiftSegmentLocationEntity.class);
                entityQuery.where(ConfigWhereImpl.eq("id.clusterId", SwiftProperty.getProperty().getClusterId()));
                List<SwiftSegmentLocationEntity> entities = entityQuery.executeQuery();
                if (entities.isEmpty()) {
                    return new Page<>();
                }
                Set<String> segIds = new HashSet<>(entities.size());
                for (SwiftSegmentLocationEntity entity : entities) {
                    segIds.add(entity.getSegmentId());
                }
                configCondition.addWhere(ConfigWhereImpl.in("id", segIds));
                final ConfigQuery<SwiftSegmentEntity> segmentEntityConfigQuery = p.createEntityQuery(SwiftSegmentEntity.class);
                final Page<SwiftSegmentEntity> data = segmentEntityConfigQuery.executeQuery(page, size);
                Page<SegmentKey> result = new Page<>();
                result.setData(new ArrayList<SegmentKey>(data.getData()));
                result.setTotal(data.getTotal());
                result.setCurrentPage(data.getCurrentPage());
                result.setPageSize(data.getPageSize());
                return result;
            }
        });

    }

    @Override
    public Set<SegmentKey> getByIds(final Set<String> segIds) {
        return new HashSet<SegmentKey>(queryBus.get(SwiftConfigConditionImpl.newInstance().addWhere(ConfigWhereImpl.in("id", segIds))));
    }

    @Override
    public Map<SourceKey, List<SegmentKey>> getOwnSegments() {
        return getOwnSegments(SwiftProperty.getProperty().getClusterId());
    }

    @Override
    public Map<SourceKey, List<SegmentKey>> getOwnSegments(final String clusterId) {
        return queryBus.get(new SwiftConfigQuery<Map<SourceKey, List<SegmentKey>>>() {
            @Override
            public Map<SourceKey, List<SegmentKey>> apply(ConfigSession session) {
                ConfigQuery<SwiftSegmentLocationEntity> locationQuery = session.createEntityQuery(SwiftSegmentLocationEntity.class);
                locationQuery.where(ConfigWhereImpl.eq("id.clusterId", clusterId));
                final List<SwiftSegmentLocationEntity> entities = locationQuery.executeQuery();
                if (entities.isEmpty()) {
                    return Collections.emptyMap();
                }
                final Set<String> segmentIds = new HashSet<String>(entities.size());
                for (SwiftSegmentLocationEntity entity : entities) {
                    segmentIds.add(entity.getSegmentId());
                }
                ConfigQuery<SwiftSegmentEntity> entityQuery = session.createEntityQuery(SwiftSegmentEntity.class);
                entityQuery.where(ConfigWhereImpl.in("id", segmentIds));
                final List<SwiftSegmentEntity> list = entityQuery.executeQuery();
                Map<SourceKey, List<SegmentKey>> result = new HashMap<SourceKey, List<SegmentKey>>();
                for (SegmentKey segmentKey : list) {
                    SourceKey sourceKey = segmentKey.getTable();
                    if (!result.containsKey(sourceKey)) {
                        result.put(sourceKey, new ArrayList<SegmentKey>());
                    }
                    result.get(sourceKey).add(segmentKey);
                }
                for (SegmentContainer value : SegmentContainer.values()) {
                    value.register(result);
                }
                return result;
            }
        });
    }


    @Override
    public List<SegmentKey> find(final ConfigWhere... criterion) {
        SwiftConfigCondition condition = SwiftConfigConditionImpl.newInstance();
        for (ConfigWhere configWhere : criterion) {
            condition.addWhere(configWhere);
        }
        return new ArrayList<SegmentKey>(queryBus.get(condition));
    }
}
