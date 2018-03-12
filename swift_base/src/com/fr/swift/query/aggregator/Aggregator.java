package com.fr.swift.query.aggregator;

import com.fr.swift.segment.column.Column;
import com.fr.swift.structure.iterator.RowTraversal;

/**
 * Created by pony on 2017/10/10.
 * 聚合器
 */
public interface Aggregator<T extends AggregatorValue> {
    /**
     * 根据明细聚合出结果
     *
     * @param traversal
     * @param column
     * @return
     */
    T aggregate(RowTraversal traversal, Column column);

    /**
     * 根据聚合的结果再计算
     * 合并两个值，并把合并后的值设置给
     *
     * @param value
     * @param other
     */
    void combine(T value, T other);
}
