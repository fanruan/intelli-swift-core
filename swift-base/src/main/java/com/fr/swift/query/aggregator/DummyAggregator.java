package com.fr.swift.query.aggregator;

import com.fr.swift.segment.column.Column;
import com.fr.swift.structure.iterator.RowTraversal;

/**
 * Created by Lyon on 2018/4/11.
 */
public class DummyAggregator implements Aggregator<AggregatorValue> {

    protected static final Aggregator INSTANCE = new SumAggregate();

    @Override
    public AggregatorValue aggregate(RowTraversal traversal, Column column) {
        throw new UnsupportedOperationException();
    }

    @Override
    public AggregatorValue createAggregatorValue(AggregatorValue value) {
        return null;
    }

    @Override
    public void combine(AggregatorValue current, AggregatorValue other) {
        // 啥都不做
    }
}
