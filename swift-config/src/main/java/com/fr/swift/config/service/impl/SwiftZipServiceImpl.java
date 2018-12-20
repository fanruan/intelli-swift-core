package com.fr.swift.config.service.impl;

import com.fr.swift.config.SwiftConfigConstants;
import com.fr.swift.config.convert.base.AbstractSimpleConfigConvert;
import com.fr.swift.config.dao.SwiftConfigDao;
import com.fr.swift.config.entity.SwiftConfigEntity;
import com.fr.swift.config.service.SwiftConfigService;
import com.fr.swift.config.service.SwiftZipService;
import com.fr.third.org.hibernate.Session;
import com.fr.third.springframework.beans.factory.annotation.Autowired;
import com.fr.third.springframework.stereotype.Service;

import java.sql.SQLException;

/**
 * @author yee
 * @date 2018/6/27
 */
@Service("swiftZipService")
@Deprecated
public class SwiftZipServiceImpl implements SwiftZipService {

    private final SwiftConfigService.ConfigConvert<Boolean> CONVERT = new AbstractSimpleConfigConvert<Boolean>(Boolean.class) {

        @Override
        public Boolean toBean(SwiftConfigDao<SwiftConfigEntity> dao, Session session, Object... args) throws SQLException {
            try {
                return super.toBean(dao, session, args);
            } catch (Exception e) {
                for (SwiftConfigEntity swiftConfigEntity : toEntity(true)) {
                    dao.saveOrUpdate(session, swiftConfigEntity);
                }
                return true;
            }
        }

        @Override
        protected String getNameSpace() {
            return SwiftConfigConstants.FRConfiguration.ZIP_NAMESPACE;
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
