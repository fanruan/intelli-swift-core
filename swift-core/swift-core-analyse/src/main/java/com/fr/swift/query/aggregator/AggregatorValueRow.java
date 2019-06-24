package com.fr.swift.query.aggregator;

import com.fr.swift.source.Row;

/**
 * @author yee
 * @date 2019-06-24
 */
public interface AggregatorValueRow<T extends AggregatorValue> extends Row {

    void setValue(int i, T value);

    @Override
    T getValue(int i);

    Row data();
}
