package com.fr.swift.segment;

import com.fr.swift.service.SwiftService;

import java.io.Serializable;
import java.net.URI;

/**
 * @author yee
 * @date 2018/6/13
 */
public interface SegmentDestination extends Serializable {
    boolean isRemote();

    Class<? extends SwiftService> getServiceClass();

    String getMethodName();

    String getAddress();

    int order();

    URI getUri();
}
