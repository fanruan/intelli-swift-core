package com.fr.swift.config.service.impl;

import com.fr.swift.config.bean.SwiftServiceInfoBean;
import com.fr.swift.config.dao.SwiftServiceInfoDao;
import com.fr.swift.config.entity.SwiftServiceInfoEntity;
import com.fr.swift.config.hibernate.transaction.AbstractTransactionWorker;
import com.fr.swift.config.hibernate.transaction.HibernateTransactionManager;
import com.fr.swift.config.service.SwiftServiceInfoService;
import com.fr.swift.log.SwiftLoggers;
import com.fr.third.org.hibernate.Session;
import com.fr.third.org.hibernate.criterion.Criterion;
import com.fr.third.springframework.beans.factory.annotation.Autowired;
import com.fr.third.springframework.stereotype.Service;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * This class created on 2018/5/29
 *
 * @author Lucifer
 * @description
 * @since Advanced FineBI 5.0
 */
@Service("swiftServiceInfoService")
class SwiftServiceInfoServiceImpl implements SwiftServiceInfoService {

    @Autowired
    private HibernateTransactionManager transactionManager;
    @Autowired
    private SwiftServiceInfoDao swiftServiceInfoDao;

    public SwiftServiceInfoServiceImpl() {
    }

    @Override
    public boolean saveOrUpdate(final SwiftServiceInfoBean serviceInfoBean) {
        try {
            return transactionManager.doTransactionIfNeed(new AbstractTransactionWorker<Boolean>() {
                @Override
                public Boolean work(Session session) throws SQLException {
                    swiftServiceInfoDao.saveOrUpdate(session, serviceInfoBean.convert());
                    return true;
                }
            });
        } catch (Exception e) {
            SwiftLoggers.getLogger().error("Add or update ServiceInfo error!", e);
            return false;
        }
    }

    @Override
    public boolean removeServiceInfo(final SwiftServiceInfoBean serviceInfoBean) {
        try {
            return transactionManager.doTransactionIfNeed(new AbstractTransactionWorker<Boolean>() {
                @Override
                public Boolean work(Session session) throws SQLException {
                    swiftServiceInfoDao.deleteById(session, serviceInfoBean.convert().getId());
                    return true;
                }
            });
        } catch (Exception e) {
            SwiftLoggers.getLogger().error("remove ServiceInfo error!", e);
            return false;
        }
    }

    @Override
    public SwiftServiceInfoBean getServiceInfo(final SwiftServiceInfoBean serviceInfoBean) {
        try {
            return transactionManager.doTransactionIfNeed(new AbstractTransactionWorker<SwiftServiceInfoBean>() {
                @Override
                public SwiftServiceInfoBean work(Session session) throws SQLException {
                    return swiftServiceInfoDao.select(session, serviceInfoBean.convert().getId()).convert();
                }

                @Override
                public boolean needTransaction() {
                    return false;
                }
            });
        } catch (Exception e) {
            SwiftLoggers.getLogger().error("get ServiceInfo error!", e);
            return null;
        }
    }

    @Override
    public List<SwiftServiceInfoBean> getAllServiceInfo() {

        try {
            return transactionManager.doTransactionIfNeed(new AbstractTransactionWorker<List<SwiftServiceInfoBean>>() {
                @Override
                public List<SwiftServiceInfoBean> work(Session session) {
                    List<SwiftServiceInfoBean> beanList = new ArrayList<SwiftServiceInfoBean>();
                    for (SwiftServiceInfoEntity entity : swiftServiceInfoDao.find(session)) {
                        beanList.add(entity.convert());
                    }
                    return beanList;
                }

                @Override
                public boolean needTransaction() {
                    return false;
                }
            });
        } catch (Exception e) {
            SwiftLoggers.getLogger().error("get all ServiceInfo error!", e);
            return new ArrayList<SwiftServiceInfoBean>();
        }
    }

    @Override
    public List<SwiftServiceInfoBean> getServiceInfoByService(final String service) {

        try {
            return transactionManager.doTransactionIfNeed(new AbstractTransactionWorker<List<SwiftServiceInfoBean>>() {
                @Override
                public List<SwiftServiceInfoBean> work(Session session) {
                    List<SwiftServiceInfoBean> beanList = new ArrayList<SwiftServiceInfoBean>();
                    for (SwiftServiceInfoEntity entity : swiftServiceInfoDao.getServiceInfoByService(session, service)) {
                        beanList.add(entity.convert());
                    }
                    return beanList;
                }

                @Override
                public boolean needTransaction() {
                    return false;
                }
            });
        } catch (Exception e) {
            SwiftLoggers.getLogger().error("get ServiceInfo by service error!", e);
            return new ArrayList<SwiftServiceInfoBean>();
        }
    }

    @Override
    public List<SwiftServiceInfoBean> find(final Criterion... criterion) {
        try {
            return transactionManager.doTransactionIfNeed(new AbstractTransactionWorker<List<SwiftServiceInfoBean>>() {
                @Override
                public List<SwiftServiceInfoBean> work(Session session) {
                    List<SwiftServiceInfoBean> beanList = new ArrayList<SwiftServiceInfoBean>();
                    for (SwiftServiceInfoEntity entity : swiftServiceInfoDao.find(session, criterion)) {
                        beanList.add(entity.convert());
                    }
                    return beanList;
                }

                @Override
                public boolean needTransaction() {
                    return false;
                }
            });
        } catch (Exception e) {
            SwiftLoggers.getLogger().error("get ServiceInfo by service error!", e);
            return new ArrayList<SwiftServiceInfoBean>();
        }
    }
}
