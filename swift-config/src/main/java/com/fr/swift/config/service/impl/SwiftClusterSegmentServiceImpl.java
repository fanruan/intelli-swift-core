package com.fr.swift.config.service.impl;

import com.fr.swift.config.bean.SegmentKeyBean;
import com.fr.swift.config.convert.hibernate.transaction.AbstractTransactionWorker;
import com.fr.swift.config.dao.SwiftSegmentLocationDao;
import com.fr.swift.config.entity.SwiftSegmentEntity;
import com.fr.swift.config.entity.SwiftSegmentLocationEntity;
import com.fr.swift.config.entity.key.SwiftSegLocationEntityId;
import com.fr.swift.config.service.SwiftClusterSegmentService;
import com.fr.swift.cube.io.Types;
import com.fr.swift.cube.io.Types.StoreType;
import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.segment.SegmentKey;
import com.fr.swift.source.SourceKey;
import com.fr.swift.structure.Pair;
import com.fr.swift.util.Crasher;
import com.fr.third.org.hibernate.Session;
import com.fr.third.org.hibernate.criterion.Criterion;
import com.fr.third.org.hibernate.criterion.Restrictions;
import com.fr.third.springframework.beans.factory.annotation.Autowired;
import com.fr.third.springframework.stereotype.Service;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * @author yee
 * @date 2018/6/7
 */
@Service("swiftClusterSegmentService")
public class SwiftClusterSegmentServiceImpl extends AbstractSegmentService implements SwiftClusterSegmentService {
    @Autowired
    private SwiftSegmentLocationDao segmentLocationDao;

    private String clusterId;

    @Override
    public void setClusterId(String clusterId) {
        this.clusterId = clusterId;
    }

    @Override
    public boolean addSegments(final List<SegmentKey> segments) {
        try {
            return transactionManager.doTransactionIfNeed(new AbstractTransactionWorker<Boolean>() {

                @Override
                public Boolean work(Session session) throws SQLException {
                    return addSegmentsWithoutTransaction(session, segments);
                }
            });
        } catch (SQLException e) {
            SwiftLoggers.getLogger().error("add segment error! ", e);
        }
        return false;
    }

    @Override
    public boolean removeSegments(final String... sourceKey) {
        try {
            return transactionManager.doTransactionIfNeed(new AbstractTransactionWorker<Boolean>() {
                @Override
                public Boolean work(Session session) throws SQLException {
                    for (String key : sourceKey) {
                        segmentLocationDao.deleteBySourceKey(session, key);
                        swiftSegmentDao.deleteBySourceKey(session, key);
                    }
                    return true;
                }
            });
        } catch (SQLException e) {
            SwiftLoggers.getLogger().error("remove segment error! ", e);
        }
        return false;
    }

    @Override
    public boolean removeSegments(final List<SegmentKey> segmentKeys) {
        try {
            if (null == segmentKeys) {
                return false;
            }
            return transactionManager.doTransactionIfNeed(new AbstractTransactionWorker<Boolean>() {
                @Override
                public Boolean work(Session session) throws SQLException {
                    try {
                        for (SegmentKey segmentKey : segmentKeys) {
                            List<SwiftSegmentLocationEntity> list = segmentLocationDao.findBySegmentId(session, segmentKey.toString());
                            for (SwiftSegmentLocationEntity locationEntity : list) {
                                segmentLocationDao.deleteById(session, locationEntity.getId());
                            }
                            swiftSegmentDao.deleteById(session, segmentKey.toString());
                        }
                    } catch (Exception e) {
                        throw new SQLException(e);
                    }
                    return true;
                }
            });
        } catch (SQLException e) {
            SwiftLoggers.getLogger().error("remove segment error! ", e);
            return false;
        }
    }

