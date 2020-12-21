package com.fr.swift.config.entity;

import com.fr.swift.db.MigrateType;
import com.fr.swift.db.NodeType;
import com.fr.swift.source.alloter.impl.hash.function.HashType;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

/**
 * @author Heng.J
 * @date 2020/10/23
 * @description
 * @since swift-1.2.0
 */
@Table
@Entity(name = "fine_swift_node_info")
public class SwiftNodeInfoEntity implements Serializable, SwiftNodeInfo {

    private static final long serialVersionUID = -8816997675837995276L;

    // 和swift.properties相同
    @Id
    @Column(name = "nodeId")
    private String nodeId;

    @Column(name = "limitNum")
    private int limitNum;

    @Column(name = "beginIndex")
    private String beginIndex;

    @Column(name = "endIndex")
    private String endIndex;

    // 和swift.properties相同
    @Column(name = "cubePath")
    private String cubePath;

    @Column(name = "migrateTime")
    private String migrateTime;

    @Column(name = "migrateTarget")
    private String migrateTarget;

    @Column(name = "migrateType")
    @Enumerated(EnumType.STRING)
    private MigrateType migrateType;

    @Column(name = "nodeType")
    @Enumerated(EnumType.STRING)
    private NodeType nodeType;

    @Column(name = "blockingIndex")
    private String blockingIndex;

    @Column(name = "relatedHashType")
    @Enumerated(EnumType.STRING)
    private HashType relatedHashType;

    @Column(name = "migServerAddress")
    private String migServerAddress;

    @Column(name = "limitStartHour")
    private int limitStartHour;

    @Column(name = "readyStatus")
    private int readyStatus;

    @Column(name = "limitTransferHour")
    private int limitTransferHour;

    @Override
    public String getNodeId() {
        return nodeId;
    }

    @Override
    public int getLimitNum() {
        return limitNum;
    }

    @Override
    public String getBeginIndex() {
        return beginIndex;
    }

    @Override
    public void setBeginIndex(String beginIndex) {
        this.beginIndex = beginIndex;
    }

    @Override
    public String getEndIndex() {
        return endIndex;
    }

    @Override
    public void setEndIndex(String endIndex) {
        this.endIndex = endIndex;
    }

    @Override
    public String getCubePath() {
        return cubePath;
    }

    @Override
    public String getMigrateTime() {
        return migrateTime;
    }

    @Override
    public String getMigrateTarget() {
        return migrateTarget;
    }

    @Override
    public MigrateType getMigrateType() {
        return migrateType;
    }

    @Override
    public void setMigrateType(MigrateType migrateType) {
        this.migrateType = migrateType;
    }

    @Override
    public NodeType getNodeType() {
        return nodeType;
    }

    @Override
    public String getBlockingIndex() {
        return blockingIndex;
    }

    @Override
    public void setBlockingIndex(String blockingIndex) {
        this.blockingIndex = blockingIndex;
    }

    @Override
    public HashType getRelatedHashType() {
        return relatedHashType;
    }

    @Override
    public String getMigServerAddress() {
        return migServerAddress;
    }

    @Override
    public int getLimitStartHour() {
        return limitStartHour;
    }

    @Override
    public int getReadyStatus() {
        return readyStatus;
    }

    @Override
    public void setReadyStatus(int readyStatus) {
        this.readyStatus = readyStatus;
    }

    @Override
    public int getLimitTransferHour() {
        return limitTransferHour;
    }

    @Override
    public void setLimitTransferHour(int limitTransferHour) {
        this.limitTransferHour = limitTransferHour;
    }
}
