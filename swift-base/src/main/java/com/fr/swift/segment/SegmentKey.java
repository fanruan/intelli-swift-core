package com.fr.swift.segment;

import com.fr.swift.cube.io.Types;

import java.net.URI;

/**
 * Created by pony on 2017/10/16.
 */
public class SegmentKey {
    private String name;
    private URI uri;
    /**
     * @deprecated fixme 不是通用的抽象，可以扩展个按行分块的key
     */
    @Deprecated
    private int segmentOrder;
    private String sourceId;
    private Types.StoreType storeType;

    public SegmentKey(String name, URI uri, int segmentOrder, Types.StoreType storeType) {
        this.name = name;
        this.uri = uri;
        this.segmentOrder = segmentOrder;
        this.storeType = storeType;
    }

    public SegmentKey(String name) {
        this.name = name;
    }

    public SegmentKey() {
    }

    public void setName(String name) {
        this.name = name;
    }

    public Types.StoreType getStoreType() {
        return storeType;
    }

    public void setStoreType(Types.StoreType storeType) {
        this.storeType = storeType;
    }

    public String getName() {
        return name;
    }

    public URI getUri() {
        return uri;
    }

    public void setUri(URI uri) {
        this.uri = uri;
    }

    public int getSegmentOrder() {
        return segmentOrder;
    }

    public void setSegmentOrder(int segmentOrder) {
        this.segmentOrder = segmentOrder;
    }

    public String getSourceId() {
        return sourceId;
    }

    public void setSourceId(String sourceId) {
        this.sourceId = sourceId;
    }
}
