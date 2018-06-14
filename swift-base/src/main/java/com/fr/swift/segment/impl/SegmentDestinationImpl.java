package com.fr.swift.segment.impl;

import com.fr.stable.StringUtils;
import com.fr.swift.segment.SegmentDestination;
import com.fr.swift.service.SwiftService;

import java.net.URI;

/**
 * @author yee
 * @date 2018/6/13
 */
public class SegmentDestinationImpl implements SegmentDestination {

    private String clusterId;
    private URI uri;
    private int order;
    private Class<? extends SwiftService> serviceClass;
    private String methodName;

    public SegmentDestinationImpl(String clusterId, URI uri, int order, Class<? extends SwiftService> serviceClass, String methodName) {
        this.clusterId = clusterId;
        this.uri = uri;
        this.order = order;
        this.serviceClass = serviceClass;
        this.methodName = methodName;
    }

    public SegmentDestinationImpl(URI uri, int order) {
        this.uri = uri;
        this.order = order;
    }

    public String getClusterId() {
        return clusterId;
    }

    public void setClusterId(String clusterId) {
        this.clusterId = clusterId;
    }

    public URI getUri() {
        return uri;
    }

    public void setUri(URI uri) {
        this.uri = uri;
    }

    @Override
    public boolean isRemote() {
        return !StringUtils.isEmpty(clusterId);
    }

    @Override
    public Class<? extends SwiftService> getServiceClass() {
        return serviceClass;
    }

    @Override
    public String getMethodName() {
        return methodName;
    }

    @Override
    public String getAddress() {
        return clusterId;
    }

    @Override
    public int order() {
        return order;
    }

}
