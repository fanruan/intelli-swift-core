package com.fr.swift.source.alloter.impl;

import com.fr.swift.source.alloter.SegmentInfo;

/**
 * @author anchore
 * @date 2018/6/5
 */
public class SwiftSegmentInfo implements SegmentInfo {
    private int order;

    public SwiftSegmentInfo(int order) {
        this.order = order;
    }

    @Override
    public int getOrder() {
        return order;
    }
}