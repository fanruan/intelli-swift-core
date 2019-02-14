package com.fr.swift.config.hibernate;

import com.fr.data.pool.DBCPConnectionPoolAttr;
import com.fr.finedb.FineDBProperties;
import com.fr.general.ComparatorUtils;
import com.fr.stable.db.option.DBOption;
import com.fr.swift.beans.annotation.SwiftBean;
import com.fr.swift.property.SwiftProperty;
import com.fr.workspace.WorkContext;

import java.util.Properties;

/**
 * @author yee
 * @date 2018/6/29
 */
@SwiftBean
public class SwiftConfigProperties {
    private DBOption option;
    private boolean selfStart;
    private boolean frEnable = false;

    public SwiftConfigProperties() {
        this.option = new DBOption().addRawProperty("hibernate.connection.autocommit", false);
        this.option = new DBOption().addRawProperty("hibernate.connection.provider_class", "com.fr.third.alibaba.druid.support.hibernate.DruidConnectionProvider");
        this.selfStart = SwiftProperty.getProperty().isSelfStart();
        this.option.setDriverClass(SwiftProperty.getProperty().getConfigDbDriverClass());
        this.option.setDialectClass(SwiftProperty.getProperty().getConfigDbDialect());
        this.option.setUrl(SwiftProperty.getProperty().getConfigDbJdbcUrl());
        this.option.setUsername(SwiftProperty.getProperty().getConfigDbUsername());
        this.option.setPassword(SwiftProperty.getProperty().getConfigDbPasswd());
    }

    public Properties reConfiguration() {
        if (isNeedReConfigure(getOption())) {
            option = getOption();
            return getProperties();
        }
        return null;
    }

    public Properties getProperties() {
        return getOption().getProperties();
    }

    private DBOption getOption() {
        if (selfStart) {
            return this.option;
        }
        try {
            DBOption option = FineDBProperties.getInstance().get();
            frEnable = true;
            return option;
        } catch (Exception e) {
            return frEnable ? option : getDefault();
        }
    }

    private DBOption getDefault() {
        final String string = "jdbc:hsqldb:file://" + WorkContext.getCurrent().getPath() + "/" + "embed" + "/finedb/db";
        final DBCPConnectionPoolAttr dbcpConnectionPoolAttr = new DBCPConnectionPoolAttr();
        return new DBOption().dialectClass("com.fr.third.org.hibernate.dialect.HSQLDialect")
                .driverClass("com.fr.third.org.hsqldb.jdbcDriver").url(string).username("sa")
                .addRawProperty("default_schema", "PUBLIC")
                .addRawProperty("hibernate.connection.provider_class", "com.fr.third.alibaba.druid.support.hibernate.DruidConnectionProvider")
                .addRawProperty("initialSize", dbcpConnectionPoolAttr.getInitialSize())
                .addRawProperty("maxActive", dbcpConnectionPoolAttr.getMaxActive())
                .addRawProperty("minIdle", dbcpConnectionPoolAttr.getMinIdle())
                .addRawProperty("maxWait", dbcpConnectionPoolAttr.getMaxWait())
                .addRawProperty("validationQuery", dbcpConnectionPoolAttr.getValidationQuery())
                .addRawProperty("testOnBorrow", dbcpConnectionPoolAttr.isTestOnBorrow())
                .addRawProperty("testOnReturn", dbcpConnectionPoolAttr.isTestOnReturn())
                .addRawProperty("testWhileIdle", dbcpConnectionPoolAttr.isTestWhileIdle())
                .addRawProperty("timeBetweenEvictionRunsMillis", dbcpConnectionPoolAttr.getTimeBetweenEvictionRunsMillis())
                .addRawProperty("numTestsPerEvictionRun", dbcpConnectionPoolAttr.getNumTestsPerEvictionRun())
                .addRawProperty("minEvictableIdleTimeMillis", dbcpConnectionPoolAttr.getMinEvictableIdleTimeMillis());
    }

    public String getDriverClass() {
        return this.option.getDriverClass();
    }

    public String getDialectClass() {
        return this.option.getDialectClass();
    }

    public String getUrl() {
        return this.option.getUrl();
    }

    public String getUsername() {
        return this.option.getUsername();
    }

    public String getPassword() {
        return this.option.getPassword();
    }

    public boolean isSelfStart() {
        return selfStart;
    }

    private boolean isNeedReConfigure(DBOption option) {
        return !ComparatorUtils.equals(this.option.getDriverClass(), option.getDriverClass())
                || !ComparatorUtils.equals(this.option.getUrl(), option.getUrl())
                || !ComparatorUtils.equals(this.option.getDialectClass(), option.getDialectClass())
                || !ComparatorUtils.equals(this.option.getUsername(), option.getUsername())
                || !ComparatorUtils.equals(this.option.getPassword(), option.getPassword());
    }
}
