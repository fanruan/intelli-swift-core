package com.fr.swift.query.aggregator;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Xiaolei.liu
 */

public class DistinctCountAggregatorValue implements AggregatorValue<Double>, Serializable {
    private static final long serialVersionUID = -688533902689550756L;

    private Set<Object> values = new HashSet<Object>();

    public Set<Object> getValues() {
        return values;
    }

    public void setValues(Set<Object> set) {
        this.values = set;
    }

    @Override
    public double calculate() {
        return values.size();
    }

    @Override
    public Double calculateValue() {
        return Double.valueOf(values.size());
    }

    @Override
    public DistinctCountAggregatorValue clone() {
        DistinctCountAggregatorValue value = new DistinctCountAggregatorValue();
        Set<Object> copy = new HashSet<Object>(values);
        value.setValues(copy);
        return value;
    }
}
