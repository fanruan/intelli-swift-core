package com.fr.swift.config.service.impl;

import com.fr.swift.SwiftContext;
import com.fr.swift.beans.annotation.SwiftBean;
import com.fr.swift.config.dao.SwiftSegmentBucketDao;
import com.fr.swift.config.dao.SwiftSegmentDao;
import com.fr.swift.config.dao.SwiftSegmentLocationDao;
import com.fr.swift.config.entity.SwiftSegmentBucket;
import com.fr.swift.config.entity.SwiftSegmentBucketElement;
import com.fr.swift.config.entity.SwiftSegmentLocationEntity;
import com.fr.swift.config.oper.BaseTransactionWorker;
import com.fr.swift.config.oper.ConfigSession;
import com.fr.swift.config.oper.TransactionManager;
import com.fr.swift.config.oper.impl.ConfigWhereImpl;
import com.fr.swift.config.service.SwiftSegmentBucketService;
import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.segment.SegmentKey;
import com.fr.swift.source.SourceKey;

import java.sql.SQLException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * @author lucifer
 * @date 2019-06-25
 * @description
 * @since advanced swift 1.0
 */
@SwiftBean
public class SwiftSegmentBucketServiceImpl implements SwiftSegmentBucketService {
    private SwiftSegmentBucketDao swiftSegmentBucketDao;
    private TransactionManager transactionManager;
    private SwiftSegmentDao swiftSegmentDao;
    private SwiftSegmentLocationDao locationDao;

    public SwiftSegmentBucketServiceImpl() {
        this.swiftSegmentBucketDao = SwiftContext.get().getBean(SwiftSegmentBucketDao.class);
        this.transactionManager = SwiftContext.get().getBean(TransactionManager.class);
        this.swiftSegmentDao = SwiftContext.get().getBean(SwiftSegmentDao.class);
        this.locationDao = SwiftContext.get().getBean(SwiftSegmentLocationDao.class);
    }

    @Override
    public SwiftSegmentBucket getBucketByTable(final SourceKey sourceKey) {
        try {
            return transactionManager.doTransactionIfNeed(new BaseTransactionWorker<SwiftSegmentBucket>() {
                @Override
                public SwiftSegmentBucket work(ConfigSession session) throws SQLException {
                    List<SwiftSegmentBucketElement> elementList = swiftSegmentBucketDao.find(session,
                            ConfigWhereImpl.eq("unionKey.sourceKey", sourceKey.getId()));
                    List<SwiftSegmentLocationEntity> locationEntities = locationDao.find(session, ConfigWhereImpl.eq("sourceKey", sourceKey.getId()));
                    Set<String> locationKeys = new HashSet<>();
                    for (SwiftSegmentLocationEntity locationEntity : locationEntities) {
                        locationKeys.add(locationEntity.getSegmentId());
                    }
                    Iterator<SwiftSegmentBucketElement> iterator = elementList.iterator();
                    while (iterator.hasNext()) {
                        SwiftSegmentBucketElement next = iterator.next();
                        if (!locationKeys.contains(next.getRealSegmentKey())) {
                            iterator.remove();
                        }
                    }
                    SwiftSegmentBucket swiftSegmentBucket = new SwiftSegmentBucket(sourceKey);
                    for (SwiftSegmentBucketElement bucketElement : elementList) {
                        SegmentKey segmentKey = swiftSegmentDao.select(session, bucketElement.getRealSegmentKey());
                        swiftSegmentBucket.put(bucketElement.getBucketIndex(), segmentKey);
                    }
                    return swiftSegmentBucket;
                }
            });
        } catch (Exception e) {
            SwiftLoggers.getLogger().error("getBucketByTable {} error!", sourceKey.getId(), e);
            return null;
        }
    }

    @Override
    public boolean saveElement(final SwiftSegmentBucketElement element) {
        try {
            return transactionManager.doTransactionIfNeed(new BaseTransactionWorker<Boolean>() {
                @Override
                public Boolean work(ConfigSession session) throws SQLException {
                    return swiftSegmentBucketDao.saveOrUpdate(session, element);
                }
            });
        } catch (Exception e) {
            SwiftLoggers.getLogger().error("saveElement {} error!", element.toString(), e);
            return false;
        }
    }
}
