package com.fr.swift.query.aggregator;

import com.fr.swift.bitmap.traversal.TraversalAction;
import com.fr.swift.segment.column.Column;
import com.fr.swift.segment.column.DetailColumn;
import com.fr.swift.structure.iterator.RowTraversal;

/**
 * Created by pony on 2018/3/27.
 */
public class DateMinAggregate extends AbstractAggregator<LongAmountAggregateValue>{
    protected static final Aggregator INSTANCE = new DateMinAggregate();
    @Override
    public LongAmountAggregateValue aggregate(RowTraversal traversal, Column column) {
        final LongAmountAggregateValue value = new LongAmountAggregateValue();
        final DetailColumn detailColumn = column.getDetailColumn();
        value.setValue(Long.MAX_VALUE);
        getNotNullTraversal(traversal, column).traversal(new TraversalAction() {
            @Override
            public void actionPerformed(int row) {
                value.setValue(Math.min(value.getValue(), detailColumn.getLong(row)));
            }
        });
        return value;
    }

    @Override
    public void combine(LongAmountAggregateValue value, LongAmountAggregateValue other) {
        value.setValue(Math.min(value.getValue(), other.getValue()));
    }
}