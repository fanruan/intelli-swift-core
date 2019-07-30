package com.fr.swift.config.service.impl;

import com.fr.swift.SwiftContext;
import com.fr.swift.beans.annotation.SwiftBean;
import com.fr.swift.config.SwiftConfigConstants;
import com.fr.swift.config.dao.SwiftSegmentBucketDao;
import com.fr.swift.config.dao.SwiftSegmentDao;
import com.fr.swift.config.dao.SwiftSegmentLocationDao;
import com.fr.swift.config.entity.SwiftSegmentEntity;
import com.fr.swift.config.entity.SwiftSegmentLocationEntity;
import com.fr.swift.config.oper.BaseTransactionWorker;
import com.fr.swift.config.oper.ConfigSession;
import com.fr.swift.config.oper.ConfigSessionCreator;
import com.fr.swift.config.oper.ConfigWhere;
import com.fr.swift.config.oper.Order;
import com.fr.swift.config.oper.Page;
import com.fr.swift.config.oper.exception.SwiftConstraintViolationException;
import com.fr.swift.config.oper.exception.SwiftEntityExistsException;
import com.fr.swift.config.oper.exception.SwiftNonUniqueObjectException;
import com.fr.swift.config.oper.impl.ConfigWhereImpl;
import com.fr.swift.config.oper.impl.OrderImpl;
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

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
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
    private SwiftSegmentLocationDao segmentLocationDao = SwiftContext.get().getBean(SwiftSegmentLocationDao.class);
    private ConfigSessionCreator configSessionCreator = SwiftContext.get().getBean(ConfigSessionCreator.class);
    private SwiftSegmentDao swiftSegmentDao = SwiftContext.get().getBean(SwiftSegmentDao.class);
    private SwiftSegmentBucketDao segmentBucketDao = SwiftContext.get().getBean(SwiftSegmentBucketDao.class);

    @Override
    public boolean saveOrUpdate(SegmentKey obj) {
        return addSegments(Collections.singletonList(obj));
    }

    @Override
    public void checkOldConfig() {
        try {
            configSessionCreator.doTransactionIfNeed(new BaseTransactionWorker() {
                @Override
                public Void work(final ConfigSession session) throws SQLException {
                    try {
                        List<SwiftSegmentLocationEntity> locations = segmentLocationDao.findAll(session);
                        if (locations.isEmpty()) {
                            for (SegmentKey segmentKey : swiftSegmentDao.findAll(session)) {
                                SwiftSegmentLocationEntity bean = new SwiftSegmentLocationEntity("LOCAL", segmentKey.getId(), segmentKey.getTable().getId());
                                segmentLocationDao.saveOrUpdate(session, bean);
                            }
                        }
                        return null;
                    } catch (Throwable e) {
                        if (e instanceof SQLException) {
                            throw (SQLException) e;
                        }
                        throw new SQLException(e);
                    }
                }
            });
        } catch (SQLException e) {
            SwiftLoggers.getLogger().warn("add segment error! ", e);
        }
    }

    @Override
    public boolean addSegments(final List<SegmentKey> segments) {
        try {
            return configSessionCreator.doTransactionIfNeed(new BaseTransactionWorker<Boolean>() {

                @Override
                public Boolean work(ConfigSession session) throws SQLException {
                    return addSegmentsWithoutTransaction(session, segments);
                }
            });
        } catch (SQLException e) {
            SwiftLoggers.getLogger().warn("add segment error! ", e);
        }
        return false;
    }

    @Override
    public boolean removeSegments(final String... sourceKey) {
        try {
            return configSessionCreator.doTransactionIfNeed(new BaseTransactionWorker<Boolean>() {
                @Override
                public Boolean work(ConfigSession session) throws SQLException {
                    for (String key : sourceKey) {
                        segmentLocationDao.deleteBySourceKey(session, key);
                        swiftSegmentDao.deleteBySourceKey(session, key);
                        segmentBucketDao.deleteBySourceKey(session, key);
                        for (SegmentContainer value : SegmentContainer.values()) {
                            value.remove(new SourceKey(key));
                        }
                    }
                    return true;
                }
            });
        } catch (SQLException e) {
            SwiftLoggers.getLogger().warn("remove segment error! ", e);
        }
        return false;
    }

    @Override
    public boolean removeSegments(final List<SegmentKey> segmentKeys) {
        try {
            if (null == segmentKeys) {
                return false;
            }
            return configSessionCreator.doTransactionIfNeed(new BaseTransactionWorker<Boolean>() {
                @Override
                public Boolean work(final ConfigSession session) throws SQLException {
                    try {
                        for (final SegmentKey segmentKey : segmentKeys) {
                            for (SwiftSegmentLocationEntity item : segmentLocationDao.findBySegmentId(session, segmentKey.getId())) {
                                session.delete(item);
                                for (SegmentContainer value : SegmentContainer.values()) {
                                    value.remove(segmentKey);
                                }
                            }
                            swiftSegmentDao.deleteById(session, segmentKey.getId());
                            segmentBucketDao.deleteBySegmentKey(session, segmentKey.getId());
                        }
                    } catch (Throwable e) {
                        throw new SQLException(e);
                    }
                    return true;
                }
            });
        } catch (SQLException e) {
            SwiftLoggers.getLogger().warn("remove segment error! ", e);
            return false;
        }
    }

    @Override
    public boolean removeSegments(final Set<String> segmentKeys) {
        try {
            return configSessionCreator.doTransactionIfNeed(new BaseTransactionWorker<Boolean>() {
                @Override
                public Boolean work(final ConfigSession session) throws SQLException {
                    try {
                        for (final String segmentKey : segmentKeys) {
                            for (SwiftSegmentLocationEntity item : segmentLocationDao.findBySegmentId(session, segmentKey)) {
                                session.delete(item);
                                for (SegmentContainer value : SegmentContainer.values()) {
                                    value.remove(swiftSegmentDao.select(session, segmentKey));
                                }
                            }
                            swiftSegmentDao.deleteById(session, segmentKey);
                            segmentBucketDao.deleteBySegmentKey(session, segmentKey);
                        }
                    } catch (Throwable e) {
                        throw new SQLException(e);
                    }
                    return true;
                }
            });
        } catch (SQLException e) {
            return false;
        }
    }

    private boolean addSegmentsWithoutTransaction(ConfigSession session, Collection<SegmentKey> segments) throws SQLException {
        for (SegmentKey segment : segments) {
            swiftSegmentDao.addOrUpdateSwiftSegment(session, segment);
            SwiftSegmentLocationEntity locationEntity = new SwiftSegmentLocationEntity(SwiftProperty.getProperty().getClusterId(), segment.getId(), segment.getTable().getId());
            segmentLocationDao.saveOrUpdate(session, locationEntity);
            for (SegmentContainer value : SegmentContainer.values()) {
                value.register(segment);
            }
        }
        return true;

    }

    @Override
    public boolean updateSegments(final String sourceKey, final List<SegmentKey> segments) {
        try {
            return configSessionCreator.doTransactionIfNeed(new BaseTransactionWorker<Boolean>() {
                @Override
                public Boolean work(ConfigSession session) throws SQLException {
                    segmentLocationDao.deleteBySourceKey(session, sourceKey);
                    swiftSegmentDao.deleteBySourceKey(session, sourceKey);
                    return addSegmentsWithoutTransaction(session, segments);
                }
            });
        } catch (SQLException e) {
            SwiftLoggers.getLogger().warn("update segment error! ", e);
        }
        return false;
    }

    @Override
    public Map<SourceKey, List<SegmentKey>> getAllSegments() {
        try {
            return configSessionCreator.doTransactionIfNeed(new BaseTransactionWorker<Map<SourceKey, List<SegmentKey>>>(false) {
                @Override
                public Map<SourceKey, List<SegmentKey>> work(ConfigSession session) throws SQLException {
                    Map<SourceKey, List<SegmentKey>> result = swiftSegmentDao.findSegmentKeyWithSourceKey(session);
                    for (SegmentContainer value : SegmentContainer.values()) {
                        value.register(result);
                    }
                    return result;
                }
            });

        } catch (Exception e) {
            SwiftLoggers.getLogger().warn("Select segments error!", e);
        }
        return Collections.emptyMap();
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
        try {
            return configSessionCreator.doTransactionIfNeed(new BaseTransactionWorker<Boolean>() {
                @Override
                public Boolean work(ConfigSession session) throws SQLException {
                    if (null != swiftSegmentDao.select(session, segmentKey.getId())) {
                        return !segmentLocationDao.find(session,
                                ConfigWhereImpl.eq("id.clusterId", SwiftProperty.getProperty().getClusterId()),
                                ConfigWhereImpl.eq("id.segmentId", segmentKey.getId())).isEmpty();
                    }
                    return false;
                }
            });

        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public SegmentKey tryAppendSegment(final SourceKey tableKey, final StoreType storeType) {
        try {
            final SegmentKey segmentKey = tryAppendSegmentWithoutLocation(tableKey, storeType);

            return configSessionCreator.doTransactionIfNeed(new BaseTransactionWorker<SegmentKey>() {
                @Override
                public SegmentKey work(ConfigSession session) throws SQLException {
                    addSegmentsWithoutTransaction(session, Collections.singleton(segmentKey));
                    return segmentKey;
                }
            });
        } catch (SQLException e) {
            return Crasher.crash(e);
        }
    }

    private SegmentKey tryAppendSegmentWithoutLocation(final SourceKey tableKey, final StoreType storeType) throws SQLException {
        do {
            try {
                return configSessionCreator.doTransactionIfNeed(new BaseTransactionWorker<SegmentKey>() {
                    @Override
                    public SegmentKey work(ConfigSession session) throws SQLException {

                        List<SegmentKey> entities = swiftSegmentDao.find(session,
                                new Order[]{OrderImpl.desc(SwiftConfigConstants.SegmentConfig.COLUMN_SEGMENT_ORDER)},
                                ConfigWhereImpl.eq(SwiftConfigConstants.SegmentConfig.COLUMN_SEGMENT_OWNER, tableKey.getId()));

                        int appendOrder;
                        if (entities.isEmpty()) {
                            appendOrder = 0;
                        } else {
                            appendOrder = entities.get(0).getOrder() + 1;
                        }
                        SwiftSegmentEntity segKeyEntity = new SwiftSegmentEntity(tableKey, appendOrder, storeType,
                                SwiftDatabase.getInstance().getTable(tableKey).getMetadata().getSwiftSchema());
                        swiftSegmentDao.persist(session, segKeyEntity);
                        return segKeyEntity;
                    }
                });

            } catch (SQLException e) {
                Throwable cause = e.getCause();
                if (cause instanceof SwiftConstraintViolationException
                        || cause instanceof SwiftNonUniqueObjectException
                        || cause instanceof SwiftEntityExistsException) {
                    continue;
                }
                throw e;
            }
        } while (true);
    }

    @Override
    public Page<SegmentKey> selectSelective(SegmentKey segmentKey, final int page, final int size) {
        final List<ConfigWhere> wheres = new ArrayList<ConfigWhere>();
        SourceKey sourceKey = segmentKey.getTable();
        if (null != sourceKey) {
            wheres.add(ConfigWhereImpl.eq(SwiftConfigConstants.SegmentConfig.COLUMN_SEGMENT_OWNER, sourceKey.getId()));
        }
        StoreType storeType = segmentKey.getStoreType();
        if (null != storeType) {
            wheres.add(ConfigWhereImpl.eq(SwiftConfigConstants.SegmentConfig.COLUMN_STORE_TYPE, storeType));
        }
        SwiftSchema swiftSchema = segmentKey.getSwiftSchema();
        if (null != swiftSchema) {
            wheres.add(ConfigWhereImpl.eq("swiftSchema", swiftSchema));
        }
        try {
            return configSessionCreator.doTransactionIfNeed(new BaseTransactionWorker<Page<SegmentKey>>(false) {
                @Override
                public Page<SegmentKey> work(ConfigSession session) throws SQLException {
                    try {
                        final Set<String> segmentIds = new HashSet<String>();
                        for (SwiftSegmentLocationEntity bean : segmentLocationDao.find(session,
                                ConfigWhereImpl.eq("id.clusterId", SwiftProperty.getProperty().getClusterId()))) {
                            segmentIds.add(bean.getSegmentId());
                        }
                        if (segmentIds.isEmpty()) {
                            return new Page<SegmentKey>();
                        }
                        wheres.add(ConfigWhereImpl.in("id", segmentIds));
                        return swiftSegmentDao.findPage(session, page, size, new Order[]{OrderImpl.asc("id"), OrderImpl.asc("segmentOrder")}, wheres.toArray(new ConfigWhere[0]));
                    } catch (Throwable e) {
                        throw new SQLException(e);
                    }
                }
            });
        } catch (SQLException e) {
            return new Page<SegmentKey>();
        }
    }

    @Override
    public Map<SourceKey, List<SegmentKey>> getOwnSegments() {
        return getOwnSegments(SwiftProperty.getProperty().getClusterId());
    }

    @Override
    public Map<SourceKey, List<SegmentKey>> getOwnSegments(final String clusterId) {
        try {
            return configSessionCreator.doTransactionIfNeed(new BaseTransactionWorker<Map<SourceKey, List<SegmentKey>>>(false) {
                @Override
                public Map<SourceKey, List<SegmentKey>> work(final ConfigSession session) throws SQLException {
                    final Set<String> segmentIds = new HashSet<String>();
                    try {
                        for (SwiftSegmentLocationEntity item : segmentLocationDao.findByClusterId(session, clusterId)) {
                            segmentIds.add(item.getSegmentId());
                        }
                        if (segmentIds.isEmpty()) {
                            return Collections.emptyMap();
                        }
                        Map<SourceKey, List<SegmentKey>> result = swiftSegmentDao.findSegmentKeyWithSourceKey(session, ConfigWhereImpl.in("id", segmentIds));
                        for (SegmentContainer value : SegmentContainer.values()) {
                            value.register(result);
                        }
                        return result;
                    } catch (Throwable e) {
                        if (e instanceof SQLException) {
                            throw (SQLException) e;
                        }
                        throw new SQLException(e);
                    }
                }
            });
        } catch (SQLException e) {
            return Collections.emptyMap();
        }
    }

    @Override
    public boolean updateSegmentTable(final Map<String, Set<SegmentKey>> segmentTable) {
        try {
            return configSessionCreator.doTransactionIfNeed(new BaseTransactionWorker<Boolean>() {
                @Override
                public Boolean work(ConfigSession session) throws SQLException {
                    for (Map.Entry<String, Set<SegmentKey>> entry : segmentTable.entrySet()) {
                        for (SegmentKey segmentKey : entry.getValue()) {
                            SwiftSegmentLocationEntity locationEntity = new SwiftSegmentLocationEntity(entry.getKey(), segmentKey.getId(), segmentKey.getTable().getId());
                            segmentLocationDao.saveOrUpdate(session, locationEntity);
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

    @Override
    public List<SegmentKey> find(final ConfigWhere... criterion) {
        try {
            return configSessionCreator.doTransactionIfNeed(new BaseTransactionWorker<List<SegmentKey>>() {
                @Override
                public List<SegmentKey> work(final ConfigSession session) throws SQLException {

                    try {
                        final Set<String> segmentIds = new HashSet<String>();
                        for (SwiftSegmentLocationEntity bean : segmentLocationDao.find(session,
                                ConfigWhereImpl.eq("id.clusterId", SwiftProperty.getProperty().getClusterId()))) {
                            segmentIds.add(bean.getSegmentId());
                        }
                        if (segmentIds.isEmpty()) {
                            return Collections.emptyList();
                        }
                        List<ConfigWhere> criterionList = new ArrayList<ConfigWhere>();
                        criterionList.addAll(Arrays.asList(criterion));
                        criterionList.add(ConfigWhereImpl.in("id", segmentIds));
                        List<SegmentKey> result = swiftSegmentDao.find(session, criterionList.toArray(new ConfigWhere[0]));
                        for (SegmentContainer value : SegmentContainer.values()) {
                            value.register(result);
                        }
                        return result;
                    } catch (Throwable e) {
                        throw new SQLException(e);
                    }
                }

                @Override
                public boolean needTransaction() {
                    return false;
                }
            });
        } catch (SQLException e) {
            return Collections.emptyList();
        }
    }
}
