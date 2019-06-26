package com.fr.swift.query.filter.match;

import com.fr.swift.query.aggregator.AggregatorValueRow;
import com.fr.swift.query.sort.Sort;
import com.fr.swift.query.sort.SortType;
import com.fr.swift.result.SwiftNode;

import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

/**
 * TODO 2019/06/23 这里只取了一条
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

                        Iterator<AggregatorValueRow> it1 = o1.getAggregatorValue().iterator();
                        Number v1 = (Number) it1.next().getValue(sort.getTargetIndex() - dimensionSorts.size()).calculateValue();
                        Iterator<AggregatorValueRow> it2 = o2.getAggregatorValue().iterator();
                        Number v2 = (Number) it2.next().getValue(sort.getTargetIndex() - dimensionSorts.size()).calculateValue();
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
