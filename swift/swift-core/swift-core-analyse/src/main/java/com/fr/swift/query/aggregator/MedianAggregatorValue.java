package com.fr.swift.query.aggregator;


import java.util.TreeMap;

/**
 * @author Xiaolei.liu
 */

public class MedianAggregatorValue implements AggregatorValue<Number> {

    private static final long serialVersionUID = 2208307766852392287L;
    private double median;
    private int count = 0;
    //取值和值的个数作为map保存下来，数据量大且重复值少时会有些问题
    private TreeMap<Double, Integer> values = new TreeMap<Double, Integer>();

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public TreeMap<Double, Integer> getValues() {
        return values;
    }

    public void setValues(TreeMap<Double, Integer> values) {
        this.values = values;
    }

    public double getMedian() {
        return median;
    }

    public void setMedian(double median) {
        this.median = median;
    }

    @Override
    public double calculate() {
        return count == 0 ? null : median;
    }

    @Override
    public Double calculateValue() {
        return count == 0 ? null : median;
    }

    @Override
    public Object clone() {
        MedianAggregatorValue value = new MedianAggregatorValue();
        value.count = this.count;
        value.median = this.median;
        value.values = new TreeMap<Double, Integer>();
        value.values.putAll(this.values);
        return value;
    }
}
