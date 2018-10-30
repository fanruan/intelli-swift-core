package com.fr.swift.segment;

import com.fr.swift.query.Queryable;
import com.fr.swift.segment.impl.SegmentDestinationImpl;
import com.fr.third.fasterxml.jackson.databind.annotation.JsonDeserialize;

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
