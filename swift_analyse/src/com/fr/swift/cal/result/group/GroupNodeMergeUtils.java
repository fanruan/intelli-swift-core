package com.fr.swift.cal.result.group;

import com.fr.swift.query.aggregator.Aggregator;
import com.fr.swift.query.aggregator.Combiner;
import com.fr.swift.result.node.GroupNode;
import com.fr.swift.result.node.iterator.ChildIterator;
import com.fr.swift.structure.queue.SortedListMergingUtils;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

/**
 * Created by Lyon on 2018/4/17.
 */
public class GroupNodeMergeUtils {

    public static GroupNode merge(List<GroupNode> roots, List<Comparator<GroupNode>> nodeComparators,
                                  List<Aggregator> aggregators) {
        GroupNode mergeRoot = new GroupNode(0, -1, null);
        List<Iterator<GroupNode>> iterators = new ArrayList<Iterator<GroupNode>>();
        for (GroupNode node : roots) {
            iterators.add(new ChildIterator(node));
        }
        Iterator<GroupNode> iterator = SortedListMergingUtils.mergeIterator(iterators, nodeComparators.get(0),
                new NodeCombiner(1, nodeComparators));
        while (iterator.hasNext()) {
            mergeRoot.addChild(iterator.next());
        }
        return mergeRoot;
    }

    private static class NodeCombiner implements Combiner<GroupNode> {

        private int childrenDimensionIndex;
        List<Comparator<GroupNode>> comparators;

        public NodeCombiner(int childrenDimensionIndex, List<Comparator<GroupNode>> comparators) {
            this.childrenDimensionIndex = childrenDimensionIndex;
            this.comparators = comparators;
        }

        @Override
        public void combine(GroupNode current, GroupNode other) {
            // TODO: 2018/4/17 合并两个子节点，然后看情况决定是否需要合并子节点
            if (current.getChildrenSize() == 0 && other.getChildrenSize() == 0) {
                // 子节点为空，返回
                return;
            }
            // 合并两个节点的子节点
            List<Iterator<GroupNode>> iterators = new ArrayList<Iterator<GroupNode>>();
            iterators.add(new ChildIterator(current));
            iterators.add(new ChildIterator(other));
            Iterator<GroupNode> iterator = SortedListMergingUtils.mergeIterator(iterators, comparators.get(childrenDimensionIndex),
                    new NodeCombiner(childrenDimensionIndex + 1, comparators));
            // TODO: 2018/4/17 clear childrenMap of current
            while (iterator.hasNext()) {
                current.addChild(iterator.next());
            }
        }
    }
}
