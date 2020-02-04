package com.fr.swift.query.aggregator.extension;

import com.fr.swift.query.aggregator.AggregatorValue;

import java.io.Serializable;

/**
 * @author Moira
 * @date 2020/2/3
 * @description
 * @since swift 1.0
 */
public class FirstRowAggregatorValue implements AggregatorValue<Object>, Serializable {


    private static final long serialVersionUID = -6243512007008948667L;
    private Object value;

    public void setValue(Object value) {
        this.value = value;
    }

    @Override
    public double calculate() {
        return 0;
    }

    @Override
    public Object calculateValue() {
        return value;
    }

    @Override
    public Object clone() {
        FirstRowAggregatorValue value = new FirstRowAggregatorValue();
        value.value = this.value;
        return value;
    }
}
