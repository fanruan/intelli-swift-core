package com.fr.swift.cloud.quartz.config;

import com.fr.swift.cloud.config.ConfigInputUtil;
import com.fr.swift.cloud.util.Crasher;

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

    private String executorMachineId;

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
            executorMachineId = properties.getProperty("executor.machine.id", "CLOUD_1");
        } catch (IOException e) {
            Crasher.crash(e);
            executorMachineId = "CLOUD_1";
        }
    }

    public Properties getProperties() {
        return properties;
    }

    public String getExecutorMachineId() {
        return executorMachineId;
    }
}

