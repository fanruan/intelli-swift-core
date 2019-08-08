package com.fr.swift.config.bean;

import com.fr.swift.SwiftContext;
import com.fr.swift.config.SwiftConfig;
import com.fr.swift.config.SwiftConfigConstants;
import com.fr.swift.config.annotation.ConfigField;
import com.fr.swift.config.entity.SwiftConfigEntity;
import com.fr.swift.config.query.SwiftConfigEntityQueryBus;
import com.fr.swift.context.ContextProvider;
import com.fr.swift.cube.io.impl.fineio.connector.CommonConnectorType;

/**
 * @author yee
 * @date 2018-12-20
 */
public class CommonConnectorConfig implements FineIOConnectorConfig {
    @ConfigField
    private CommonConnectorType type;

    public CommonConnectorConfig(CommonConnectorType type) {
        this.type = type;
    }

    public CommonConnectorConfig() {
    }

    @Override
    public String type() {
        return type.name();
    }

    @Override
    public String basePath() {
        final String contextPath = SwiftContext.get().getBean(ContextProvider.class).getContextPath();
        final SwiftConfigEntityQueryBus query = (SwiftConfigEntityQueryBus) SwiftContext.get().getBean(SwiftConfig.class).query(SwiftConfigEntity.class);
        return query.select(SwiftConfigConstants.Namespace.SWIFT_CUBE_PATH, String.class, contextPath);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        CommonConnectorConfig that = (CommonConnectorConfig) o;

        return type == that.type;
    }

    @Override
    public int hashCode() {
        return type != null ? type.hashCode() : 0;
    }
}
