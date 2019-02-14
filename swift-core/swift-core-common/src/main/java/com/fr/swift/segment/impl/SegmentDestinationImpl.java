package com.fr.swift.segment.impl;

import com.fr.swift.base.json.annotation.JsonIgnoreProperties;
import com.fr.swift.base.json.annotation.JsonProperty;
import com.fr.swift.segment.SegmentDestination;
import com.fr.swift.util.Strings;
import com.fr.swift.util.Util;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author yee
 * @date 2018/6/13
 */
@JsonIgnoreProperties(value = {"remote"})
public class SegmentDestinationImpl implements SegmentDestination, Serializable {

    private static final long serialVersionUID = 3016733438741210788L;
    @JsonProperty
    protected String clusterId;
    @JsonProperty
    protected String address;
    @JsonProperty
    private transient String currentNode;
    @JsonProperty
    protected String segmentId = Strings.EMPTY;
    @JsonProperty
    private int order;
    @JsonProperty
    private List<String> spareNodes;

    public SegmentDestinationImpl() {
        this.spareNodes = new ArrayList<String>() {
            @Override
            public boolean add(String s) {
                return !contains(s) && super.add(s);
            }
        };
    }

    public SegmentDestinationImpl(String clusterId, String segmentId, int order) {
        this.clusterId = clusterId;
        this.address = clusterId;
        this.currentNode = clusterId;
        this.segmentId = segmentId;
        this.order = order;
        this.spareNodes = new ArrayList<String>() {
            @Override
            public boolean add(String s) {
                return !contains(s) && super.add(s);
            }
        };
    }

    public SegmentDestinationImpl(SegmentDestination destination) {
        this.clusterId = destination.getClusterId();
        this.address = destination.getAddress();
        this.currentNode = destination.getCurrentNode();
        this.segmentId = destination.getSegmentId();
        this.order = destination.getOrder();
        this.spareNodes = destination.getSpareNodes();
    }

    public SegmentDestinationImpl(String segmentId, int order) {
        this.segmentId = segmentId;
        this.order = order;
        this.spareNodes = new ArrayList<String>() {
            @Override
            public boolean add(String s) {
                return !contains(s) && super.add(s);
            }
        };
    }

    @Override
    public String getClusterId() {
        return clusterId;
    }

    @Override
    public void setClusterId(String clusterId) {
        this.clusterId = clusterId;
    }

    @Override
    public List<String> getSpareNodes() {
        return spareNodes;
    }

    @Override
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
        return !Strings.isEmpty(clusterId) && !Util.equals(currentNode, clusterId);
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

    @Override
    public String getCurrentNode() {
        return currentNode;
    }

    @Override
    public SegmentDestination copy() {
        return new SegmentDestinationImpl(this);
    }

    public void setAddress(String address) {
        this.address = address;
    }

    @Override
    public int compareTo(SegmentDestination o) {
        int result = order - o.getOrder();
        if (0 == result) {
            return isRemote() ? result : -1;
        }
        return result;
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
        return segmentId != null ? segmentId.equals(that.segmentId) : that.segmentId == null;
    }

    @Override
    public int hashCode() {
        int result = segmentId != null ? segmentId.hashCode() : 0;
        result = 31 * result + order;
        return result;
    }
}
