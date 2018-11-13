package com.fr.swift.cluster.service;

import com.fr.swift.basics.annotation.InvokeMethod;
import com.fr.swift.basics.handler.MasterProcessHandler;
import com.fr.swift.heart.HeartBeatInfo;
import com.fr.swift.heart.NodeState;

import java.util.Collection;

/**
 * This class created on 2018/7/17
 *
 * @author Lucifer
 * @description
 * @since Advanced FineBI 5.0
 */
public interface MasterService {

    /**
     * 接收心跳
     *
     * @param heartBeatInfo
     * @throws Exception
     */
    @InvokeMethod(MasterProcessHandler.class)
    void receiveHeartBeat(HeartBeatInfo heartBeatInfo) throws Exception;

    /**
     * 主动推送所有节点状态
     *
     * @throws Exception
     */
    void pushNodeStates() throws Exception;

    /**
     * 被动拉取所有节点状态
     *
     * @throws Exception
     */
    @InvokeMethod(MasterProcessHandler.class)
    Collection<NodeState> pullNodeStates() throws Exception;

}