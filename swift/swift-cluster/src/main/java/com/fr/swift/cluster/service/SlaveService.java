package com.fr.swift.cluster.service;

import com.fr.swift.basics.annotation.InvokeMethod;
import com.fr.swift.heart.HeartBeatInfo;
import com.fr.swift.heart.NodeState;
import com.fr.swift.basics.handler.NodesProcessHandler;

import java.util.Collection;

/**
 * This class created on 2018/7/17
 *
 * @author Lucifer
 * @description
 * @since Advanced FineBI 5.0
 */
public interface SlaveService {

    /**
     * 发送心跳
     *
     * @param heartBeatInfo
     * @throws Exception
     */
    void sendHeartBeat(HeartBeatInfo heartBeatInfo) throws Exception;

    /**
     * 被动同步心跳
     *
     * @param collection
     * @throws Exception
     */
    @InvokeMethod(NodesProcessHandler.class)
    void syncNodeStates(Collection<NodeState> collection) throws Exception;

    /**
     * 主动同步心跳
     *
     * @throws Exception
     */
    void syncNodeStates() throws Exception;
}
