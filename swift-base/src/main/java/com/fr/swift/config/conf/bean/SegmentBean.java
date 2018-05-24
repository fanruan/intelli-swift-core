package com.fr.swift.config.conf.bean;

import com.fr.decision.base.data.BaseDataRecord;
import com.fr.swift.config.conf.entity.SegmentEntity;
import com.fr.swift.cube.io.Types;

import java.io.Serializable;
import java.net.URI;

/**
 * @author yee
 * @date 2018/5/24
 */
public class SegmentBean extends BaseDataRecord implements Serializable, Convert<SegmentEntity> {

    public static final String COLUMN_SOURCE_KEY = "sourceKey";
    private String sourceKey;
    private String name;
    private URI uri;
    private int order;
    private Types.StoreType storeType;

    public SegmentBean(String id, String sourceKey, String name, URI uri, int order, Types.StoreType storeType) {
        this.setId(id);
        this.sourceKey = sourceKey;
        this.name = name;
        this.uri = uri;
        this.order = order;
        this.storeType = storeType;
    }

    public SegmentBean() {
    }

    public String getSourceKey() {
        return sourceKey;
    }

    public void setSourceKey(String sourceKey) {
        this.sourceKey = sourceKey;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    @Override
    public SegmentEntity convert() {
        SegmentEntity entity = new SegmentEntity();
        entity.setId(getId());
        entity.setSourceKey(sourceKey);
        entity.setName(name);
        entity.setOrder(order);
        entity.setStoreType(storeType);
        entity.setUri(uri);
        return entity;
    }
}
