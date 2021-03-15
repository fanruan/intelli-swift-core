package com.fr.swift.cloud.config;

import java.util.Properties;

/**
 * @author yee
 * @date 2019-01-02
 */
public interface ConfigLoader<ConfigBean> {
    ConfigBean loadFromProperties(Properties properties);
}