package com.fr.bi.cluster;

import com.fr.file.ClusterService;

/**
 * Created by Hiram on 2015/2/27.
 */
public interface ClusterHostManagerInterface {
    String getIp();

    int getPort();

    void setPort(int port);

    String getWebAppName();

    String getServiceName();

    boolean isSelf();

    ClusterService getHostClusterService();

    String getLocalRpcPort();

    String getBuildCubeIp();

    int getBuildCubePort();

    boolean isBuildCube();

}