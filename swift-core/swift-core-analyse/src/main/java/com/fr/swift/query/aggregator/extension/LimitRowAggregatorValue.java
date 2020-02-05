package com.fr.swift.query.aggregator.extension;

import com.fr.swift.query.aggregator.AggregatorValue;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Moira
 * @date 2020/2/3
 * @description
 * @since swift 1.0
 */
public class LimitRowAggregatorValue implements AggregatorValue<Object>, Serializable {


    private static final long serialVersionUID = -6243512007008948667L;
    private List<Object> values = new ArrayList<>();

    public void setValue(List<Object> value) {
        this.values = value;
    }

    @Override
    public double calculate() {
        return 0;
    }

    @Override
    public List<Object> calculateValue() {
        return values;
    }

    @Override
    public Object clone() {
        LimitRowAggregatorValue value = new LimitRowAggregatorValue();
        List<Object> copy = new ArrayList<>(values);
        value.setValue(copy);
        return value;
    }
}
