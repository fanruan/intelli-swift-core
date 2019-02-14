package com.fr.swift.config.entity.key;


import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;

/**
 * @author yee
 * @date 2018/7/3
 */
@Embeddable
public class SwiftSegLocationEntityId implements Serializable {
    private static final long serialVersionUID = 2145513184016170123L;
    @Column
    private String clusterId;
    @Column
    private String segmentId;

    public SwiftSegLocationEntityId(String clusterId, String segmentId) {
        this.clusterId = clusterId;
        this.segmentId = segmentId;
    }

    public SwiftSegLocationEntityId() {
    }

    public String getClusterId() {
        return clusterId;
    }

    public void setClusterId(String clusterId) {
        this.clusterId = clusterId;
    }

    public String getSegmentId() {
        return segmentId;
    }

    public void setSegmentId(String segmentId) {
        this.segmentId = segmentId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        SwiftSegLocationEntityId that = (SwiftSegLocationEntityId) o;

        if (clusterId != null ? !clusterId.equals(that.clusterId) : that.clusterId != null) {
            return false;
        }
        return segmentId != null ? segmentId.equals(that.segmentId) : that.segmentId == null;
    }

    @Override
    public int hashCode() {
        int result = clusterId != null ? clusterId.hashCode() : 0;
        result = 31 * result + (segmentId != null ? segmentId.hashCode() : 0);
        return result;
    }
}
