package com.fr.swift.cube.io.impl.fineio.connector.builder;

import com.fineio.storage.Connector;
import com.fr.swift.config.ConfigLoader;
import com.fr.swift.config.bean.FineIOConnectorConfig;

/**
 * @author yee
 * @date 2018-12-20
 */
public interface FineIOConnectorBuilder extends ConfigLoader<FineIOConnectorConfig> {
    FineIOConnectorBuilder setBasePath(String basePath);

    Connector build();
}
