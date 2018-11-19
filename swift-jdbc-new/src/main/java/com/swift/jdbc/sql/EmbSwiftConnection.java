package com.swift.jdbc.sql;

import java.sql.Driver;

/**
 * @author yee
 * @date 2018/11/16
 */
public class EmbSwiftConnection extends BaseSwiftConnection {
    EmbSwiftConnection(Driver driver, String database) {
        super(driver, database);
    }

    @Override
    public ConnectionConfig getConfig() {
        return null;
    }
}
