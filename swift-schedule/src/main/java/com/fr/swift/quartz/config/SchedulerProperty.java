package com.fr.swift.quartz.config;

import com.fr.swift.config.ConfigInputUtil;
import com.fr.swift.util.Crasher;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * @author Heng.J
 * @date 2020/5/13
 * @description
 * @since swift 1.1
 */
public class SchedulerProperty {

    private Properties properties;

    private SchedulerProperty() {
        initProperty();
    }

    private static final SchedulerProperty INSTANCE = new SchedulerProperty();

    public static SchedulerProperty get() {
        return INSTANCE;
    }

    public void initProperty() {
        properties = new Properties();
        InputStream quartzIn = ConfigInputUtil.getConfigInputStream("quartz.properties");
        try (InputStream in = quartzIn) {
            properties.load(in);
        } catch (IOException e) {
            Crasher.crash(e);
        }
    }

    public Properties getProperties() {
        return properties;
    }
}

