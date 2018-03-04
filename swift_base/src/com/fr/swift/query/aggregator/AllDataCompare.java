package com.fr.swift.query.aggregator;


import com.fr.swift.bitmap.traversal.CalculatorTraversalAction;
import com.fr.swift.segment.column.Column;
import com.fr.swift.segment.column.DetailColumn;
import com.fr.swift.segment.column.impl.base.DoubleDetailColumn;
import com.fr.swift.segment.column.impl.base.LongDetailColumn;
import com.fr.swift.structure.iterator.RowTraversal;

import static com.fr.swift.cube.io.IOConstant.NULL_DOUBLE;
import static com.fr.swift.cube.io.IOConstant.NULL_INT;
import static com.fr.swift.cube.io.IOConstant.NULL_LONG;


/**
 * @author Xiaolei.liu
 */
public abstract class AllDataCompare implements Aggregator<DoubleAmountAggregateValue> {

    @Override
    public DoubleAmountAggregateValue aggregate(RowTraversal traversal, Column column) {

        final DoubleAmountAggregateValue minOrMaxValue = new DoubleAmountAggregateValue();
        final DetailColumn getter = column.getDetailColumn();
        CalculatorTraversalAction ss;
        if (getter instanceof LongDetailColumn) {
            return aggregateLongSum(traversal, getter);
        } else if (getter instanceof DoubleDetailColumn) {
            return aggregateDoubleSum(traversal, getter);
        } else {
            ss = new CalculatorTraversalAction() {

                int sum = NULL_INT;


                @Override
                public double getCalculatorValue() {

                    if (sum == NULL_INT) {
                        return NULL_DOUBLE;
                    }
                    return sum;
                }

                @Override
                public void actionPerformed(int row) {

                    int v = getter.getInt(row);
                    sum = compare(sum, v);
                }
            };
        }
        traversal.traversal(ss);
        minOrMaxValue.setValue(ss.getCalculatorValue());
        return minOrMaxValue;
    }

    private DoubleAmountAggregateValue aggregateLongSum(RowTraversal traversal, final DetailColumn getter) {

        final DoubleAmountAggregateValue minOrMaxValue = new DoubleAmountAggregateValue();
        final LongDetailColumn g = (LongDetailColumn) getter;
        CalculatorTraversalAction ss;
        //bitMap.getAndNot()
        ss = new CalculatorTraversalAction() {

            long sum = NULL_LONG;

            @Override
            public double getCalculatorValue() {

                if (sum == NULL_LONG) {
                    return NULL_DOUBLE;
                }
                return sum;
            }


            @Override
            public void actionPerformed(int row) {

                long value = g.getLong(row);
                sum = compare(value, sum);
            }
        };
        traversal.traversal(ss);
        minOrMaxValue.setValue(ss.getCalculatorValue());
        return minOrMaxValue;
    }

    private DoubleAmountAggregateValue aggregateDoubleSum(RowTraversal bitMap, final DetailColumn getter) {

        final DoubleAmountAggregateValue minOrMaxValue = new DoubleAmountAggregateValue();
        final DoubleDetailColumn g = (DoubleDetailColumn) getter;
        CalculatorTraversalAction ss;
        ss = new CalculatorTraversalAction() {

            double sum = NULL_DOUBLE;

            @Override
            public double getCalculatorValue() {

                return sum;
            }

            @Override
            public void actionPerformed(int row) {

                double value = g.getDouble(row);
                sum = compare(value, sum);
            }
        };
        bitMap.traversal(ss);
        minOrMaxValue.setValue(ss.getCalculatorValue());
        return minOrMaxValue;
    }


    protected abstract double compare(double sum, double rowValue);

    protected abstract long compare(long sum, long rowValue);

    protected abstract int compare(int sum, int rowValue);
}

