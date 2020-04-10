package com.fr.swift.property;

import com.fr.swift.config.ConfigInputUtil;
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

    private String rpcAddress;

    private int rpcMaxObjectSize;

    private String cubesPath;

    /**
     * swift业务相关service
     */
    private Set<String> swiftServiceNames;

    /**
     * swift中server相关服务
     */
    private Set<String> serverServiceNames;

    private String[] executorTaskType;
    private String machineId;
    private String collateTime;

    private SwiftProperty() {
        initProperties();
    }

    private static final SwiftProperty INSTANCE = new SwiftProperty();

    public static SwiftProperty get() {
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
            initCubesPath();
            initMachineId();
            initCollateTime();
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

    private void initCollateTime() {
        collateTime = (String) properties.getOrDefault("swift.collate.time", "2:00:00");
    }

    public String getCollateTime() {
        return collateTime;
    }

    private void initSwiftServiceNames() {
        String swiftServiceName = properties.getProperty("swift.service.name");
        swiftServiceNames = new HashSet<>();
        String[] swiftServiceNameArray = swiftServiceName.split(",");
        swiftServiceNames.addAll(Arrays.asList(swiftServiceNameArray));
    }

    private void initServerServiceNames() {
        String serverServiceName = properties.getProperty("server.service.name");
        serverServiceNames = new HashSet<>();
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

    private void initExecutorTaskType() {
        this.executorTaskType = properties.getProperty("swift.executorTaskType").split(";");
    }

    public void setCluster(boolean cluster) {
        this.isCluster = cluster;
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

    public Set<String> getSwiftServiceNames() {
        return new HashSet<String>(swiftServiceNames);
    }

    public Set<String> getServerServiceNames() {
        return new HashSet<String>(serverServiceNames);
    }

    public String getCubesPath() {
        return cubesPath;
    }

    public String[] getExecutorTaskType() {
        return executorTaskType;
    }

    public String getMachineId() {
        return machineId;
    }
}
