package com.fr.swift.segment.impl;

import com.fr.swift.segment.SegmentDestination;
import com.fr.swift.segment.SegmentLocationInfo;
import com.fr.swift.service.ServiceType;
import com.fr.swift.structure.Pair;

import java.util.List;
import java.util.Map;

/**
 * @author yee
 * @date 2018/6/13
 */
public class SegmentLocationInfoImpl implements SegmentLocationInfo {
    private static final long serialVersionUID = -4569365852706720673L;

    private ServiceType serviceType;
    private Map<String, Pair<Integer, List<SegmentDestination>>> segments;

    public SegmentLocationInfoImpl(ServiceType serviceType, Map<String, Pair<Integer, List<SegmentDestination>>> segments) {
        this.serviceType = serviceType;
        this.segments = segments;
    }

    @Override
    public Map<String, Pair<Integer, List<SegmentDestination>>> getDestinations() {
        return segments;
    }

    @Override
    public ServiceType serviceType() {
        return serviceType;
    }

}
