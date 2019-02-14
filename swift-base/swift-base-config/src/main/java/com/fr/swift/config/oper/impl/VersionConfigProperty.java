package com.fr.swift.config.oper.impl;

import com.fr.swift.config.oper.ConfigQuery;
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
    private Class session;
    private Class nonUniqueObjectException;
    private Class constraintViolationException;
    private Class entityExistsException;
    private Class<? extends ConfigQuery> query;
    private String defaultRepository;

    private VersionConfigProperty(String version, String session, String nonUniqueObjectException, String constraintViolationException, String entityExistsException, String defaultRepository) {
        this.version = version;
        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        try {
            this.session = loader.loadClass(session);
            this.nonUniqueObjectException = loader.loadClass(nonUniqueObjectException);
            this.constraintViolationException = loader.loadClass(constraintViolationException);
            this.entityExistsException = loader.loadClass(entityExistsException);
            this.query = (Class<? extends ConfigQuery>) loader.loadClass("com.fr.swift.config.hibernate.HibernateQuery");
            this.defaultRepository = defaultRepository;
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
                String restrictions = properties.getProperty("hibernate.session");
                String nonUniqueObjectException = properties.getProperty("hibernate.exp.nonUnique");
                String constraintViolationException = properties.getProperty("hibernate.exp.constraint");
                String entityExistsException = properties.getProperty("jpa.exp.exists");
                String defaultRepo = properties.getProperty("default.repo");
                property = new VersionConfigProperty(version, restrictions, nonUniqueObjectException, constraintViolationException, entityExistsException, defaultRepo);
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

    public Class getSession() {
        return session;
    }

    public Class getConstraintViolationException() {
        return constraintViolationException;
    }

    public Class getNonUniqueObjectException() {
        return nonUniqueObjectException;
    }

    public Class<? extends ConfigQuery> getQuery() {
        return query;
    }

    public Class getEntityExistsException() {
        return entityExistsException;
    }

    public String getDefaultRepository() {
        return defaultRepository;
    }
}
