package com.fr.swift.config.hibernate;

import com.fr.finedb.FineDBProperties;
import com.fr.general.ComparatorUtils;
import com.fr.stable.db.option.DBOption;
import com.fr.swift.beans.annotation.SwiftBean;
import com.fr.swift.property.SwiftProperty;

import java.util.Properties;

/**
 * @author yee
 * @date 2018/6/29
 */
@SwiftBean
public class SwiftConfigProperties {
    private DBOption option;
    private boolean selfStart;

    private SwiftProperty swiftProperty = SwiftProperty.getProperty();

    public SwiftConfigProperties() {
        this.option = new DBOption().addRawProperty("hibernate.connection.autocommit", false);
        this.option = new DBOption().addRawProperty("hibernate.connection.provider_class", "com.fr.third.alibaba.druid.support.hibernate.DruidConnectionProvider");
        this.selfStart = swiftProperty.isSelfStart();
        this.option.setDriverClass(swiftProperty.getConfigDbDriverClass());
        this.option.setDialectClass(swiftProperty.getConfigDbDialect());
        this.option.setUrl(swiftProperty.getConfigDbJdbcUrl());
        this.option.setUsername(swiftProperty.getConfigDbUsername());
        this.option.setPassword(swiftProperty.getConfigDbPasswd());
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
        return selfStart ? this.option : FineDBProperties.getInstance().get();
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
