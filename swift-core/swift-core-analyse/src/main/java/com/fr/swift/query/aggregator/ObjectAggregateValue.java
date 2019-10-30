package com.fr.swift.query.aggregator;

import java.io.Serializable;

/**
 * @author pony
 * @date 2018/3/26
 */
public class ObjectAggregateValue implements AggregatorValue<Object>, Serializable {
    private static final long serialVersionUID = 7506086100310009309L;
    private Object value;

    public Object getValue() {
        return value;
    }

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
        ObjectAggregateValue value = new ObjectAggregateValue();
        value.value = this.value;
        return value;
    }
}
