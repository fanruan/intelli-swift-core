package com.fr.swift.query.filter.match;

import com.fr.swift.query.aggregator.AggregatorValueSet;
import com.fr.swift.query.sort.Sort;
import com.fr.swift.query.sort.SortType;
import com.fr.swift.result.SwiftNode;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by pony on 2018/5/4.
 */
public class NodeSorter {
    public static void sort(SwiftNode node, List<Sort> dimensionSorts) {
        sort(node, 0, dimensionSorts);
    }

    private static void sort(SwiftNode node, int deep, final List<Sort> dimensionSorts) {

        if (deep < dimensionSorts.size()) {
            final Sort sort = dimensionSorts.get(deep);
            if (sort != null) {
                final int fDeep = deep;
                Collections.sort(node.getChildren(), new Comparator<SwiftNode>() {

                    @Override
                    public int compare(SwiftNode o1, SwiftNode o2) {

                        AggregatorValueSet set1 = o1.getAggregatorValue();
                        Number v1 = (Number) set1.next().getValue(sort.getTargetIndex() - dimensionSorts.size()).calculateValue();
                        set1.reset();
                        AggregatorValueSet set2 = o2.getAggregatorValue();
                        Number v2 = (Number) set2.next().getValue(sort.getTargetIndex() - dimensionSorts.size()).calculateValue();
                        set2.reset();
                        if (v1 == null) {
                            return 1;
                        }
                        if (v2 == null) {
                            return -1;
                        }
                        if (v1.doubleValue() == v2.doubleValue()) {
                            return 0;
                        }
                        boolean v = v1.doubleValue() < v2.doubleValue();
                        return (sort.getSortType() == SortType.ASC == v) ? -1 : 1;
                    }
                });
            }
            List<SwiftNode> children = node.getChildren();
            for (SwiftNode n : children) {
                sort(n, deep + 1, dimensionSorts);
                // 避免用了排序前的顺序
                n.setSibling(null);
            }
        }
    }
}
