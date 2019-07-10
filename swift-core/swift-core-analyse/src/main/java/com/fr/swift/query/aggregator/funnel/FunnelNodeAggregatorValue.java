package com.fr.swift.query.aggregator.funnel;

import com.fr.swift.query.aggregator.AggregatorValue;
import com.fr.swift.query.aggregator.DoubleAmountAggregatorValue;
import com.fr.swift.query.aggregator.FunnelAggValue;
import com.fr.swift.query.aggregator.SwiftNodeAggregatorValue;
import com.fr.swift.query.group.FunnelGroupKey;
import com.fr.swift.result.GroupNode;
import com.fr.swift.result.SwiftNode;

import java.util.List;

/**
 * @author yee
 * @date 2019-07-10
 */
public class FunnelNodeAggregatorValue implements SwiftNodeAggregatorValue {
    private FunnelGroupKey key;
    private FunnelAggValue value;
    private SwiftNode leaf;

    public FunnelNodeAggregatorValue(FunnelGroupKey key, FunnelAggValue value) {
        this.key = key;
        this.value = value;
    }

    @Override
    public SwiftNode calculateValue(int depth) {
        int d = depth + 1;
        GroupNode node = new GroupNode(d, null);
        node.setData(key.getDate());
        switch (key.getType()) {
            case RANGE:
                List<Double> rangePair = key.getRangePair();
                GroupNode child = new GroupNode(d + 1, rangePair.get(0) + "-" + rangePair.get(1));
                node.addChild(child);
                leaf = child;
                break;
            case NORMAL:
                String strGroup = key.getStrGroup();
                GroupNode child1 = new GroupNode(d + 1, strGroup);
                node.addChild(child1);
                leaf = child1;
            default:
                leaf = node;
                break;
        }
        int[] count = value.getCount();
        AggregatorValue[] values = new AggregatorValue[count.length];
        for (int i = 0; i < count.length; i++) {
            values[i] = new DoubleAmountAggregatorValue(count[i]);
        }
        leaf.setAggregatorValue(values);
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
        return new FunnelNodeAggregatorValue(key, value);
    }
}
