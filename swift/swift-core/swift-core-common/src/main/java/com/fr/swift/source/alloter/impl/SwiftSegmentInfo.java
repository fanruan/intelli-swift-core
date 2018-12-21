package com.fr.swift.source.alloter.impl;

import com.fr.swift.cube.io.Types.StoreType;
import com.fr.swift.source.alloter.SegmentInfo;

/**
 * @author anchore
 * @date 2018/6/5
 */
public class SwiftSegmentInfo implements SegmentInfo {

    private int order;

    private StoreType storeType;

    public SwiftSegmentInfo(int order, StoreType storeType) {
        this.order = order;
        this.storeType = storeType;
    }

    @Override
    public int getOrder() {
        return order;
    }

    @Override
    public StoreType getStoreType() {
        return storeType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        SwiftSegmentInfo that = (SwiftSegmentInfo) o;

        if (order != that.order) {
            return false;
        }
        return storeType == that.storeType;
    }

    @Override
    public int hashCode() {
        int result = order;
        result = 31 * result + (storeType != null ? storeType.hashCode() : 0);
        return result;
    }
}