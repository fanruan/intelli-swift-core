package com.fr.swift.node;

import com.fr.general.ComparatorUtils;
import com.fr.swift.ClusterNodeManager;
import com.fr.swift.context.SwiftContext;
import com.fr.swift.property.SwiftProperty;
import com.fr.swift.util.Crasher;
import com.fr.third.jodd.util.StringUtil;

/**
 * This class created on 2018/6/13
 *
 * @author Lucifer
 * @description
 * @since Advanced FineBI 5.0
 */
public class SwiftClusterNodeManager implements ClusterNodeManager<SwiftClusterNode> {

    private SwiftClusterNode masterNode;
    private final SwiftClusterNode currentNode;
    private boolean isCluster;

    private static final SwiftClusterNodeManager INSTANCE = new SwiftClusterNodeManager();

    private SwiftClusterNodeManager() {
        SwiftProperty swiftProperty = SwiftContext.get().getBean("swiftProperty", SwiftProperty.class);
        this.isCluster = swiftProperty.isCluster();
        String masterAddress = swiftProperty.getMasterAddress();
        String serverAddress = swiftProperty.getServerAddress();
        if (masterAddress != null) {
            String[] addressArray = StringUtil.split(masterAddress, ":");
            String ip = addressArray[0];
            int port = Integer.parseInt(addressArray[1]);
            this.masterNode = new SwiftClusterNodeImpl(masterAddress, masterAddress, ip, port);
        }

        if (serverAddress == null) {
            Crasher.crash("ServerAddress is null ! Please check!");
        }
        String[] addressArray = StringUtil.split(serverAddress, ":");
        String ip = addressArray[0];
        int port = Integer.parseInt(addressArray[1]);
        this.currentNode = new SwiftClusterNodeImpl(serverAddress, serverAddress, ip, port);
    }

    public static SwiftClusterNodeManager getInstance() {
        return INSTANCE;
    }

    @Override
    public void setMasterNode(SwiftClusterNode masterNode) {
        this.masterNode = masterNode;
    }

    @Override
    public SwiftClusterNode getMasterNode() {
        return masterNode;
    }

    @Override
    public SwiftClusterNode getCurrentNode() {
        return currentNode;
    }

    @Override
    public String getCurrentId() {
        if (currentNode == null) {
            return null;
        }
        return currentNode.getId();
    }

    @Override
    public String getMasterId() {
        if (masterNode == null) {
            return null;
        }
        return masterNode.getId();
    }

    @Override
    public boolean isCluster() {
        synchronized (SwiftClusterNodeManager.class) {
            return isCluster;
        }
    }

    @Override
    public void setCluster(boolean cluster) {
        synchronized (SwiftClusterNodeManager.class) {
            this.isCluster = cluster;
        }
    }

    @Override
    public boolean isMaster() {
        synchronized (SwiftClusterNodeManager.class) {
            if (masterNode == null) {
                return false;
            }
            return ComparatorUtils.equals(getMasterId(), getCurrentId());
        }
    }
}
