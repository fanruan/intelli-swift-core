package com.fr.swift.query.aggregator;

import java.util.Map;

/**
 * @author Xiaolei.liu
 */

public class MedianAggregatorValue implements AggregatorValue<Number> {
    private double median;
    private int count;
    //取值和值的个数作为map保存下来，数据量大且重复值少时会有些问题
    private Map<Double, Integer> values;

    public int getCount() {
        return count;
    }

    public Map<Double, Integer> getValues() {
        return values;
    }

    public double getMedian() {
        return median;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public void setMedian(double median) {
        this.median = median;
    }

    public void setValues(Map<Double, Integer> values) {
        this.values = values;
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
