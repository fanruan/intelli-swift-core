package com.fr.swift.config.service.impl;

import com.fr.swift.config.SwiftConfigConstants;
import com.fr.swift.config.SwiftConfigConstants.SegmentConfig;
import com.fr.swift.config.bean.SegmentKeyBean;
import com.fr.swift.config.dao.SwiftSegmentDao;
import com.fr.swift.config.entity.SwiftSegmentEntity;
import com.fr.swift.config.hibernate.transaction.AbstractTransactionWorker;
import com.fr.swift.config.hibernate.transaction.HibernateTransactionManager;
import com.fr.swift.config.hibernate.transaction.HibernateTransactionManager.HibernateWorker;
import com.fr.swift.config.service.SwiftSegmentService;
import com.fr.swift.cube.io.Types;
import com.fr.swift.cube.io.Types.StoreType;
import com.fr.swift.db.impl.SwiftDatabase;
import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.segment.SegmentKey;
import com.fr.swift.source.SourceKey;
import com.fr.swift.util.Crasher;
import com.fr.third.org.hibernate.NonUniqueObjectException;
import com.fr.third.org.hibernate.Session;
import com.fr.third.org.hibernate.criterion.Order;
import com.fr.third.org.hibernate.criterion.Restrictions;
import com.fr.third.org.hibernate.exception.ConstraintViolationException;
import com.fr.third.springframework.beans.factory.annotation.Autowired;

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
    @Autowired
    HibernateTransactionManager transactionManager;

    @Autowired
    SwiftSegmentDao swiftSegmentDao;

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

    @Override
    public SegmentKey tryAppendSegment(final SourceKey tableKey, final StoreType storeType) {
        try {
            return transactionManager.doTransactionIfNeed(new HibernateWorker<SegmentKey>() {
                @Override
                public boolean needTransaction() {
                    return false;
                }

                @Override
                public SegmentKey work(Session session) {
                    List<SwiftSegmentEntity> entities = swiftSegmentDao.find(session,
                            new Order[]{Order.desc(SegmentConfig.COLUMN_SEGMENT_ORDER)},
                            Restrictions.eq(SegmentConfig.COLUMN_SEGMENT_OWNER, tableKey.getId()));

                    int appendOrder;
                    if (entities.isEmpty()) {
                        appendOrder = 0;
                    } else {
                        appendOrder = entities.get(0).getSegmentOrder() + 1;
                    }

                    do {
                        SwiftSegmentEntity segKeyEntity = new SwiftSegmentEntity(tableKey, appendOrder, storeType,
                                SwiftDatabase.getInstance().getTable(tableKey).getMetadata().getSwiftDatabase());
                        try {
                            swiftSegmentDao.persist(session, segKeyEntity);
                            session.flush();
                            return segKeyEntity.convert();
                        } catch (ConstraintViolationException e) {
                            appendOrder++;
                        } catch (NonUniqueObjectException e) {
                            appendOrder++;
                        }
                    } while (true);
                }
            });
        } catch (Exception e) {
            return Crasher.crash(e);
        }
    }
}
