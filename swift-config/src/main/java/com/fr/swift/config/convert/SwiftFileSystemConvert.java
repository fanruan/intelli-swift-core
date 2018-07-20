package com.fr.swift.config.convert;

import com.fr.swift.config.SwiftConfigConstants;
import com.fr.swift.config.bean.SwiftFileSystemConfig;
import com.fr.swift.config.convert.base.AbstractObjectConfigConvert;
import com.fr.swift.config.dao.SwiftConfigDao;
import com.fr.swift.config.entity.SwiftConfigEntity;
import com.fr.third.org.hibernate.Session;

/**
 * @author yee
 * @date 2018/7/6
 */
public class SwiftFileSystemConvert extends AbstractObjectConfigConvert<SwiftFileSystemConfig> {

    @Override
    public SwiftFileSystemConfig toBean(SwiftConfigDao<SwiftConfigEntity> dao, Session session, Object... args) {
        try {
            return super.toBean(dao, session, args);
        } catch (Exception e) {
            return SwiftFileSystemConfig.DEFAULT;
        }
    }

    @Override
    protected String getNameSpace() {
        return SwiftConfigConstants.FRConfiguration.REPOSITORY_CONF_NAMESPACE;
    }
}
