package com.fr.swift.config.service.impl;

import com.fr.swift.config.SwiftConfigConstants;
import com.fr.swift.config.bean.SwiftConfDbBean;
import com.fr.swift.config.dao.SwiftConfigDao;
import com.fr.swift.config.entity.SwiftConfigEntity;
import com.fr.swift.config.service.SwiftConfDbService;
import com.fr.swift.config.service.SwiftConfigService;
import com.fr.third.org.hibernate.Session;
import com.fr.third.springframework.beans.factory.annotation.Autowired;
import com.fr.third.springframework.stereotype.Service;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author yee
 * @date 2018/7/6
 */
@Service("swiftConfDbService")
public class SwiftConfDbServiceImpl implements SwiftConfDbService {

    private final SwiftConfigService.ConfigConvert<SwiftConfDbBean> CONVERT = new SwiftConfigService.ConfigConvert<SwiftConfDbBean>() {
        @Override
        public SwiftConfDbBean toBean(SwiftConfigDao<SwiftConfigEntity> dao, Session session, Object... args) throws SQLException {
            SwiftConfigEntity driverClass = dao.select(session, getKey("driverClass"));
            if (null == driverClass) {
                return null;
            }
            SwiftConfigEntity username = dao.select(session, getKey("username"));
            SwiftConfigEntity password = dao.select(session, getKey("password"));
            SwiftConfigEntity url = dao.select(session, getKey("url"));
            SwiftConfigEntity dialectClass = dao.select(session, getKey("dialectClass"));
            SwiftConfDbBean bean = new SwiftConfDbBean();
            bean.setDriverClass(driverClass.getConfigValue());
            bean.setDialectClass(dialectClass.getConfigValue());
            bean.setUsername(username.getConfigValue());
            bean.setPassword(password.getConfigValue());
            bean.setUrl(url.getConfigValue());
            return bean;
        }

        @Override
        public List<SwiftConfigEntity> toEntity(SwiftConfDbBean swiftConfDbBean, Object... args) {
            List<SwiftConfigEntity> list = new ArrayList<SwiftConfigEntity>();
            list.add(new SwiftConfigEntity(getKey("driverClass"), swiftConfDbBean.getDriverClass()));
            list.add(new SwiftConfigEntity(getKey("username"), swiftConfDbBean.getUsername()));
            list.add(new SwiftConfigEntity(getKey("password"), swiftConfDbBean.getPassword()));
            list.add(new SwiftConfigEntity(getKey("url"), swiftConfDbBean.getUrl()));
            list.add(new SwiftConfigEntity(getKey("dialectClass"), swiftConfDbBean.getDialectClass()));
            return Collections.unmodifiableList(list);
        }

        private String getKey(String key) {
            return String.format("%s_%s", SwiftConfigConstants.FRConfiguration.SWIFT_DB_CONF_NAMESPACE, key);
        }
    };

    @Autowired
    private SwiftConfigService configService;

    @Override
    public SwiftConfDbBean getConfig() {
        return configService.getConfigBean(CONVERT);
    }

    @Override
    public boolean saveDbConfig(SwiftConfDbBean config) {
        return configService.updateConfigBean(CONVERT, config);
    }
}
