package com.fr.swift.cloud.source.alloter;

import com.fr.swift.cloud.cube.io.Types.StoreType;

/**
 * @author anchore
 * @date 2018/6/5
 */
public interface SegmentInfo {

    String getTempDir();

    int getOrder();

    StoreType getStoreType();
}