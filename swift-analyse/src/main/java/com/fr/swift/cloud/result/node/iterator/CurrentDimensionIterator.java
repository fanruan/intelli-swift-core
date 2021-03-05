package com.fr.swift.cloud.result.node.iterator;

import com.fr.swift.cloud.query.aggregator.AggregatorValue;
import com.fr.swift.cloud.result.SwiftNode;
import com.fr.swift.cloud.structure.iterator.Filter;
import com.fr.swift.cloud.structure.iterator.FilteredIterator;
import com.fr.swift.cloud.structure.iterator.MapperIterator;
import com.fr.swift.cloud.util.function.Function;

import java.util.Iterator;
import java.util.List;

/**
 * Created by pony on 2018/5/16.
 */
public class CurrentDimensionIterator implements Iterator<List<AggregatorValue[]>> {

    private Iterator<List<AggregatorValue[]>> rows;

    public CurrentDimensionIterator(SwiftNode root, Function<SwiftNode, List<AggregatorValue[]>> fn) {
        init(root, fn);
    }

    private void init(SwiftNode root, Function<SwiftNode, List<AggregatorValue[]>> fn) {
        Iterator<SwiftNode> iterator = root.getChildren().iterator();
        FilteredIterator<SwiftNode> filteredIterator = new FilteredIterator<SwiftNode>(iterator, new Filter<SwiftNode>() {
            @Override
            public boolean accept(SwiftNode biGroupNode) {
                return biGroupNode.getChildrenSize() == 0;
            }
        });
        rows = new MapperIterator<SwiftNode, List<AggregatorValue[]>>(filteredIterator, fn);
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