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

    private VersionConfigProperty(String version, String restrictions, String matchMode, String order) {
        this.version = version;
        ClassLoader loader = VersionConfigProperty.class.getClassLoader();
        try {
            this.restrictions = loader.loadClass(restrictions);
            this.matchMode = loader.loadClass(matchMode);
            this.order = loader.loadClass(order);
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
                property = new VersionConfigProperty(version, restrictions, matchMode, order);
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
}
