package com.fr.swift.config.entity;

import com.fr.swift.annotation.persistence.Column;
import com.fr.swift.annotation.persistence.Entity;
import com.fr.swift.annotation.persistence.Id;
import com.fr.swift.annotation.persistence.Table;
import com.fr.swift.config.entity.key.SwiftSegLocationEntityId;

import java.io.Serializable;


/**
 * @author yee
 * @date 2018/7/3
 */
@Entity
@Table(name = "fine_swift_seg_location")
public class SwiftSegmentLocationEntity implements Serializable {
    @Id
    private SwiftSegLocationEntityId id;
    @Column
    private String sourceKey;

    public SwiftSegmentLocationEntity(String clusterId, String segmentId, String sourceKey) {
        this.sourceKey = sourceKey;
        this.id = new SwiftSegLocationEntityId(clusterId, segmentId);
    }

    public SwiftSegmentLocationEntity() {
    }

    public SwiftSegLocationEntityId getId() {
        return id;
    }

    public void setId(SwiftSegLocationEntityId id) {
        this.id = id;
    }

    public String getClusterId() {
        return id.getClusterId();
    }

    public String getSegmentId() {
        return id.getSegmentId();
    }

    public String getSourceKey() {
        return sourceKey;
    }

    public void setSourceKey(String sourceKey) {
        this.sourceKey = sourceKey;
    }

}
