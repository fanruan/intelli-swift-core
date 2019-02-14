package com.fr.swift.query.builder;

import com.fr.swift.bitmap.ImmutableBitMap;
import com.fr.swift.query.aggregator.Aggregator;
import com.fr.swift.query.aggregator.AggregatorType;
import com.fr.swift.query.aggregator.AggregatorValue;
import com.fr.swift.query.filter.detail.DetailFilter;
import com.fr.swift.segment.column.Column;
import com.fr.swift.structure.iterator.RowTraversal;

/**
 * Created by pony on 2018/4/17.
 */
public class MetricFilterAggregator implements Aggregator {

    private static final long serialVersionUID = 7648446984086973413L;
    private Aggregator aggregator;
    // FIXME: 2018/5/7 Aggregator在结果合并过程中传递是要清理一下这边的索引。每次调用聚合都过滤一次明显有性能问题
    private transient ImmutableBitMap bitMap;

    public MetricFilterAggregator(Aggregator aggregator, DetailFilter filter) {
        this.aggregator = aggregator;
        this.bitMap = filter.createFilterIndex();
    }

    @Override
    public AggregatorValue aggregate(RowTraversal traversal, Column column) {
        traversal = bitMap == null ? traversal : bitMap.getAnd(traversal.toBitMap());
        return aggregator.aggregate(traversal, column);
    }

    @Override
    public AggregatorValue createAggregatorValue(AggregatorValue value) {
        return aggregator.createAggregatorValue(value);
    }

    @Override
    public AggregatorType getAggregatorType() {
        return aggregator.getAggregatorType();
    }

    @Override
    public void combine(Object current, Object other) {
        aggregator.combine(current, other);
    }
}
