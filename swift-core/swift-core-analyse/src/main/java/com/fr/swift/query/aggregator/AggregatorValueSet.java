package com.fr.swift.query.aggregator;

import java.util.Iterator;

/**
 * @author yee
 * @date 2019-06-24
 */
public interface AggregatorValueSet extends Iterator<AggregatorValueRow> {
    void reset();

    int size();
}
