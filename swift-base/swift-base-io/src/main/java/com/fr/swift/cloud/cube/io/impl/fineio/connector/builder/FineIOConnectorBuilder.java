package com.fr.swift.cloud.cube.io.impl.fineio.connector.builder;

import com.fineio.storage.Connector;
import com.fineio.v3.connector.PackageConnector;
import com.fr.swift.cloud.config.ConfigLoader;
import com.fr.swift.cloud.config.bean.FineIOConnectorConfig;

/**
 * @author yee
 * @date 2018-12-20
 */
public interface FineIOConnectorBuilder extends ConfigLoader<FineIOConnectorConfig> {
    FineIOConnectorBuilder setBasePath(String basePath);

    Connector build();

    PackageConnector buildPackageConnector();
}
