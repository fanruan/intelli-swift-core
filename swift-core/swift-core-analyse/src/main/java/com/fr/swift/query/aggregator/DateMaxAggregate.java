package com.fr.swift.query.aggregator;

import com.fr.swift.bitmap.traversal.TraversalAction;
import com.fr.swift.segment.column.Column;
import com.fr.swift.segment.column.DetailColumn;
import com.fr.swift.structure.iterator.RowTraversal;

/**
 * Created by pony on 2018/3/27.
 */
public class DateMaxAggregate extends AbstractAggregator<DateAmountAggregateValue> {
    protected static final Aggregator INSTANCE = new DateMaxAggregate();
    private static final long serialVersionUID = 1119589524768495049L;

    @Override
    public DateAmountAggregateValue aggregate(RowTraversal traversal, Column column) {
        final DateAmountAggregateValue value = new DateAmountAggregateValue();
        final DetailColumn detailColumn = column.getDetailColumn();
        RowTraversal notNullTraversal = getNotNullTraversal(traversal, column);
        if (notNullTraversal.isEmpty()) {
            return new DateAmountAggregateValue();
        }
        notNullTraversal.traversal(new TraversalAction() {
            @Override
            public void actionPerformed(int row) {
                value.setValue(Math.max(value.getValue(), detailColumn.getLong(row)));
            }
        });
        return value;
    }

    @Override
    public AggregatorType getAggregatorType() {
        return AggregatorType.DATE_MAX;
    }

    @Override
    public void combine(DateAmountAggregateValue value, DateAmountAggregateValue other) {
        value.setValue(Math.max(value.getValue(), other.getValue()));
    }
}
