package com.fr.swift.segment.impl;

import com.fr.stable.AssistUtils;
import com.fr.swift.segment.SegmentDestination;
import com.fr.swift.service.SwiftService;

/**
 * @author yee
 * @date 2018/9/5
 */
public class RealTimeSwgDestImpl extends SegmentDestinationImpl {
    private static final long serialVersionUID = -5969030726680132148L;

    public RealTimeSwgDestImpl() {
    }

    public RealTimeSwgDestImpl(String clusterId, String segmentId, int order, Class<? extends SwiftService> serviceClass, String methodName) {
        super(clusterId, segmentId, order, serviceClass, methodName);
    }

    public RealTimeSwgDestImpl(SegmentDestination destination) {
        super(destination);
    }

    public RealTimeSwgDestImpl(String segmentId, int order) {
        super(segmentId, order);
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof RealTimeSwgDestImpl
                && AssistUtils.equals(this.clusterId, ((RealTimeSwgDestImpl) o).clusterId)
                && AssistUtils.equals(this.serviceClass, ((RealTimeSwgDestImpl) o).serviceClass)
                && AssistUtils.equals(this.methodName, ((RealTimeSwgDestImpl) o).methodName);
    }

    @Override
    public int hashCode() {
        return AssistUtils.hashCode(this.clusterId, this.serviceClass, this.methodName);
    }

    @Override
    public SegmentDestination copy() {
        return new RealTimeSwgDestImpl(this);
    }
}
