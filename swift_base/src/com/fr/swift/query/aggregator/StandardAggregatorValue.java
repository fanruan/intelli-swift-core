package com.fr.swift.query.aggregator;

/**
 * @author Xiaolei.liu
 */


public class StandardAggregatorValue extends VarianceAggregatorValue {
    private VarianceAggregatorValue variance;

    public void setVariance(VarianceAggregatorValue variance) {
        this.variance = variance;
    }

    public VarianceAggregatorValue getCalVariance() {
        return variance;
    }

    @Override
    public double calculate() {
        return Math.sqrt(variance.calculate());
    }

    @Override
    public Number calculateValue() {
        return Math.sqrt((double)variance.calculateValue());
    }
}
