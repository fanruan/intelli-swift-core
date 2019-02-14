package com.fr.swift.rm.service;

import com.fr.swift.basics.annotation.ProxyService;
import com.fr.swift.basics.base.selector.ProxySelector;
import com.fr.swift.beans.annotation.SwiftBean;
import com.fr.swift.cluster.service.MasterService;
import com.fr.swift.cluster.service.SlaveService;
import com.fr.swift.container.NodeContainer;
import com.fr.swift.heart.HeartBeatInfo;
import com.fr.swift.heart.NodeState;
import com.fr.swift.heart.NodeType;
import com.fr.swift.log.SwiftLoggers;

import java.util.Collection;
import java.util.List;

/**
 * This class created on 2018/7/17
 *
 * @author Lucifer
 * @description
 * @since Advanced FineBI 5.0
 */
@SwiftBean(name = "swiftMasterService")
@ProxyService(value = MasterService.class, type = ProxyService.ServiceType.INTERNAL)
public class SwiftMasterService implements MasterService {

    @Override
    public synchronized void receiveHeartBeat(HeartBeatInfo heartBeatInfo) {
        SwiftLoggers.getLogger().debug("Collect heartbeat:{}", heartBeatInfo);
        NodeState nodeState = NodeContainer.getNode(heartBeatInfo.getNodeId());
        boolean need2Sync = false;
        if (nodeState == null || nodeState.getNodeType() != NodeType.ONLINE) {
            need2Sync = true;
        }
        NodeContainer.updateNodeState(new NodeState(heartBeatInfo, NodeType.ONLINE));
        if (need2Sync) {
            pushNodeStates();
        }
    }

    @Override
    public void pushNodeStates() {
        SwiftLoggers.getLogger().debug("Start to sync node states!");
        List<NodeState> nodeStateList = NodeContainer.getAllNodeStates();
        try {
            SlaveService slaveService = ProxySelector.getInstance().getFactory().getProxy(SlaveService.class);
            slaveService.syncNodeStates(nodeStateList);
        } catch (Exception e) {
            SwiftLoggers.getLogger().error(e);
        }
    }

    @Override
    public Collection<NodeState> pullNodeStates() {
        List<NodeState> nodeStateList = NodeContainer.getAllNodeStates();
        return nodeStateList;
    }
}