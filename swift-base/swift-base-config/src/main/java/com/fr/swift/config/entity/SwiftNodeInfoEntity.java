package com.fr.swift.config.entity;

import com.fr.swift.db.MigrateType;
import com.fr.swift.db.NodeType;

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

    @Id
    @Column(name = "nodeId")
    private String nodeId;

    @Column(name = "monthNum")
    private int monthNum;

    @Column(name = "beginMonth")
    private String beginMonth;

    @Column(name = "endMonth")
    private String endMonth;

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

    @Override
    public String getNodeId() {
        return nodeId;
    }

    @Override
    public int getMonthNum() {
        return monthNum;
    }

    @Override
    public String getBeginMonth() {
        return beginMonth;
    }

    @Override
    public String getEndMonth() {
        return endMonth;
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
    public NodeType getNodeType() {
        return nodeType;
    }
}
