package com.fr.swift.query.aggregator;

import com.fr.swift.result.SwiftNode;

/**
 * @author yee
 * @date 2019-07-07
 */
public interface SwiftNodeNodeAggregatorValue extends AggregatorValue<SwiftNode> {
    SwiftNode calculateValue(int depth);

    SwiftNode getLeafNode();
}
