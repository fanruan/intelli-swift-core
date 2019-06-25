package com.fr.swift.query.aggregator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author yee
 * @date 2019-06-24
 */
public class ListAggregatorValueRow implements AggregatorValueRow<AggregatorValue> {
    private List<AggregatorValue> list;

    public ListAggregatorValueRow(List<AggregatorValue> list) {
        this.list = list;
    }

    public ListAggregatorValueRow(AggregatorValue[] values) {
        this(Arrays.asList(values));
    }

    public ListAggregatorValueRow(int size) {
        this(Arrays.asList(new AggregatorValue[size]));
    }

    @Override
    public void setValue(int i, AggregatorValue value) {
        list.set(i, value);
    }

    @Override
    public AggregatorValue getValue(int i) {
        return list.get(i);
    }

    @Override
    public List<Object> data() {
        List<Object> objects = new ArrayList<Object>();
        if (list != null) {
            for (AggregatorValue value : list) {
                objects.add(value == null ? null : value.calculateValue());
            }
        }
        return objects;
    }

    @Override
    public int getSize() {
        return list.size();
    }
}
