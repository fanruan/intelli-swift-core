package com.fr.swift.query.aggregator;


import com.fr.swift.bitmap.traversal.CalculatorTraversalAction;
import com.fr.swift.segment.column.Column;
import com.fr.swift.segment.column.DetailColumn;
import com.fr.swift.structure.iterator.RowTraversal;

import static com.fr.swift.cube.io.IOConstant.NULL_DOUBLE;
import static com.fr.swift.cube.io.IOConstant.NULL_INT;
import static com.fr.swift.cube.io.IOConstant.NULL_LONG;


/**
 * @author Xiaolei.liu
 */
public abstract class AllDataCompare extends AbstractAggregator<DoubleAmountAggregatorValue> {

    private static final long serialVersionUID = -8853595719196731039L;

    @Override
    public DoubleAmountAggregatorValue aggregate(RowTraversal traversal, Column column) {

        final DoubleAmountAggregatorValue minOrMaxValue = new DoubleAmountAggregatorValue();
        final DetailColumn getter = column.getDetailColumn();
        RowTraversal notNullTraversal = getNotNullTraversal(traversal, column);
        if (notNullTraversal.isEmpty()) {
            minOrMaxValue.setValue(NULL_DOUBLE);
            return minOrMaxValue;
        }
        switch (column.getDictionaryEncodedColumn().getType()) {
            case LONG:
                return aggregateLongSum(notNullTraversal, getter);
            case DOUBLE:
                return aggregateDoubleSum(notNullTraversal, getter);
            default:
                return aggregateIntSum(notNullTraversal, getter);
        }
    }

    private DoubleAmountAggregatorValue aggregateIntSum(RowTraversal traversal, final DetailColumn getter) {
        final DoubleAmountAggregatorValue minOrMaxValue = new DoubleAmountAggregatorValue();
        CalculatorTraversalAction ss = new CalculatorTraversalAction() {

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
        traversal.traversal(ss);
        minOrMaxValue.setValue(ss.getCalculatorValue());
        return minOrMaxValue;
    }

    private DoubleAmountAggregatorValue aggregateLongSum(RowTraversal traversal, final DetailColumn getter) {

        final DoubleAmountAggregatorValue minOrMaxValue = new DoubleAmountAggregatorValue();
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

                long value = getter.getLong(row);
                sum = compare(value, sum);
            }
        };

        traversal.traversal(ss);
        minOrMaxValue.setValue(ss.getCalculatorValue());
        return minOrMaxValue;
    }

    private DoubleAmountAggregatorValue aggregateDoubleSum(RowTraversal bitMap, final DetailColumn getter) {

        final DoubleAmountAggregatorValue minOrMaxValue = new DoubleAmountAggregatorValue();
        CalculatorTraversalAction ss;
        ss = new CalculatorTraversalAction() {

            double sum = NULL_DOUBLE;

            @Override
            public double getCalculatorValue() {

                return sum;
            }

            @Override
            public void actionPerformed(int row) {

                double value = getter.getDouble(row);
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

