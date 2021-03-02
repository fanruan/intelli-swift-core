package com.fr.swift.cloud.query.aggregator.funnel;

import com.fr.swift.cloud.query.aggregator.AggregatorValue;
import com.fr.swift.cloud.query.aggregator.ExtensionAggregatorValue;
import com.fr.swift.cloud.query.aggregator.FunnelAggregatorValue;
import com.fr.swift.cloud.query.aggregator.FunnelHelperValue;
import com.fr.swift.cloud.query.aggregator.FunnelPathsAggregatorValue;
import com.fr.swift.cloud.query.aggregator.SwiftNodeAggregatorValue;
import com.fr.swift.cloud.query.group.FunnelGroupKey;
import com.fr.swift.cloud.result.GroupNode;
import com.fr.swift.cloud.result.SwiftNode;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author yee
 * @date 2019-07-12
 */
public class FunnelPathsNodeAggregatorValue implements SwiftNodeAggregatorValue, ExtensionAggregatorValue<SwiftNode> {
    private FunnelPathKey key;
    private FunnelAggregatorValue value;
    private SwiftNode leaf;
    private int length;

    public FunnelPathsNodeAggregatorValue(FunnelPathKey key, FunnelAggregatorValue value) {
        this.key = key;
        this.value = value;
        this.length = key.size();
    }

    @Override
    public SwiftNode calculateValue(int depth) {
        int d = depth + 1;
        GroupNode node = new GroupNode(d, key.getPath());
        leaf = node;
        node.setAggregatorValue(new AggregatorValue[]{new FunnelPathsAggregatorValue(new HashMap<FunnelPathKey, FunnelAggregatorValue>(Collections.singletonMap(key, value)))});
        return node;
    }

    @Override
    public SwiftNode getLeafNode() {
        return leaf;
    }

    @Override
    public double calculate() {
        throw new UnsupportedOperationException();
    }

    @Override
    public SwiftNode calculateValue() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Object clone() {
        return new FunnelPathsNodeAggregatorValue(key, value);
    }

    @Override
    public List calculateAndExtension() {
        Map.Entry<FunnelGroupKey, FunnelHelperValue> next = value.getValueMap().entrySet().iterator().next();
        int[] count = next.getValue().getCount();
        return Collections.singletonList(100 * ((double) count[length - 1]) / count[length - 2]);
    }
}
