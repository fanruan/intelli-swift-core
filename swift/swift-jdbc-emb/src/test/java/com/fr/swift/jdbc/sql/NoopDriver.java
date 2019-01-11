package com.fr.swift.jdbc.sql;

/**
 * @author yee
 * @date 2019-01-09
 */
public class NoopDriver extends UnregisteredDriver {

    static {
        new NoopDriver().register();
    }

    @Override
    protected String getConnectionSchema() {
        return "jdbc:swift:test";
    }
}
