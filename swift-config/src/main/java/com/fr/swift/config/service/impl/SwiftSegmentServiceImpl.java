package com.fr.swift.config.service.impl;

import com.fr.swift.config.bean.SegmentKeyBean;
import com.fr.swift.config.dao.SwiftSegmentDao;
import com.fr.swift.config.hibernate.transaction.AbstractTransactionWorker;
import com.fr.swift.config.hibernate.transaction.HibernateTransactionManager;
import com.fr.swift.config.service.SwiftSegmentService;
import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.segment.SegmentKey;
import com.fr.third.org.hibernate.Session;
import com.fr.third.springframework.beans.factory.annotation.Autowired;
import com.fr.third.springframework.stereotype.Service;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author yee
 * @date 2018/6/6
 */
@Service("swiftSegmentService")
public class SwiftSegmentServiceImpl implements SwiftSegmentService {

    @Autowired
    private HibernateTransactionManager transactionManager;
    @Autowired
    private SwiftSegmentDao swiftSegmentDao;

    @Override
    public boolean addSegments(final List<SegmentKey> segments) {
        try {
            return transactionManager.doTransactionIfNeed(new AbstractTransactionWorker<Boolean>() {
                @Override
                public Boolean work(Session session) throws SQLException {
                    for (SegmentKey bean : segments) {
                        swiftSegmentDao.addOrUpdateSwiftSegment(session, (SegmentKeyBean) bean);
                    }
                    return true;
                }


            });
        } catch (Exception e) {
            SwiftLoggers.getLogger().error("Add or update segments error!", e);
            return false;
        }
    }

    @Override
    public boolean removeSegments(final String... sourceKeys) {
        try {
            return transactionManager.doTransactionIfNeed(new AbstractTransactionWorker<Boolean>() {
                @Override
                public Boolean work(Session session) throws SQLException {
                    for (String sourceKey : sourceKeys) {
                        swiftSegmentDao.deleteBySourceKey(session, sourceKey);
                    }
                    return true;
                }


            });
        } catch (Exception e) {
            SwiftLoggers.getLogger().error("Remove segments error!", e);
            return false;
        }
    }

    @Override
    public boolean updateSegments(final String sourceKey, final List<SegmentKey> segments) {
        try {
            return transactionManager.doTransactionIfNeed(new AbstractTransactionWorker<Boolean>() {
                @Override
                public Boolean work(Session session) throws SQLException {
                    swiftSegmentDao.deleteBySourceKey(session, sourceKey);
                    for (SegmentKey segment : segments) {
                        swiftSegmentDao.addOrUpdateSwiftSegment(session, (SegmentKeyBean) segment);
                    }
                    return true;
                }


            });
        } catch (Exception e) {
            SwiftLoggers.getLogger().error("Update segment failed!", e);
            return false;
        }
    }

    @Override
    public Map<String, List<SegmentKey>> getAllSegments() {

        try {
            return transactionManager.doTransactionIfNeed(new AbstractTransactionWorker<Map<String, List<SegmentKey>>>() {
                @Override
                public Map<String, List<SegmentKey>> work(Session session) {
                    Map<String, List<SegmentKey>> result = new HashMap<String, List<SegmentKey>>();
                    List<SegmentKey> beans = swiftSegmentDao.findAll(session);
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
            SwiftLoggers.getLogger().error("Select segments error!", e);
        }
        return Collections.emptyMap();
    }

    @Override
    public List<SegmentKey> getSegmentByKey(final String sourceKey) {
        try {
            return transactionManager.doTransactionIfNeed(new AbstractTransactionWorker<List<SegmentKey>>() {
                @Override
                public List<SegmentKey> work(Session session) {
                    return swiftSegmentDao.findBySourceKey(session, sourceKey);
                }

                @Override
                public boolean needTransaction() {
                    return false;
                }
            });
        } catch (Exception e) {
            SwiftLoggers.getLogger().error("Select segments error!", e);
            return Collections.emptyList();
        }
    }

    @Override
    public boolean containsSegment(final SegmentKey segmentKey) {
        try {
            return transactionManager.doTransactionIfNeed(new AbstractTransactionWorker<Boolean>() {
                @Override
                public Boolean work(Session session) {
                    List<SegmentKey> list = swiftSegmentDao.selectSelective(session, segmentKey);
                    return null != list && !list.isEmpty();
                }

                @Override
                public boolean needTransaction() {
                    return false;
                }
            });
        } catch (Exception e) {
            SwiftLoggers.getLogger().error("Update segment failed!", e);
            return false;
        }
    }
}
