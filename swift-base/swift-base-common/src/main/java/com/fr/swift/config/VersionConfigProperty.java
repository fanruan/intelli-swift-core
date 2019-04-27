package com.fr.swift.config;

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
    private Class jsonProperty;
    private String defaultRepository;
    private Class objectMapper;

    private VersionConfigProperty(String version) {
        this.version = version;
    }

    private static void load() throws IOException {
        InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream("com-fr-swift-version.properties");
        try {
            if (null != is) {
                Properties properties = new Properties();
                properties.load(is);
                String version = properties.getProperty("version");
                String session = properties.getProperty("hibernate.session");
                String nonUniqueObjectException = properties.getProperty("hibernate.exp.nonUnique");
                String constraintViolationException = properties.getProperty("hibernate.exp.constraint");
                String entityExistsException = properties.getProperty("jpa.exp.exists");
                String defaultRepo = properties.getProperty("default.repo");
                String jsonProperty = properties.getProperty("json.property");
                String objectMapper = properties.getProperty("json.objectMapper");
                property = new VersionConfigProperty(version);
                ClassLoader loader = Thread.currentThread().getContextClassLoader();
                property.session = loader.loadClass(session);
                property.nonUniqueObjectException = loader.loadClass(nonUniqueObjectException);
                property.constraintViolationException = loader.loadClass(constraintViolationException);
                property.entityExistsException = loader.loadClass(entityExistsException);
                property.defaultRepository = defaultRepo;
                property.jsonProperty = loader.loadClass(jsonProperty);
                property.objectMapper = loader.loadClass(objectMapper);
            }
        } catch (ClassNotFoundException e) {
            Crasher.crash(e);
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

    public Class getEntityExistsException() {
        return entityExistsException;
    }

    public String getDefaultRepository() {
        return defaultRepository;
    }

    public Class getJsonProperty() {
        return jsonProperty;
    }

    public Class getObjectMapper() {
        return objectMapper;
    }
}
