package com.fr.swift.config.bean;

import com.fr.swift.config.entity.SwiftSegmentEntity;
import com.fr.swift.cube.io.Types;
import com.fr.swift.segment.SegmentKey;
import com.fr.swift.source.SourceKey;

import java.io.Serializable;
import java.net.URI;

/**
 * @author yee
 * @date 2018/5/24
 */
public class SegmentKeyBean implements Serializable, Convert<SwiftSegmentEntity>, SegmentKey {

    public static final String COLUMN_SEGMENT_OWNER = "segmentOwner";
    public static final String COLUMN_SEGMENT_ORDER = "segmentOrder";
    public static final String COLUMN_STORE_TYPE = "storeType";
    public static final String COLUMN_SEGMENT_URI = "segmentUri";

    /**
     * sourceKey@storeType@order
     */
    private String id;
    private String sourceKey;
    private URI uri;
    private int order;
    private Types.StoreType storeType;

    public SegmentKeyBean(String sourceKey, URI uri, int order, Types.StoreType storeType) {
        this.sourceKey = sourceKey;
        this.uri = uri;
        this.order = order;
        this.storeType = storeType;
        this.id = toString();
    }

    public SegmentKeyBean() {
    }

    public String getSourceKey() {
        return sourceKey;
    }

    public void setSourceKey(String sourceKey) {
        this.sourceKey = sourceKey;
    }

    @Override
    public SourceKey getTable() {
        return new SourceKey(sourceKey);
    }

    public URI getUri() {
        return uri;
    }

    public void setUri(URI uri) {
        this.uri = uri;
    }

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    public Types.StoreType getStoreType() {
        return storeType;
    }

    public void setStoreType(Types.StoreType storeType) {
        this.storeType = storeType;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public SwiftSegmentEntity convert() {
        SwiftSegmentEntity entity = new SwiftSegmentEntity();
        entity.setId(id);
        entity.setSegmentOwner(sourceKey);
        entity.setSegmentOrder(order);
        entity.setStoreType(storeType);
        entity.setSegmentUri(uri);
        return entity;
    }

    @Override
    public String toString() {
        return sourceKey + '@' + storeType + "@" + order;
    }
}
