package com.fr.swift.config.service.impl;

import com.fr.swift.config.bean.SegmentKeyBean;
import com.fr.swift.config.bean.SwiftServiceInfoBean;
import com.fr.swift.config.dao.SwiftSegmentDao;
import com.fr.swift.config.dao.SwiftServiceInfoDao;
import com.fr.swift.config.entity.SwiftServiceInfoEntity;
import com.fr.swift.config.hibernate.transaction.AbstractTransactionWorker;
import com.fr.swift.config.hibernate.transaction.HibernateTransactionManager;
import com.fr.swift.config.service.SwiftClusterSegmentService;
import com.fr.swift.config.service.SwiftSegmentService;
import com.fr.swift.log.SwiftLogger;
import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.segment.SegmentKey;
import com.fr.third.org.hibernate.Session;
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
public class SwiftClusterSegmentServiceImpl implements SwiftClusterSegmentService {
    private static final SwiftLogger LOGGER = SwiftLoggers.getLogger(SwiftSegmentService.class);
    private static final String SEGMENT = "SEGMENT";

    @Autowired
    private HibernateTransactionManager transactionManager;
    @Autowired
    private SwiftSegmentDao swiftSegmentDao;
    @Autowired
    private SwiftServiceInfoDao swiftServiceInfoDao;

