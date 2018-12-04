package com.fr.swift.config.service.impl;

import com.fr.swift.SwiftContext;
import com.fr.swift.beans.annotation.SwiftBean;
import com.fr.swift.config.SwiftConfigConstants;
import com.fr.swift.config.bean.SwiftConfigBean;
import com.fr.swift.config.convert.base.AbstractSimpleConfigConvert;
import com.fr.swift.config.dao.SwiftConfigDao;
import com.fr.swift.config.oper.ConfigSession;
import com.fr.swift.config.service.SwiftConfigService;
import com.fr.swift.config.service.SwiftZipService;

import java.sql.SQLException;

/**
 * @author yee
 * @date 2018/6/27
 */
@SwiftBean(name = "swiftZipService")
public class SwiftZipServiceImpl implements SwiftZipService {

    private final SwiftConfigService.ConfigConvert<Boolean> CONVERT = new AbstractSimpleConfigConvert<Boolean>(Boolean.class) {

        @Override
        public Boolean toBean(SwiftConfigDao<SwiftConfigBean> dao, ConfigSession session, Object... args) throws SQLException {
            try {
                return super.toBean(dao, session, args);
            } catch (Exception e) {
                for (SwiftConfigBean swiftConfigEntity : toEntity(true)) {
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

    private SwiftConfigService configService = SwiftContext.get().getBean(SwiftConfigService.class);

    @Override
    public boolean isZip() {
        return configService.getConfigBean(CONVERT);
    }

    @Override
    public boolean setZip(boolean isZip) {
        return configService.updateConfigBean(CONVERT, isZip);
    }
}
