package com.fr.swift.query.aggregator;

import com.fr.swift.segment.column.Column;
import com.fr.swift.structure.iterator.RowTraversal;

/**
 * Created by pony on 2018/3/26.
 */
public class CountAggregator implements Aggregator<DoubleAmountAggregateValue> {

    protected static final Aggregator INSTANCE = new CountAggregator();

    @Override
    public DoubleAmountAggregateValue aggregate(RowTraversal traversal, Column column) {
        DoubleAmountAggregateValue value = new DoubleAmountAggregateValue();
        value.setValue(traversal.getCardinality());
        return value;
    }

    @Override
    public void combine(DoubleAmountAggregateValue value, DoubleAmountAggregateValue other) {
        value.setValue(value.getValue() + other.getValue());
    }
}
