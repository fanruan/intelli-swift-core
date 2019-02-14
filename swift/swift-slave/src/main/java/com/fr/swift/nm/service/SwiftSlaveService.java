package com.fr.swift.nm.service;

import com.fr.swift.basics.annotation.ProxyService;
import com.fr.swift.basics.base.selector.ProxySelector;
import com.fr.swift.beans.annotation.SwiftBean;
import com.fr.swift.cluster.service.MasterService;
import com.fr.swift.cluster.service.SlaveService;
import com.fr.swift.container.NodeContainer;
import com.fr.swift.heart.HeartBeatInfo;
import com.fr.swift.heart.NodeState;
import com.fr.swift.log.SwiftLoggers;

import java.util.Collection;

/**
 * This class created on 2018/7/17
 *
 * @author Lucifer
 * @description
 * @since Advanced FineBI 5.0
 */
@SwiftBean(name = "swiftSlaveService")
@ProxyService(value = SlaveService.class, type = ProxyService.ServiceType.INTERNAL)
public class SwiftSlaveService implements SlaveService {

    @Override
    public void sendHeartBeat(HeartBeatInfo heartBeatInfo) throws Exception {
        MasterService masterService = ProxySelector.getInstance().getFactory().getProxy(MasterService.class);
        masterService.receiveHeartBeat(heartBeatInfo);
    }

    @Override
    public synchronized void syncNodeStates(Collection<NodeState> collection) {
        SwiftLoggers.getLogger().debug("sync node states");
        NodeContainer.removeNodeStates();
        NodeContainer.addAll(collection);
    }

    @Override
    public void syncNodeStates() throws Exception {
        MasterService masterService = ProxySelector.getInstance().getFactory().getProxy(MasterService.class);
        Collection<NodeState> collection = masterService.pullNodeStates();
        syncNodeStates(collection);
    }
}