package com.fr.swift.config.service.impl;

import com.fr.swift.SwiftContext;
import com.fr.swift.beans.annotation.SwiftBean;
import com.fr.swift.config.SwiftConfigConstants;
import com.fr.swift.config.bean.SegLocationBean;
import com.fr.swift.config.bean.SegmentKeyBean;
import com.fr.swift.config.dao.SwiftSegmentDao;
import com.fr.swift.config.dao.SwiftSegmentLocationDao;
import com.fr.swift.config.oper.BaseTransactionWorker;
import com.fr.swift.config.oper.ConfigSession;
import com.fr.swift.config.oper.ConfigWhere;
import com.fr.swift.config.oper.Order;
import com.fr.swift.config.oper.TransactionManager;
import com.fr.swift.config.oper.exception.SwiftConstraintViolationException;
import com.fr.swift.config.oper.exception.SwiftEntityExistsException;
import com.fr.swift.config.oper.exception.SwiftNonUniqueObjectException;
import com.fr.swift.config.oper.impl.ConfigWhereImpl;
import com.fr.swift.config.oper.impl.OrderImpl;
import com.fr.swift.config.service.SwiftClusterSegmentService;
import com.fr.swift.config.service.SwiftSegmentService;
import com.fr.swift.converter.FindList;
import com.fr.swift.cube.io.Types.StoreType;
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
    private TransactionManager transactionManager = SwiftContext.get().getBean(TransactionManager.class);
    private SwiftSegmentDao swiftSegmentDao = SwiftContext.get().getBean(SwiftSegmentDao.class);

    @Override
    public boolean saveOrUpdate(SegmentKey obj) {
        return addSegments(Collections.singletonList(obj));
    }

    @Override
    public void checkOldConfig() {
        try {
            transactionManager.doTransactionIfNeed(new BaseTransactionWorker() {
                @Override
                public Void work(final ConfigSession session) throws SQLException {
                    try {
                        FindList<SegLocationBean> locations = segmentLocationDao.findAll(session);
                        if (locations.isEmpty()) {
                            swiftSegmentDao.findAll(session).forEach(new FindList.SimpleEach<SegmentKey>() {
                                @Override
                                public void each(int idx, SegmentKey segmentKey) throws SQLException {
                                    SegLocationBean bean = new SegLocationBean("LOCAL", segmentKey.getId(), segmentKey.getTable().getId());
                                    segmentLocationDao.saveOrUpdate(session, bean);
                                }
                            });
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
            return transactionManager.doTransactionIfNeed(new BaseTransactionWorker<Boolean>() {

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
            return transactionManager.doTransactionIfNeed(new BaseTransactionWorker<Boolean>() {
                @Override
                public Boolean work(ConfigSession session) throws SQLException {
                    for (String key : sourceKey) {
                        segmentLocationDao.deleteBySourceKey(session, key);
                        swiftSegmentDao.deleteBySourceKey(session, key);
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
            return transactionManager.doTransactionIfNeed(new BaseTransactionWorker<Boolean>() {
                @Override
                public Boolean work(final ConfigSession session) throws SQLException {
                    try {
                        for (final SegmentKey segmentKey : segmentKeys) {
                            segmentLocationDao.findBySegmentId(session, segmentKey.toString()).justForEach(new FindList.ConvertEach() {
                                @Override
                                public Object forEach(int idx, Object item) throws Exception {
                                    session.delete(item);
                                    for (SegmentContainer value : SegmentContainer.values()) {
                                        value.remove(segmentKey);
                                    }
                                    return item;
                                }
                            });
                            swiftSegmentDao.deleteById(session, segmentKey.toString());
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

    private boolean addSegmentsWithoutTransaction(ConfigSession session, List<SegmentKey> segments) throws SQLException {
        for (SegmentKey segment : segments) {
            swiftSegmentDao.addOrUpdateSwiftSegment(session, segment);
            SegLocationBean locationEntity = new SegLocationBean(SwiftProperty.getProperty().getClusterId(), segment.getId(), segment.getTable().getId());
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
            return transactionManager.doTransactionIfNeed(new BaseTransactionWorker<Boolean>() {
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
            return transactionManager.doTransactionIfNeed(new BaseTransactionWorker<Map<SourceKey, List<SegmentKey>>>(false) {
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
            return transactionManager.doTransactionIfNeed(new BaseTransactionWorker<Boolean>() {
                @Override
                public Boolean work(ConfigSession session) throws SQLException {
                    if (null != swiftSegmentDao.select(session, segmentKey.toString())) {
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
            return transactionManager.doTransactionIfNeed(new BaseTransactionWorker<SegmentKey>() {
                @Override
                public SegmentKey work(ConfigSession session) throws SQLException {

                    List<SegmentKey> entities = swiftSegmentDao.find(session,
                            new Order[]{OrderImpl.desc(SwiftConfigConstants.SegmentConfig.COLUMN_SEGMENT_ORDER)},
                            ConfigWhereImpl.eq(SwiftConfigConstants.SegmentConfig.COLUMN_SEGMENT_OWNER, tableKey.getId())).list();

                    int appendOrder;
                    if (entities.isEmpty()) {
                        appendOrder = 0;
                    } else {
                        appendOrder = entities.get(0).getOrder() + 1;
                    }
                    SegmentKeyBean segKeyEntity = null;
                    do {
                        segKeyEntity = new SegmentKeyBean(tableKey, appendOrder, storeType,
                                SwiftDatabase.getInstance().getTable(tableKey).getMetadata().getSwiftDatabase());
                        try {
                            swiftSegmentDao.persist(session, segKeyEntity);
                            for (SegmentContainer value : SegmentContainer.values()) {
                                value.register(segKeyEntity);
                            }
                            break;
                        } catch (SwiftConstraintViolationException e) {
                            appendOrder++;
                        } catch (SwiftNonUniqueObjectException e) {
                            appendOrder++;
                        } catch (SwiftEntityExistsException e) {
                            appendOrder++;
                        }
                    } while (true);
                    SegLocationBean locationEntity = new SegLocationBean(SwiftProperty.getProperty().getClusterId(), segKeyEntity.getId(), segKeyEntity.getSourceKey());
                    locationEntity.setSourceKey(segKeyEntity.getTable().getId());
                    segmentLocationDao.saveOrUpdate(session, locationEntity);
                    return segKeyEntity;
                }
            });

        } catch (Exception e) {
            return Crasher.crash(e);
        }
    }

    @Override
    public Map<SourceKey, List<SegmentKey>> getOwnSegments() {
        return getOwnSegments(SwiftProperty.getProperty().getClusterId());
    }

    @Override
    public Map<SourceKey, List<SegmentKey>> getOwnSegments(final String clusterId) {
        try {
            return transactionManager.doTransactionIfNeed(new BaseTransactionWorker<Map<SourceKey, List<SegmentKey>>>(false) {
                @Override
                public Map<SourceKey, List<SegmentKey>> work(final ConfigSession session) throws SQLException {
                    final Set<String> segmentIds = new HashSet<String>();
                    try {
                        segmentLocationDao.findByClusterId(session, clusterId).forEach(new FindList.SimpleEach<SegLocationBean>() {
                            @Override
                            public void each(int idx, SegLocationBean item) throws Exception {
                                segmentIds.add(item.getSegmentId());
                            }
                        });
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
            return transactionManager.doTransactionIfNeed(new BaseTransactionWorker<Boolean>() {
                @Override
                public Boolean work(ConfigSession session) throws SQLException {
                    for (Map.Entry<String, Set<SegmentKey>> entry : segmentTable.entrySet()) {
                        for (SegmentKey segmentKey : entry.getValue()) {
                            SegLocationBean locationEntity = new SegLocationBean(entry.getKey(), segmentKey.getId(), segmentKey.getTable().getId());
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
            return transactionManager.doTransactionIfNeed(new BaseTransactionWorker<List<SegmentKey>>() {
                @Override
                public List<SegmentKey> work(final ConfigSession session) throws SQLException {

                    try {
                        final Set<String> segmentIds = new HashSet<String>();
                        segmentLocationDao.find(session,
                                ConfigWhereImpl.eq("id.clusterId", SwiftProperty.getProperty().getClusterId())).forEach(new FindList.SimpleEach<SegLocationBean>() {
                            @Override
                            protected void each(int idx, SegLocationBean bean) throws Exception {
                                segmentIds.add(bean.getSegmentId());
                            }
                        });
                        List<ConfigWhere> criterionList = new ArrayList<ConfigWhere>();
                        criterionList.addAll(Arrays.asList(criterion));
                        criterionList.add(ConfigWhereImpl.in("id", segmentIds));
                        List<SegmentKey> result = swiftSegmentDao.find(session, criterionList.toArray(new ConfigWhere[0])).list();
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
