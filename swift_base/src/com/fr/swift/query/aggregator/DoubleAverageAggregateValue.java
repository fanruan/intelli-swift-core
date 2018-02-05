package com.fr.swift.query.aggregator;

/**
 * @author Xiaolei.liu
 */

public class DoubleAverageAggregateValue implements AggregatorValue<Double> {
    private int rowCount;
    private double value;

    public void setRowCount(int rowCount) {
        this.rowCount = rowCount;
    }

    public void setValue(double value) {
        this.value = value;
    }

    public int getRowCount() {
        return rowCount;
    }

    public double getValue() {
        return value;
    }

    @Override
    public double calculate() {
        return value / rowCount;
    }
}
