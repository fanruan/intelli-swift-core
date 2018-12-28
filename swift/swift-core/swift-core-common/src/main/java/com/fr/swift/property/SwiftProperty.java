package com.fr.swift.property;

import com.fr.swift.config.SwiftConfigConstants;
import com.fr.swift.util.Crasher;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

/**
 * This class created on 2018/6/8
 *
 * @author Lucifer
 * @description
 * @since Advanced FineBI 5.0
 */
public class SwiftProperty {

    private Properties properties;

    private boolean isCluster;

    private String clusterId;

    private String masterAddress;

    private String rpcAddress;

    private boolean selfStart;

    private String configDbDriverClass;

    private String configDbUsername;

    private String configDbPasswd;

    private String configDbDialect;

    private String configDbJdbcUrl;

    private int rpcMaxObjectSize;

    /**
     * swift业务相关service
     */
    private Set<String> swiftServiceNames;

    /**
     * swift中server相关服务
     */
    private Set<String> serverServiceNames;

    private String redisIp;
    private int redisPort;
    private String redisPassward;
    private int redisTimeout;

    private SwiftProperty() {
        initProperties();
    }

    private static final SwiftProperty INSTANCE = new SwiftProperty();

    public static SwiftProperty getProperty() {
        return INSTANCE;
    }

    private void initProperties() {
        properties = new Properties();
        InputStream swiftIn = SwiftProperty.class.getClassLoader().getResourceAsStream("swift.properties");
        InputStream swiftBeansIn = SwiftProperty.class.getClassLoader().getResourceAsStream("swift-beans.properties");
        try {
            properties.load(swiftBeansIn);
            properties.load(swiftIn);
            initSwiftServiceNames();
            initServerServiceNames();
            initRpcMaxObjectSize();
            initRpcAddress();
            initCluster();
            initClusterId();
            initMasterAddress();
            initSelfStart();
            initConfigDbDriverClass();
            initConfigDbUsername();
            initConfigDbPasswd();
            initConfigDbDialect();
            initConfigDbJdbcUrl();
            initRedisConf();
        } catch (IOException e) {
            Crasher.crash(e);
        }
    }

    public void setSwiftServiceNames(Set<String> swiftServiceNames) {
        if (swiftServiceNames != null) {
            this.swiftServiceNames = swiftServiceNames;
        }
    }

    public void setServerServiceNames(Set<String> serverServiceNames) {
        this.serverServiceNames = serverServiceNames;
    }

    private void initSwiftServiceNames() {
        String swiftServiceName = properties.getProperty("swift.service.name");
        swiftServiceNames = new HashSet<String>();
        String[] swiftServiceNameArray = swiftServiceName.split(",");
        swiftServiceNames.addAll(Arrays.asList(swiftServiceNameArray));
    }

    private void initServerServiceNames() {
        String serverServiceName = properties.getProperty("server.service.name");
        serverServiceNames = new HashSet<String>();
        String[] serverServiceNameArray = serverServiceName.split(",");
        serverServiceNames.addAll(Arrays.asList(serverServiceNameArray));
    }

    private void initRpcMaxObjectSize() {
        this.rpcMaxObjectSize = Integer.parseInt(properties.getProperty("swift.rpcMaxObjectSize"));
    }

    public void setRpcAddress(String currentId) {
        this.rpcAddress = currentId;
    }

    private void initRpcAddress() {
        this.rpcAddress = properties.getProperty("swift.rpc_server_address");
    }

    private void initCluster() {
        this.isCluster = Boolean.parseBoolean(properties.getProperty("swift.is_cluster"));
    }

    public void setCluster(boolean cluster) {
        this.isCluster = cluster;
    }

    //TODO 配置要修改
    private void initClusterId() {
        if (isCluster) {
            this.clusterId = properties.getProperty("swift.clusterId");
        } else {
            this.clusterId = SwiftConfigConstants.LOCALHOST;
        }
    }

    public void setClusterId(String clusterId) {
        this.clusterId = clusterId;
    }

    public void setMasterAddress(String masterId) {
        this.masterAddress = masterId;
    }

    private void initMasterAddress() {
        this.masterAddress = properties.getProperty("swift.master_address");
    }

    private void initSelfStart() {
        this.selfStart = Boolean.parseBoolean(properties.getProperty("swift.selfStart"));
    }

    private void initConfigDbDriverClass() {
        this.configDbDriverClass = properties.getProperty("swift.configDb.driver");
    }

    private void initConfigDbUsername() {
        this.configDbUsername = properties.getProperty("swift.configDb.username");
    }

    private void initConfigDbPasswd() {
        this.configDbPasswd = properties.getProperty("swift.configDb.passwd");
    }

    private void initConfigDbDialect() {
        this.configDbDialect = properties.getProperty("swift.configDb.dialect");
    }

    private void initConfigDbJdbcUrl() {
        this.configDbJdbcUrl = properties.getProperty("swift.configDb.url");
    }

    private void initRedisConf() {
        this.redisIp = properties.getProperty("redis.ip");
        this.redisPort = Integer.valueOf(properties.getProperty("redis.port"));
        this.redisPassward = properties.getProperty("redis.passward");
        this.redisTimeout = Integer.valueOf(properties.getProperty("redis.timeout"));
    }

    public int getRpcMaxObjectSize() {
        return rpcMaxObjectSize;
    }

    public String getServerAddress() {
        return rpcAddress;
    }

    public boolean isCluster() {
        return isCluster;
    }

    public String getClusterId() {
        return clusterId;
    }

    public String getMasterAddress() {
        return masterAddress;
    }

    public boolean isSelfStart() {
        return selfStart;
    }

    public String getConfigDbDriverClass() {
        return configDbDriverClass;
    }

    public String getConfigDbUsername() {
        return configDbUsername;
    }

    public String getConfigDbPasswd() {
        return configDbPasswd;
    }

    public String getConfigDbDialect() {
        return configDbDialect;
    }

    public String getConfigDbJdbcUrl() {
        return configDbJdbcUrl;
    }

    public Set<String> getSwiftServiceNames() {
        return new HashSet<String>(swiftServiceNames);
    }

    public Set<String> getServerServiceNames() {
        return new HashSet<String>(serverServiceNames);
    }

    public String getRedisIp() {
        return redisIp;
    }

    public int getRedisPort() {
        return redisPort;
    }

    public String getRedisPassward() {
        return redisPassward;
    }

    public int getRedisTimeout() {
        return redisTimeout;
    }
}
