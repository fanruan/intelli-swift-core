package com.fr.swift.source.alloter;

import com.fr.swift.cube.io.Types.StoreType;

/**
 * @author anchore
 * @date 2018/6/5
 */
public interface SegmentInfo {

    int getOrder();

    StoreType getStoreType();
}