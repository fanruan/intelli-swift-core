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
public class DateMaxAggregate extends AbstractAggregator<DateAmountAggregateValue> implements Serializable {
    protected static final Aggregator INSTANCE = new DateMaxAggregate();
    private static final long serialVersionUID = 1119589524768495049L;

    @Override
    public DateAmountAggregateValue aggregate(RowTraversal traversal, Column<?> column) {
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
