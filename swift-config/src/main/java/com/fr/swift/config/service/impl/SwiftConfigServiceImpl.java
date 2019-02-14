package com.fr.swift.config.service.impl;

import com.fr.swift.SwiftContext;
import com.fr.swift.beans.annotation.SwiftBean;
import com.fr.swift.config.bean.SwiftConfigBean;
import com.fr.swift.config.dao.impl.SwiftConfigDaoImpl;
import com.fr.swift.config.oper.BaseTransactionWorker;
import com.fr.swift.config.oper.ConfigSession;
import com.fr.swift.config.oper.TransactionManager;
import com.fr.swift.config.service.SwiftConfigService;
import com.fr.swift.log.SwiftLoggers;

import java.sql.SQLException;
import java.util.List;


/**
 * @author yee
 * @date 2018/7/6
 */
@SwiftBean
public class SwiftConfigServiceImpl implements SwiftConfigService {

    private TransactionManager transactionManager = SwiftContext.get().getBean(TransactionManager.class);
    private SwiftConfigDaoImpl swiftConfigDao = SwiftContext.get().getBean(SwiftConfigDaoImpl.class);

    @Override
    public <ConfigBean> ConfigBean getConfigBean(final ConfigConvert<ConfigBean> convert, final Object... args) {
        try {
            return transactionManager.doTransactionIfNeed(new BaseTransactionWorker<ConfigBean>() {
                @Override
                public ConfigBean work(ConfigSession session) throws SQLException {
                    return convert.toBean(swiftConfigDao, session, args);
                }

                @Override
                public boolean needTransaction() {
                    return false;
                }
            });
        } catch (SQLException e) {
            SwiftLoggers.getLogger().debug("Find config bean error. return null.", e);
        }
        return null;
    }

    @Override
    public <ConfigBean> boolean updateConfigBean(ConfigConvert<ConfigBean> convert, ConfigBean bean, Object... args) {
        final List<SwiftConfigBean> configEntities = convert.toEntity(bean, args);
        try {
            return transactionManager.doTransactionIfNeed(new BaseTransactionWorker<Boolean>() {
                @Override
                public Boolean work(ConfigSession session) throws SQLException {
                    for (SwiftConfigBean configEntity : configEntities) {
                        swiftConfigDao.saveOrUpdate(session, configEntity);
                    }
                    return true;
                }
            });
        } catch (SQLException e) {
            SwiftLoggers.getLogger().warn(e);
        }
        return false;
    }

    @Override
    public <ConfigBean> boolean deleteConfigBean(ConfigConvert<ConfigBean> convert, ConfigBean bean, Object... args) {
        final List<SwiftConfigBean> configEntities = convert.toEntity(bean, args);
        try {
            return transactionManager.doTransactionIfNeed(new BaseTransactionWorker<Boolean>() {
                @Override
                public Boolean work(ConfigSession session) throws SQLException {
                    for (SwiftConfigBean configEntity : configEntities) {
                        swiftConfigDao.deleteById(session, configEntity.getConfigKey());
                    }
                    return true;
                }
            });
        } catch (SQLException e) {
            SwiftLoggers.getLogger().warn(e);
        }
        return false;
    }
}
