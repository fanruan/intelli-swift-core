package com.fr.swift.config.service.impl;

import com.fr.swift.config.bean.SegmentKeyBean;
import com.fr.swift.config.dao.SwiftSegmentDao;
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
    private static final SwiftLogger LOGGER = SwiftLoggers.getLogger(SwiftSegmentService.class);

    @Autowired
    private SwiftTransactionManager transactionManager;
    @Autowired
    private SwiftSegmentDao swiftSegmentDao;

    @Override
    public boolean addSegments(final List<SegmentKey> segments) {
        try {
            return (Boolean) transactionManager.doTransactionIfNeed(new AbstractTransactionWorker() {
                @Override
                public Object work() throws SQLException {
                    for (SegmentKey bean : segments) {
                        swiftSegmentDao.addOrUpdateSwiftSegment((SegmentKeyBean) bean);
                    }
                    return true;
                }


            });
        } catch (Exception e) {
            LOGGER.error("Add or update segments error!", e);
            return false;
        }
    }

    @Override
    public boolean removeSegments(final String... sourceKeys) {
        try {
            return (Boolean) transactionManager.doTransactionIfNeed(new AbstractTransactionWorker() {
                @Override
                public Object work() throws SQLException {
                    for (String sourceKey : sourceKeys) {
                        swiftSegmentDao.deleteBySourceKey(sourceKey);
                    }
                    return true;
                }


            });
        } catch (Exception e) {
            LOGGER.error("Remove segments error!", e);
            return false;
        }
    }

    @Override
    public boolean updateSegments(final String sourceKey, final List<SegmentKey> segments) {
        try {
            return (Boolean) transactionManager.doTransactionIfNeed(new AbstractTransactionWorker() {
                @Override
                public Object work() throws SQLException {
                    swiftSegmentDao.deleteBySourceKey(sourceKey);
                    for (SegmentKey segment : segments) {
                        swiftSegmentDao.addOrUpdateSwiftSegment((SegmentKeyBean) segment);
                    }
                    return true;
                }


            });
        } catch (Exception e) {
            LOGGER.error("Update segment failed!", e);
            return false;
        }
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
    public List<SegmentKey> getSegmentByKey(final String sourceKey) {
        try {
            return swiftSegmentDao.findBySourceKey(sourceKey);
        } catch (Exception e) {
            LOGGER.error("Select segments error!", e);
            return Collections.emptyList();
        }
    }

    @Override
    public boolean containsSegment(final SegmentKey segmentKey) {
        try {
            return (Boolean) transactionManager.doTransactionIfNeed(new AbstractTransactionWorker() {
                @Override
                public Object work() throws SQLException {
                    return null != swiftSegmentDao.select(((SegmentKeyBean) segmentKey).getId());
                }

                @Override
                public boolean needTransaction() {
                    return false;
                }
            });
        } catch (Exception e) {
            LOGGER.error("Update segment failed!", e);
            return false;
        }
    }
}
