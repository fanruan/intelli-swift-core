package com.fr.swift.container;

import com.fr.swift.ClusterContainer;
import com.fr.swift.heart.HeartBeatInfo;

import java.util.Collection;
import java.util.List;

/**
 * This class created on 2018/7/17
 *
 * @author Lucifer
 * @description 节点心跳容器
 * @since Advanced FineBI 5.0
 * todo 心跳持久化和初始化
 */
public class NodeContainer extends ClusterContainer<HeartBeatInfo> {

    private static final NodeContainer INSTANCE = new NodeContainer();

    private NodeContainer() {
    }

    public static NodeContainer getInstance() {
        return INSTANCE;
    }

    public void updateHeartBeatInfo(HeartBeatInfo newHeartBeat) {
        HeartBeatInfo oldHeartBeat = super.get(newHeartBeat.getNodeId());
        if (oldHeartBeat != null) {
            oldHeartBeat.setHeartbeatTime(newHeartBeat.getHeartbeatTime());
            oldHeartBeat.setServerCurrentStatus(newHeartBeat.getServerCurrentStatus());
            oldHeartBeat.setAttachment(newHeartBeat.getAttachment());
            oldHeartBeat.setAddress(newHeartBeat.getAddress());
        } else {
            super.add(newHeartBeat.getNodeId(), newHeartBeat);
        }
    }

    public List<HeartBeatInfo> getAllHeartBeatInfos() {
        return super.getAllUseable();
    }

    public void removeAllHeartBeatInfos() {
        super.removeAll();
    }

    public void addAll(Collection<HeartBeatInfo> heartBeatInfoList) {
        for (HeartBeatInfo heartBeatInfo : heartBeatInfoList) {
            super.add(heartBeatInfo.getNodeId(), heartBeatInfo);
        }
    }
}
