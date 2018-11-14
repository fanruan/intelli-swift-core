package com.fr.swift.query.aggregator;

import com.fr.swift.segment.column.Column;
import com.fr.swift.structure.iterator.RowTraversal;

/**
 * Created by pony on 2017/10/10.
 * 聚合器
 */
public interface Aggregator<T extends AggregatorValue> extends Combiner<T> {
    /**
     * 根据明细聚合出结果
     *
     * @param traversal
     * @param column
     * @return
     */
    T aggregate(RowTraversal traversal, Column column);

    /**
     * 根据已有的值创建新的aggregatorvalue，主要用于切换合计方式
     *
     * @param value
     * @return
     */
    T createAggregatorValue(AggregatorValue value);

    AggregatorType getAggregatorType();
}
