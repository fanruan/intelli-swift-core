package com.fr.swift.rm.service;

import com.fr.swift.annotation.RpcService;
import com.fr.swift.annotation.RpcServiceType;
import com.fr.swift.cluster.service.MasterService;
import com.fr.swift.cluster.service.SlaveService;
import com.fr.swift.container.NodeContainer;
import com.fr.swift.heart.HeartBeatInfo;
import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.utils.ClusterProxyUtils;
import com.fr.third.springframework.stereotype.Service;

import java.util.List;

/**
 * This class created on 2018/7/17
 *
 * @author Lucifer
 * @description
 * @since Advanced FineBI 5.0
 */
@Service("swiftMasterService")
@RpcService(value = MasterService.class, type = RpcServiceType.SERVER_SERVICE)
public class SwiftMasterService implements MasterService {

    @Override
    public synchronized void collectHeartBeat(HeartBeatInfo heartBeatInfo) {
        SwiftLoggers.getLogger().debug("Collect heartbeat:" + heartBeatInfo.toString());
        NodeContainer.getInstance().updateHeartBeatInfo(heartBeatInfo);
    }

    @Override
    public synchronized void syncHeartBeat() {
        SwiftLoggers.getLogger().debug("Start to sync heartbeat!");
        List<HeartBeatInfo> heartBeatInfoList = NodeContainer.getInstance().getAllHeartBeatInfos();
        for (HeartBeatInfo heartBeatInfo : heartBeatInfoList) {
            try {
                SlaveService slaveService = ClusterProxyUtils.getSlaveProxy(SlaveService.class, heartBeatInfo);
                SwiftLoggers.getLogger().debug("Sync heartbeat:" + heartBeatInfo);
                slaveService.synHeartBeat(heartBeatInfoList);
            } catch (Exception e) {
                SwiftLoggers.getLogger().error(e);
            }
        }
    }
}
