package com.fr.swift.query.group.by2.node;

import com.fr.swift.query.aggregator.AggregatorValue;
import com.fr.swift.query.aggregator.AggregatorValueCombiner;
import com.fr.swift.query.group.info.GroupByInfo;
import com.fr.swift.query.group.info.MetricInfo;
import com.fr.swift.result.GroupNode;
import com.fr.swift.result.NodeMergeQueryResultSetImpl;
import com.fr.swift.result.qrs.QueryResultSet;
import com.fr.swift.structure.iterator.RowTraversal;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by Lyon on 2018/4/27.
 */
public class NodeGroupByUtils {

    /**
     * 聚合单块segment数据，得到node结果集和压缩的字典值
     *
     * @param groupByInfo 维度相关信息
     * @param metricInfo  指标相关信息
     * @return
     */
    public static Iterator<QueryResultSet<GroupPage>> groupBy(final GroupByInfo groupByInfo, final MetricInfo metricInfo) {
        if (groupByInfo.getDimensions().isEmpty()) {
            // 只有指标的情况
            final GroupNode root = new GroupNode(-1, null);
            List<QueryResultSet<GroupPage>> list = new ArrayList<QueryResultSet<GroupPage>>();
            list.add(new NodeMergeQueryResultSetImpl(groupByInfo.getFetchSize(), root, null) {
                @Override
                public GroupPage getPage() {
                    // 只有一页，适配ChainedResultSet
                    hasNextPage = false;
                    aggregateRoot(root, groupByInfo.getDetailFilter().createFilterIndex(), metricInfo);
                    return new GroupPage(root, new ArrayList<Map<Integer, Object>>());
                }
            });
            return list.iterator();
        }
        return new NodePageIterator(groupByInfo.getFetchSize(), groupByInfo, metricInfo);
    }

    private static void aggregateRoot(GroupNode root, RowTraversal traversal, MetricInfo metricInfo) {
//        AggregatorValue[] values = RowMapper.aggregateRow(traversal, metricInfo.getTargetLength(),
//                metricInfo.getMetrics(), metricInfo.getAggregators());
        AggregatorValueCombiner values = RowMapper.aggregatorValueCombiner(traversal, metricInfo.getTargetLength(),
                metricInfo.getMetrics(), metricInfo.getAggregators());
        if (values.isNeedCombine()) {
            Iterator<AggregatorValue[]> combineIterator = values.getCombineIterator();
            GroupNode child = new GroupNode(root.getDepth() + 1, null);
            child.setAggregatorValue(combineIterator.next());
            root.addChild(child);
        } else {
            root.setAggregatorValue(values.getAggregatorValue());
        }
    }
}
