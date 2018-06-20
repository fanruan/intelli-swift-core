package com.fr.swift.segment.impl;

import com.fr.stable.StringUtils;
import com.fr.swift.segment.SegmentDestination;
import com.fr.swift.service.SwiftService;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

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
    private List<String> spareNodes;

    public SegmentDestinationImpl(String clusterId, URI uri, int order, Class<? extends SwiftService> serviceClass, String methodName) {
        this.clusterId = clusterId;
        this.uri = uri;
        this.order = order;
        this.serviceClass = serviceClass;
        this.methodName = methodName;
        this.spareNodes = new ArrayList<String>();
    }

    public SegmentDestinationImpl(SegmentDestination destination) {
        this(destination.getClusterId(), destination.getUri(), destination.order(), destination.getServiceClass(), destination.getMethodName());
    }

    public SegmentDestinationImpl(URI uri, int order) {
        this.uri = uri;
        this.order = order;
    }

    @Override
    public String getClusterId() {
        return clusterId;
    }

    public void setClusterId(String clusterId) {
        this.clusterId = clusterId;
    }

    public URI getUri() {
        return uri;
    }

    @Override
    public List<String> getSpareNodes() {
        return spareNodes;
    }

    public void setSpareNodes(List<String> spareNodes) {
        this.spareNodes = spareNodes;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SegmentDestinationImpl that = (SegmentDestinationImpl) o;

        if (order != that.order) return false;
        if (uri != null ? !uri.equals(that.uri) : that.uri != null) return false;
        if (serviceClass != null ? !serviceClass.equals(that.serviceClass) : that.serviceClass != null) return false;
        return methodName != null ? methodName.equals(that.methodName) : that.methodName == null;
    }

    @Override
    public int hashCode() {
        int result = uri != null ? uri.hashCode() : 0;
        result = 31 * result + order;
        result = 31 * result + (serviceClass != null ? serviceClass.hashCode() : 0);
        result = 31 * result + (methodName != null ? methodName.hashCode() : 0);
        return result;
    }

    @Override
    public int compareTo(SegmentDestination o) {
        return order - o.order();
    }
}
