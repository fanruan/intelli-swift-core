package com.fr.swift.config.bean;

import com.fr.swift.SwiftContext;
import com.fr.swift.config.annotation.ConfigField;
import com.fr.swift.config.service.SwiftCubePathService;
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
        return SwiftContext.get().getBean(SwiftCubePathService.class).getSwiftPath();
    }
}
