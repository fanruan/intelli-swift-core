package com.fr.swift.config.entity;

import com.fr.swift.config.entity.key.SwiftSegLocationEntityId;
import com.fr.third.javax.persistence.Column;
import com.fr.third.javax.persistence.Entity;
import com.fr.third.javax.persistence.Id;
import com.fr.third.javax.persistence.Table;

/**
 * @author yee
 * @date 2018/7/3
 */
@Entity
@Table(name = "fine_swift_segment_location")
public class SwiftSegmentLocationEntity {
    @Id
    private SwiftSegLocationEntityId id;
    @Column
    private String sourceKey;

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
