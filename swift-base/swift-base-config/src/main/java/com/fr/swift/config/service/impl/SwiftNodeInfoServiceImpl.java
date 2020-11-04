package com.fr.swift.config.service.impl;

import com.fr.swift.beans.annotation.SwiftBean;
import com.fr.swift.config.dao.SwiftDao;
import com.fr.swift.config.dao.SwiftDaoImpl;
import com.fr.swift.config.entity.SwiftNodeInfo;
import com.fr.swift.config.entity.SwiftNodeInfoEntity;
import com.fr.swift.config.service.SwiftNodeInfoService;

import java.util.List;

/**
 * @author Heng.J
 * @date 2020/10/26
 * @description
 * @since swift-1.2.0
 */
@SwiftBean(name = "SwiftNodeInfoService")
public class SwiftNodeInfoServiceImpl implements SwiftNodeInfoService {

    private SwiftDao<SwiftNodeInfoEntity> dao = new SwiftDaoImpl<>(SwiftNodeInfoEntity.class);

    @Override
    public void save(SwiftNodeInfoEntity nodeInfo) {
        dao.insert(nodeInfo);
    }

    @Override
    public void update(SwiftNodeInfoEntity newNodeInfo) {
        dao.update(newNodeInfo);
    }

    @Override
    public SwiftNodeInfoEntity getNodeInfo(String nodeId) {
        return null;
    }

    @Override
    public List<SwiftNodeInfo> getAllNodeInfo() {
        return null;
    }
}
