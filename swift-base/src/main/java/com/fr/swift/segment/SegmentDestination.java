package com.fr.swift.segment;

import com.fr.swift.service.SwiftService;

import java.io.Serializable;
import java.net.URI;
import java.util.List;

/**
 * @author yee
 * @date 2018/6/13
 */
public interface SegmentDestination extends Serializable, Comparable<SegmentDestination> {
    boolean isRemote();

    Class<? extends SwiftService> getServiceClass();

    String getMethodName();

    String getAddress();

    int order();

    String getClusterId();

    URI getUri();

    List<String> getSpareNodes();
}
