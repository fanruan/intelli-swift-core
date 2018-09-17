package com.fr.swift.query.filter.match;

import com.fr.swift.query.aggregator.AggregatorValue;
import com.fr.swift.result.SwiftNode;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by pony on 2018/4/18.
 */
public class NodeFilter {

    private static void filter(SwiftNode node, int deep, List<MatchFilter> matchFilters) {
        if (deep < matchFilters.size()) {
            MatchFilter filter = matchFilters.get(deep);
            if (filter != null) {
                List<SwiftNode> children = filter(node.getChildren(), deep, matchFilters);
                node.clearChildren();
                if (children == null || children.isEmpty()) {
                    clearEmptyNode(node);
                } else {
                    for (SwiftNode n : children) {
                        node.addChild(n);
                    }
                }
            }
            List<SwiftNode> children = node.getChildren();
            for (SwiftNode n : children) {
                filter(n, deep + 1, matchFilters);
            }
        }
    }


    private static void clearEmptyNode(SwiftNode node) {

        SwiftNode parent = node.getParent();
        //BI-21146,空节点清空汇总值
        node.setAggregatorValue(new AggregatorValue[node.getAggregatorValue().length]);
        if (parent != null) {
            List<SwiftNode> children = parent.getChildren();
            parent.clearChildren();
            for (SwiftNode child : children) {
                if (child != node) {
                    parent.addChild(child);
                }
            }
            if (parent.getChildrenSize() == 0) {
                clearEmptyNode(parent);
            }
        }
    }

    private static List<SwiftNode> filter(List<SwiftNode> children, final int deep, List<MatchFilter> matchFilters) {
        MatchFilter filter = matchFilters.get(deep);
        List<SwiftNode> results = new ArrayList<SwiftNode>();
        if (filter == null) {
            results = children;
        } else {
            for (SwiftNode result : children) {
                if (filter.matches(result)) {
                    results.add(result);
                }
            }
        }
        return results;
    }

    public static void filter(SwiftNode node, List<MatchFilter> matchFilters) {
        filter(node, 0, matchFilters);
    }
}
