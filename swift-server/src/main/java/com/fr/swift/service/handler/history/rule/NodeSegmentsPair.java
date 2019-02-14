package com.fr.swift.service.handler.history.rule;

import java.util.Set;

/**
 * @author yee
 * @date 2018/9/14
 */
class NodeSegmentsPair implements Comparable<NodeSegmentsPair> {

    private String clusterId;
    private Set<String> segIds;

    NodeSegmentsPair(String clusterId, Set<String> segIds) {
        this.clusterId = clusterId;
        this.segIds = segIds;
    }

    @Override
    public int compareTo(NodeSegmentsPair o) {
        return segIds.size() - o.segIds.size();
    }

    public String getClusterId() {
        return clusterId;
    }

    public void setClusterId(String clusterId) {
        this.clusterId = clusterId;
    }

    public Set<String> getSegIds() {
        return segIds;
    }

    public boolean containsSeg(String segId) {
        return segIds.contains(segId);
    }

    public void addSeg(String segId) {
        segIds.add(segId);
    }
}
