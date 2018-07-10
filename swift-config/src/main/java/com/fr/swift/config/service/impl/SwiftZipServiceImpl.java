package com.fr.swift.config.service.impl;

import com.fr.swift.config.SwiftConfigConstants;
import com.fr.swift.config.dao.SwiftConfigDao;
import com.fr.swift.config.entity.SwiftConfigEntity;
import com.fr.swift.config.service.SwiftConfigService;
import com.fr.swift.config.service.SwiftZipService;
import com.fr.third.org.hibernate.Session;
import com.fr.third.springframework.beans.factory.annotation.Autowired;
import com.fr.third.springframework.stereotype.Service;

import java.sql.SQLException;
import java.util.Collections;
import java.util.List;

/**
 * @author yee
 * @date 2018/6/27
 */
@Service("swiftZipService")
public class SwiftZipServiceImpl implements SwiftZipService {

    private final SwiftConfigService.ConfigConvert<Boolean> CONVERT = new SwiftConfigService.ConfigConvert<Boolean>() {
        @Override
        public Boolean toBean(SwiftConfigDao<SwiftConfigEntity> dao, Session session, Object... args) throws SQLException {
            SwiftConfigEntity entity = dao.select(session, SwiftConfigConstants.FRConfiguration.ZIP_NAMESPACE);
            if (null != entity) {
                return Boolean.parseBoolean(entity.getConfigValue());
            }
            for (SwiftConfigEntity swiftConfigEntity : toEntity(true)) {
                dao.saveOrUpdate(session, swiftConfigEntity);
            }
            return true;
        }

        @Override
        public List<SwiftConfigEntity> toEntity(Boolean aBoolean, Object... args) {
            SwiftConfigEntity entity = new SwiftConfigEntity();
            entity.setConfigKey(SwiftConfigConstants.FRConfiguration.ZIP_NAMESPACE);
            entity.setConfigValue(String.valueOf(aBoolean));
            return Collections.singletonList(entity);
        }
    };

    @Autowired
    private SwiftConfigService configService;

    @Override
    public boolean isZip() {
        return configService.getConfigBean(CONVERT);
    }

    @Override
    public boolean setZip(boolean isZip) {
        return configService.updateConfigBean(CONVERT, isZip);
    }
}
