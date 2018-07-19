package com.fr.swift.cluster.service;

import com.fr.swift.heart.HeartBeatInfo;

/**
 * This class created on 2018/7/17
 *
 * @author Lucifer
 * @description
 * @since Advanced FineBI 5.0
 */
public interface MasterService {

    void collectHeartBeat(HeartBeatInfo heartBeatInfo) throws Exception;

    void syncHeartBeat() throws Exception;
}