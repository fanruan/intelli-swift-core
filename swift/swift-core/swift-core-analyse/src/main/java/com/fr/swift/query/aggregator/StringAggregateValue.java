package com.fr.swift.query.aggregator;

/**
 * Created by pony on 2018/3/26.
 */
public class StringAggregateValue implements AggregatorValue<String> {
    private static final long serialVersionUID = 1724795657370108135L;
    private String value;

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public double calculate() {
        return 0;
    }

    @Override
    public String calculateValue() {
        return value;
    }

    @Override
    public Object clone() {
        StringAggregateValue value = new StringAggregateValue();
        value.value = this.value;
        return value;
    }
}
