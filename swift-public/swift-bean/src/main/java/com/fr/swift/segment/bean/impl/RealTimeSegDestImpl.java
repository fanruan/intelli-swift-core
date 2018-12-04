package com.fr.swift.segment.bean.impl;

import com.fr.swift.query.Queryable;
import com.fr.swift.segment.SegmentDestination;

/**
 * @author yee
 * @date 2018/9/5
 */
public class RealTimeSegDestImpl extends SegmentDestinationImpl {
    private static final long serialVersionUID = -5969030726680132148L;
    public RealTimeSegDestImpl() {
    }

    public RealTimeSegDestImpl(String clusterId, String segmentId, int order, Class<? extends Queryable> serviceClass, String methodName) {
        super(clusterId, segmentId, order, serviceClass, methodName);
    }

    public RealTimeSegDestImpl(SegmentDestination destination) {
        super(destination);
    }

    public RealTimeSegDestImpl(String segmentId, int order) {
        super(segmentId, order);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        RealTimeSegDestImpl that = (RealTimeSegDestImpl) o;

        if (clusterId != null ? !clusterId.equals(that.clusterId) : that.clusterId != null) {
            return false;
        }
        if (segmentId != null ? !segmentId.equals(that.segmentId) : that.segmentId != null) {
            return false;
        }
        return methodName != null ? methodName.equals(that.methodName) : that.methodName == null;
    }

    @Override
    public int hashCode() {
        int result = clusterId != null ? clusterId.hashCode() : 0;
        result = 31 * result + (segmentId != null ? segmentId.hashCode() : 0);
        result = 31 * result + (methodName != null ? methodName.hashCode() : 0);
        return result;
    }

    @Override
    public SegmentDestination copy() {
        return new RealTimeSegDestImpl(this);
    }
}
