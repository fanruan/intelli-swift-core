package com.fr.swift.query.aggregator;

/**
 * @author Xiaolei.liu
 */

public class DoubleAmountAggregatorValue implements AggregatorValue<Double> {

    private double value;

    public DoubleAmountAggregatorValue(double value) {
        this.value = value;
    }

    public DoubleAmountAggregatorValue() {}

    public void setValue(double value) {
        this.value = value;
    }

    public double getValue() {
        return value;
    }

    @Override
    public double calculate() {
        return value;
    }

    @Override
    public Double calculateValue() {
        return value;
    }

    @Override
    public Object clone() {
        DoubleAmountAggregatorValue value = new DoubleAmountAggregatorValue();
        value.value = this.value;
        return value;
    }
}
