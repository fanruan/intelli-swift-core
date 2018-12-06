package com.fr.swift.segment;

import com.fr.swift.query.Queryable;

import java.util.List;

/**
 * @author yee
 * @date 2018/6/13
 * todo 补javadoc
 */
public interface SegmentDestination extends Comparable<SegmentDestination> {
    boolean isRemote();

    Class<? extends Queryable> getServiceClass();

    /**
     * todo 这个方法感觉和这个接口不搭配鸭
     *
     * @return
     */
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
