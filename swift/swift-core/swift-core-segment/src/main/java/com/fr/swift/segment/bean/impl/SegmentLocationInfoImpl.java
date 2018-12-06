package com.fr.swift.segment.bean.impl;

import com.fr.swift.segment.SegmentDestination;
import com.fr.swift.segment.SegmentLocationInfo;
import com.fr.swift.service.ServiceType;
import com.fr.swift.source.SourceKey;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * @author yee
 * @date 2018/6/13
 */
public class SegmentLocationInfoImpl implements SegmentLocationInfo, Serializable {
    private static final long serialVersionUID = -4569365852706720673L;

    private ServiceType serviceType;
    private Map<SourceKey, List<SegmentDestination>> segments;

    public SegmentLocationInfoImpl(ServiceType serviceType, Map<SourceKey, List<SegmentDestination>> segments) {
        this.serviceType = serviceType;
        this.segments = segments;
    }

    @Override
    public Map<SourceKey, List<SegmentDestination>> getDestinations() {
        return segments;
    }

    @Override
    public ServiceType serviceType() {
        return serviceType;
    }

}
