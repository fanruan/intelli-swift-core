package com.fr.swift.query.aggregator;

import com.fr.swift.segment.column.Column;
import com.fr.swift.segment.column.ColumnKey;
import com.fr.swift.structure.iterator.RowTraversal;

import java.util.Map;

/**
 * @author pony
 * @date 2017/10/10
 * 聚合器
 */
public interface Aggregator<T extends AggregatorValue<?>> extends Combiner<T> {
    /**
     * 根据明细聚合出结果
     *
     * @param traversal 行号集合，聚合哪几行
     * @param columns   要聚合的列，多列聚合，如漏斗计算
     * @return 聚合结果
     */
    T aggregate(RowTraversal traversal, Map<ColumnKey, Column<?>> columns);

    /**
     * 根据明细聚合出结果
     *
     * @param traversal 行号集合，聚合哪几行
     * @param column    要聚合的列，单列聚合，一般聚合走这个
     * @return 聚合结果
     */
    T aggregate(RowTraversal traversal, Column<?> column);

    /**
     * 根据已有的值创建新的aggregator value，主要用于切换合计方式
     *
     * @param value aggregator value
     * @return aggregator value
     */
    T createAggregatorValue(AggregatorValue<?> value);

    /**
     * type
     *
     * @return type
     */
    AggregatorType getAggregatorType();
}
