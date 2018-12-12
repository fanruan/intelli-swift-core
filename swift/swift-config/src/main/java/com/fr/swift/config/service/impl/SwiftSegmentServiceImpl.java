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
import com.fr.swift.config.oper.RestrictionFactory;
import com.fr.swift.config.oper.TransactionManager;
import com.fr.swift.config.oper.exception.SwiftConstraintViolationException;
import com.fr.swift.config.oper.exception.SwiftEntityExistsException;
import com.fr.swift.config.oper.exception.SwiftNonUniqueObjectException;
import com.fr.swift.config.oper.impl.ConfigOrder;
import com.fr.swift.config.oper.impl.RestrictionFactoryImpl;
import com.fr.swift.config.service.SwiftClusterSegmentService;
import com.fr.swift.config.service.SwiftSegmentService;
import com.fr.swift.converter.FindList;
import com.fr.swift.cube.io.Types.StoreType;
import com.fr.swift.db.impl.SwiftDatabase;
import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.segment.SegmentKey;
import com.fr.swift.segment.container.SegmentContainer;
import com.fr.swift.source.SourceKey;
import com.fr.swift.util.Crasher;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
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
    private String clusterId = "LOCAL";
    private TransactionManager transactionManager = SwiftContext.get().getBean(TransactionManager.class);
    private SwiftSegmentDao swiftSegmentDao = SwiftContext.get().getBean(SwiftSegmentDao.class);

    private RestrictionFactory factory = RestrictionFactoryImpl.INSTANCE;

    public Map<SourceKey, List<SegmentKey>> getAllSegments(final SwiftSegmentDao swiftSegmentDao) {
        try {
            return transactionManager.doTransactionIfNeed(new BaseTransactionWorker<Map<SourceKey, List<SegmentKey>>>(false) {
                @Override
                public Map<SourceKey, List<SegmentKey>> work(ConfigSession session) throws SQLException {

                    final Map<SourceKey, List<SegmentKey>> result = new HashMap<SourceKey, List<SegmentKey>>();
                    try {
                        swiftSegmentDao.findAll(session).forEach(new FindList.SimpleEach<SegmentKey>() {
                            @Override
                            public void each(int idx, SegmentKey keyBean) throws Exception {
                                if (!result.containsKey(keyBean.getTable())) {
                                    result.put(keyBean.getTable(), new ArrayList<SegmentKey>());
                                }
                                result.get(keyBean.getTable()).add(keyBean);
                            }
                        });
                    } catch (Exception e) {
                        if (e instanceof SQLException) {
                            throw (SQLException) e;
                        }
                        throw new SQLException(e);
                    }
                    return result;
                }
            });

        } catch (Exception e) {
            SwiftLoggers.getLogger().warn("Select segments error!", e);
        }
        return Collections.emptyMap();
    }

    public Map<SourceKey, List<SegmentKey>> getAllRealTimeSegments(final SwiftSegmentDao swiftSegmentDao) {
        try {
            return transactionManager.doTransactionIfNeed(new BaseTransactionWorker<Map<SourceKey, List<SegmentKey>>>(false) {
                @Override
                public Map<SourceKey, List<SegmentKey>> work(ConfigSession session) {
                    final Map<SourceKey, List<SegmentKey>> result = new HashMap<SourceKey, List<SegmentKey>>();
                    try {
                        swiftSegmentDao.find(session, factory.eq(SwiftConfigConstants.SegmentConfig.COLUMN_STORE_TYPE, StoreType.MEMORY)).forEach(new FindList.SimpleEach<SegmentKey>() {
                            @Override
                            public void each(int idx, SegmentKey bean) {
                                if (!result.containsKey(bean.getTable())) {
                                    result.put(bean.getTable(), new ArrayList<SegmentKey>());
                                }
                                result.get(bean.getTable()).add(bean);
                            }
                        });
                    } catch (Throwable e) {
                        SwiftLoggers.getLogger().warn("Select segments error!", e);
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
    public void setClusterId(String clusterId) {
        this.clusterId = clusterId;
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
                        SegmentContainer.NORMAL.remove(new SourceKey(key));
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
                        for (SegmentKey segmentKey : segmentKeys) {
                            segmentLocationDao.findBySegmentId(session, segmentKey.toString()).forEach(new FindList.SimpleEach<SegLocationBean>() {
                                @Override
                                public void each(int idx, SegLocationBean item) throws Exception {
                                    segmentLocationDao.delete(session, item);
                                }
                            });
                            swiftSegmentDao.deleteById(session, segmentKey.toString());
                            SegmentContainer.NORMAL.remove(segmentKey);
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
            SegLocationBean locationEntity = new SegLocationBean(clusterId, segment.getId(), segment.getTable().getId());
            segmentLocationDao.saveOrUpdate(session, locationEntity);
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
        return getAllSegments(swiftSegmentDao);
    }

    @Override
    public Map<SourceKey, List<SegmentKey>> getAllRealTimeSegments() {
        return getAllRealTimeSegments(swiftSegmentDao);
    }

    @Override
    public List<SegmentKey> getSegmentByKey(String sourceKey) {
        List<SegmentKey> result = getOwnSegments().get(sourceKey);
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
                        return !segmentLocationDao.find(session, factory.eq("id.clusterId", clusterId),
                                factory.eq("id.segmentKey", segmentKey.getId())).isEmpty();
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
                            new Object[]{ConfigOrder.desc(SwiftConfigConstants.SegmentConfig.COLUMN_SEGMENT_ORDER)},
                            factory.eq(SwiftConfigConstants.SegmentConfig.COLUMN_SEGMENT_OWNER, tableKey.getId())).list();

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
                            break;
                        } catch (SwiftConstraintViolationException e) {
                            appendOrder++;
                        } catch (SwiftNonUniqueObjectException e) {
                            appendOrder++;
                        } catch (SwiftEntityExistsException e) {
                            appendOrder++;
                        }
                    } while (true);
                    SegLocationBean locationEntity = new SegLocationBean(clusterId, segKeyEntity.getId(), segKeyEntity.getSourceKey());
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
        return getOwnSegments(clusterId);
    }

    @Override
    public Map<SourceKey, List<SegmentKey>> getOwnSegments(final String clusterId) {
        try {
            return transactionManager.doTransactionIfNeed(new BaseTransactionWorker<Map<SourceKey, List<SegmentKey>>>() {
                @Override
                public Map<SourceKey, List<SegmentKey>> work(final ConfigSession session) throws SQLException {
                    final Map<SourceKey, List<SegmentKey>> result = new HashMap<SourceKey, List<SegmentKey>>();
                    try {
                        segmentLocationDao.findByClusterId(session, clusterId).forEach(new FindList.SimpleEach<SegLocationBean>() {
                            @Override
                            public void each(int idx, SegLocationBean item) throws Exception {
                                SegmentKey segmentEntity = swiftSegmentDao.select(session, item.getSegmentId());
                                if (null != segmentEntity) {
                                    if (!result.containsKey(segmentEntity.getTable())) {
                                        result.put(segmentEntity.getTable(), new ArrayList<SegmentKey>());
                                    }
                                    result.get(segmentEntity.getTable()).add(segmentEntity);
                                }
                            }
                        });
                    } catch (Throwable e) {
                        if (e instanceof SQLException) {
                            throw (SQLException) e;
                        }
                        throw new SQLException(e);
                    }
                    return result;
                }
            });
        } catch (SQLException e) {
            return Crasher.crash(e);
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
                            SegLocationBean locationEntity = new SegLocationBean(clusterId, segmentKey.getTable().getId(), segmentKey.getId());
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
    public List<SegmentKey> find(final Object... criterion) {
        try {
            return transactionManager.doTransactionIfNeed(new BaseTransactionWorker<List<SegmentKey>>() {
                @Override
                public List<SegmentKey> work(final ConfigSession session) throws SQLException {
                    try {
                        return swiftSegmentDao.find(session, criterion).forEach(new FindList.SimpleEach<SegmentKey>() {
                            @Override
                            public void each(int idx, SegmentKey item) throws Exception {
                                if (!segmentLocationDao.find(session, factory.eq("id.clusterId", clusterId),
                                        factory.eq("id.segmentId", item.getId())).isEmpty()) {
                                }
                            }
                        });
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