    private String clusterId;

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
            LOGGER.error("add segment error! ", e);
        }
        return false;
    }

    @Override
    public boolean removeSegments(final String... sourceKey) {
        try {
            return transactionManager.doTransactionIfNeed(new AbstractTransactionWorker<Boolean>() {
                @Override
                public Boolean work(Session session) throws SQLException {

                    List<SegmentKey> list = new ArrayList<SegmentKey>();
                    for (String key : sourceKey) {
                        list.addAll(swiftSegmentDao.findBySourceKey(session, key));
                        swiftSegmentDao.deleteBySourceKey(session, key);
                    }
                    for (SegmentKey bean : list) {
                        SegmentKeyBean keyBean = (SegmentKeyBean) bean;
                        swiftServiceInfoDao.deleteByServiceInfo(session, keyBean.getId());
                    }
                    return true;
                }
            });
        } catch (SQLException e) {
            LOGGER.error("remove segment error! ", e);
        }
        return false;
    }

    private boolean addSegmentsWithoutTransaction(Session session, List<SegmentKey> segments) throws SQLException {
        for (SegmentKey segment : segments) {
            SegmentKeyBean bean = (SegmentKeyBean) segment;
            swiftSegmentDao.addOrUpdateSwiftSegment(session, bean);
            SwiftServiceInfoEntity entity = new SwiftServiceInfoBean(SEGMENT, clusterId, bean.getId(), false).convert();
            entity.setId(entity.getId() + segment.getOrder());
            swiftServiceInfoDao.saveOrUpdate(session, entity);
        }
        return true;

    }

    @Override
    public boolean updateSegments(final String sourceKey, final List<SegmentKey> segments) {
        try {
            return transactionManager.doTransactionIfNeed(new AbstractTransactionWorker<Boolean>() {
                @Override
                public Boolean work(Session session) throws SQLException {
                    List<SegmentKey> list = swiftSegmentDao.findBySourceKey(session, sourceKey);
                    swiftSegmentDao.deleteBySourceKey(session, sourceKey);
                    for (SegmentKey bean : list) {
                        SegmentKeyBean keyBean = (SegmentKeyBean) bean;
                        swiftServiceInfoDao.deleteByServiceInfo(session, keyBean.getId());
                    }
                    return addSegmentsWithoutTransaction(session, segments);
                }
            });
        } catch (SQLException e) {
            LOGGER.error("update segment error! ", e);
        }
        return false;
    }

    @Override
    public Map<String, List<SegmentKey>> getAllSegments() {

        try {
            return transactionManager.doTransactionIfNeed(new AbstractTransactionWorker<Map<String, List<SegmentKey>>>() {
                @Override
                public Map<String, List<SegmentKey>> work(Session session) {
                    List<SegmentKey> beans = swiftSegmentDao.findAll(session);
                    Map<String, List<SegmentKey>> result = new HashMap<String, List<SegmentKey>>();
                    for (SegmentKey bean : beans) {
                        SegmentKeyBean keyBean = (SegmentKeyBean) bean;
                        if (!result.containsKey(keyBean.getSourceKey())) {
                            result.put(keyBean.getSourceKey(), new ArrayList<SegmentKey>());
                        }
                        result.get(keyBean.getSourceKey()).add(bean);
                    }
                    return result;
                }

                @Override
                public boolean needTransaction() {
                    return false;
                }
            });

        } catch (Exception e) {
            LOGGER.error("Select segments error!", e);
        }
        return Collections.emptyMap();
    }

    @Override
    public List<SegmentKey> getSegmentByKey(String sourceKey) {
        return getOwnSegments().get(sourceKey);
    }

    @Override
    public boolean containsSegment(final SegmentKey segmentKey) {
        try {
            return transactionManager.doTransactionIfNeed(new AbstractTransactionWorker<Boolean>() {
                @Override
                public Boolean work(Session session) {
                    List<SwiftServiceInfoEntity> list =
                            swiftServiceInfoDao.getServiceInfoBySelective(session,
                                    new SwiftServiceInfoBean(SEGMENT, clusterId, segmentKey.toString(), false));
                    return !list.isEmpty();
                }
            });

        } catch (Exception e) {
            return false;
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
                    List<SwiftServiceInfoEntity> list = swiftServiceInfoDao.getServiceInfoBySelective(session, new SwiftServiceInfoBean(SEGMENT, clusterId, null, false));
                    for (SwiftServiceInfoEntity entity : list) {
                        SegmentKeyBean bean = swiftSegmentDao.select(session, entity.getServiceInfo()).convert();
                        if (!result.containsKey(bean.getSourceKey())) {
                            result.put(bean.getSourceKey(), new ArrayList<SegmentKey>());
                        }
                        result.get(bean.getSourceKey()).add(bean);
                    }
                    return result;
                }
            });

        } catch (Exception e) {
            LOGGER.error("Select segments error!", e);
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
                    List<SwiftServiceInfoEntity> list = swiftServiceInfoDao.getServiceInfoBySelective(session, new SwiftServiceInfoBean(SEGMENT, null, null, false));
                    for (SwiftServiceInfoEntity entity : list) {
                        SegmentKeyBean bean = swiftSegmentDao.select(session, entity.getServiceInfo()).convert();
                        if (!result.containsKey(entity.getClusterId())) {
                            result.put(entity.getClusterId(), new ArrayList<SegmentKey>());
                        }
                        result.get(entity.getClusterId()).add(bean);
                    }
                    return result;
                }
            });

        } catch (Exception e) {
            LOGGER.error("Select segments error!", e);
        }
        return Collections.emptyMap();
    }

    @Override
    public boolean updateSegmentTable(final Map<String, List<String>> segmentTable) {
        try {
            return transactionManager.doTransactionIfNeed(new AbstractTransactionWorker<Boolean>() {
                @Override
                public Boolean work(Session session) throws SQLException {
                    Iterator<Map.Entry<String, List<String>>> iterator = segmentTable.entrySet().iterator();
                    while (iterator.hasNext()) {
                        Map.Entry<String, List<String>> entry = iterator.next();
                        String clusterId = entry.getKey();
                        List<String> segmentKeys = entry.getValue();
                        for (String segmentKey : segmentKeys) {
                            SwiftServiceInfoEntity entity = new SwiftServiceInfoBean(SEGMENT, clusterId, segmentKey, false).convert();

                            entity.setId(entity.getId() + segmentKey.substring(segmentKey.lastIndexOf("@") + 1));
                            swiftServiceInfoDao.saveOrUpdate(session, entity);
                        }
                    }
                    return true;
                }
            });
        } catch (SQLException e) {
            LOGGER.error("Update table error!", e);
            return false;
        }
    }
}
