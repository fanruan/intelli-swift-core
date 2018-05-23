package com.fr.swift.segment;

import com.fr.swift.cube.io.Types;
import com.fr.swift.cube.io.Types.StoreType;
import com.fr.swift.source.SourceKey;

import java.net.URI;

/**
 * @author pony
 * @date 2017/10/16
 */
public class SwiftSegmentKey implements SegmentKey {
    private SourceKey table;
    private String name;
    private URI uri;
    private int order;
    private Types.StoreType storeType;

    public SwiftSegmentKey(SegmentKey segKey) {
        this(segKey.getTable(), segKey.getName(), segKey.getUri(), segKey.getOrder(), segKey.getStoreType());
    }

    public SwiftSegmentKey(SourceKey table, String name, URI uri, int order, StoreType storeType) {
        this.table = table;
        this.name = name;
        this.uri = uri;
        this.order = order;
        this.storeType = storeType;
    }

    @Override
    public SourceKey getTable() {
        return table;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public URI getUri() {
        return uri;
    }

    @Override
    public int getOrder() {
        return order;
    }

    @Override
    public Types.StoreType getStoreType() {
        return storeType;
    }
}
