package com.fr.swift.source.db;

import com.fr.data.impl.Connection;

/**
 * This class created on 2017-12-19 15:39:42
 *
 * @author Lucifer
 * @description
 * @since Advanced FineBI Analysis 1.0
 */
public class SwiftConnectionInfo implements ConnectionInfo {

    private String schema;

    private Connection frConnection;

    public SwiftConnectionInfo(String schema, Connection frConnection) {
        this.schema = schema;
        this.frConnection = frConnection;
    }

    @Override
    public String getSchema() {
        return schema;
    }

    @Override
    public Connection getFrConnection() {
        return frConnection;
    }
}
