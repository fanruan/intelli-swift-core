package com.fr.swift.result.node.iterator;

import com.fr.swift.query.aggregator.AggregatorValue;
import com.fr.swift.result.GroupNode;
import com.fr.swift.structure.iterator.Filter;
import com.fr.swift.structure.iterator.FilteredIterator;
import com.fr.swift.structure.iterator.MapperIterator;
import com.fr.swift.util.function.Function;

import java.util.Iterator;
import java.util.List;

/**
 * Created by pony on 2018/5/16.
 */
public class CurrentDimensionIterator implements Iterator<List<AggregatorValue[]>> {

    private Iterator<List<AggregatorValue[]>> rows;

    public CurrentDimensionIterator(GroupNode root, Function<GroupNode, List<AggregatorValue[]>> fn) {
        init(root, fn);
    }

    private void init(GroupNode root, Function<GroupNode, List<AggregatorValue[]>> fn) {
        Iterator<GroupNode> iterator = root.getChildren().iterator();
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