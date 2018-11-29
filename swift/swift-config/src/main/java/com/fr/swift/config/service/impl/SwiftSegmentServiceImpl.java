package com.fr.swift.config.service.impl;

import com.fr.swift.config.SwiftConfigConstants;
import com.fr.swift.config.bean.SegLocationBean;
import com.fr.swift.config.bean.SegmentKeyBean;
import com.fr.swift.config.dao.SwiftSegmentDao;
import com.fr.swift.config.dao.SwiftSegmentLocationDao;
import com.fr.swift.config.oper.BaseTransactionWorker;
import com.fr.swift.config.oper.ConfigSession;
import com.fr.swift.config.oper.FindList;
import com.fr.swift.config.oper.RestrictionFactory;
import com.fr.swift.config.oper.TransactionManager;
import com.fr.swift.config.oper.impl.ConfigOrder;
import com.fr.swift.config.oper.impl.RestrictionFactoryImpl;
import com.fr.swift.config.service.SwiftClusterSegmentService;
import com.fr.swift.config.service.SwiftSegmentService;
import com.fr.swift.cube.io.Types.StoreType;
import com.fr.swift.db.impl.SwiftDatabase;
import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.segment.SegmentKey;
import com.fr.swift.segment.container.SegmentContainer;
import com.fr.swift.source.SourceKey;
import com.fr.swift.structure.Pair;
import com.fr.swift.util.Crasher;
import com.fr.third.org.hibernate.NonUniqueObjectException;
import com.fr.third.org.hibernate.exception.ConstraintViolationException;
import com.fr.third.springframework.beans.factory.annotation.Autowired;
import com.fr.third.springframework.stereotype.Service;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author yee
 * @date 2018/6/7
 */
@Service("swiftClusterSegmentService")
public class SwiftSegmentServiceImpl implements SwiftClusterSegmentService, SwiftSegmentService {
    @Autowired
    private SwiftSegmentLocationDao segmentLocationDao;

    private String clusterId = "LOCAL";
    @Autowired
    private TransactionManager transactionManager;

    @Autowired
    private SwiftSegmentDao swiftSegmentDao;

    private RestrictionFactory factory = RestrictionFactoryImpl.INSTANCE;