    private boolean addSegmentsWithoutTransaction(Session session, List<SegmentKey> segments) throws SQLException {
        for (SegmentKey segment : segments) {
            SwiftSegmentEntity entity = new SwiftSegmentEntity(segment);
            swiftSegmentDao.addOrUpdateSwiftSegment(session, segment);
            SwiftSegmentLocationEntity locationEntity = new SwiftSegmentLocationEntity();
            SwiftSegLocationEntityId id = new SwiftSegLocationEntityId();
            id.setClusterId(clusterId);
            id.setSegmentId(entity.getId());
            locationEntity.setId(id);
            locationEntity.setSourceKey(segment.getTable().getId());
            segmentLocationDao.saveOrUpdate(session, locationEntity);
        }
        return true;

    }

    @Override
    public boolean updateSegments(final String sourceKey, final List<SegmentKey> segments) {
        try {
            return transactionManager.doTransactionIfNeed(new AbstractTransactionWorker<Boolean>() {
                @Override
                public Boolean work(Session session) throws SQLException {
                    segmentLocationDao.deleteBySourceKey(session, sourceKey);
                    swiftSegmentDao.deleteBySourceKey(session, sourceKey);
                    return addSegmentsWithoutTransaction(session, segments);
                }
            });
        } catch (SQLException e) {
            SwiftLoggers.getLogger().error("update segment error! ", e);
        }
        return false;
    }

    @Override
    public Map<String, List<SegmentKey>> getAllSegments() {
        return getAllSegments(transactionManager, swiftSegmentDao);
    }

