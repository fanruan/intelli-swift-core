package com.fr.swift.cloud.query.aggregator;

import com.fr.swift.cloud.segment.column.Column;
import com.fr.swift.cloud.segment.column.ColumnKey;
import com.fr.swift.cloud.structure.iterator.RowTraversal;

import java.util.Collections;

/**
 * @author anchore
 * @date 2019/7/9
 * <p>
 * 聚合多列的aggregator，目前由漏斗计算使用
 */
abstract class MultiColumnAggregator<T extends AggregatorValue<?>> implements Aggregator<T> {
    @Override
    public T aggregate(RowTraversal traversal, Column<?> column) {
        return aggregate(traversal, Collections.<ColumnKey, Column<?>>singletonMap(null, column));
    }
}