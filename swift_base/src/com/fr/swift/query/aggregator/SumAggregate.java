package com.fr.swift.query.aggregator;


import com.fr.swift.bitmap.traversal.CalculatorTraversalAction;
import com.fr.swift.bitmap.traversal.TraversalAction;
import com.fr.swift.segment.column.Column;
import com.fr.swift.segment.column.DetailColumn;
import com.fr.swift.segment.column.impl.base.DoubleDetailColumn;
import com.fr.swift.segment.column.impl.base.IntDetailColumn;
import com.fr.swift.segment.column.impl.base.LongDetailColumn;
import com.fr.swift.structure.iterator.RowTraversal;

import static com.fr.swift.cube.io.IOConstant.NULL_DOUBLE;

/**
 * @author Xiaolei.liu
 */

public class SumAggregate implements Aggregator<DoubleAmountAggregateValue> {

    public static SumAggregate INSTANCE = new SumAggregate();


    @Override
    public DoubleAmountAggregateValue aggregate(RowTraversal traversal, Column column) {
        final DoubleAmountAggregateValue valueAmount = new DoubleAmountAggregateValue();
        final DetailColumn detailColumn = column.getDetailColumn();
        TraversalAction ss;
        if (detailColumn instanceof LongDetailColumn) {
            return aggregateLong(traversal, detailColumn);
        } else if (detailColumn instanceof DoubleDetailColumn) {
            return aggregateDouble(traversal, detailColumn);
        } else {
            final IntDetailColumn idc = (IntDetailColumn) detailColumn;
            if (traversal.isEmpty()) {
                valueAmount.setValue(NULL_DOUBLE);
                return valueAmount;
            }
            ss = new CalculatorTraversalAction() {
                @Override
                public double getCalculatorValue() {
                    return sum;
                }

                @Override
                public void actionPerformed(int row) {
                    sum += idc.getInt(row);
                }
            };
        }
        traversal.traversal(ss);
        double value = ((CalculatorTraversalAction) ss).getCalculatorValue();
        valueAmount.setValue(value);
        return valueAmount;
    }

    private DoubleAmountAggregateValue aggregateLong(RowTraversal traversal, final DetailColumn detailColumn) {
        final DoubleAmountAggregateValue valueAmount = new DoubleAmountAggregateValue();
        final LongDetailColumn ldc = (LongDetailColumn) detailColumn;
        CalculatorTraversalAction ss;
        if (traversal.isEmpty()) {
            valueAmount.setValue(NULL_DOUBLE);
            return valueAmount;
        }
        ss = new CalculatorTraversalAction() {
            @Override
            public double getCalculatorValue() {
                return sum;
            }

            @Override
            public void actionPerformed(int row) {
                sum += ldc.getLong(row);
            }
        };
        traversal.traversal(ss);
        valueAmount.setValue(ss.getCalculatorValue());
        return valueAmount;
    }

    private DoubleAmountAggregateValue aggregateDouble(RowTraversal traversal, DetailColumn detailColumn) {
        final DoubleAmountAggregateValue valueAmount = new DoubleAmountAggregateValue();
        final DoubleDetailColumn ldc = (DoubleDetailColumn) detailColumn;
        CalculatorTraversalAction ss;
        if (traversal.isEmpty()) {
            valueAmount.setValue(NULL_DOUBLE);
            return valueAmount;
        }
        ss = new CalculatorTraversalAction() {
            @Override
            public double getCalculatorValue() {
                return sum;
            }

            @Override
            public void actionPerformed(int row) {
                sum += ldc.getDouble(row);
            }
        };
        traversal.traversal(ss);
        valueAmount.setValue(ss.getCalculatorValue());
        return valueAmount;
    }

    @Override
    public void combine(DoubleAmountAggregateValue value, DoubleAmountAggregateValue other) {
        value.setValue(value.getValue() + other.getValue());
    }
}