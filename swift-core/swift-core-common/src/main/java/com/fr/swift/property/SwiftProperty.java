package com.fr.swift.property;

import com.fr.swift.config.SwiftConfigConstants;
import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.util.Crasher;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
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
        InputStream swiftIn = null;
        try {
            SwiftLoggers.getLogger().info("read external swift.properties!");
            swiftIn = new BufferedInputStream(new FileInputStream(("swift.properties")));
        } catch (FileNotFoundException e) {
            SwiftLoggers.getLogger().warn("Failed to read external swift.properties, read internal swift.properties instead!");
            swiftIn = SwiftProperty.class.getClassLoader().getResourceAsStream("swift.properties");
        }
        InputStream swiftBeansIn = null;
        try {
            SwiftLoggers.getLogger().info("read external swift-beans.properties!");
            swiftBeansIn = new BufferedInputStream(new FileInputStream(("swift-beans.properties")));
        } catch (FileNotFoundException e) {
            SwiftLoggers.getLogger().warn("Failed to read external swift.properties, read internal swift-beans.properties instead!");
            swiftBeansIn = SwiftProperty.class.getClassLoader().getResourceAsStream("swift-beans.properties");
        }

        try (InputStream in = swiftIn; InputStream beanIn = swiftBeansIn) {
            properties.load(in);
            properties.load(beanIn);
            initSwiftServiceNames();
            initServerServiceNames();
            initRpcMaxObjectSize();
            initRpcAddress();
            initCluster();
            initClusterId();
            initMasterAddress();
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
        this.rpcAddress = properties.getProperty("swift.rpcServerAddress");
    }

    private void initCluster() {
        this.isCluster = Boolean.parseBoolean(properties.getProperty("swift.isCluster"));
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
        this.masterAddress = properties.getProperty("swift.masterAddress");
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
