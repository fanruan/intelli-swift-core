package com.fr.swift.query.aggregator;

/**
 * @author Xiaolei.liu
 */

public class MedianAggregatorValue implements AggregatorValue<Number> {
    private double median;

    public double getMedian() {
        return median;
    }

    public void setMedian(double median) {
        this.median = median;
    }

    @Override
    public double calculate() {
        return median;
    }

    @Override
    public Double calculateValue() {
        return median;
    }
}
