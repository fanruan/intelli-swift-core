package com.fr.swift.segment;

import com.fr.swift.service.ServiceType;
import com.fr.swift.structure.Pair;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * Created by Lyon on 2018/5/28.
 */
public interface SegmentLocationInfo extends Serializable {
    Map<String, Pair<Integer, List<SegmentDestination>>> getDestinations();

    ServiceType serviceType();

    enum UpdateType {
        ALL, PART
    }
}
