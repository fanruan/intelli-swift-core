package com.fr.swift.cube.io.impl.fineio.connector.config;

import com.fr.swift.SwiftContext;
import com.fr.swift.config.bean.FineIOConnectorConfig;
import com.fr.swift.config.service.SwiftCubePathService;
import com.fr.swift.cube.io.impl.fineio.connector.CommonConnectorType;

/**
 * @author yee
 * @date 2018-12-20
 */
public class CommonConnectorConfig implements FineIOConnectorConfig {
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
        return SwiftContext.get().getBean(SwiftCubePathService.class).getSwiftPath();
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
