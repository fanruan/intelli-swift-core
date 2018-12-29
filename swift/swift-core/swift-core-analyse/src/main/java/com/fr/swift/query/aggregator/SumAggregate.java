package com.fr.swift.query.aggregator;


import com.fr.swift.bitmap.traversal.CalculatorTraversalAction;
import com.fr.swift.segment.column.Column;
import com.fr.swift.segment.column.DetailColumn;
import com.fr.swift.structure.iterator.RowTraversal;

import static com.fr.swift.cube.io.IOConstant.NULL_DOUBLE;

/**
 * @author Xiaolei.liu
 */

public class SumAggregate extends AbstractAggregator<DoubleAmountAggregatorValue> {

    protected static final Aggregator INSTANCE = new SumAggregate();
    private static final long serialVersionUID = -6996921485360000948L;


    @Override
    public DoubleAmountAggregatorValue aggregate(RowTraversal traversal, Column column) {
        if (traversal.isEmpty()) {
            return new DoubleAmountAggregatorValue(NULL_DOUBLE);
        }
        final DoubleAmountAggregatorValue valueAmount = new DoubleAmountAggregatorValue();
        final DetailColumn detailColumn = column.getDetailColumn();
        RowTraversal notNullTraversal = getNotNullTraversal(traversal, column);
        if (notNullTraversal.isEmpty()) {
            valueAmount.setValue(NULL_DOUBLE);
            return valueAmount;
        }
        switch (column.getDictionaryEncodedColumn().getType()) {
            case DOUBLE:
                return aggregateDouble(notNullTraversal, detailColumn);
            case LONG:
                return aggregateLong(notNullTraversal, detailColumn);
            default:
                return aggregateInt(notNullTraversal, detailColumn);
        }
    }

    private DoubleAmountAggregatorValue aggregateInt(RowTraversal traversal, final DetailColumn detailColumn) {
        final DoubleAmountAggregatorValue valueAmount = new DoubleAmountAggregatorValue();
        if (traversal.isEmpty()) {
            valueAmount.setValue(NULL_DOUBLE);
            return valueAmount;
        }
        CalculatorTraversalAction ss = new CalculatorTraversalAction() {
            @Override
            public double getCalculatorValue() {
                return result;
            }

            @Override
            public void actionPerformed(int row) {
                result += detailColumn.getInt(row);
            }
        };
        traversal.traversal(ss);
        valueAmount.setValue(ss.getCalculatorValue());
        return valueAmount;
    }

    private DoubleAmountAggregatorValue aggregateLong(RowTraversal traversal, final DetailColumn detailColumn) {
        final DoubleAmountAggregatorValue valueAmount = new DoubleAmountAggregatorValue();
        CalculatorTraversalAction ss;
        if (traversal.isEmpty()) {
            valueAmount.setValue(NULL_DOUBLE);
            return valueAmount;
        }
        ss = new CalculatorTraversalAction() {
            @Override
            public double getCalculatorValue() {
                return result;
            }

            @Override
            public void actionPerformed(int row) {
                result += detailColumn.getLong(row);
            }
        };
        traversal.traversal(ss);
        valueAmount.setValue(ss.getCalculatorValue());
        return valueAmount;
    }

    private DoubleAmountAggregatorValue aggregateDouble(RowTraversal traversal, final DetailColumn detailColumn) {
        final DoubleAmountAggregatorValue valueAmount = new DoubleAmountAggregatorValue();
        CalculatorTraversalAction ss;
        if (traversal.isEmpty()) {
            valueAmount.setValue(NULL_DOUBLE);
            return valueAmount;
        }
        ss = new CalculatorTraversalAction() {
            @Override
            public double getCalculatorValue() {
                return result;
            }

            @Override
            public void actionPerformed(int row) {
                result += detailColumn.getDouble(row);
            }
        };
        traversal.traversal(ss);
        valueAmount.setValue(ss.getCalculatorValue());
        return valueAmount;
    }

    @Override
    public DoubleAmountAggregatorValue createAggregatorValue(AggregatorValue value) {
        DoubleAmountAggregatorValue valueAmount = new DoubleAmountAggregatorValue();
        if (value.calculateValue() == null) {
            valueAmount.setValue(0);
            return valueAmount;
        }
        return new DoubleAmountAggregatorValue(value.calculate());
    }

    @Override
    public AggregatorType getAggregatorType() {
        return AggregatorType.SUM;
    }

    @Override
    public void combine(DoubleAmountAggregatorValue value, DoubleAmountAggregatorValue other) {
        if (!Double.isNaN(other.getValue())) {
            if (!Double.isNaN(value.getValue())) {
                value.setValue(other.getValue() + value.getValue());
            } else {
                value.setValue(other.getValue());
            }
        }
    }

}