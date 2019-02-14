package com.fr.swift.segment;

import java.util.List;

/**
 * @author yee
 * @date 2018/6/13
 * todo 补javadoc
 */
public interface SegmentDestination extends Comparable<SegmentDestination> {

    boolean isRemote();

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
