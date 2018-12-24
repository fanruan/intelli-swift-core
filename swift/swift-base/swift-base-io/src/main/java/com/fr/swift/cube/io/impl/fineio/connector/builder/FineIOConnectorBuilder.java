package com.fr.swift.cube.io.impl.fineio.connector.builder;

import com.fineio.storage.Connector;

/**
 * @author yee
 * @date 2018-12-20
 */
public interface FineIOConnectorBuilder {
    FineIOConnectorBuilder setBasePath(String basePath);

    Connector build();
}
