package com.fr.swift.query.aggregator;

/**
 * Created by pony on 2018/3/26.
 */
public class StringAggregateValue implements AggregatorValue<String> {
    private String value;

    public void setValue(String value) {
        this.value = value;
    }

    public String  getValue() {
        return value;
    }

    @Override
    public double calculate() {
        return 0;
    }

    @Override
    public String calculateValue() {
        return value;
    }
}
