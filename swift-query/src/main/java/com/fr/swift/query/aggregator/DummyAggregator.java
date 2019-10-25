package com.fr.swift.query.aggregator;

import com.fr.swift.segment.column.Column;
import com.fr.swift.segment.column.ColumnKey;
import com.fr.swift.structure.iterator.RowTraversal;

import java.util.Map;

/**
 * @author Lyon
 * @date 2018/4/11
 */
public enum DummyAggregator implements Aggregator<AggregatorValue<?>> {
    //
    INSTANCE;

    private static final long serialVersionUID = -8029762611968533612L;

    @Override
    public AggregatorValue<?> aggregate(RowTraversal traversal, Map<ColumnKey, Column<?>> columns) {
        throw new UnsupportedOperationException();
    }

    @Override
    public AggregatorValue<?> aggregate(RowTraversal traversal, Column<?> column) {
        throw new UnsupportedOperationException();
    }

    @Override
    public AggregatorValue<?> createAggregatorValue(AggregatorValue<?> value) {
        return null;
    }

    @Override
    public AggregatorType getAggregatorType() {
        return null;
    }

    @Override
    public void combine(AggregatorValue current, AggregatorValue other) {
        // 啥都不做
    }
}
