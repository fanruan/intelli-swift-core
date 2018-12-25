package com.fr.swift.cube.io.impl.fineio.connector.builder;

/**
 * @author yee
 * @date 2018-12-20
 */
public abstract class BaseConnectorBuilder implements FineIOConnectorBuilder {
    protected String basePath;

    @Override
    public FineIOConnectorBuilder setBasePath(String basePath) {
        this.basePath = basePath;
        return this;
    }

}
