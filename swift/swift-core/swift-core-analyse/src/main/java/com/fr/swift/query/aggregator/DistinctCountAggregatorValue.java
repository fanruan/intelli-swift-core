package com.fr.swift.query.aggregator;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Xiaolei.liu
 */

public class DistinctCountAggregatorValue implements AggregatorValue<Double>, Serializable {

    private static final long serialVersionUID = -6054571707233716739L;
    private Set set = new HashSet();

    public Set getBitMap() {
        return set;
    }

    public void setBitMap(Set set) {
        this.set = set;
    }

    @Override
    public double calculate() {
        return set.size();
    }

    @Override
    public Double calculateValue() {
        return Double.valueOf(set.size());
    }

    @Override
    public Object clone() {
        DistinctCountAggregatorValue value = new DistinctCountAggregatorValue();
        Set copy = new HashSet();
        for (Object item : set) {
            copy.add(item);
        }
        value.setBitMap(copy);
        return value;
    }
}
