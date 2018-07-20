package com.fr.swift.nm.service;

import com.fr.swift.annotation.RpcService;
import com.fr.swift.annotation.RpcServiceType;
import com.fr.swift.cluster.service.MasterService;
import com.fr.swift.cluster.service.SlaveService;
import com.fr.swift.container.NodeContainer;
import com.fr.swift.heart.HeartBeatInfo;
import com.fr.swift.utils.ClusterProxyUtils;
import com.fr.third.springframework.stereotype.Service;

import java.util.Collection;

/**
 * This class created on 2018/7/17
 *
 * @author Lucifer
 * @description
 * @since Advanced FineBI 5.0
 */
@Service("swiftSlaveService")
@RpcService(value = SlaveService.class, type = RpcServiceType.CLIENT_SERVICE)
public class SwiftSlaveService implements SlaveService {

    @Override
    public synchronized void collectHeartBeat(HeartBeatInfo heartBeatInfo) throws Exception {
        MasterService masterService = ClusterProxyUtils.getMasterProxy(MasterService.class);
        masterService.collectHeartBeat(heartBeatInfo);
    }

    @Override
    public synchronized void synHeartBeat(Collection<HeartBeatInfo> heartBeatInfos) {
        NodeContainer.getInstance().removeAllHeartBeatInfos();
        NodeContainer.getInstance().addAll(heartBeatInfos);
    }
}
