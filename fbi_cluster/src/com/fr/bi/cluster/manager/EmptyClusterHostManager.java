package com.fr.bi.cluster.manager;

import com.fr.bi.cluster.ClusterHostManagerInterface;
import com.fr.file.ClusterService;

/**
 * Created by Hiram on 2015/2/28.
 */
public class EmptyClusterHostManager implements ClusterHostManagerInterface {
    private static EmptyClusterHostManager ourInstance = new EmptyClusterHostManager();

    private EmptyClusterHostManager() {
    }

    public static EmptyClusterHostManager getInstance() {
        return ourInstance;
    }

    @Override
    public String getIp() {
        return null;
    }

    @Override
    public int getPort() {
        return 0;
    }

    @Override
    public void setPort(int port) {

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
        return false;
    }

    @Override
    public ClusterService getHostClusterService() {
        return null;
    }

    @Override
    public String getLocalRpcPort() {
        return null;
    }

    @Override
    public String getBuildCubeIp() {
        return null;
    }

    @Override
    public int getBuildCubePort() {
        return 0;
    }

    @Override
    public boolean isBuildCube() {
        return false;
    }
}