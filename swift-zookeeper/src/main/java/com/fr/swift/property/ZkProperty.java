package com.fr.swift.property;

import com.fr.third.springframework.beans.factory.annotation.Autowired;
import com.fr.third.springframework.beans.factory.annotation.Value;
import com.fr.third.springframework.stereotype.Service;

/**
 * This class created on 2018/8/19
 *
 * @author Lucifer
 * @description
 * @since Advanced FineBI 5.0
 */
@Service
public class ZkProperty {
    private String zookeeperAddress;

    private int sessionTimeout = 5000;
    private int connectionTimeout = 1000;

    @Autowired
    public void setZookeeperAddress(@Value("${swift.zookeeper_address}") String zookeeperAddress) {
        this.zookeeperAddress = zookeeperAddress;
    }

    public String getZookeeperAddress() {
        return zookeeperAddress;
    }

    public int getSessionTimeout() {
        return sessionTimeout;
    }

    @Autowired(required = false)
    public void setSessionTimeout(@Value("${swift.zookeeper_session_timeout}") int sessionTimeout) {
        this.sessionTimeout = sessionTimeout;
    }

    public int getConnectionTimeout() {
        return connectionTimeout;
    }

    @Autowired(required = false)
    public void setConnectionTimeout(@Value("${swift.zookeeper_connection_timeout}") int connectionTimeout) {
        this.connectionTimeout = connectionTimeout;
    }
}
