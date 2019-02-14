package com.fr.swift.query.aggregator;

import com.fr.swift.bitmap.ImmutableBitMap;
import com.fr.swift.segment.column.Column;
import com.fr.swift.structure.iterator.RowTraversal;

/**
 * Created by pony on 2018/3/27.
 */
public abstract class AbstractAggregator<T extends AggregatorValue> implements Aggregator<T> {
    private static final long serialVersionUID = -4795472056172909847L;

    protected RowTraversal getNotNullTraversal(RowTraversal traversal, Column column) {
        ImmutableBitMap nullIndex = column.getBitmapIndex().getNullIndex();
        if (nullIndex != null && !nullIndex.isEmpty()) {
            traversal = traversal.toBitMap().getAndNot(nullIndex);
        }
        return traversal;
    }

    @Override
    public T createAggregatorValue(AggregatorValue value) {
        return (T) new DoubleAmountAggregatorValue(value.calculate());
    }
}
