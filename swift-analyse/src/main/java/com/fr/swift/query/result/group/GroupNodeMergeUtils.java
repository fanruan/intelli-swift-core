package com.fr.swift.query.result.group;

import com.fr.swift.query.aggregator.Aggregator;
import com.fr.swift.query.aggregator.AggregatorValue;
import com.fr.swift.query.aggregator.AggregatorValueUtils;
import com.fr.swift.query.aggregator.Combiner;
import com.fr.swift.result.GroupNode;
import com.fr.swift.structure.queue.SortedListMergingUtils;

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
    public static GroupNode merge(List<GroupNode> roots, List<Comparator<GroupNode>> nodeComparators,
                                  List<Aggregator> aggregators) {
//        if (roots.size() == 1) {
//            return roots.get(0);
//        }
        GroupNode mergeRoot = roots.get(0);
        AggregatorValue[] mergeRootValues = mergeRoot.getAggregatorValue();
        for (int i = 1; i < roots.size(); i++) {
            AggregatorValue[] values = roots.get(i).getAggregatorValue();
            if (values.length == 0) {
                continue;
            }
            mergeRootValues = aggregateValues(aggregators, mergeRootValues, values);
        }
        // 从新设置一下值
        mergeRoot.setAggregatorValue(mergeRootValues);
        List<Iterator<GroupNode>> iterators = new ArrayList<Iterator<GroupNode>>();
        for (GroupNode node : roots) {
            if (node.getChildrenSize() == 0) {
                // 跳过为空的root
                continue;
            }
            iterators.add(node.getChildren().iterator());
        }
        if (iterators.isEmpty()) {
            return mergeRoot;
        }
        Iterator<GroupNode> iterator = SortedListMergingUtils.mergeIterator(iterators, nodeComparators.get(0),
                new NodeCombiner(1, aggregators, nodeComparators));
        mergeRoot.clearChildren();
        while (iterator.hasNext()) {
            mergeRoot.addChild(iterator.next());
        }
        return mergeRoot;
    }

    private static AggregatorValue[] aggregateValues(List<Aggregator> aggregators, AggregatorValue[] current, AggregatorValue[] other) {
        if (current.length == 0 && other.length == 0) {
            return new AggregatorValue[0];
        }
        AggregatorValue[] result = new AggregatorValue[current.length];
        for (int i = 0; i < aggregators.size(); i++) {
            result[i] = AggregatorValueUtils.combine(current[i], other[i], aggregators.get(i));
        }
        return result;
    }

    private static class NodeCombiner implements Combiner<GroupNode> {

        private int childrenDimensionIndex;
        private List<Aggregator> aggregators;
        private List<Comparator<GroupNode>> comparators;

        public NodeCombiner(int childrenDimensionIndex, List<Aggregator> aggregators, List<Comparator<GroupNode>> comparators) {
            this.childrenDimensionIndex = childrenDimensionIndex;
            this.aggregators = aggregators;
            this.comparators = comparators;
        }

        @Override
        public void combine(GroupNode current, GroupNode other) {
            AggregatorValue[] currentValues = current.getAggregatorValue();
            AggregatorValue[] otherValues = other.getAggregatorValue();
            // 合并两个节点，并把合并结果设置到current节点
            current.setAggregatorValue(aggregateValues(aggregators, currentValues, otherValues));
            if (current.getChildrenSize() == 0 && other.getChildrenSize() == 0) {
                // 子节点为空，返回
                return;
            }
            // 合并两个节点的子节点
            List<Iterator<GroupNode>> iterators = new ArrayList<Iterator<GroupNode>>();
            iterators.add(current.getChildren().iterator());
            iterators.add(other.getChildren().iterator());
            Iterator<GroupNode> iterator = SortedListMergingUtils.mergeIterator(iterators, comparators.get(childrenDimensionIndex),
                    new NodeCombiner(childrenDimensionIndex + 1, aggregators, comparators));
            current.clearChildren();
            while (iterator.hasNext()) {
                current.addChild(iterator.next());
            }
        }
    }
}
