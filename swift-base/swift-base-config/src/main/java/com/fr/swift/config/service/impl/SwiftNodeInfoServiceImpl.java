package com.fr.swift.config.service.impl;

import com.fr.swift.beans.annotation.SwiftBean;
import com.fr.swift.config.dao.SwiftDao;
import com.fr.swift.config.dao.SwiftDaoImpl;
import com.fr.swift.config.entity.SwiftNodeInfo;
import com.fr.swift.config.entity.SwiftNodeInfoEntity;
import com.fr.swift.config.service.SwiftNodeInfoService;
import com.fr.swift.property.SwiftProperty;

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
    public void save(SwiftNodeInfo nodeInfo) {
        dao.insert((SwiftNodeInfoEntity) nodeInfo);
    }

    @Override
    public void update(SwiftNodeInfo newNodeInfo) {
        dao.update((SwiftNodeInfoEntity) newNodeInfo);
    }

    @Override
    public SwiftNodeInfoEntity getNodeInfo(String nodeId) {
        final List<?> select = dao.selectQuery((query, builder, from) ->
                query.select(from).where(builder.equal(from.get("nodeId"), nodeId)));
        if (select.isEmpty()) {
            return null;
        }
        SwiftNodeInfoEntity entity = (SwiftNodeInfoEntity) select.get(0);
        return entity;
    }

    @Override
    public SwiftNodeInfo getOwnNodeInfo() {
        return getNodeInfo(SwiftProperty.get().getMachineId());
    }

    @Override
    public List<SwiftNodeInfo> getAllNodeInfo() {
        return (List<SwiftNodeInfo>) dao.selectAll();
    }
}
