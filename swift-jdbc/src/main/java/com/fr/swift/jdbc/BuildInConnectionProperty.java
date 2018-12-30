package com.fr.swift.jdbc;

import java.util.Properties;

/**
 * @author yee
 * @date 2018/11/19
 */
public enum BuildInConnectionProperty {
    //
    CONNECTION_TIMEOUT("conn_timeout", JdbcProperty.get().getConnectionTimeout()),
    USERNAME("swift_username", SwiftJdbcConstants.EMPTY),
    PASSWORD("swift_password", SwiftJdbcConstants.EMPTY),
    STATEMENT_MAX_IDLE("sttm_max_idle", JdbcProperty.get().getStatementMaxIdle()),
    GRAMMAR("grammar", "com.fr.swift.jdbc.checker.impl.SwiftGrammarChecker"),
    PRINCIPAL("principal", null),
    KEYTAB("keytab", null),
    URL("swift_url", null);

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
        String value = properties.getProperty(this.propertyName, null);
        if (null == value) {
            return properties.getProperty(name().toLowerCase(), defaultValue != null ? defaultValue.toString() : null);
        }
        return value;
    }
}
