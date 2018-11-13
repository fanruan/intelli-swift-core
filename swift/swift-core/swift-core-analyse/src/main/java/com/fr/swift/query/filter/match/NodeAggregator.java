package com.fr.swift.query.filter.match;

import com.fr.swift.query.aggregator.Aggregator;
import com.fr.swift.query.aggregator.AggregatorValue;
import com.fr.swift.result.SwiftNode;
import com.fr.swift.structure.Pair;

/**
 * Created by pony on 2018/4/19.
 */
public class NodeAggregator {
    public static void aggregate(SwiftNode node, Pair<Aggregator, Boolean>[] aggregators) {
        if (hasDifferentAggregator(aggregators)) {
            checkValueTypes(node, aggregators);
        }
        agg(node, aggregators);
    }

    /**
     * 转化aggregatoValue类型
     *
     * @param node
     * @param aggregators
     */
    private static void checkValueTypes(SwiftNode node, Pair<Aggregator, Boolean>[] aggregators) {
        for (int i = 0; i < node.getChildrenSize(); i++) {
            checkValueTypes(node.getChild(i), aggregators);
        }
        if (node.getChildrenSize() == 0) {
            for (int i = 0; i < aggregators.length; i++) {
                if (aggregators[i] != null && aggregators[i].getValue()) {
                    node.setAggregatorValue(i, aggregators[i].getKey().createAggregatorValue(node.getAggregatorValue(i)));
                }
            }
        }
    }

    private static boolean hasDifferentAggregator(Pair<Aggregator, Boolean>[] aggregators) {
        for (Pair<Aggregator, Boolean> aggPair : aggregators) {
            if (aggPair != null && aggPair.getValue()) {
                return true;
            }
        }
        return false;
    }

    private static void agg(SwiftNode node, Pair<Aggregator, Boolean>[] aggregators) {
        for (int i = 0; i < node.getChildrenSize(); i++) {
            agg(node.getChild(i), aggregators);
        }
        for (int i = 0; i < node.getChildrenSize(); i++) {
            SwiftNode child = node.getChild(i);
            if (i == 0) {
                for (int j = 0; j < aggregators.length; j++) {
                    if (aggregators[j] == null) {
                        continue;
                    }
                    node.setAggregatorValue(j, (AggregatorValue) child.getAggregatorValue(j).clone());
                }
            } else {
                for (int j = 0; j < aggregators.length; j++) {
                    if (aggregators[j] == null) {
                        continue;
                    }
                    aggregators[j].getKey().combine(node.getAggregatorValue(j), child.getAggregatorValue(j));
                }
            }
        }
    }
}
