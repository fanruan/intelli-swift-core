package com.fr.swift.config.service;

import com.fr.swift.config.dao.SwiftConfigDao;
import com.fr.swift.config.entity.SwiftConfigEntity;
import com.fr.swift.config.oper.ConfigSession;

import java.sql.SQLException;
import java.util.List;

/**
 * @author yee
 * @date 2018/7/6
 */
public interface SwiftConfigService {

    <ConfigBean> ConfigBean getConfigBean(ConfigConvert<ConfigBean> convert, Object... args);

    <ConfigBean> boolean updateConfigBean(ConfigConvert<ConfigBean> convert, ConfigBean bean, Object... args);

    <ConfigBean> boolean deleteConfigBean(ConfigConvert<ConfigBean> convert, ConfigBean bean, Object... args);

    interface ConfigConvert<Bean> {
        /**
         * 转Bean
         *
         * @param dao
         * @param session
         * @return
         * @throws SQLException
         */
        Bean toBean(SwiftConfigDao<SwiftConfigEntity> dao, ConfigSession session, Object... args) throws SQLException;

        /**
         * @param bean
         * @return
         */
        List<SwiftConfigEntity> toEntity(Bean bean, Object... args);
    }
}
