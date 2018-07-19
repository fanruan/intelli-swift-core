package com.fr.swift.config.service.impl;

import com.fr.swift.config.SwiftConfigConstants;
import com.fr.swift.config.bean.SegmentKeyBean;
import com.fr.swift.config.dao.SwiftSegmentDao;
import com.fr.swift.config.entity.SwiftSegmentEntity;
import com.fr.swift.config.hibernate.transaction.AbstractTransactionWorker;
import com.fr.swift.config.hibernate.transaction.HibernateTransactionManager;
import com.fr.swift.config.service.SwiftSegmentService;
import com.fr.swift.cube.io.Types;
import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.segment.SegmentKey;
import com.fr.third.org.hibernate.Session;
import com.fr.third.org.hibernate.criterion.Restrictions;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author yee
 * @date 2018/7/11
 */
public abstract class AbstractSegmentService implements SwiftSegmentService {

    public Map<String, List<SegmentKey>> getAllSegments(HibernateTransactionManager transactionManager, final SwiftSegmentDao swiftSegmentDao) {
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
            SwiftLoggers.getLogger().error("Select segments error!", e);
        }
        return Collections.emptyMap();
    }

    public Map<String, List<SegmentKey>> getAllRealTimeSegments(HibernateTransactionManager transactionManager, final SwiftSegmentDao swiftSegmentDao) {
        try {
            return transactionManager.doTransactionIfNeed(new AbstractTransactionWorker<Map<String, List<SegmentKey>>>() {
                @Override
                public Map<String, List<SegmentKey>> work(Session session) {
                    List<SwiftSegmentEntity> beans = swiftSegmentDao.find(session, Restrictions.eq(SwiftConfigConstants.SegmentConfig.COLUMN_STORE_TYPE, Types.StoreType.MEMORY));
                    Map<String, List<SegmentKey>> result = new HashMap<String, List<SegmentKey>>();
                    for (SwiftSegmentEntity bean : beans) {
                        SegmentKeyBean keyBean = bean.convert();
                        if (!result.containsKey(keyBean.getSourceKey())) {
                            result.put(keyBean.getSourceKey(), new ArrayList<SegmentKey>());
                        }
                        result.get(keyBean.getSourceKey()).add(keyBean);
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
    public boolean saveOrUpdate(SegmentKey obj) {
        return addSegments(Collections.singletonList(obj));
    }
}
