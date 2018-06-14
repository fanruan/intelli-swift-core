package com.fr.swift.segment.impl;

import com.fr.stable.StringUtils;
import com.fr.swift.segment.SegmentDestination;

import java.net.URI;

/**
 * @author yee
 * @date 2018/6/13
 */
public class SegmentDestinationImpl implements SegmentDestination {

    private String clusterId;
    private URI uri;
    private int order;

    public SegmentDestinationImpl(String clusterId, URI uri, int order) {
        this.clusterId = clusterId;
        this.uri = uri;
        this.order = order;
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
    public String getNode() {
        return clusterId;
    }

    @Override
    public int order() {
        return order;
    }

}
