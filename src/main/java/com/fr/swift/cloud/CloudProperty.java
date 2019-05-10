package com.fr.swift.cloud;

import com.fr.swift.property.SwiftProperty;
import com.fr.swift.util.Crasher;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * This class created on 2019/5/8
 *
 * @author Lucifer
 * @description
 */
public class CloudProperty {

    private Properties properties;
    private String kafkaServerUrl;
    private int kafkaServerPort;
    private String kafkaGroupId;

    private void initProperties() {
        properties = new Properties();
        InputStream cloudIn = SwiftProperty.class.getClassLoader().getResourceAsStream("cloud.properties");
        try {
            properties.load(cloudIn);
            kafkaServerUrl = properties.getProperty("kafka.server.url", "127.0.0.1");
            kafkaServerPort = Integer.valueOf(properties.getProperty("kafka.server.port", "9092"));
            kafkaGroupId = properties.getProperty("kafka.group.id", SwiftProperty.getProperty().getClusterId());
        } catch (IOException e) {
            kafkaServerUrl = "127.0.0.1";
            kafkaServerPort = 9092;
            kafkaGroupId = SwiftProperty.getProperty().getClusterId();
            Crasher.crash(e);
        }
    }

    private CloudProperty() {
        initProperties();
    }

    private static final CloudProperty INSTANCE = new CloudProperty();

    public static CloudProperty getProperty() {
        return INSTANCE;
    }

    public String getKafkaServerUrl() {
        return kafkaServerUrl;
    }

    public int getKafkaServerPort() {
        return kafkaServerPort;
    }

    public String getKafkaGroupId() {
        return kafkaGroupId;
    }
}
