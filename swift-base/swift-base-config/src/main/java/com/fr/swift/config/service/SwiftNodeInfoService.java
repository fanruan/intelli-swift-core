package com.fr.swift.config.service;

import com.fr.swift.annotation.service.DbService;
import com.fr.swift.config.entity.SwiftNodeInfo;
import com.fr.swift.config.entity.SwiftNodeInfoEntity;

import java.util.List;

/**
 * @author Heng.J
 * @date 2020/10/26
 * @description
 * @since swift-1.2.0
 */
@DbService
public interface SwiftNodeInfoService {

    void save(SwiftNodeInfoEntity nodeInfo);

    void update(SwiftNodeInfoEntity newNodeInfo);

    SwiftNodeInfo getNodeInfo(String nodeId);

    List<SwiftNodeInfo> getAllNodeInfo();
}
