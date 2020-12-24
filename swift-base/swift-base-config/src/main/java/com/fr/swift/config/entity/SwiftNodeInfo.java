package com.fr.swift.config.entity;

import com.fr.swift.db.MigrateType;
import com.fr.swift.db.NodeType;
import com.fr.swift.source.alloter.impl.hash.function.HashType;

/**
 * @author Heng.J
 * @date 2020/10/26
 * @description
 * @since swift-1.2.0
 */
public interface SwiftNodeInfo {

    String getNodeId();

    int getLimitNum();

    String getBeginIndex();

    void setBeginIndex(String beginIndex);

    String getEndIndex();

    void setEndIndex(String endIndex);

    String getCubePath();

    String getMigrateTime();

    String getMigrateTarget();

    MigrateType getMigrateType();

    void setMigrateType(MigrateType migrateType);

    NodeType getNodeType();

    String getBlockingIndex();

    void setBlockingIndex(String blockingIndex);

    HashType getRelatedHashType();

    String getMigServerAddress();

    int getLimitStartHour();

    int getReadyStatus();

    void setReadyStatus(int isReady);

    int getLimitTransferHour();

    void setLimitTransferHour(int limitTransferHour);
}
