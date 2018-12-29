package com.fr.swift.query.aggregator;

import com.fr.swift.segment.column.Column;
import com.fr.swift.structure.iterator.RowTraversal;

/**
 * 用于结果合并的聚合器
 * Created by Lyon on 2018/5/9.
 */
public class WrappedAggregator<T extends AggregatorValue> implements Aggregator<T> {

    private static final long serialVersionUID = -499945849290982910L;
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
    public AggregatorType getAggregatorType() {
        return null != changedAgg ? changedAgg.getAggregatorType() : metricAgg.getAggregatorType();
    }

    @Override
    public void combine(T current, T other) {
        if (changedAgg == null) {
            metricAgg.combine(current, other);
        } else {
            changedAgg.combine(current, changedAgg.createAggregatorValue(other));
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        WrappedAggregator<?> that = (WrappedAggregator<?>) o;

        if (metricAgg != null ? !metricAgg.equals(that.metricAgg) : that.metricAgg != null) return false;
        return changedAgg != null ? changedAgg.equals(that.changedAgg) : that.changedAgg == null;
    }

    @Override
    public int hashCode() {
        int result = metricAgg != null ? metricAgg.hashCode() : 0;
        result = 31 * result + (changedAgg != null ? changedAgg.hashCode() : 0);
        return result;
    }
}
