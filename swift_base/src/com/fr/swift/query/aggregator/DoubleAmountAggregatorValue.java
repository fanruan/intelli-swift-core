package com.fr.swift.query.aggregator;

/**
 * @author Xiaolei.liu
 */

public class DoubleAmountAggregatorValue implements AggregatorValue<Double> {

    private Double value;

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
}
