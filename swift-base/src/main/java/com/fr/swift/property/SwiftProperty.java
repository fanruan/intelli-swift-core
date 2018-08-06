package com.fr.swift.property;

import com.fr.third.springframework.beans.factory.annotation.Autowired;
import com.fr.third.springframework.beans.factory.annotation.Value;
import com.fr.third.springframework.stereotype.Service;

/**
 * This class created on 2018/6/8
 *
 * @author Lucifer
 * @description
 * @since Advanced FineBI 5.0
 */
@Service
public class SwiftProperty {

    private boolean isCluster;

    private String clusterId;

    private String masterAddress;

    private String rpcAddress;

    private String httpAddress;

    private String configDbDriverClass;

    private String configDbUsername;

    private String configDbPasswd;

    private String configDbJdbcUrl;

    private int rpcMaxObjectSize;

    @Autowired
    public void setRpcAddress(@Value("${swift.rpc_server_address}") String rpcAddress) {
        this.rpcAddress = rpcAddress;
    }

    @Autowired
    public void setMasterAddress(@Value("${swift.master_address}") String masterAddress) {
        this.masterAddress = masterAddress;
    }

    public boolean isCluster() {
        return isCluster;
    }

    public String getClusterId() {
        return clusterId;
    }

    @Autowired
    public void setCluster(@Value("${swift.is_cluster}") String cluster) {
        this.isCluster = Boolean.parseBoolean(cluster);
    }

    public String getConfigDbDriverClass() {
        return configDbDriverClass;
    }

    @Autowired
    public void setConfigDbDriverClass(@Value("${swift.configDb.driver}") String configDbDriverClass) {
        this.configDbDriverClass = configDbDriverClass;
    }

    public String getConfigDbUsername() {
        return configDbUsername;
    }

    @Autowired
    public void setConfigDbUsername(@Value("${swift.configDb.username}") String configDbUsername) {
        this.configDbUsername = configDbUsername;
    }

    public String getConfigDbPasswd() {
        return configDbPasswd;
    }

    @Autowired
    public void setConfigDbPasswd(@Value("${swift.configDb.passwd}") String configDbPasswd) {
        this.configDbPasswd = configDbPasswd;
    }

    public String getConfigDbJdbcUrl() {
        return configDbJdbcUrl;
    }

    @Autowired
    public void setConfigDbJdbcUrl(@Value("${swift.configDb.url}") String configDbJdbcUrl) {
        this.configDbJdbcUrl = configDbJdbcUrl;
    }

    public void setClusterId(@Value("${swift.clusterId}") String clusterId) {
        this.clusterId = clusterId;
    }

    public String getServerAddress() {
        return rpcAddress;
    }

    public String getMasterAddress() {
        return masterAddress;
    }

    public String getHttpAddress() {
        return httpAddress;
    }

    @Autowired
    public void setHttpAddress(@Value("${swift.http_server_address}") String httpAddress) {
        this.httpAddress = httpAddress;
    }

    public int getRpcMaxObjectSize() {
        return rpcMaxObjectSize;
    }

    @Autowired
    public void setRpcMaxObjectSize(@Value("${swift.rpcMaxObjectSize}") int rpcMaxObjectSize) {
        this.rpcMaxObjectSize = rpcMaxObjectSize;
    }
}
