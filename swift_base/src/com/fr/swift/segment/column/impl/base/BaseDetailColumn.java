package com.fr.swift.segment.column.impl.base;

import com.fr.swift.cube.io.ResourceDiscovery;
import com.fr.swift.cube.io.ResourceDiscoveryImpl;
import com.fr.swift.cube.io.location.IResourceLocation;
import com.fr.swift.segment.column.DetailColumn;
import com.fr.swift.util.Crasher;

/**
 * @author anchore
 * @date 2017/11/7
 */
abstract class BaseDetailColumn<T> implements DetailColumn<T> {
    private static final String DETAIL = "detail";

    IResourceLocation location;

    static final ResourceDiscovery DISCOVERY = ResourceDiscoveryImpl.getInstance();

    BaseDetailColumn(IResourceLocation parent) {
        location = parent.buildChildLocation(DETAIL);
    }

    @Override
    public int getInt(int pos) {
        return Crasher.crash("not allowed");
    }

    @Override
    public long getLong(int pos) {
        return Crasher.crash("not allowed");
    }

    @Override
    public double getDouble(int pos) {
        return Crasher.crash("not allowed");
    }
}