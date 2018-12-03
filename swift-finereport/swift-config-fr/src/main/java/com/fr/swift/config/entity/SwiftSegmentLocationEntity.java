package com.fr.swift.config.entity;

import com.fr.swift.config.bean.SegLocationBean;
import com.fr.swift.config.entity.key.SwiftSegLocationEntityId;
import com.fr.swift.converter.ObjectConverter;
import com.fr.third.javax.persistence.Column;
import com.fr.third.javax.persistence.Entity;
import com.fr.third.javax.persistence.Id;
import com.fr.third.javax.persistence.Table;


/**
 * @author yee
 * @date 2018/7/3
 */
@Entity
@Table(name = "fine_swift_seg_location")
public class SwiftSegmentLocationEntity implements ObjectConverter<SegLocationBean> {
    @Id
    private SwiftSegLocationEntityId id;
    @Column
    private String sourceKey;

    public SwiftSegmentLocationEntity(SegLocationBean value) {
        this.id = new SwiftSegLocationEntityId(value.getClusterId(), value.getSegmentId());
        this.sourceKey = value.getSourceKey();
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

    @Override
    public SegLocationBean convert() {
        return new SegLocationBean(this.getClusterId(), this.getSegmentId(), this.getSourceKey());
    }
}
