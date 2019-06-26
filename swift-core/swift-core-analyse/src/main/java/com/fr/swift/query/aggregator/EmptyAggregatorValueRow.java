package com.fr.swift.query.aggregator;

import java.util.Collections;
import java.util.List;

/**
 * @author yee
 * @date 2019-06-26
 */
public class EmptyAggregatorValueRow implements AggregatorValueRow {

    @Override
    public void setValue(int i, AggregatorValue value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public AggregatorValue getValue(int i) {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<Object> data() {
        return Collections.emptyList();
    }

    @Override
    public int getSize() {
        return 0;
    }
}
