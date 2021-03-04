package com.fr.swift.cloud.query.aggregator;

import com.fr.swift.cloud.result.SwiftNode;

/**
 * @author yee
 * @date 2019-07-07
 */
public interface SwiftNodeAggregatorValue extends AggregatorValue<SwiftNode> {
    /**
     * 计算值
     *
     * @param depth 当前节点深度
     * @return
     */
    SwiftNode calculateValue(int depth);

    /**
     * 获取野子节点
     *
     * @return
     */
    SwiftNode getLeafNode();
}
