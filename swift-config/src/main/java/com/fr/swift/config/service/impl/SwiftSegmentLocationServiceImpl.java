package com.fr.swift.config.service.impl;

import com.fr.swift.config.dao.SwiftSegmentLocationDao;
import com.fr.swift.config.entity.SwiftSegmentLocationEntity;
import com.fr.swift.config.hibernate.transaction.AbstractTransactionWorker;
import com.fr.swift.config.hibernate.transaction.HibernateTransactionManager;
import com.fr.swift.config.service.SwiftSegmentLocationService;
import com.fr.swift.log.SwiftLoggers;
import com.fr.third.org.hibernate.Session;
import com.fr.third.org.hibernate.criterion.Criterion;
import com.fr.third.org.hibernate.criterion.Restrictions;
import com.fr.third.springframework.beans.factory.annotation.Autowired;
import com.fr.third.springframework.stereotype.Service;

import java.sql.SQLException;
import java.util.Collections;
import java.util.List;

/**
 * @author yee
 * @date 2018/7/24
 */
@Service
public class SwiftSegmentLocationServiceImpl implements SwiftSegmentLocationService {

    @Autowired
    private SwiftSegmentLocationDao segmentLocationDao;
    @Autowired
    private HibernateTransactionManager tx;

    @Override
    public boolean delete(final String table, final String clusterId) {
        try {
            return tx.doTransactionIfNeed(new AbstractTransactionWorker<Boolean>() {
                @Override
                public Boolean work(Session session) {
                    List<SwiftSegmentLocationEntity> list = segmentLocationDao.find(session,
                            Restrictions.eq("id.clusterId", clusterId),
                            Restrictions.eq("sourceKey", table));
                    for (SwiftSegmentLocationEntity locationEntity : list) {
                        session.delete(locationEntity);
                    }
                    return true;
                }
            });
        } catch (SQLException e) {
            SwiftLoggers.getLogger().error(e);
            return false;
        }
    }

    @Override
    public List<SwiftSegmentLocationEntity> find(final Criterion... criterion) {
        try {
            return tx.doTransactionIfNeed(new AbstractTransactionWorker<List<SwiftSegmentLocationEntity>>() {
                @Override
                public List<SwiftSegmentLocationEntity> work(Session session) {
                    return segmentLocationDao.find(session, criterion);
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

    @Override
    public boolean saveOrUpdate(final SwiftSegmentLocationEntity obj) {
        try {
            return tx.doTransactionIfNeed(new AbstractTransactionWorker<Boolean>() {
                @Override
                public Boolean work(Session session) throws SQLException {
                    return segmentLocationDao.saveOrUpdate(session, obj);
                }
            });
        } catch (SQLException e) {
            SwiftLoggers.getLogger().error(e);
            return false;
        }
    }
}
