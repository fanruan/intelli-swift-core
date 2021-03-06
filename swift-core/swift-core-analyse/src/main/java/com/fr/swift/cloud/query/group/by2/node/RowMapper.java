package com.fr.swift.cloud.query.group.by2.node;

import com.fr.swift.cloud.query.aggregator.Aggregator;
import com.fr.swift.cloud.query.aggregator.AggregatorValueCombiner;
import com.fr.swift.cloud.query.aggregator.CartesianAggregatorCombiner;
import com.fr.swift.cloud.query.group.by.GroupByEntry;
import com.fr.swift.cloud.query.group.info.MetricInfo;
import com.fr.swift.cloud.result.GroupNode;
import com.fr.swift.cloud.result.SwiftNode;
import com.fr.swift.cloud.segment.column.Column;
import com.fr.swift.cloud.structure.iterator.RowTraversal;
import com.fr.swift.cloud.util.function.BinaryFunction;

import java.util.Iterator;
import java.util.List;

/**
 * Created by Lyon on 2018/4/27.
 */
class RowMapper implements BinaryFunction<GroupByEntry, GroupNode, GroupNode> {

    private int targetLength;
    private List<Column> metrics;
    private List<Aggregator> aggregators;

    public RowMapper(MetricInfo metricInfo) {
        this.targetLength = metricInfo.getTargetLength();
        this.metrics = metricInfo.getMetrics();
        this.aggregators = metricInfo.getAggregators();
    }

    static AggregatorValueCombiner aggregatorValueCombiner(RowTraversal traversal, int targetLength,
                                                           List<Column> metrics, List<Aggregator> aggregators) {
        AggregatorValueCombiner values = new CartesianAggregatorCombiner(targetLength);
        for (int i = 0; i < metrics.size(); i++) {
            if (traversal.isEmpty()) {
                // 索引为空跳过，这边设置aggregatorValue为null，合并的时候另外判断是否为空
                values.setValue(i, null);
                continue;
            }
            // 如果指标比较多，这边也可以增加多线程计算
            values.setValue(i, aggregators.get(i).aggregate(traversal, metrics.get(i)));
        }
        return values;
    }

    @Override
    public GroupNode apply(GroupByEntry groupByEntry, GroupNode node) {
//        AggregatorValue[] values = aggregateRow(groupByEntry.getTraversal(), targetLength, metrics, aggregators);
//        node.setAggregatorValue(values);
        AggregatorValueCombiner combiner = aggregatorValueCombiner(groupByEntry.getTraversal(), targetLength, metrics, aggregators);
        if (combiner.isNeedCombine()) {
            Iterator<SwiftNode> combineIterator = combiner.getSwiftNodeIterator(node.getDepth());
            while (combineIterator.hasNext()) {
                node.addChild(combineIterator.next());
            }
        } else {
            node.setAggregatorValue(combiner.getAggregatorValue());
        }
        return node;
    }
}
