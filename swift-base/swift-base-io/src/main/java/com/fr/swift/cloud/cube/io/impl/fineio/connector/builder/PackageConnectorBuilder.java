package com.fr.swift.cloud.cube.io.impl.fineio.connector.builder;

import com.fineio.v3.connector.PackageConnector;
import com.fr.swift.cloud.config.ConfigLoader;
import com.fr.swift.cloud.config.bean.FineIOConnectorConfig;

/**
 * @author yee
 * @date 2019-05-28
 */
public interface PackageConnectorBuilder<Config extends FineIOConnectorConfig> extends ConfigLoader<Config> {
    PackageConnector build(Config config);
}
