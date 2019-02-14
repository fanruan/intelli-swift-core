package com.fr.swift.config.bean;

import com.fr.swift.converter.ObjectConverter;
import com.fr.swift.log.SwiftLoggers;

import java.lang.reflect.Constructor;

/**
 * @author yee
 * @date 2018-11-26
 */
public class SwiftConfigBean implements ObjectConverter {
    public static final Class TYPE = entityType();
    private String configKey;
    private String configValue;

    public SwiftConfigBean() {
    }

    public SwiftConfigBean(String configKey, String configValue) {
        this.configKey = configKey;
        this.configValue = configValue;
    }

    private static Class entityType() {
        try {
            return Class.forName("com.fr.swift.config.entity.SwiftConfigEntity");
        } catch (ClassNotFoundException e) {
            return null;
        }
    }

    public String getConfigKey() {
        return configKey;
    }

    public void setConfigKey(String configKey) {
        this.configKey = configKey;
    }

    public String getConfigValue() {
        return configValue;
    }

    public void setConfigValue(String configValue) {
        this.configValue = configValue;
    }

    @Override
    public Object convert() {
        try {
            Constructor constructor = TYPE.getDeclaredConstructor(SwiftConfigBean.class);
            return constructor.newInstance(this);
        } catch (Exception e) {
            SwiftLoggers.getLogger().error(e);
        }
        return null;
    }
}
