package com.fr.swift.config.convert.hibernate;

import com.fr.data.pool.DBCPConnectionPoolAttr;
import com.fr.finedb.FineDBProperties;
import com.fr.general.ComparatorUtils;
import com.fr.stable.db.option.DBOption;
import com.fr.third.springframework.beans.factory.annotation.Autowired;
import com.fr.third.springframework.beans.factory.annotation.Value;
import com.fr.third.springframework.stereotype.Service;
import com.fr.workspace.WorkContext;

import java.util.Properties;

/**
 * @author yee
 * @date 2018/6/29
 */
@Service
public class SwiftConfigProperties {
    private DBOption option;
    private boolean selfStart;

    public SwiftConfigProperties() {
        this.option = new DBOption().addRawProperty("hibernate.connection.autocommit", false);
        this.option = new DBOption().addRawProperty("hibernate.connection.provider_class", "com.fr.third.alibaba.druid.support.hibernate.DruidConnectionProvider");
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
            return FineDBProperties.getInstance().get();
        } catch (Exception e) {
            return getDefault();
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

    @Autowired
    public void setDriverClass(@Value("${swift.configDb.driver}") String driverClass) {
        this.option.setDriverClass(driverClass);
    }

    public String getDialectClass() {
        return this.option.getDialectClass();
    }

    @Autowired
    public void setDialectClass(@Value("${swift.configDb.dialect}") String dialectClass) {
        this.option.setDialectClass(dialectClass);
    }

    public String getUrl() {
        return this.option.getUrl();
    }

    @Autowired
    public void setUrl(@Value("${swift.configDb.url}") String url) {
        this.option.setUrl(url);
    }

    public String getUsername() {
        return this.option.getUsername();
    }

    @Autowired
    public void setUsername(@Value("${swift.configDb.username}") String username) {
        this.option.setUsername(username);
    }

    public String getPassword() {
        return this.option.getPassword();
    }

    @Autowired
    public void setPassword(@Value("${swift.configDb.passwd}") String password) {
        this.option.setPassword(password);
    }

    public boolean isSelfStart() {
        return selfStart;
    }

    @Autowired
    public void setSelfStart(@Value("${swift.selfStart}") boolean selfStart) {
        this.selfStart = selfStart;
    }

    private boolean isNeedReConfigure(DBOption option) {
        return !ComparatorUtils.equals(this.option.getDriverClass(), option.getDriverClass())
                || !ComparatorUtils.equals(this.option.getUrl(), option.getUrl())
                || !ComparatorUtils.equals(this.option.getDialectClass(), option.getDialectClass())
                || !ComparatorUtils.equals(this.option.getUsername(), option.getUsername())
                || !ComparatorUtils.equals(this.option.getPassword(), option.getPassword());
    }
}
