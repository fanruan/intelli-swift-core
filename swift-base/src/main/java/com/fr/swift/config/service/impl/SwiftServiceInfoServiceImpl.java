package com.fr.swift.config.service.impl;

import com.fr.swift.config.bean.SwiftServiceInfoBean;
import com.fr.swift.config.dao.SwiftServiceInfoDao;
import com.fr.swift.config.entity.SwiftServiceInfoEntity;
import com.fr.swift.config.service.SwiftServiceInfoService;
import com.fr.swift.config.transaction.AbstractTransactionWorker;
import com.fr.swift.config.transaction.SwiftTransactionManager;
import com.fr.swift.log.SwiftLogger;
import com.fr.swift.log.SwiftLoggers;
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

    private static final SwiftLogger LOGGER = SwiftLoggers.getLogger(SwiftServiceInfoServiceImpl.class);

    @Autowired
    private SwiftTransactionManager transactionManager;
    @Autowired
    private SwiftServiceInfoDao swiftServiceInfoDao;

    public SwiftServiceInfoServiceImpl() {
    }

    @Override
    public boolean saveOrUpdateServiceInfo(final SwiftServiceInfoBean serviceInfoBean) {
        try {
            return (Boolean) transactionManager.doTransactionIfNeed(new AbstractTransactionWorker() {
                @Override
                public Object work() throws SQLException {
                    swiftServiceInfoDao.saveOrUpdate(serviceInfoBean.convert());
                    return true;
                }


            });
        } catch (Exception e) {
            LOGGER.error("Add or update ServiceInfo error!", e);
            return false;
        }
    }

    @Override
    public boolean removeServiceInfo(final SwiftServiceInfoBean serviceInfoBean) {
        try {
            return (Boolean) transactionManager.doTransactionIfNeed(new AbstractTransactionWorker() {
                @Override
                public Object work() throws SQLException {
                    swiftServiceInfoDao.deleteById(serviceInfoBean.convert().getId());
                    return true;
                }


            });
        } catch (Exception e) {
            LOGGER.error("remove ServiceInfo error!", e);
            return false;
        }
    }

    @Override
    public SwiftServiceInfoBean getServiceInfo(final SwiftServiceInfoBean serviceInfoBean) {
        try {
            return (SwiftServiceInfoBean) transactionManager.doTransactionIfNeed(new AbstractTransactionWorker() {
                @Override
                public Object work() throws SQLException {
                    return swiftServiceInfoDao.select(serviceInfoBean.convert().getId()).convert();
                }

                @Override
                public boolean needTransaction() {
                    return false;
                }
            });
        } catch (Exception e) {
            LOGGER.error("get ServiceInfo error!", e);
            return null;
        }
    }

    @Override
    public List<SwiftServiceInfoBean> getAllServiceInfo() {

        try {
            return (List<SwiftServiceInfoBean>) transactionManager.doTransactionIfNeed(new AbstractTransactionWorker() {
                @Override
                public Object work() {
                    List<SwiftServiceInfoBean> beanList = new ArrayList<SwiftServiceInfoBean>();
                    for (SwiftServiceInfoEntity entity : swiftServiceInfoDao.find()) {
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
            LOGGER.error("get all ServiceInfo error!", e);
            return new ArrayList<SwiftServiceInfoBean>();
        }
    }

    @Override
    public List<SwiftServiceInfoBean> getServiceInfoByService(final String service) {

        try {
            return (List<SwiftServiceInfoBean>) transactionManager.doTransactionIfNeed(new AbstractTransactionWorker() {
                @Override
                public Object work() {
                    List<SwiftServiceInfoBean> beanList = new ArrayList<SwiftServiceInfoBean>();
                    for (SwiftServiceInfoEntity entity : swiftServiceInfoDao.getServiceInfoByService(service)) {
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
            LOGGER.error("get ServiceInfo by service error!", e);
            return new ArrayList<SwiftServiceInfoBean>();
        }
    }
}
