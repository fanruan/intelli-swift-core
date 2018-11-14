package com.fr.swift.query.aggregator;


/**
 * @author Xiaolei.liu
 */


public class VarianceAggregatorValue implements AggregatorValue<Number> {

    private static final long serialVersionUID = -1088961855163086997L;
    private double sum;
    private double squareSum;
    private int count;
    //variance实际是方差*个数
    private double variance;

    //取标准差
//    public double getStandarDeviation() {
//        return Math.sqrt(variance);
//    }

    public double getSum() {
        return sum;
    }

    public void setSum(double sum) {
        this.sum = sum;
    }

    public double getSquareSum() {
        return squareSum;
    }

    public void setSquareSum(double squareSum) {
        this.squareSum = squareSum;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public double getVariance() {
        return variance;
    }

    public void setVariance(double variance) {
        this.variance = variance;
    }

    @Override
    public double calculate() {
        return (count == 0 ? null : variance / count);
    }

    @Override
    public Number calculateValue() {
        return (count == 0 ? null : variance / count);
    }

    @Override
    public Object clone() {
        VarianceAggregatorValue value = new VarianceAggregatorValue();
        value.count = this.count;
        value.squareSum = this.squareSum;
        value.sum = this.sum;
        value.variance = this.variance;
        return value;
    }

}
