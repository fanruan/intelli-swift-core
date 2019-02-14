package com.fr.swift.query.aggregator;


import com.fr.swift.segment.column.Column;
import com.fr.swift.structure.iterator.RowTraversal;

/**
 * @author Xiaolei.liu
 */
public class AverageAggregate extends AbstractAggregator<DoubleAverageAggregatorValue> {

    protected static final Aggregator INSTANCE = new AverageAggregate();
    private static final long serialVersionUID = 8294759910803617178L;

    @Override
    public DoubleAverageAggregatorValue aggregate(RowTraversal traversal, Column column) {

        DoubleAverageAggregatorValue averageValue = new DoubleAverageAggregatorValue();
        RowTraversal notNullTraversal = getNotNullTraversal(traversal, column);
        averageValue.setValue((getSumValue(traversal, column)).getValue());
        averageValue.setRowCount(notNullTraversal.getCardinality());
        return averageValue;
    }

    @Override
    public DoubleAverageAggregatorValue createAggregatorValue(AggregatorValue value) {

        DoubleAverageAggregatorValue averageAggregatorValue = new DoubleAverageAggregatorValue();
        if (value.calculateValue() == null) {
            averageAggregatorValue.setRowCount(0);
            averageAggregatorValue.setValue(0);
            return averageAggregatorValue;
        }
        averageAggregatorValue.setRowCount(1);
        averageAggregatorValue.setValue(value.calculate());
        return averageAggregatorValue;
    }

    @Override
    public AggregatorType getAggregatorType() {
        return AggregatorType.AVERAGE;
    }

    @Override
    public void combine(DoubleAverageAggregatorValue value, DoubleAverageAggregatorValue other) {
        double dValue = Double.isNaN(value.getValue()) ? 0 : value.getValue();
        double dOther = Double.isNaN(other.getValue()) ? 0 : other.getValue();
        value.setRowCount(value.getRowCount() + other.getRowCount());
        value.setValue(dValue + dOther);
    }


    protected DoubleAmountAggregatorValue getSumValue(RowTraversal traversal, Column column) {
        return ((DoubleAmountAggregatorValue) SumAggregate.INSTANCE.aggregate(traversal, column));
    }


}