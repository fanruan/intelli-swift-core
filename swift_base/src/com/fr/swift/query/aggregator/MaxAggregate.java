package com.fr.swift.query.aggregator;

import static com.fr.swift.cube.io.IOConstant.NULL_INT;
import static com.fr.swift.cube.io.IOConstant.NULL_LONG;

/**
 * @author Xiaolei.liu
 */
public class MaxAggregate extends AllDataCompare {

    protected static final MaxAggregate INSTANCE = new MaxAggregate();


    @Override
    protected double compare(double sum, double rowValue) {
        if (Double.isNaN(sum)) {
            return rowValue;
        } else if (Double.isNaN(rowValue)) {
            return sum;
        }
        return Math.max(sum, rowValue);
    }

    @Override
    protected long compare(long sum, long rowValue) {
        if (sum == NULL_LONG) {
            return rowValue;
        } else if (rowValue == NULL_LONG) {
            return sum;
        }
        return Math.max(sum, rowValue);
    }

    @Override
    protected int compare(int sum, int rowValue) {
        if (sum == NULL_INT) {
            return rowValue;
        } else if (rowValue == NULL_INT) {
            return sum;
        }
        return Math.max(sum, rowValue);
    }

    @Override
    public void combine(DoubleAmountAggregateValue value, DoubleAmountAggregateValue other) {
        value.setValue(Math.max(value.getValue(), other.getValue()));
    }
}
