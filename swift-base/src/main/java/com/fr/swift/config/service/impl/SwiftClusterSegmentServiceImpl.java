package com.fr.swift.config.service.impl;

import com.fr.swift.config.bean.SegmentKeyBean;
import com.fr.swift.config.bean.SwiftServiceInfoBean;
import com.fr.swift.config.dao.SwiftSegmentDao;
import com.fr.swift.config.dao.SwiftServiceInfoDao;
import com.fr.swift.config.entity.SwiftServiceInfoEntity;
import com.fr.swift.config.service.SwiftClusterSegmentService;
import com.fr.swift.config.service.SwiftSegmentService;
import com.fr.swift.config.transaction.AbstractTransactionWorker;
import com.fr.swift.config.transaction.SwiftTransactionManager;
import com.fr.swift.log.SwiftLogger;
import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.segment.SegmentKey;
import com.fr.third.springframework.beans.factory.annotation.Autowired;
import com.fr.third.springframework.stereotype.Service;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
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
    private SwiftTransactionManager transactionManager;
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
            return (Boolean) transactionManager.doTransactionIfNeed(new AbstractTransactionWorker() {
                @Override
                public Object work() throws SQLException {
                    return addSegmentsWithoutTransaction(segments);
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
            return (Boolean) transactionManager.doTransactionIfNeed(new AbstractTransactionWorker() {
                @Override
                public Object work() throws SQLException {

                    List<SegmentKey> list = new ArrayList<SegmentKey>();
                    for (String key : sourceKey) {
                        list.addAll(swiftSegmentDao.findBySourceKey(key));
                        swiftSegmentDao.deleteBySourceKey(key);
                    }
                    for (SegmentKey bean : list) {
                        SegmentKeyBean keyBean = (SegmentKeyBean) bean;
                        swiftServiceInfoDao.deleteByServiceInfo(keyBean.getId());
                    }
                    return true;
                }
            });
        } catch (SQLException e) {
            LOGGER.error("remove segment error! ", e);
        }
        return false;
    }

    private boolean addSegmentsWithoutTransaction(List<SegmentKey> segments) throws SQLException {
        for (SegmentKey segment : segments) {
            SegmentKeyBean bean = (SegmentKeyBean) segment;
            swiftSegmentDao.addOrUpdateSwiftSegment(bean);
            SwiftServiceInfoEntity entity = new SwiftServiceInfoBean(SEGMENT, clusterId, bean.getId(), false).convert();
            entity.setId(entity.getId() + segment.getOrder());
            swiftServiceInfoDao.saveOrUpdate(entity);
        }
        return true;

    }

    @Override
    public boolean updateSegments(final String sourceKey, final List<SegmentKey> segments) {
        try {
            return (Boolean) transactionManager.doTransactionIfNeed(new AbstractTransactionWorker() {
                @Override
                public Object work() throws SQLException {
                    List<SegmentKey> list = swiftSegmentDao.findBySourceKey(sourceKey);
                    swiftSegmentDao.deleteBySourceKey(sourceKey);
                    for (SegmentKey bean : list) {
                        SegmentKeyBean keyBean = (SegmentKeyBean) bean;
                        swiftServiceInfoDao.deleteByServiceInfo(keyBean.getId());
                    }
                    return addSegmentsWithoutTransaction(segments);
                }
            });
        } catch (SQLException e) {
            LOGGER.error("update segment error! ", e);
        }
        return false;
    }

    @Override
    public Map<String, List<SegmentKey>> getAllSegments() {
        Map<String, List<SegmentKey>> result = new HashMap<String, List<SegmentKey>>();
        try {
            List<SegmentKey> beans = swiftSegmentDao.findAll();
            for (SegmentKey bean : beans) {
                SegmentKeyBean keyBean = (SegmentKeyBean) bean;
                if (!result.containsKey(keyBean.getSourceKey())) {
                    result.put(keyBean.getSourceKey(), new ArrayList<SegmentKey>());
                }
                result.get(keyBean.getSourceKey()).add(bean);
            }
        } catch (Exception e) {
            LOGGER.error("Select segments error!", e);
        }
        return result;
    }

    @Override
    public List<SegmentKey> getSegmentByKey(String sourceKey) {
        return getOwnSegments().get(sourceKey);
    }

    @Override
    public boolean containsSegment(SegmentKey segmentKey) {
        try {
            List<SwiftServiceInfoEntity> list = swiftServiceInfoDao.getServiceInfoBySelective(new SwiftServiceInfoBean(SEGMENT, clusterId, segmentKey.toString(), false));
            return !list.isEmpty();
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public Map<String, List<SegmentKey>> getOwnSegments() {
        return getOwnSegments(clusterId);
    }

    @Override
    public Map<String, List<SegmentKey>> getOwnSegments(String clusterId) {
        Map<String, List<SegmentKey>> result = new HashMap<String, List<SegmentKey>>();
        try {
            List<SwiftServiceInfoEntity> list = swiftServiceInfoDao.getServiceInfoBySelective(new SwiftServiceInfoBean(SEGMENT, clusterId, null, false));
            for (SwiftServiceInfoEntity entity : list) {
                SegmentKeyBean bean = swiftSegmentDao.select(entity.getServiceInfo()).convert();
                if (!result.containsKey(bean.getSourceKey())) {
                    result.put(bean.getSourceKey(), new ArrayList<SegmentKey>());
                }
                result.get(bean.getSourceKey()).add(bean);
            }
        } catch (Exception e) {
            LOGGER.error("Select segments error!", e);
        }
        return result;
    }

    @Override
    public Map<String, List<SegmentKey>> getClusterSegments() {
        Map<String, List<SegmentKey>> result = new HashMap<String, List<SegmentKey>>();
        try {
            List<SwiftServiceInfoEntity> list = swiftServiceInfoDao.getServiceInfoBySelective(new SwiftServiceInfoBean(SEGMENT, null, null, false));
            for (SwiftServiceInfoEntity entity : list) {
                SegmentKeyBean bean = swiftSegmentDao.select(entity.getServiceInfo()).convert();
                if (!result.containsKey(entity.getClusterId())) {
                    result.put(entity.getClusterId(), new ArrayList<SegmentKey>());
                }
                result.get(entity.getClusterId()).add(bean);
            }
        } catch (Exception e) {
            LOGGER.error("Select segments error!", e);
        }
        return result;
    }
}
