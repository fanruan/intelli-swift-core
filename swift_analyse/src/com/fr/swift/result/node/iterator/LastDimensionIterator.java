package com.fr.swift.result.node.iterator;

import com.fr.swift.query.aggregator.AggregatorValue;
import com.fr.swift.structure.iterator.Filter;
import com.fr.swift.structure.iterator.FilteredIterator;
import com.fr.swift.structure.iterator.MapperIterator;
import com.fr.swift.result.node.GroupNode;
import com.fr.swift.util.function.Function;

import java.util.Iterator;
import java.util.List;

/**
 * 用于“所有值”类的计算指标计算。比如根据所有值累计、求和、平均等。
 *
 * Created by Lyon on 2018/4/4.
 */
public class LastDimensionIterator implements Iterator<List<AggregatorValue[]>> {

    private Iterator<List<AggregatorValue[]>> rows;

    public LastDimensionIterator(GroupNode root, Function<GroupNode, List<AggregatorValue[]>> fn) {
        init(root, fn);
    }

    private void init(GroupNode root, Function<GroupNode, List<AggregatorValue[]>> fn) {
        Iterator<GroupNode> iterator = new BFTGroupNodeIterator(root);
        FilteredIterator<GroupNode> filteredIterator = new FilteredIterator<GroupNode>(iterator, new Filter<GroupNode>() {
            @Override
            public boolean accept(GroupNode biGroupNode) {
                return biGroupNode.getChildrenSize() == 0;
            }
        });
        rows = new MapperIterator<GroupNode, List<AggregatorValue[]>>(filteredIterator, fn);
    }

    @Override
    public boolean hasNext() {
        return rows.hasNext();
    }

    @Override
    public List<AggregatorValue[]> next() {
        return rows.next();
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException();
    }
}
