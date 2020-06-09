package com.fr.swift.cluster.base.selector;

import com.fr.swift.basics.Selector;
import com.fr.swift.cluster.base.node.ClusterNode;
import com.fr.swift.cluster.base.node.ClusterNodeContainer;
import com.fr.swift.cluster.base.node.ClusterNodeManager;
import com.fr.swift.cluster.base.node.SwiftClusterNodeImpl;
import com.fr.swift.property.SwiftProperty;

import java.util.Collections;
import java.util.Map;

/**
 * @author lucifer
 * @date 2020/5/8
 * @description
 * @since swift 1.1
 */
public class ClusterNodeSelector implements Selector<ClusterNodeManager> {

    private ClusterNodeManager clusterNodeManager;

    private ClusterNodeSelector() {
        this.clusterNodeManager = new DefaultNodeManager();
    }

    private static final ClusterNodeSelector INSTANCE = new ClusterNodeSelector();

    public static ClusterNodeSelector getInstance() {
        return INSTANCE;
    }

    public ClusterNodeContainer getContainer() {
        return clusterNodeManager;
    }

    @Override
    public ClusterNodeManager getFactory() {
        return clusterNodeManager;
    }

    @Override
    public void switchFactory(ClusterNodeManager factory) {
        this.clusterNodeManager = factory;
    }

    private class DefaultNodeManager implements ClusterNodeManager {
        private String clusterId;
        private String address;
        private Map<String, ClusterNode> self;

        public DefaultNodeManager() {
            this.clusterId = SwiftProperty.get().getMachineId();
            this.address = SwiftProperty.get().getServerAddress();
            this.self = Collections.singletonMap(clusterId, new SwiftClusterNodeImpl(clusterId, address));
        }

        @Override
        public void setMasterNode(String masterNodeId, String masterNodeAddress) {
        }

        @Override
        public void putHistoryNode(String historyNodeId, String historyNodeAddress) {

        }

        @Override
        public void setCurrentNode(String currentNodeId, String currentNodeAddress) {
        }

        @Override
        public void handleNodeChange(Map<String, String> nodes) {
        }

        @Override
        public Map<String, ClusterNode> getOnlineNodes() {
            return self;
        }

        @Override
        public Map<String, ClusterNode> getHistoryNodes() {
            return Collections.EMPTY_MAP;
        }

        @Override
        public Map<String, ClusterNode> getOfflineNodes() {
            return Collections.EMPTY_MAP;
        }

        @Override
        public ClusterNode getMasterNode() {
            return self.get(clusterId);
        }

        @Override
        public ClusterNode getCurrentNode() {
            return self.get(clusterId);
        }
    }
}
