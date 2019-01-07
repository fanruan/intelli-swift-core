package com.fr.swift.config.convert;

import com.fr.swift.config.bean.CommonConnectorConfig;
import com.fr.swift.config.bean.FineIOConnectorConfig;
import com.fr.swift.config.bean.SwiftConfigBean;
import com.fr.swift.config.convert.base.AbstractObjectConfigConvert;
import com.fr.swift.config.dao.SwiftConfigDao;
import com.fr.swift.config.oper.ConfigSession;
import com.fr.swift.cube.io.impl.fineio.connector.CommonConnectorType;
import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.property.SwiftProperty;

import java.sql.SQLException;

/**
 * @author yee
 * @date 2018-12-20
 */
public class FineIOConfigConvert extends AbstractObjectConfigConvert<FineIOConnectorConfig> {
    private static final String FINE_IO_CONNECTOR = "FINE_IO_CONNECTOR";

    public FineIOConfigConvert() {
        super(FineIOConnectorConfig.class);
    }

    @Override
    public FineIOConnectorConfig toBean(SwiftConfigDao<SwiftConfigBean> dao, ConfigSession session, Object... args) throws SQLException {
        try {
            return super.toBean(dao, session, args);
        } catch (Exception e) {
            SwiftLoggers.getLogger().warn("Cannot find FineIO config. use default");
            return new CommonConnectorConfig(CommonConnectorType.LZ4);
        }
    }

    @Override
    protected String transferClassName(String className) {
        return className;
    }

    @Override
    protected String getNameSpace() {
        return FINE_IO_CONNECTOR + "." + SwiftProperty.getProperty().getClusterId();
    }
}
