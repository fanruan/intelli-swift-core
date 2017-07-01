package com.fr.bi.cluster.manager;

import com.fr.bi.cluster.ClusterHostManagerInterface;
import com.fr.bi.cluster.utils.ClusterEnv;
import com.fr.bi.cluster.utils.PropertiesUtils;
import com.fr.bi.stable.utils.program.BIStringUtils;
import com.fr.file.ClusterService;

import java.io.File;
import java.util.Properties;

/**
 * 从zooKeeper中获取主机信息  //TO 暂时没实现
 * Created by Hiram on 2015/2/27.
 */
public class ZooKeeperClusterHostManager implements ClusterHostManagerInterface {

    private boolean isSelf;
    private ClusterService hostClusterService;
    private Boolean isBuildCube;
    private String buildCubePort;
    private String buildCubeIp;
    private String localRpcPort;

    public ZooKeeperClusterHostManager() {
        init();
    }

    @Override
    public String getIp() {
        return getHostClusterService().getIp();
    }

    public void setIp(String ip) {
        getHostClusterService().setIp(ip);
    }

    @Override
    public int getPort() {
        return Integer.parseInt(getHostClusterService().getPort());
    }

    public void setPort(int port) {
        getHostClusterService().setPort(String.valueOf(port));
    }

    @Override
    public String getWebAppName() {
        return null;
    }

    @Override
    public String getServiceName() {
        return null;
    }

    @Override
    public boolean isSelf() {
        return isSelf;
    }

    public void setSelf(boolean isSelf) {
        this.isSelf = isSelf;
    }

    @Override
    public ClusterService getHostClusterService() {
        return hostClusterService;
    }

    public String getLocalRpcPort() {
        return localRpcPort;
    }

    @Override
    public String getBuildCubeIp() {
        return buildCubeIp;
    }

    @Override
    public int getBuildCubePort() {
        return Integer.parseInt(buildCubePort);
    }

    @Override
    public boolean isBuildCube() {
        return isBuildCube;
    }

    public void setLocalRpcPort(String localRpcPort) {
        this.localRpcPort = localRpcPort;
    }

    private void init() {
        File infoFile = ClusterEnv.getRedirectInfoFile();
        Properties properties = PropertiesUtils.load(infoFile);
        if (properties == null) {
            return;
        }
        hostClusterService = new ClusterService();
        hostClusterService.setPort(properties.getProperty("port"));
        hostClusterService.setWebAppName(properties.getProperty("webAppName"));
        hostClusterService.setServiceName(properties.getProperty("serviceName"));
        this.isBuildCube = Boolean.valueOf(properties.getProperty("isBuildCube"));
        this.buildCubeIp = properties.getProperty("buildCubeIp");
        this.buildCubePort = properties.getProperty("buildCubeIp");
        if(BIStringUtils.isEmptyString(this.buildCubeIp)|| BIStringUtils.isEmptyString(this.buildCubeIp)){
            this.isBuildCube = this.isSelf;
            this.buildCubeIp = hostClusterService.getIp();
            this.buildCubePort = hostClusterService.getPort();
        }
    }
}