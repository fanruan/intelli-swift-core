package com.fr.swift.cloud.query.aggregator;

import com.fr.swift.cloud.bitmap.traversal.TraversalAction;
import com.fr.swift.cloud.segment.column.Column;
import com.fr.swift.cloud.segment.column.DetailColumn;
import com.fr.swift.cloud.structure.iterator.RowTraversal;

import java.io.Serializable;

/**
 * @author pony
 * @date 2018/3/27
 */
public class DateMinAggregate extends AbstractAggregator<DateAmountAggregateValue> implements Serializable {
    protected static final Aggregator INSTANCE = new DateMinAggregate();
    private static final long serialVersionUID = 385852056373051802L;

    @Override
    public DateAmountAggregateValue aggregate(RowTraversal traversal, Column<?> column) {
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