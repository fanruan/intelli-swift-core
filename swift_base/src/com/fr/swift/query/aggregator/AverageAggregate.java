package com.fr.swift.query.aggregator;


import com.fr.swift.segment.column.Column;
import com.fr.swift.structure.iterator.RowTraversal;

/**
 * @author Xiaolei.liu
 */
public class AverageAggregate implements Aggregator <DoubleAverageAggregateValue>{

    public static AverageAggregate INSTANCE = new AverageAggregate();

    @Override
    public DoubleAverageAggregateValue aggregate(RowTraversal traversal, Column column) {

        DoubleAverageAggregateValue averageValue = new DoubleAverageAggregateValue();
        averageValue.setValue(((DoubleAmountAggregateValue)getSumValue(traversal, column)).getValue());
        averageValue.setRowCount(traversal.getCardinality());
        return averageValue;
    }

    @Override
    public void combine(DoubleAverageAggregateValue value, DoubleAverageAggregateValue other) {
        value.setRowCount(value.getRowCount() + other.getRowCount());
        value.setValue(value.getValue() + other.getValue());
    }



    protected AggregatorValue getSumValue(RowTraversal traversal, Column column) {
        return SumAggregate.INSTANCE.aggregate(traversal, column);
    }


}