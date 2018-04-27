package com.fr.swift.cal.builder;

import com.fr.swift.bitmap.ImmutableBitMap;
import com.fr.swift.query.aggregator.Aggregator;
import com.fr.swift.query.aggregator.AggregatorValue;
import com.fr.swift.query.filter.detail.DetailFilter;
import com.fr.swift.segment.column.Column;
import com.fr.swift.structure.iterator.RowTraversal;

/**
 * Created by pony on 2018/4/17.
 */
public class MetricFilterAggregator implements Aggregator {

    private Aggregator aggregator;
    private DetailFilter filter;

    public MetricFilterAggregator(Aggregator aggregator, DetailFilter filter) {
        this.aggregator = aggregator;
        this.filter = filter;
    }

    @Override
    public AggregatorValue aggregate(RowTraversal traversal, Column column) {
        // 这边调用聚合方法的时候才加载明细索引比较好，不然Aggregator可能在合并结果过程中继续使用，而索引却没有释放
        ImmutableBitMap bitMap = filter == null ? null : filter.createFilterIndex();
        traversal = bitMap == null ? traversal : bitMap.getAnd(traversal.toBitMap());
        return aggregator.aggregate(traversal, column);
    }

    @Override
    public AggregatorValue createAggregatorValue(AggregatorValue value) {
        return aggregator.createAggregatorValue(value);
    }

    @Override
    public void combine(Object current, Object other) {
        aggregator.combine(current, other);
    }
}
