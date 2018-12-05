package com.fr.swift.config.oper.impl;

import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.util.Crasher;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * @author yee
 * @date 2018-11-28
 */
public final class VersionConfigProperty {

    private static VersionConfigProperty property;

    static {
        try {
            load();
        } catch (IOException e) {
            SwiftLoggers.getLogger().error(e);
        }
    }

    private String version;
    private Class restrictions;
    private Class matchMode;
    private Class order;
    private Class criterion;
    private Class nonUniqueObjectException;
    private Class constraintViolationException;
    private Class entityExistsException;

    private VersionConfigProperty(String version, String restrictions, String matchMode, String criterion, String order, String nonUniqueObjectException, String constraintViolationException, String entityExistsException) {
        this.version = version;
        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        try {
            this.restrictions = loader.loadClass(restrictions);
            this.matchMode = loader.loadClass(matchMode);
            this.order = loader.loadClass(order);
            this.criterion = loader.loadClass(criterion);
            this.nonUniqueObjectException = loader.loadClass(nonUniqueObjectException);
            this.constraintViolationException = loader.loadClass(constraintViolationException);
            this.entityExistsException = loader.loadClass(entityExistsException);
        } catch (Exception e) {
            Crasher.crash(e);
        }
    }

    private static void load() throws IOException {
        InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream("com-fr-swift-version.properties");
        try {
            if (null != is) {
                Properties properties = new Properties();
                properties.load(is);
                String version = properties.getProperty("version");
                String restrictions = properties.getProperty("hibernate.restrictions");
                String matchMode = properties.getProperty("hibernate.matchMode");
                String order = properties.getProperty("hibernate.order");
                String criterion = properties.getProperty("hibernate.criterion");
                String nonUniqueObjectException = properties.getProperty("hibernate.exp.nonUnique");
                String constraintViolationException = properties.getProperty("hibernate.exp.constraint");
                String entityExistsException = properties.getProperty("jpa.exp.exists");
                property = new VersionConfigProperty(version, restrictions, matchMode, criterion, order, nonUniqueObjectException, constraintViolationException, entityExistsException);
            }
        } finally {
            if (null != is) {
                is.close();
            }
        }
    }

    public static VersionConfigProperty get() {
        return property;
    }

    public String getVersion() {
        return version;
    }

    public Class getRestrictions() {
        return restrictions;
    }


    public Class getMatchMode() {
        return matchMode;
    }

    public Class getOrder() {
        return order;
    }

    public Class getConstraintViolationException() {
        return constraintViolationException;
    }

    public Class getNonUniqueObjectException() {
        return nonUniqueObjectException;
    }

    public Class getCriterion() {
        return criterion;
    }

    public Class getEntityExistsException() {
        return entityExistsException;
    }
}
