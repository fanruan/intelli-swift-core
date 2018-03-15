package com.fr.swift.config;

/**
 * @author yee
 * @date 2018/3/9
 */
public interface ISegmentKey {
    void setName(String name);

    String getStoreType();

    void setStoreType(String storeType);

    String getName();

    String getUri();

    void setUri(String uri);

    int getSegmentOrder();

    void setSegmentOrder(int segmentOrder);

    String getSourceId();

    void setSourceId(String sourceId);
}
