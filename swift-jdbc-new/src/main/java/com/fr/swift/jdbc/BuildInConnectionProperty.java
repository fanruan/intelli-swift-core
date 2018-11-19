package com.fr.swift.jdbc;

import java.util.Properties;

/**
 * @author yee
 * @date 2018/11/19
 */
public enum BuildInConnectionProperty {
    //
    CONNECTION_TIMEOUT("conn_timeout", JdbcProperty.get().getConnectionTimeout()),
    SWIFT_USERNAME("swift_username", SwiftJdbcConstants.EMPTY),
    SWIFT_PASSWORD("swift_password", SwiftJdbcConstants.EMPTY),
    STATEMENT_MAX_IDLE("sttm_max_idle", JdbcProperty.get().getStatementMaxIdle()),
    PRINCIPAL("principal", null),
    KEYTAB("keytab", null);

    private String propertyName;
    private Object defaultValue;

    BuildInConnectionProperty(String propertyName, Object defaultValue) {
        this.propertyName = propertyName;
        this.defaultValue = defaultValue;
    }

    public String getPropertyName() {
        return propertyName;
    }

    public Object getDefaultValue() {
        return defaultValue;
    }

    public String getValue(Properties properties) {
        return properties.getProperty(this.propertyName, defaultValue.toString());
    }
}
