package com.fr.swift.cloud.query.builder;

import com.fr.swift.cloud.bitmap.ImmutableBitMap;
import com.fr.swift.cloud.query.aggregator.Aggregator;
import com.fr.swift.cloud.query.aggregator.AggregatorType;
import com.fr.swift.cloud.query.aggregator.AggregatorValue;
import com.fr.swift.cloud.query.filter.detail.DetailFilter;
import com.fr.swift.cloud.segment.column.Column;
import com.fr.swift.cloud.segment.column.ColumnKey;
import com.fr.swift.cloud.structure.iterator.RowTraversal;

import java.io.Serializable;
import java.util.Map;

/**
 * @author pony
 * @date 2018/4/17
 */
public class MetricFilterAggregator implements Serializable, Aggregator<AggregatorValue<?>> {

    private static final long serialVersionUID = 7648446984086973413L;
    private Aggregator aggregator;
    // FIXME: 2018/5/7 Aggregator在结果合并过程中传递是要清理一下这边的索引。每次调用聚合都过滤一次明显有性能问题
    private transient ImmutableBitMap bitMap;

    public MetricFilterAggregator(Aggregator aggregator, DetailFilter filter) {
        this.aggregator = aggregator;
        this.bitMap = filter.createFilterIndex();
    }

    @Override
    public AggregatorValue aggregate(RowTraversal traversal, Map<ColumnKey, Column<?>> columns) {
        traversal = bitMap == null ? traversal : bitMap.getAnd(traversal.toBitMap());
        return aggregator.aggregate(traversal, columns);
    }

    @Override
    public AggregatorValue aggregate(RowTraversal traversal, Column<?> column) {
        traversal = bitMap == null ? traversal : bitMap.getAnd(traversal.toBitMap());
        return aggregator.aggregate(traversal, column);
    }

    @Override
    public AggregatorValue createAggregatorValue(AggregatorValue<?> value) {
        return aggregator.createAggregatorValue(value);
    }

    @Override
    public AggregatorType getAggregatorType() {
        return aggregator.getAggregatorType();
    }

    @Override
    public void combine(AggregatorValue<?> current, AggregatorValue<?> other) {
        aggregator.combine(current, other);
    }
}
