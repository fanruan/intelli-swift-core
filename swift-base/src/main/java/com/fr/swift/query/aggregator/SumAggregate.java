package com.fr.swift.query.aggregator;


import com.fr.swift.bitmap.traversal.CalculatorTraversalAction;
import com.fr.swift.bitmap.traversal.TraversalAction;
import com.fr.swift.segment.column.Column;
import com.fr.swift.segment.column.DetailColumn;
import com.fr.swift.segment.column.impl.base.DoubleDetailColumn;
import com.fr.swift.segment.column.impl.base.FormulaDetailColumn;
import com.fr.swift.segment.column.impl.base.LongDetailColumn;
import com.fr.swift.structure.iterator.RowTraversal;

import static com.fr.swift.cube.io.IOConstant.NULL_DOUBLE;

/**
 * @author Xiaolei.liu
 */

public class SumAggregate extends AbstractAggregator<DoubleAmountAggregatorValue> {

    protected static final Aggregator INSTANCE = new SumAggregate();


    @Override
    public DoubleAmountAggregatorValue aggregate(RowTraversal traversal, Column column) {
        if (traversal.isEmpty()) {
            return null;
        }
        final DoubleAmountAggregatorValue valueAmount = new DoubleAmountAggregatorValue();
        final DetailColumn detailColumn = column.getDetailColumn();
        RowTraversal notNullTraversal = getNotNullTraversal(traversal, column);
        if (notNullTraversal.isEmpty()){
            valueAmount.setValue(NULL_DOUBLE);
            return valueAmount;
        }
        TraversalAction ss;
        if (detailColumn instanceof LongDetailColumn) {
            return aggregateLong(notNullTraversal, detailColumn);
        } else if (detailColumn instanceof DoubleDetailColumn || detailColumn instanceof FormulaDetailColumn) {
            return aggregateDouble(notNullTraversal, detailColumn);
        } else {
            final DetailColumn idc = detailColumn;
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
                    result += idc.getInt(row);
                }
            };
        }
        notNullTraversal.traversal(ss);
        double value = ((CalculatorTraversalAction) ss).getCalculatorValue();
        valueAmount.setValue(value);
        return valueAmount;
    }

    private DoubleAmountAggregatorValue aggregateLong(RowTraversal traversal, final DetailColumn detailColumn) {
        final DoubleAmountAggregatorValue valueAmount = new DoubleAmountAggregatorValue();
        final LongDetailColumn ldc = (LongDetailColumn) detailColumn;
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
                result += ldc.getLong(row);
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
    public void combine(DoubleAmountAggregatorValue value, DoubleAmountAggregatorValue other) {
        if (!Double.isNaN(other.getValue())){
            if (!Double.isNaN(value.getValue())) {
                value.setValue(other.getValue() + value.getValue());
            } else {
                value.setValue(other.getValue());
            }
        }
    }

}