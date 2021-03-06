package com.fr.swift.cloud.query.aggregator;

import com.fr.swift.cloud.bitmap.ImmutableBitMap;
import com.fr.swift.cloud.segment.column.Column;
import com.fr.swift.cloud.structure.iterator.RowTraversal;

/**
 * @author pony
 * @date 2018/3/27
 */
public abstract class AbstractAggregator<T extends AggregatorValue<?>> extends SingleColumnAggregator<T> {

    protected RowTraversal getNotNullTraversal(RowTraversal traversal, Column<?> column) {
        ImmutableBitMap nullIndex = column.getBitmapIndex().getNullIndex();
        if (nullIndex != null && !nullIndex.isEmpty()) {
            traversal = traversal.toBitMap().getAndNot(nullIndex);
        }
        return traversal;
    }

    @Override
    public T createAggregatorValue(AggregatorValue<?> value) {
        return (T) new DoubleAmountAggregatorValue(value.calculate());
    }
}
