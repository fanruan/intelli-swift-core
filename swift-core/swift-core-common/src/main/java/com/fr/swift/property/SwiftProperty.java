package com.fr.swift.property;

import com.fr.swift.config.SwiftConfigConstants;
import com.fr.swift.util.ConfigInputUtil;
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

    private String cubesPath;

    private boolean isMigration;

    private Set<String> migrationTableName;

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
    private String[] executorTaskType;
    private String machineId;

    private SwiftProperty() {
        initProperties();
    }

    private static final SwiftProperty INSTANCE = new SwiftProperty();

    public static SwiftProperty getProperty() {
        return INSTANCE;
    }

    private void initProperties() {
        properties = new Properties();
        InputStream swiftIn = ConfigInputUtil.getConfigInputStream("swift.properties");
        InputStream swiftBeansIn = ConfigInputUtil.getConfigInputStream("swift-beans.properties");
        try (InputStream in = swiftIn; InputStream beanIn = swiftBeansIn) {
            properties.load(in);
            properties.load(beanIn);
            initSwiftServiceNames();
            initServerServiceNames();
            initRpcMaxObjectSize();
            initRpcAddress();
            initCluster();
            initExecutorTaskType();
            initClusterId();
            initMasterAddress();
            initCubesPath();
            initNeedMigration();
            initMigrationTableName();
            initMachineId();
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

    private void initCubesPath() {
        this.cubesPath = properties.getProperty("swift.cubesPath");
    }

    private void initNeedMigration() {
        this.isMigration = Boolean.parseBoolean(properties.getProperty("swift.isMigration"));
    }

    private void initMigrationTableName() {
        String[] tableNames = properties.getProperty("swift.migrationTableName").split(";");
        this.migrationTableName = new HashSet<>(Arrays.asList(tableNames));
    }

    private void initExecutorTaskType() {
        this.executorTaskType = properties.getProperty("swift.executorTaskType").split(";");
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

    private void initMachineId() {
        this.machineId = properties.getProperty("swift.machine.id", rpcAddress);
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

    public String getCubesPath() {
        return cubesPath;
    }

    public boolean isMigration() {
        return isMigration;
    }

    public Set<String> getMigrationTableSet() {
        return migrationTableName;
    }

    public String[] getExecutorTaskType() {
        return executorTaskType;
    }

    public String getMachineId() {
        return machineId;
    }
}
