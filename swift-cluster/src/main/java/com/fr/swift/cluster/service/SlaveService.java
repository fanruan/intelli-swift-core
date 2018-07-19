package com.fr.swift.cluster.service;

import com.fr.swift.heart.HeartBeatInfo;

import java.util.Collection;

/**
 * This class created on 2018/7/17
 *
 * @author Lucifer
 * @description
 * @since Advanced FineBI 5.0
 */
public interface SlaveService {

    void collectHeartBeat(HeartBeatInfo heartBeatInfo) throws Exception;

    void synHeartBeat(Collection<HeartBeatInfo> collection) throws Exception;
}
