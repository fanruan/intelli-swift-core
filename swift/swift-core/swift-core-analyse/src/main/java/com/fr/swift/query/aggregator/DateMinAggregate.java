package com.fr.swift.query.aggregator;

import com.fr.swift.bitmap.traversal.TraversalAction;
import com.fr.swift.segment.column.Column;
import com.fr.swift.segment.column.DetailColumn;
import com.fr.swift.structure.iterator.RowTraversal;

/**
 * Created by pony on 2018/3/27.
 */
public class DateMinAggregate extends AbstractAggregator<DateAmountAggregateValue> {
    protected static final Aggregator INSTANCE = new DateMinAggregate();
    private static final long serialVersionUID = 385852056373051802L;

    @Override
    public DateAmountAggregateValue aggregate(RowTraversal traversal, Column column) {
        final DateAmountAggregateValue value = new DateAmountAggregateValue();
        final DetailColumn detailColumn = column.getDetailColumn();
        RowTraversal notNullTraversal = getNotNullTraversal(traversal, column);
        if (notNullTraversal.isEmpty()) {
            return new DateAmountAggregateValue();
        }
        value.setValue(Long.MAX_VALUE);
        notNullTraversal.traversal(new TraversalAction() {
            @Override
            public void actionPerformed(int row) {
                value.setValue(Math.min(value.getValue(), detailColumn.getLong(row)));
            }
        });
        return value;
    }

    @Override
    public AggregatorType getAggregatorType() {
        return AggregatorType.DATE_MIN;
    }

    @Override
    public void combine(DateAmountAggregateValue value, DateAmountAggregateValue other) {
        value.setValue(Math.min(value.getValue(), other.getValue()));
    }
}