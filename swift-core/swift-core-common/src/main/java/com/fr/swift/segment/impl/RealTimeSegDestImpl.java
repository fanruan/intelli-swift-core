package com.fr.swift.segment.impl;

import com.fr.swift.segment.SegmentDestination;

import java.io.Serializable;

/**
 * @author yee
 * @date 2018/9/5
 */
public class RealTimeSegDestImpl extends SegmentDestinationImpl implements Serializable {

    private static final long serialVersionUID = -5969030726680132148L;

    public RealTimeSegDestImpl(String clusterId, String segmentId, int order) {
        super(clusterId, segmentId, order);
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
        if (!super.equals(o)) {
            return false;
        }

        RealTimeSegDestImpl that = (RealTimeSegDestImpl) o;

        if (clusterId != null ? !clusterId.equals(that.clusterId) : that.clusterId != null) {
            return false;
        }
        return segmentId != null ? segmentId.equals(that.segmentId) : that.segmentId == null;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (clusterId != null ? clusterId.hashCode() : 0);
        result = 31 * result + (segmentId != null ? segmentId.hashCode() : 0);
        return result;
    }

    @Override
    public SegmentDestination copy() {
        return new RealTimeSegDestImpl(this);
    }
}
