package com.fr.swift.query.aggregator;

/**
 * Created by pony on 2018/3/27.
 */
public class DateAmountAggregateValue implements AggregatorValue<Long> {

    private static final long serialVersionUID = -4924535512001583885L;
    //日期都是大于0的，用负无穷表示null没关系
    private long value = Long.MIN_VALUE;

    public long getValue() {
        return value;
    }

    public void setValue(long value) {
        this.value = value;
    }

    @Override
    public double calculate() {
        return value;
    }

    @Override
    public Long calculateValue() {
        return value == Long.MIN_VALUE ? null : value;
    }

    @Override
    public Object clone() {
        DateAmountAggregateValue value = new DateAmountAggregateValue();
        value.value = this.value;
        return value;
    }
}
