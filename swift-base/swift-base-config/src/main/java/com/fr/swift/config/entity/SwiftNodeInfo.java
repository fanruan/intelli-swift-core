package com.fr.swift.config.entity;

import com.fr.swift.db.MigrateType;
import com.fr.swift.db.NodeType;

/**
 * @author Heng.J
 * @date 2020/10/26
 * @description
 * @since swift-1.2.0
 */
public interface SwiftNodeInfo {

    String getNodeId();

    int getMonthNum();

    String getBeginMonth();

    String getEndMonth();

    String getCubePath();

    String getMigrateTime();

    String getMigrateTarget();

    MigrateType getMigrateType();

    NodeType getNodeType();

}
