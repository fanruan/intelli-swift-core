package com.fr.swift.zk;

import org.I0Itec.zkclient.IZkChildListener;
import org.I0Itec.zkclient.IZkDataListener;
import org.I0Itec.zkclient.IZkStateListener;
import org.I0Itec.zkclient.ZkClient;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.data.ACL;

import java.util.List;

/**
 * This class created on 2018/8/17
 *
 * @author Lucifer
 * @description
 * @since Advanced FineBI 5.0
 */
public class SwiftZkClient {
    private ZkClient zkClient;
    private static int SESSION_TIMEOUT = 5000;
    private static int CONNECTION_TIMEOUT = 1000;

    public SwiftZkClient(String zkAddress, int sessionTimeout, int connectionTimeout) {
        zkClient = new ZkClient(zkAddress, sessionTimeout, connectionTimeout);
    }

    public SwiftZkClient(String zkAddress, int sessionTimeout) {
        this(zkAddress, sessionTimeout, CONNECTION_TIMEOUT);
    }

    public SwiftZkClient(String zkAddress) {
        this(zkAddress, SESSION_TIMEOUT, CONNECTION_TIMEOUT);
    }

    public void subscribeDataChanges(String path, IZkDataListener listener) {
        zkClient.subscribeDataChanges(path, listener);
    }

    public void unsubscribeDataChanges(String path, IZkDataListener dataListener) {
        zkClient.unsubscribeDataChanges(path, dataListener);
    }

    public void subscribeChildChanges(String path, IZkChildListener childListener) {
        zkClient.subscribeChildChanges(path, childListener);
    }

    public void unsubscribeChildChanges(String path, IZkChildListener childListener) {
        zkClient.unsubscribeChildChanges(path, childListener);
    }

    public void subscribeStateChanges(IZkStateListener listener) {
        zkClient.subscribeStateChanges(listener);
    }

    public void unsubscribeStateChanges(IZkStateListener stateListener) {
        zkClient.unsubscribeStateChanges(stateListener);
    }

    public void unsubscribeAll() {
        zkClient.unsubscribeAll();
    }

    public String create(final String path, Object data, final CreateMode mode) {
        return zkClient.create(path, data, mode);

    }

    public String create(final String path, Object data, final List<ACL> acl, final CreateMode mode) {
        return zkClient.create(path, data, acl, mode);
    }

    public void createEphemeral(final String path) {
        zkClient.createEphemeral(path);
    }

    public void createEphemeral(final String path, final Object data) {
        zkClient.createEphemeral(path, data);
    }

    public void createPersistent(String path) {
        zkClient.createPersistent(path);
    }

    public void createPersistent(String path, boolean createParents) {
        zkClient.createPersistent(path, createParents);
    }

    public void createPersistent(String path, Object data) {
        zkClient.createPersistent(path, data);
    }

    public String createPersistentSequential(String path, Object data) {
        return zkClient.createPersistentSequential(path, data);
    }

    public String createEphemeralSequential(final String path, final Object data) {
        return zkClient.createEphemeralSequential(path, data);
    }

    public boolean exists(String path) {
        return zkClient.exists(path);
    }

    public <T> T readData(String path) {
        return zkClient.readData(path, false);
    }

    public void close() {
        zkClient.close();
    }
}