    @Override
    public Map<String, List<SegmentKey>> getAllRealTimeSegments() {
        return getAllRealTimeSegments(transactionManager, swiftSegmentDao);
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
            return transactionManager.doTransactionIfNeed(new AbstractTransactionWorker<Boolean>() {
                @Override
                public Boolean work(Session session) throws SQLException {
                    if (null != swiftSegmentDao.select(session, segmentKey.toString())) {
                        SwiftSegLocationEntityId id = new SwiftSegLocationEntityId();
                        id.setClusterId(clusterId);
                        id.setSegmentId(segmentKey.toString());
                        return null != segmentLocationDao.select(session, id);
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
            return transactionManager.doTransactionIfNeed(new AbstractTransactionWorker<SegmentKey>() {
                @Override
                public SegmentKey work(Session session) throws SQLException {
                    SegmentKey segKey = SwiftClusterSegmentServiceImpl.super.tryAppendSegment(tableKey, storeType);
                    SwiftSegmentLocationEntity locationEntity = new SwiftSegmentLocationEntity();
                    SwiftSegLocationEntityId id = new SwiftSegLocationEntityId();
                    id.setClusterId(clusterId);
                    id.setSegmentId(new SwiftSegmentEntity(segKey).getId());
                    locationEntity.setId(id);
                    locationEntity.setSourceKey(segKey.getTable().getId());
                    segmentLocationDao.saveOrUpdate(session, locationEntity);
                    return segKey;
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
            return transactionManager.doTransactionIfNeed(new AbstractTransactionWorker<Map<String, List<SegmentKey>>>() {
                @Override
                public Map<String, List<SegmentKey>> work(Session session) throws SQLException {
                    Map<String, List<SegmentKey>> result = new HashMap<String, List<SegmentKey>>();
                    List<SwiftSegmentLocationEntity> list = segmentLocationDao.findByClusterId(session, clusterId);
                    for (SwiftSegmentLocationEntity entity : list) {
                        SegmentKeyBean bean = swiftSegmentDao.select(session, entity.getSegmentId()).convert();
                        if (!result.containsKey(bean.getSourceKey())) {
                            result.put(bean.getSourceKey(), new ArrayList<SegmentKey>());
                        }
                        result.get(bean.getSourceKey()).add(bean);
                    }
                    return result;
                }
            });

        } catch (Exception e) {
            SwiftLoggers.getLogger().error("Select segments error!", e);
        }
        return Collections.emptyMap();
    }

    @Override
    public Map<String, List<SegmentKey>> getOwnRealTimeSegments(final String clusterId) {
        try {
            return transactionManager.doTransactionIfNeed(new AbstractTransactionWorker<Map<String, List<SegmentKey>>>() {
                @Override
                public Map<String, List<SegmentKey>> work(Session session) throws SQLException {
                    Map<String, List<SegmentKey>> result = new HashMap<String, List<SegmentKey>>();
                    List<SwiftSegmentLocationEntity> list = segmentLocationDao.findByClusterId(session, clusterId);
                    for (SwiftSegmentLocationEntity entity : list) {
                        SegmentKeyBean bean = swiftSegmentDao.select(session, entity.getSegmentId()).convert();
                        if (bean.getStoreType() == Types.StoreType.MEMORY && !result.containsKey(bean.getSourceKey())) {
                            result.put(bean.getSourceKey(), new ArrayList<SegmentKey>());
                        }
                        result.get(bean.getSourceKey()).add(bean);
                    }
                    return result;
                }
            });

        } catch (Exception e) {
            SwiftLoggers.getLogger().error("Select segments error!", e);
        }
        return Collections.emptyMap();
    }

    @Override
    public Map<String, List<SegmentKey>> getClusterSegments() {

        try {
            return transactionManager.doTransactionIfNeed(new AbstractTransactionWorker<Map<String, List<SegmentKey>>>() {
                @Override
                public Map<String, List<SegmentKey>> work(Session session) throws SQLException {
                    Map<String, List<SegmentKey>> result = new HashMap<String, List<SegmentKey>>();
                    List<SwiftSegmentLocationEntity> list = segmentLocationDao.findAll(session);
                    for (SwiftSegmentLocationEntity entity : list) {
                        SegmentKeyBean bean = swiftSegmentDao.select(session, entity.getSegmentId()).convert();
                        if (!result.containsKey(entity.getClusterId())) {
                            result.put(entity.getClusterId(), new ArrayList<SegmentKey>());
                        }
                        result.get(entity.getClusterId()).add(bean);
                    }
                    return result;
                }
            });

        } catch (Exception e) {
            SwiftLoggers.getLogger().error("Select segments error!", e);
        }
        return Collections.emptyMap();
    }

    @Override
    public boolean updateSegmentTable(final Map<String, List<Pair<String, String>>> segmentTable) {
        try {
            return transactionManager.doTransactionIfNeed(new AbstractTransactionWorker<Boolean>() {
                @Override
                public Boolean work(Session session) throws SQLException {
                    Iterator<Map.Entry<String, List<Pair<String, String>>>> iterator = segmentTable.entrySet().iterator();
                    while (iterator.hasNext()) {
                        Map.Entry<String, List<Pair<String, String>>> entry = iterator.next();
                        String clusterId = entry.getKey();
                        List<Pair<String, String>> segmentKeys = entry.getValue();
                        for (Pair<String, String> segmentKey : segmentKeys) {
                            SwiftSegmentLocationEntity locationEntity = new SwiftSegmentLocationEntity();
                            SwiftSegLocationEntityId id = new SwiftSegLocationEntityId();
                            id.setClusterId(clusterId);
                            id.setSegmentId(segmentKey.getValue());
                            locationEntity.setId(id);
                            locationEntity.setSourceKey(segmentKey.getKey());
                            segmentLocationDao.saveOrUpdate(session, locationEntity);
                        }
                    }
                    return true;
                }
            });
        } catch (SQLException e) {
            SwiftLoggers.getLogger().error("Update table error!", e);
            return false;
        }
    }

    @Override
    public List<SegmentKey> find(final Criterion... criterion) {
        try {
            return transactionManager.doTransactionIfNeed(new AbstractTransactionWorker<List<SegmentKey>>() {
                @Override
                public List<SegmentKey> work(Session session) {
                    List<SwiftSegmentEntity> keys = swiftSegmentDao.find(session, criterion);
                    List<SegmentKey> result = new ArrayList<SegmentKey>();
                    if (null != keys) {
                        for (SwiftSegmentEntity key : keys) {
                            if (!segmentLocationDao.find(session, Restrictions.eq("id.clusterId", clusterId),
                                    Restrictions.eq("id.segmentId", key.convert().toString())).isEmpty()) {
                                result.add(key.convert());
                            }
                        }
                    }
                    return result;
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
