package com.fr.swift.segment.bean;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fr.swift.query.Queryable;
import com.fr.swift.segment.bean.impl.SegmentDestinationImpl;

import java.io.Serializable;
import java.util.List;

/**
 * @author yee
 * @date 2018/6/13
 */
@JsonDeserialize(as = SegmentDestinationImpl.class)
public interface SegmentDestination extends Serializable, Comparable<SegmentDestination> {
    boolean isRemote();

    Class<? extends Queryable> getServiceClass();

    String getMethodName();

    String getAddress();

    int getOrder();

    String getClusterId();

    String getSegmentId();

    List<String> getSpareNodes();

    String getCurrentNode();

    void setSpareNodes(List<String> spareNodes);

    void setClusterId(String clusterId);

    SegmentDestination copy();
}
