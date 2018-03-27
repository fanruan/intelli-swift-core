package com.fr.swift.query.aggregator;

/**
 * Created by pony on 2018/3/27.
 */
public class LongAmountAggregateValue implements AggregatorValue<Long> {

    private long value;

    public void setValue(long value) {
        this.value = value;
    }

    public long getValue() {
        return value;
    }

    @Override
    public double calculate() {
        return value;
    }

    @Override
    public Long calculateValue() {
        return value;
    }
}
