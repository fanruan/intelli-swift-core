package com.fr.swift.config.service.impl;

import com.fr.swift.config.dao.impl.SwiftConfigDaoImpl;
import com.fr.swift.config.entity.SwiftConfigEntity;
import com.fr.swift.config.hibernate.transaction.AbstractTransactionWorker;
import com.fr.swift.config.hibernate.transaction.HibernateTransactionManager;
import com.fr.swift.config.service.SwiftConfigService;
import com.fr.swift.log.SwiftLoggers;
import com.fr.third.org.hibernate.Session;
import com.fr.third.springframework.beans.factory.annotation.Autowired;
import com.fr.third.springframework.stereotype.Service;

import java.sql.SQLException;
import java.util.List;


/**
 * @author yee
 * @date 2018/7/6
 */
@Service
public class SwiftConfigServiceImpl implements SwiftConfigService {
    @Autowired
    private HibernateTransactionManager transactionManager;
    @Autowired
    private SwiftConfigDaoImpl swiftConfigDao;

    @Override
    public <ConfigBean> ConfigBean getConfigBean(final ConfigConvert<ConfigBean> convert, final Object... args) {
        try {
            return transactionManager.doTransactionIfNeed(new AbstractTransactionWorker<ConfigBean>() {
                @Override
                public ConfigBean work(Session session) throws SQLException {
                    return convert.toBean(swiftConfigDao, session, args);
                }

                @Override
                public boolean needTransaction() {
                    return false;
                }
            });
        } catch (SQLException e) {
            SwiftLoggers.getLogger().error(e);
        }
        return null;
    }

    @Override
    public <ConfigBean> boolean updateConfigBean(ConfigConvert<ConfigBean> convert, ConfigBean bean, Object... args) {
        final List<SwiftConfigEntity> configEntities = convert.toEntity(bean, args);
        try {
            return transactionManager.doTransactionIfNeed(new AbstractTransactionWorker<Boolean>() {
                @Override
                public Boolean work(Session session) throws SQLException {
                    for (SwiftConfigEntity configEntity : configEntities) {
                        swiftConfigDao.saveOrUpdate(session, configEntity);
                    }
                    return true;
                }
            });
        } catch (SQLException e) {
            SwiftLoggers.getLogger().error(e);
        }
        return false;
    }

    @Override
    public <ConfigBean> boolean deleteConfigBean(ConfigConvert<ConfigBean> convert, ConfigBean bean, Object... args) {
        final List<SwiftConfigEntity> configEntities = convert.toEntity(bean, args);
        try {
            return transactionManager.doTransactionIfNeed(new AbstractTransactionWorker<Boolean>() {
                @Override
                public Boolean work(Session session) throws SQLException {
                    for (SwiftConfigEntity configEntity : configEntities) {
                        swiftConfigDao.deleteById(session, configEntity.getConfigKey());
                    }
                    return true;
                }
            });
        } catch (SQLException e) {
            SwiftLoggers.getLogger().error(e);
        }
        return false;
    }
}
