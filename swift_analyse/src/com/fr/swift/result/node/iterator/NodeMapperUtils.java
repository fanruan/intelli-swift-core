package com.fr.swift.result.node.iterator;

import com.fr.swift.query.aggregator.AggregatorValue;
import com.fr.swift.result.node.GroupNode;
import com.fr.swift.result.node.xnode.XLeftNode;
import com.fr.swift.util.function.Function;

import java.util.Arrays;
import java.util.List;

/**
 * Created by Lyon on 2018/4/10.
 */
public class NodeMapperUtils {

    public static Function<GroupNode, List<AggregatorValue[]>> mapper(GroupNode groupNode) {
        return new Function<GroupNode, List<AggregatorValue[]>>() {
            @Override
            public List<AggregatorValue[]> apply(final GroupNode p) {
                return Arrays.<AggregatorValue[]>asList(p.getAggregatorValue());
            }
        };
    }

    public static Function<GroupNode, List<AggregatorValue[]>> mapper(XLeftNode xLeftNode) {
        return new Function<GroupNode, List<AggregatorValue[]>>() {
            @Override
            public List<AggregatorValue[]> apply(final GroupNode p) {
                return ((XLeftNode) p).getValueArrayList();
            }
        };
    }
}
