package com.fr.swift.service.handler.history.rule;

/**
 * @author yee
 * @date 2018/9/14
 */
public class SegmentPair implements Comparable<SegmentPair> {

    private String clusterId;
    private int count;

    public SegmentPair(String clusterId, int count) {
        this.clusterId = clusterId;
        this.count = count;
    }

    @Override
    public int compareTo(SegmentPair o) {
        return count - o.count;
    }

    public String getClusterId() {
        return clusterId;
    }

    public void setClusterId(String clusterId) {
        this.clusterId = clusterId;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }
}
