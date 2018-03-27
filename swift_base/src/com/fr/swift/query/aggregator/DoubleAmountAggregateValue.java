package com.fr.swift.query.aggregator;

/**
 * @author Xiaolei.liu
 */

public class DoubleAmountAggregateValue implements AggregatorValue<Double> {

    private double value;

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
}
