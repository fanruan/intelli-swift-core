package com.fr.swift.query.aggregator;


import com.fr.swift.segment.column.Column;
import com.fr.swift.structure.iterator.RowTraversal;

/**
 * @author Xiaolei.liu
 */
public class AverageAggregate implements Aggregator <DoubleAverageAggregatorValue>{

    protected static final Aggregator INSTANCE = new AverageAggregate();

    @Override
    public DoubleAverageAggregatorValue aggregate(RowTraversal traversal, Column column) {

        DoubleAverageAggregatorValue averageValue = new DoubleAverageAggregatorValue();
        averageValue.setValue((getSumValue(traversal, column)).getValue());
        averageValue.setRowCount(traversal.getCardinality());
        return averageValue;
    }

    @Override
    public DoubleAverageAggregatorValue createAggregatorValue(AggregatorValue value) {
        DoubleAverageAggregatorValue averageAggregatorValue = new DoubleAverageAggregatorValue();
        averageAggregatorValue.setRowCount(1);
        averageAggregatorValue.setValue(value.calculate());
        return averageAggregatorValue;
    }

    @Override
    public void combine(DoubleAverageAggregatorValue value, DoubleAverageAggregatorValue other) {
        value.setRowCount(value.getRowCount() + other.getRowCount());
        value.setValue(value.getValue() + other.getValue());
    }



    protected DoubleAmountAggregatorValue getSumValue(RowTraversal traversal, Column column) {
        return ((DoubleAmountAggregatorValue)SumAggregate.INSTANCE.aggregate(traversal, column));
    }


}