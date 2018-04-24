package com.fr.swift.query.filter.match;

import com.fr.swift.query.aggregator.Aggregator;
import com.fr.swift.query.aggregator.AggregatorValue;
import com.fr.swift.result.SwiftNode;

/**
 * Created by pony on 2018/4/19.
 */
public class NodeAggregator {
    public static void aggregate(SwiftNode node, Aggregator[] aggregators){
        for (int i = 0; i < node.getChildrenSize(); i++){
            aggregate(node.getChild(i), aggregators);
        }
        for (int i = 0; i < node.getChildrenSize(); i++){
            SwiftNode child = node.getChild(i);
            if (i == 0){
                for (int j = 0; j < aggregators.length; j++){
                    node.setAggregatorValue(j, (AggregatorValue) child.getAggregatorValue(j).clone());
                }
            } else {
                for (int j = 0; j < aggregators.length; j++){
                    aggregators[j].combine(node.getAggregatorValue(j), child.getAggregatorValue(j));
                }
            }
        }
    }
}
