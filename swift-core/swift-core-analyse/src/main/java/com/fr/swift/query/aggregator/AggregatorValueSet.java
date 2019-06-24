package com.fr.swift.query.aggregator;

import com.fr.swift.source.Row;

import java.util.Iterator;

/**
 * @author yee
 * @date 2019-06-24
 */
public interface AggregatorValueSet extends Iterator<AggregatorValueRow> {
    void reset();

    int size();

    void clear();

    Iterator<Row> data();

    boolean isEmpty();

}