    public Map<String, List<SegmentKey>> getAllSegments(final SwiftSegmentDao swiftSegmentDao) {
        try {
            return transactionManager.doTransactionIfNeed(new BaseTransactionWorker<Map<String, List<SegmentKey>>>(false) {
                @Override
                public Map<String, List<SegmentKey>> work(ConfigSession session) throws SQLException {

                    final Map<String, List<SegmentKey>> result = new HashMap<String, List<SegmentKey>>();
                    try {
                        swiftSegmentDao.findAll(session).forEach(new FindList.Each<SegmentKeyBean>() {
                            @Override
                            public void each(int idx, SegmentKeyBean keyBean) throws Exception {
                                if (!result.containsKey(keyBean.getTable().getId())) {
                                    result.put(keyBean.getTable().getId(), new ArrayList<SegmentKey>());
                                }
                                result.get(keyBean.getTable().getId()).add(keyBean);
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

    public Map<String, List<SegmentKey>> getAllRealTimeSegments(final SwiftSegmentDao swiftSegmentDao) {
        try {
            return transactionManager.doTransactionIfNeed(new BaseTransactionWorker<Map<String, List<SegmentKey>>>(false) {
                @Override
                public Map<String, List<SegmentKey>> work(ConfigSession session) {
                    final Map<String, List<SegmentKey>> result = new HashMap<String, List<SegmentKey>>();
                    try {
                        swiftSegmentDao.find(session, factory.eq(SwiftConfigConstants.SegmentConfig.COLUMN_STORE_TYPE, StoreType.MEMORY)).forEach(new FindList.Each<SegmentKeyBean>() {
                            @Override
                            public void each(int idx, SegmentKeyBean bean) {
                                if (!result.containsKey(bean.getTable().getId())) {
                                    result.put(bean.getTable().getId(), new ArrayList<SegmentKey>());
                                }
                                result.get(bean.getTable().getId()).add(bean);
                            }
                        });
                    } catch (Exception e) {
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
                public boolean needTransaction() {
                    return true;
                }

                @Override
                public Void work(final ConfigSession session) throws SQLException {
                    try {
                        FindList<SegLocationBean> locations = segmentLocationDao.findAll(session);
                        if (locations.isEmpty()) {
                            swiftSegmentDao.findAll(session).forEach(new FindList.Each<SegmentKeyBean>() {
                                @Override
                                public void each(int idx, SegmentKeyBean segmentKey) throws SQLException {
                                    SegLocationBean bean = new SegLocationBean("LOCAL", segmentKey.getId(), segmentKey.getTable().getId());
                                    segmentLocationDao.saveOrUpdate(session, bean);
                                }
                            });
                        }
                        return null;
                    } catch (Exception e) {
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
    public Map<String, List<SegmentKey>> getNotEnoughSegments(final Set<String> clusterIds, final int lessCount) {
        try {
            return transactionManager.doTransactionIfNeed(new BaseTransactionWorker<Map<String, List<SegmentKey>>>() {
                @Override
                public Map<String, List<SegmentKey>> work(final ConfigSession session) throws SQLException {
                    final Map<String, List<SegmentKey>> result = new HashMap<String, List<SegmentKey>>();
                    try {
                        swiftSegmentDao.findAll(session).forEach(new FindList.Each<SegmentKeyBean>() {
                            @Override
                            public void each(int idx, SegmentKeyBean segmentKey) throws Exception {
                                String sourceKey = segmentKey.getTable().getId();
                                if (!result.containsKey(sourceKey)) {
                                    result.put(sourceKey, new ArrayList<SegmentKey>());
                                }
                                FindList<SegLocationBean> entities = segmentLocationDao.find(
                                        session, factory.in("id.clusterId", clusterIds),
                                        factory.eq("id.segmentId", segmentKey.toString()));
                                if (entities.size() < lessCount) {
                                    result.get(sourceKey).add(segmentKey);
                                }
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
        } catch (SQLException e) {
            SwiftLoggers.getLogger().warn("getNotEnoughSegments error, return empty");
        }
        return Collections.emptyMap();
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
                            segmentLocationDao.findBySegmentId(session, segmentKey.toString()).forEach(new FindList.Each<SegLocationBean>() {
                                @Override
                                public void each(int idx, SegLocationBean item) throws Exception {
                                    segmentLocationDao.delete(session, item);
                                }
                            });
                            swiftSegmentDao.deleteById(session, segmentKey.toString());
                            SegmentContainer.NORMAL.remove(segmentKey);
                        }
                    } catch (Exception e) {
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
    public Map<String, List<SegmentKey>> getAllSegments() {
        return getAllSegments(swiftSegmentDao);
    }

    @Override
    public Map<String, List<SegmentKey>> getAllRealTimeSegments() {
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

                    List<SegmentKeyBean> entities = swiftSegmentDao.find(session,
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
                        } catch (ConstraintViolationException e) {
                            appendOrder++;
                        } catch (NonUniqueObjectException e) {
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
    public Map<String, List<SegmentKey>> getOwnSegments() {
        return getOwnSegments(clusterId);
    }

    @Override
    public Map<String, List<SegmentKey>> getOwnSegments(final String clusterId) {
        try {
            return transactionManager.doTransactionIfNeed(new BaseTransactionWorker<Map<String, List<SegmentKey>>>() {
                @Override
                public Map<String, List<SegmentKey>> work(final ConfigSession session) throws SQLException {
                    final Map<String, List<SegmentKey>> result = new HashMap<String, List<SegmentKey>>();
                    try {
                        segmentLocationDao.findByClusterId(session, clusterId).forEach(new FindList.Each<SegLocationBean>() {
                            @Override
                            public void each(int idx, SegLocationBean item) throws Exception {
                                SegmentKey segmentEntity = swiftSegmentDao.select(session, item.getSegmentId());
                                if (null != segmentEntity) {
                                    if (!result.containsKey(segmentEntity.getTable().getId())) {
                                        result.put(segmentEntity.getTable().getId(), new ArrayList<SegmentKey>());
                                    }
                                    result.get(segmentEntity.getTable().getId()).add(segmentEntity);
                                }
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
        } catch (SQLException e) {
            return Crasher.crash(e);
        }
    }

    @Override
    public Map<String, List<SegmentKey>> getOwnRealTimeSegments(final String clusterId) {
        try {
            return transactionManager.doTransactionIfNeed(new BaseTransactionWorker<Map<String, List<SegmentKey>>>() {
                @Override
                public Map<String, List<SegmentKey>> work(final ConfigSession session) throws SQLException {
                    final Map<String, List<SegmentKey>> result = new HashMap<String, List<SegmentKey>>();
                    try {
                        segmentLocationDao.findByClusterId(session, clusterId).forEach(new FindList.Each<SegLocationBean>() {
                            @Override
                            public void each(int idx, SegLocationBean entity) throws Exception {
                                SegmentKey bean = swiftSegmentDao.select(session, entity.getSegmentId());
                                if (bean.getStoreType().isTransient() && !result.containsKey(bean.getTable().getId())) {
                                    result.put(bean.getTable().getId(), new ArrayList<SegmentKey>());
                                }
                                result.get(bean.getTable().getId()).add(bean);
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

    @Override
    public Map<String, List<SegmentKey>> getClusterSegments() {

        try {
            return transactionManager.doTransactionIfNeed(new BaseTransactionWorker<Map<String, List<SegmentKey>>>() {
                @Override
                public Map<String, List<SegmentKey>> work(final ConfigSession session) throws SQLException {
                    final Map<String, List<SegmentKey>> result = new HashMap<String, List<SegmentKey>>();
                    try {
                        segmentLocationDao.findAll(session).forEach(new FindList.Each<SegLocationBean>() {
                            @Override
                            public void each(int idx, SegLocationBean item) throws Exception {
                                SegmentKey bean = swiftSegmentDao.select(session, item.getSegmentId());
                                if (!result.containsKey(item.getClusterId())) {
                                    result.put(item.getClusterId(), new ArrayList<SegmentKey>());
                                }
                                result.get(item.getClusterId()).add(bean);
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

    @Override
    public boolean updateSegmentTable(final Map<String, List<Pair<String, String>>> segmentTable) {
        try {
            return transactionManager.doTransactionIfNeed(new BaseTransactionWorker<Boolean>() {
                @Override
                public Boolean work(ConfigSession session) throws SQLException {
                    Iterator<Map.Entry<String, List<Pair<String, String>>>> iterator = segmentTable.entrySet().iterator();
                    while (iterator.hasNext()) {
                        Map.Entry<String, List<Pair<String, String>>> entry = iterator.next();
                        String clusterId = entry.getKey();
                        List<Pair<String, String>> segmentKeys = entry.getValue();
                        for (Pair<String, String> segmentKey : segmentKeys) {
                            SegLocationBean locationEntity = new SegLocationBean(clusterId, segmentKey.getValue(), segmentKey.getKey());
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
                        return new ArrayList<SegmentKey>(swiftSegmentDao.find(session, criterion).forEach(new FindList.Each<SegmentKeyBean>() {
                            @Override
                            public void each(int idx, SegmentKeyBean item) throws Exception {
                                if (!segmentLocationDao.find(session, factory.eq("id.clusterId", clusterId),
                                        factory.eq("id.segmentId", item.getId())).isEmpty()) {
                                }
                            }
                        }));
                    } catch (Exception e) {
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
