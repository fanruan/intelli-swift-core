package com.fr.swift.property;

import com.fr.swift.beans.annotation.SwiftBean;
import com.fr.swift.util.Crasher;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * This class created on 2018/8/19
 *
 * @author Lucifer
 * @description
 * @since Advanced FineBI 5.0
 */
@SwiftBean
public class ZkProperty {
    private Properties properties;
    private String zookeeperAddress;
    private int sessionTimeout = 5000;
    private int connectionTimeout = 1000;

    public ZkProperty() {
        initProperties();
    }

    private void initProperties() {
        properties = new Properties();
        InputStream swiftIn = SwiftProperty.class.getClassLoader().getResourceAsStream("swift.properties");
        try {
            properties.load(swiftIn);
            this.zookeeperAddress = properties.getProperty("swift.zookeeper_address");
            this.sessionTimeout = Integer.valueOf(properties.getProperty("swift.zookeeper_session_timeout"));
            this.connectionTimeout = Integer.valueOf(properties.getProperty("swift.zookeeper_connection_timeout"));
        } catch (IOException e) {
            Crasher.crash(e);
        }
    }

    public String getZookeeperAddress() {
        return zookeeperAddress;
    }

    public int getSessionTimeout() {
        return sessionTimeout;
    }

    public int getConnectionTimeout() {
        return connectionTimeout;
    }
}
