package com.fr.swift.config.service.impl;

import com.fr.swift.SwiftContext;
import com.fr.swift.beans.annotation.SwiftBean;
import com.fr.swift.config.dao.SwiftServiceInfoDao;
import com.fr.swift.config.entity.SwiftServiceInfoEntity;
import com.fr.swift.config.oper.BaseTransactionWorker;
import com.fr.swift.config.oper.ConfigSession;
import com.fr.swift.config.oper.ConfigWhere;
import com.fr.swift.config.oper.TransactionManager;
import com.fr.swift.config.service.SwiftServiceInfoService;
import com.fr.swift.log.SwiftLoggers;

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
@SwiftBean(name = "swiftServiceInfoService")
class SwiftServiceInfoServiceImpl implements SwiftServiceInfoService {

    private TransactionManager transactionManager = SwiftContext.get().getBean(TransactionManager.class);
    private SwiftServiceInfoDao swiftServiceInfoDao = SwiftContext.get().getBean(SwiftServiceInfoDao.class);

    public SwiftServiceInfoServiceImpl() {
    }

    @Override
    public boolean saveOrUpdate(final SwiftServiceInfoEntity serviceInfoBean) {
        try {
            return transactionManager.doTransactionIfNeed(new BaseTransactionWorker<Boolean>() {
                @Override
                public Boolean work(ConfigSession session) throws SQLException {
                    swiftServiceInfoDao.saveOrUpdate(session, serviceInfoBean);
                    return true;
                }
            });
        } catch (Exception e) {
            SwiftLoggers.getLogger().warn("Add or update ServiceInfo error!", e);
            return false;
        }
    }

    @Override
    public boolean removeServiceInfo(final SwiftServiceInfoEntity serviceInfoBean) {
        try {
            return transactionManager.doTransactionIfNeed(new BaseTransactionWorker<Boolean>() {
                @Override
                public Boolean work(ConfigSession session) throws SQLException {
                    swiftServiceInfoDao.deleteById(session, serviceInfoBean.getId());
                    return true;
                }
            });
        } catch (Exception e) {
            SwiftLoggers.getLogger().warn("remove ServiceInfo error!", e);
            return false;
        }
    }

    @Override
    public SwiftServiceInfoEntity getServiceInfo(final SwiftServiceInfoEntity serviceInfoBean) {
        try {
            return transactionManager.doTransactionIfNeed(new BaseTransactionWorker<SwiftServiceInfoEntity>(false) {
                @Override
                public SwiftServiceInfoEntity work(ConfigSession session) throws SQLException {
                    return swiftServiceInfoDao.select(session, serviceInfoBean.getId());
                }
            });
        } catch (Exception e) {
            SwiftLoggers.getLogger().warn("get ServiceInfo error!", e);
            return null;
        }
    }

    @Override
    public List<SwiftServiceInfoEntity> getAllServiceInfo() {

        try {
            return transactionManager.doTransactionIfNeed(new BaseTransactionWorker<List<SwiftServiceInfoEntity>>() {
                @Override
                public List<SwiftServiceInfoEntity> work(ConfigSession session) {
                    return swiftServiceInfoDao.find(session);
                }

                @Override
                public boolean needTransaction() {
                    return false;
                }
            });
        } catch (Exception e) {
            SwiftLoggers.getLogger().warn("get complete ServiceInfo error!", e);
            return new ArrayList<SwiftServiceInfoEntity>();
        }
    }

    @Override
    public List<SwiftServiceInfoEntity> getServiceInfoByService(final String service) {

        try {
            return transactionManager.doTransactionIfNeed(new BaseTransactionWorker<List<SwiftServiceInfoEntity>>() {
                @Override
                public List<SwiftServiceInfoEntity> work(ConfigSession session) {
                    return swiftServiceInfoDao.getServiceInfoByService(session, service);
                }

                @Override
                public boolean needTransaction() {
                    return false;
                }
            });
        } catch (Exception e) {
            SwiftLoggers.getLogger().warn("get ServiceInfo by service error!", e);
            return new ArrayList<SwiftServiceInfoEntity>();
        }
    }

    @Override
    public List<SwiftServiceInfoEntity> find(final ConfigWhere... criterion) {
        try {
            return transactionManager.doTransactionIfNeed(new BaseTransactionWorker<List<SwiftServiceInfoEntity>>() {
                @Override
                public List<SwiftServiceInfoEntity> work(ConfigSession session) {
                    return swiftServiceInfoDao.find(session, criterion);
                }

                @Override
                public boolean needTransaction() {
                    return false;
                }
            });
        } catch (Exception e) {
            SwiftLoggers.getLogger().warn("get ServiceInfo by service error!", e);
            return new ArrayList<SwiftServiceInfoEntity>();
        }
    }
}
