package com.fr.swift.cal.builder;

import com.fr.swift.bitmap.ImmutableBitMap;
import com.fr.swift.query.aggregator.Aggregator;
import com.fr.swift.query.aggregator.AggregatorValue;
import com.fr.swift.segment.column.Column;
import com.fr.swift.structure.iterator.RowTraversal;

/**
 * Created by pony on 2018/4/17.
 */
public class MetricFilterAggregator implements Aggregator {
    private Aggregator aggregator;
    private ImmutableBitMap filter;
    public MetricFilterAggregator(Aggregator aggregator, ImmutableBitMap filter) {
        this.aggregator = aggregator;
        this.filter = filter;
    }

    @Override
    public AggregatorValue aggregate(RowTraversal traversal, Column column) {
        traversal = filter == null ? traversal : filter.getAnd(traversal.toBitMap());
        return aggregator.aggregate(traversal, column);
    }

    @Override
    public void combine(Object current, Object other) {
        aggregator.combine(current, other);
    }
}
