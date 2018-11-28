package com.fr.swift.config.entity.key;

import com.fr.swift.config.SwiftConfigConstants;
import com.fr.swift.source.SourceKey;
import com.fr.third.javax.persistence.Column;
import com.fr.third.javax.persistence.Embeddable;

/**
 * @author yee
 * @date 2018/7/18
 */
@Embeddable
public class SwiftTablePathKey extends TableId {
    private static final long serialVersionUID = -7758001996419942390L;
    @Column(name = "clusterId")
    private String clusterId;

    public SwiftTablePathKey(String tableKey, String clusterId) {
        super(new SourceKey(tableKey));
        this.clusterId = clusterId;
    }

    public SwiftTablePathKey(String tableKey) {
        super(new SourceKey(tableKey));
        this.clusterId = SwiftConfigConstants.LOCALHOST;
    }

    public SwiftTablePathKey() {
        clusterId = SwiftConfigConstants.LOCALHOST;
    }

    public String getClusterId() {
        return clusterId;
    }

    public void setClusterId(String clusterId) {
        this.clusterId = clusterId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        if (!super.equals(o)) {
            return false;
        }

        SwiftTablePathKey that = (SwiftTablePathKey) o;

        return clusterId != null ? clusterId.equals(that.clusterId) : that.clusterId == null;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (clusterId != null ? clusterId.hashCode() : 0);
        return result;
    }
}
