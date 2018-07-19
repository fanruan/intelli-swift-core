package com.fr.swift.cluster.manager;

/**
 * This class created on 2018/7/18
 *
 * @author Lucifer
 * @description
 * @since Advanced FineBI 5.0
 */
public abstract class AbstractClusterManager implements ClusterManager {

    protected volatile boolean isRunning = false;

    @Override
    public void startUp() {
        installService();
        isRunning = true;
    }

    @Override
    public void shutDown() {
        uninstallService();
        isRunning = false;
    }

    protected abstract void installService();

    protected abstract void uninstallService();

    @Override
    public boolean isRunning() {
        return isRunning;
    }
}
