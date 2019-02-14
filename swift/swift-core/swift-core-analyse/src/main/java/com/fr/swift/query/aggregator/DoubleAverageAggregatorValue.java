package com.fr.swift.query.aggregator;


/**
 * @author Xiaolei.liu
 */

public class DoubleAverageAggregatorValue implements AggregatorValue<Double> {

    private static final long serialVersionUID = -1192534798672195823L;
    private int rowCount;
    private double value;

    public int getRowCount() {
        return rowCount;
    }

    public void setRowCount(int rowCount) {
        this.rowCount = rowCount;
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }

    @Override
    public double calculate() {
        return rowCount == 0 ? null : value / rowCount;
    }

    @Override
    public Double calculateValue() {
        return rowCount == 0 ? null : value / rowCount;
    }

    @Override
    public Object clone() {
        DoubleAverageAggregatorValue value = new DoubleAverageAggregatorValue();
        value.rowCount = this.rowCount;
        value.value = this.value;
        return value;
    }

}
