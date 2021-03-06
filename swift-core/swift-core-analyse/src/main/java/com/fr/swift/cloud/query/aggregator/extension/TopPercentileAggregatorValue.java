package com.fr.swift.cloud.query.aggregator.extension;

import com.fr.swift.cloud.annotation.Negative;
import com.fr.swift.cloud.query.aggregator.AggregatorValue;
import com.fr.swift.cloud.query.aggregator.extension.histogram.Histogram;

import java.io.Serializable;

/**
 * @author lyon
 * @date 2019/1/23
 */
public class TopPercentileAggregatorValue implements AggregatorValue<Double>, Serializable {

    private static final long serialVersionUID = -5940270018952259741L;

    private double percentile;
    private int numberOfSignificantValueDigits;
    private Histogram histogram;

    public TopPercentileAggregatorValue(double percentile, int numberOfSignificantValueDigits) {
        this.percentile = percentile;
        this.numberOfSignificantValueDigits = numberOfSignificantValueDigits;
        this.histogram = new Histogram(numberOfSignificantValueDigits);
    }

    @Negative(until = "201907")
    void recordValue(long value) {
        if (value >= 0) {
            histogram.recordValue(value);
        }
    }

    void add(TopPercentileAggregatorValue value) {
        histogram.add(value.histogram);
    }

    @Override
    public double calculate() {
        return histogram.getValueAtPercentile(percentile);
    }

    @Override
    public Double calculateValue() {
        return (double) histogram.getValueAtPercentile(percentile);
    }

    @Override
    public Object clone() {
        TopPercentileAggregatorValue value = new TopPercentileAggregatorValue(percentile, numberOfSignificantValueDigits);
        value.histogram = histogram.copy();
        return value;
    }
}
