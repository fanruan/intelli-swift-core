package com.fr.swift.query.aggregator;

/**
 * @author yee
 * @date 2019-06-26
 */
public interface IterableAggregatorValue<T> extends AggregatorValue<T>, Iterable<AggregatorValue<T>> {
}
