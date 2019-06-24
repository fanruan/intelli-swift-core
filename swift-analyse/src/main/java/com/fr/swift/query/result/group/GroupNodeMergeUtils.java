package com.fr.swift.query.result.group;

import com.fr.swift.query.aggregator.Aggregator;
import com.fr.swift.query.aggregator.AggregatorValue;
import com.fr.swift.query.aggregator.AggregatorValueSet;
import com.fr.swift.query.aggregator.AggregatorValueUtils;
import com.fr.swift.query.aggregator.CombineAggregatorSet;
import com.fr.swift.query.aggregator.Combiner;
import com.fr.swift.query.aggregator.EmptyAggregatorValueSet;
import com.fr.swift.result.SwiftNode;
import com.fr.swift.structure.Pair;
import com.fr.swift.structure.queue.SortedListMergingUtils;
import com.fr.swift.util.function.Function;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

/**
 * Created by Lyon on 2018/4/17.
 */
public class GroupNodeMergeUtils {

    // TODO: 2018/7/24 是否根据全局字典进行合并只要处理好nodeComparator就好了
    // TODO: 2018/7/24 同时这边的合并仅处理根节点或者叶子节点的聚合值合并，其他维度节点默认为空
    public static SwiftNode merge(List<SwiftNode> roots, List<Comparator<SwiftNode>> nodeComparators,
                                  List<Aggregator> aggregators) {
//        if (roots.size() == 1) {
//            return roots.get(0);
//        }
        SwiftNode mergeRoot = roots.get(0);
        AggregatorValueSet mergeRootValues = mergeRoot.getAggregatorValue();
        for (int i = 1; i < roots.size(); i++) {
            AggregatorValueSet values = roots.get(i).getAggregatorValue();
            if (values.size() == 0) {
                continue;
            }
            mergeRootValues = aggregateValues(aggregators, mergeRootValues, values);
        }
        // 从新设置一下值
        mergeRoot.setAggregatorValue(mergeRootValues);
        List<Iterator<SwiftNode>> iterators = new ArrayList<Iterator<SwiftNode>>();
        for (SwiftNode node : roots) {
            if (node.getChildrenSize() == 0) {
                // 跳过为空的root
                continue;
            }
            iterators.add(node.getChildren().iterator());
        }
        if (iterators.isEmpty()) {
            return mergeRoot;
        }
        Iterator<SwiftNode> iterator = SortedListMergingUtils.mergeIterator(iterators, nodeComparators.get(0),
                new NodeCombiner(1, aggregators, nodeComparators));
        mergeRoot.clearChildren();
        while (iterator.hasNext()) {
            mergeRoot.addChild(iterator.next());
        }
        return mergeRoot;
    }

    private static AggregatorValueSet aggregateValues(List<Aggregator> aggregators, AggregatorValueSet current, AggregatorValueSet other) {
        if (current.size() == 0 && other.size() == 0) {
            return new EmptyAggregatorValueSet();
        }
        return new CombineAggregatorSet(current, other, aggregators, new Function<Pair<Aggregator, List<AggregatorValue>>, AggregatorValue>() {
            @Override
            public AggregatorValue apply(Pair<Aggregator, List<AggregatorValue>> p) {
                List<AggregatorValue> value = p.getValue();
                return AggregatorValueUtils.combine(value.get(0), value.get(1), p.getKey());
            }
        });
    }

    private static class NodeCombiner implements Combiner<SwiftNode> {
        private static final long serialVersionUID = 4120194941494959923L;
        private int childrenDimensionIndex;
        private List<Aggregator> aggregators;
        private List<Comparator<SwiftNode>> comparators;

        NodeCombiner(int childrenDimensionIndex, List<Aggregator> aggregators, List<Comparator<SwiftNode>> comparators) {
            this.childrenDimensionIndex = childrenDimensionIndex;
            this.aggregators = aggregators;
            this.comparators = comparators;
        }

        @Override
        public void combine(SwiftNode current, SwiftNode other) {
            AggregatorValueSet currentValues = current.getAggregatorValue();
            AggregatorValueSet otherValues = other.getAggregatorValue();
            // 合并两个节点，并把合并结果设置到current节点
            current.setAggregatorValue(aggregateValues(aggregators, currentValues, otherValues));
            if (current.getChildrenSize() == 0 && other.getChildrenSize() == 0) {
                // 子节点为空，返回
                return;
            }
            // 合并两个节点的子节点
            List<Iterator<SwiftNode>> iterators = new ArrayList<Iterator<SwiftNode>>();
            iterators.add(current.getChildren().iterator());
            iterators.add(other.getChildren().iterator());
            Iterator<SwiftNode> iterator = SortedListMergingUtils.mergeIterator(iterators, comparators.get(childrenDimensionIndex),
                    new NodeCombiner(childrenDimensionIndex + 1, aggregators, comparators));
            current.clearChildren();
            while (iterator.hasNext()) {
                current.addChild(iterator.next());
            }
        }
    }
}
