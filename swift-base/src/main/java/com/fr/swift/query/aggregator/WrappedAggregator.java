package com.fr.swift.query.aggregator;

import com.fr.swift.segment.column.Column;
import com.fr.swift.structure.iterator.RowTraversal;

/**
 * 用于结果合并的聚合器
 * Created by Lyon on 2018/5/9.
 */
public class WrappedAggregator<T extends AggregatorValue> implements Aggregator<T> {

    private Aggregator metricAgg;
    private Aggregator changedAgg;

    public WrappedAggregator(Aggregator metricAgg) {
        this.metricAgg = metricAgg;
    }

    public WrappedAggregator(Aggregator metricAgg, Aggregator changedAgg) {
        this.metricAgg = metricAgg;
        this.changedAgg = changedAgg;
    }

    @Override
    public T aggregate(RowTraversal traversal, Column column) {
        throw new UnsupportedOperationException();
    }

    @Override
    public T createAggregatorValue(AggregatorValue value) {
        return (T) (changedAgg != null ? changedAgg.createAggregatorValue(value) : value.clone());
    }

    @Override
    public void combine(T current, T other) {
        if (changedAgg == null) {
            metricAgg.combine(current, other);
        } else {
            changedAgg.combine(current, changedAgg.createAggregatorValue(other));
        }
    }
}
