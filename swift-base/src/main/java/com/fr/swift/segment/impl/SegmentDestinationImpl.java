package com.fr.swift.segment.impl;

import com.fr.general.ComparatorUtils;
import com.fr.stable.StringUtils;
import com.fr.swift.segment.SegmentDestination;
import com.fr.swift.service.SwiftService;
import com.fr.third.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fr.third.fasterxml.jackson.annotation.JsonInclude;
import com.fr.third.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;

/**
 * @author yee
 * @date 2018/6/13
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(value = {"remote"})
public class SegmentDestinationImpl implements SegmentDestination {

    @JsonProperty
    private String clusterId;
    @JsonProperty
    private String address;
    @JsonProperty
    private transient String currentNode;
    @JsonProperty
    private String segmentId;
    @JsonProperty
    private int order;
    @JsonProperty
    private Class<? extends SwiftService> serviceClass;
    @JsonProperty
    private String methodName;
    @JsonProperty
    private List<String> spareNodes;

    public SegmentDestinationImpl() {
    }

    public SegmentDestinationImpl(String clusterId, String segmentId, int order, Class<? extends SwiftService> serviceClass, String methodName) {
        this.clusterId = clusterId;
        this.address = clusterId;
        this.currentNode = clusterId;
        this.segmentId = segmentId;
        this.order = order;
        this.serviceClass = serviceClass;
        this.methodName = methodName;
        this.spareNodes = new ArrayList<String>();
    }

    public SegmentDestinationImpl(SegmentDestination destination) {
        this(destination.getClusterId(), destination.getSegmentId(), destination.getOrder(), destination.getServiceClass(), destination.getMethodName());
    }

    public SegmentDestinationImpl(String segmentId, int order) {
        this.segmentId = segmentId;
        this.order = order;
    }

    @Override
    public String getClusterId() {
        return clusterId;
    }

    public void setClusterId(String clusterId) {
        this.clusterId = clusterId;
    }

    @Override
    public List<String> getSpareNodes() {
        return spareNodes;
    }

    public void setSpareNodes(List<String> spareNodes) {
        this.spareNodes = spareNodes;
    }

    @Override
    public String getSegmentId() {
        return segmentId;
    }

    public void setSegmentId(String segmentId) {
        this.segmentId = segmentId;
    }

    @Override
    public boolean isRemote() {
        return !StringUtils.isEmpty(clusterId) && !ComparatorUtils.equals(currentNode, clusterId);
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
        return address;
    }

    @Override
    public int getOrder() {
        return order;
    }

    public void setCurrentNode(String currentNode) {
        this.currentNode = currentNode;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    public void setServiceClass(Class<? extends SwiftService> serviceClass) {
        this.serviceClass = serviceClass;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public String getCurrentNode() {
        return currentNode;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    @Override
    public int compareTo(SegmentDestination o) {
        return order - o.getOrder();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        SegmentDestinationImpl that = (SegmentDestinationImpl) o;

        if (order != that.order) {
            return false;
        }
        if (segmentId != null ? !segmentId.equals(that.segmentId) : that.segmentId != null) {
            return false;
        }
        if (serviceClass != null ? !serviceClass.equals(that.serviceClass) : that.serviceClass != null) {
            return false;
        }
        return methodName != null ? methodName.equals(that.methodName) : that.methodName == null;
    }

    @Override
    public int hashCode() {
        int result = segmentId != null ? segmentId.hashCode() : 0;
        result = 31 * result + order;
        result = 31 * result + (serviceClass != null ? serviceClass.hashCode() : 0);
        result = 31 * result + (methodName != null ? methodName.hashCode() : 0);
        return result;
    }
}
