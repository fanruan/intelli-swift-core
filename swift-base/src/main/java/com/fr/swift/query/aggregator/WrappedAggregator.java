package com.fr.swift.query.aggregator;

import com.fr.swift.segment.column.Column;
import com.fr.swift.structure.iterator.RowTraversal;

/**
 * 用于结果合并的聚合器
 * Created by Lyon on 2018/5/9.
 */
public class WrappedAggregator<T extends AggregatorValue> implements Aggregator<T> {

    private boolean isAggregatorTypeChanged;
    private Aggregator aggregator;

    public WrappedAggregator(boolean isAggregatorTypeChanged, Aggregator aggregator) {
        this.isAggregatorTypeChanged = isAggregatorTypeChanged;
        this.aggregator = aggregator;
    }

    @Override
    public T aggregate(RowTraversal traversal, Column column) {
        throw new UnsupportedOperationException();
    }

    @Override
    public T createAggregatorValue(AggregatorValue value) {
        return (T) (isAggregatorTypeChanged ? aggregator.createAggregatorValue(value) : value.clone());
    }

    @Override
    public void combine(T current, T other) {
        aggregator.combine(current, other);
    }
}
