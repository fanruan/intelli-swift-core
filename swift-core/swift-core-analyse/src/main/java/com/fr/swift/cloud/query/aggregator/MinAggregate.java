package com.fr.swift.cloud.query.aggregator;

import static com.fr.swift.cloud.cube.io.IOConstant.NULL_DOUBLE;
import static com.fr.swift.cloud.cube.io.IOConstant.NULL_INT;
import static com.fr.swift.cloud.cube.io.IOConstant.NULL_LONG;

/**
 * @author Xiaolei.liu
 */
public class MinAggregate extends AllDataCompare {

    protected static final Aggregator INSTANCE = new MinAggregate();
    private static final long serialVersionUID = 7518903480444257620L;


    @Override
    protected double compare(double sum, double rowValue) {
        if (Double.compare(sum, NULL_DOUBLE) == 0) {
            return rowValue;
        } else if (Double.compare(rowValue, NULL_DOUBLE) == 0) {
            return sum;
        }
        return Math.min(sum, rowValue);
    }

    @Override
    protected long compare(long sum, long rowValue) {
        if (sum == NULL_LONG) {
            return rowValue;
        } else if (rowValue == NULL_LONG) {
            return sum;
        }
        return Math.min(sum, rowValue);
    }

    @Override
    protected int compare(int sum, int rowValue) {
        if (sum == NULL_INT) {
            return rowValue;
        } else if (rowValue == NULL_INT) {
            return sum;
        }
        return Math.min(sum, rowValue);
    }

    @Override
    public DoubleAmountAggregatorValue createAggregatorValue(AggregatorValue<?> value) {
        DoubleAmountAggregatorValue valueAmount = new DoubleAmountAggregatorValue();
        if (value.calculateValue() == null) {
            valueAmount.setValue(Double.MAX_VALUE);
            return valueAmount;
        }
        return new DoubleAmountAggregatorValue(value.calculate());
    }

    @Override
    public AggregatorType getAggregatorType() {
        return AggregatorType.MIN;
    }

    @Override
    public void combine(DoubleAmountAggregatorValue value, DoubleAmountAggregatorValue other) {
        double dValue = Double.isNaN(value.getValue()) ? Double.MAX_VALUE : value.getValue();
        double dOther = Double.isNaN(other.getValue()) ? Double.MAX_VALUE : other.getValue();
        value.setValue(Math.min(dValue, dOther));
    }
}
