package com.fr.swift.cloud.query.group.by2.node;

import com.fr.swift.cloud.query.aggregator.AggregatorValueCombiner;
import com.fr.swift.cloud.query.group.info.GroupByInfo;
import com.fr.swift.cloud.query.group.info.MetricInfo;
import com.fr.swift.cloud.result.GroupNode;
import com.fr.swift.cloud.result.SwiftNode;
import com.fr.swift.cloud.structure.iterator.RowTraversal;

import java.util.Collections;
import java.util.Iterator;
import java.util.Map;

/**
 * @author Lyon
 * @date 2018/4/27
 */
public class NodeGroupByUtils {

    /**
     * 聚合单块segment数据，得到node结果集和压缩的字典值
     *
     * @param groupByInfo 维度相关信息
     * @param metricInfo  指标相关信息
     * @return group page stream
     */
    public static Iterator<GroupPage> groupBy(final GroupByInfo groupByInfo, final MetricInfo metricInfo) {
        if (groupByInfo.getDimensions().isEmpty()) {
            // 只有指标的情况
            final GroupNode root = new GroupNode(-1, null);
            aggregateRoot(root, groupByInfo.getDetailFilter().createFilterIndex(), metricInfo);
            return Collections.singleton(new GroupPage(root, Collections.<Map<Integer, Object>>emptyList())).iterator();
        }
        return new GroupPageIterator(groupByInfo.getFetchSize(), groupByInfo, metricInfo);
    }

    private static void aggregateRoot(GroupNode root, RowTraversal traversal, MetricInfo metricInfo) {
//        AggregatorValue[] values = RowMapper.aggregateRow(traversal, metricInfo.getTargetLength(),
//                metricInfo.getMetrics(), metricInfo.getAggregators());
        AggregatorValueCombiner values = RowMapper.aggregatorValueCombiner(traversal, metricInfo.getTargetLength(),
                metricInfo.getMetrics(), metricInfo.getAggregators());
        if (values.isNeedCombine()) {
            Iterator<SwiftNode> combineIterator = values.getSwiftNodeIterator(root.getDepth());
            while (combineIterator.hasNext()) {
                root.addChild(combineIterator.next());
            }

        } else {
            root.setAggregatorValue(values.getAggregatorValue());
        }
    }
}
