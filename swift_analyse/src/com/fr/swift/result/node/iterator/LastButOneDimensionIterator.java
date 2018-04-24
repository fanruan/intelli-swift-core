package com.fr.swift.result.node.iterator;

import com.fr.swift.query.aggregator.AggregatorValue;
import com.fr.swift.structure.iterator.MapperIterator;
import com.fr.swift.result.node.GroupNode;
import com.fr.swift.util.function.Function;

import java.util.Iterator;

/**
 * 倒数第二个维度的子节点的迭代器。适用于“组内”类计算，比如组内累计、求和、平均等。
 *
 * Created by Lyon on 2018/4/4.
 */
public class LastButOneDimensionIterator implements Iterator<Iterator<AggregatorValue[]>> {

    private GroupNode lastButOneNode;

    public LastButOneDimensionIterator(GroupNode root) {
        initLastOneNode(root);
    }

    private void initLastOneNode(GroupNode root) {
        GroupNode tmp = root;
        while (tmp.getChild(0) != null) {
            tmp = tmp.getChild(0);
        }
        lastButOneNode = tmp.getParent();
    }

    @Override
    public boolean hasNext() {
        return lastButOneNode != null;
    }

    @Override
    public Iterator<AggregatorValue[]> next() {
        Iterator<AggregatorValue[]> iterator = new MapperIterator<GroupNode, AggregatorValue[]>(new ChildIterator(lastButOneNode), new Function<GroupNode, AggregatorValue[]>() {
            @Override
            public AggregatorValue[] apply(GroupNode p) {
                return p.getAggregatorValue();
            }
        });
        lastButOneNode = getNextNode();
        return iterator;
    }

    private GroupNode getNextNode() {
        if (lastButOneNode.getSibling() != null) {
            return lastButOneNode.getSibling();
        }
        // TODO: 2018/4/4 棘手
        return null;
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException();
    }
}
