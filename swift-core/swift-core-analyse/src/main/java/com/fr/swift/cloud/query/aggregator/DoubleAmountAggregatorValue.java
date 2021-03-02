package com.fr.swift.cloud.query.aggregator;

import java.io.Serializable;

/**
 * @author Xiaolei.liu
 */

public class DoubleAmountAggregatorValue implements AggregatorValue<Double>, Serializable {
    private static final long serialVersionUID = -2269511519928083111L;
    private double value;

    public DoubleAmountAggregatorValue(double value) {
        this.value = value;
    }

    public DoubleAmountAggregatorValue() {
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }

    @Override
    public double calculate() {
        return value;
    }

    @Override
    public Double calculateValue() {
        return Double.isNaN(value) ? null : value;
    }

    @Override
    public Object clone() {
        DoubleAmountAggregatorValue value = new DoubleAmountAggregatorValue();
        value.value = this.value;
        return value;
    }
}
