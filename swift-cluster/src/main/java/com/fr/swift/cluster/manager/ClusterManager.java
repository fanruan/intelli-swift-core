package com.fr.swift.cluster.manager;

/**
 * This class created on 2018/7/18
 *
 * @author Lucifer
 * @description
 * @since Advanced FineBI 5.0
 */
public interface ClusterManager {

    void startUp();

    void shutDown();

    boolean isRunning();
}
