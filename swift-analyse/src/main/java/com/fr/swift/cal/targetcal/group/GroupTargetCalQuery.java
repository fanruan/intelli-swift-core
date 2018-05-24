package com.fr.swift.cal.targetcal.group;

import com.fr.swift.cal.info.GroupQueryInfo;
import com.fr.swift.cal.result.ResultQuery;
import com.fr.swift.cal.targetcal.AbstractTargetCalQuery;
import com.fr.swift.query.adapter.dimension.Dimension;
import com.fr.swift.query.aggregator.Aggregator;
import com.fr.swift.query.filter.FilterBuilder;
import com.fr.swift.query.filter.info.FilterInfo;
import com.fr.swift.query.filter.match.MatchFilter;
import com.fr.swift.query.filter.match.NodeFilter;
import com.fr.swift.query.filter.match.NodeSorter;
import com.fr.swift.query.sort.Sort;
import com.fr.swift.result.GroupNode;
import com.fr.swift.result.NodeMergeResultSet;
import com.fr.swift.result.NodeResultSet;
import com.fr.swift.result.node.GroupNodeAggregateUtils;
import com.fr.swift.result.node.GroupNodeUtils;
import com.fr.swift.result.node.NodeType;
import com.fr.swift.result.node.cal.TargetCalculatorUtils;
import com.fr.swift.structure.Pair;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Lyon on 2018/4/26.
 */
public class GroupTargetCalQuery extends AbstractTargetCalQuery<NodeResultSet> {

    private ResultQuery<NodeResultSet> mergeQuery;
    private GroupQueryInfo info;

    public GroupTargetCalQuery(ResultQuery<NodeResultSet> mergeQuery, GroupQueryInfo info) {
        this.mergeQuery = mergeQuery;
        this.info = info;
    }

    @Override
    public NodeResultSet getQueryResult() throws SQLException {
        // 合并后的结果
        NodeMergeResultSet<GroupNode> mergeResult = (NodeMergeResultSet<GroupNode>) mergeQuery.getQueryResult();
        // 更新Node#data
        int dimensionSize = info.getDimensionInfo().getDimensions().length;
        GroupNodeUtils.updateNodeData(dimensionSize, ((GroupNode) mergeResult.getNode()), mergeResult.getRowGlobalDictionaries());

        // 产品确定结果过滤在明细汇总方式的基础上进行，不用考虑切换汇总方式的情况了
        GroupNodeAggregateUtils.aggregateMetric(NodeType.GROUP, info.getDimensionInfo().getDimensions().length,
                (GroupNode) mergeResult.getNode(), mergeResult.getAggregators());

        // 维度上的排序
        if (hasDimensionTargetSorts(info.getDimensionInfo().getDimensions())) {
            NodeSorter.sort(mergeResult.getNode(), getDimensionTargetSorts(info.getDimensionInfo().getDimensions()));
            // 在遍历一遍node，更新一下nodeIndex用于分页
            GroupNodeUtils.updateNodeIndexAfterSort((GroupNode) mergeResult.getNode());
        }

        // 根据合并后的结果处理计算指标的计算
        TargetCalculatorUtils.calculate(((GroupNode) mergeResult.getNode()), mergeResult.getRowGlobalDictionaries(), info.getTargetInfo().getGroupTargets());

        // 进行结果过滤
        List<MatchFilter> dimensionMatchFilter = getDimensionMatchFilters(info.getDimensionInfo().getDimensions());
        if (hasDimensionFilter(dimensionMatchFilter)) {
            NodeFilter.filter(mergeResult.getNode(), dimensionMatchFilter);
        }

        // 处理二次计算
        TargetCalculatorUtils.calculateAfterFiltering(((GroupNode) mergeResult.getNode()),
                mergeResult.getRowGlobalDictionaries(), info.getTargetInfo().getGroupTargets());

        // 取出结果，再做合计
        GroupNodeUtils.updateShowTargetsForGroupNode(dimensionSize, (GroupNode) mergeResult.getNode(),
                info.getTargetInfo().getTargetsForShowList());
        // 使用结果汇总聚合器汇总，这边不用管汇总方式的切换了，解析的时候包装了聚合器。
        GroupNodeAggregateUtils.aggregate(NodeType.GROUP, info.getDimensionInfo().getDimensions().length,
                (GroupNode) mergeResult.getNode(), info.getTargetInfo().getResultAggregators());
        return mergeResult;
    }

    static boolean hasDimensionTargetSorts(Dimension[] dimensions) {
        for (Dimension dimension : dimensions) {
            Sort sort = dimension.getSort();
            if (sort != null && sort.getTargetIndex() != dimension.getIndex()) {
                return true;
            }
        }
        return false;
    }

    private Pair<Aggregator, Boolean>[] getCombinedAggregators(List<Aggregator> aggregators, List<Aggregator> resultAggregators) {
        Pair<Aggregator, Boolean>[] combinedAggregators = new Pair[aggregators.size()];
        for (int i = 0; i < aggregators.size(); i++) {
            if (resultAggregators != null && resultAggregators.get(i) != null && resultAggregators.get(i) != aggregators.get(i)) {
                combinedAggregators[i] = Pair.of(resultAggregators.get(i), true);
            } else {
                combinedAggregators[i] = Pair.of(aggregators.get(i), false);
            }
        }
        return combinedAggregators;
    }

    private Pair<Aggregator, Boolean>[] getDifferentAggregator(List<Aggregator> aggregators, List<Aggregator> resultAggregators) {
        Pair<Aggregator, Boolean>[] combinedAggregators = new Pair[aggregators.size()];
        for (int i = 0; i < aggregators.size(); i++) {
            if (resultAggregators != null && resultAggregators.get(i) != null && resultAggregators.get(i) != aggregators.get(i)) {
                combinedAggregators[i] = Pair.of(resultAggregators.get(i), true);
            }
        }
        return combinedAggregators;
    }

    private boolean hasDifferentResultAggregator(List<Aggregator> aggregators, List<Aggregator> resultAggregators) {
        if (resultAggregators == null || resultAggregators.isEmpty()) {
            return false;
        }
        for (int i = 0; i < resultAggregators.size(); i++) {
            if (resultAggregators.get(i) != null && resultAggregators.get(i) != aggregators.get(i)) {
                return true;
            }
        }
        return false;
    }

    private boolean hasDimensionFilter(List<MatchFilter> dimensionMatchFilter) {
        if (dimensionMatchFilter == null) {
            return false;
        }
        for (int i = 0; i < dimensionMatchFilter.size(); i++) {
            if (dimensionMatchFilter.get(i) != null) {
                return true;
            }
        }
        return false;
    }

    private static List<MatchFilter> getDimensionMatchFilters(Dimension[] dimensions) {
        List<MatchFilter> matchFilters = new ArrayList<MatchFilter>(dimensions.length);
        for (Dimension dimension : dimensions) {
            FilterInfo filter = dimension.getFilter();
            if (filter != null && filter.isMatchFilter()) {
                matchFilters.add(FilterBuilder.buildMatchFilter(filter));
            } else {
                // 其他情况用null占位，表示不过滤
                matchFilters.add(null);
            }
        }
        return matchFilters;
    }


    /**
     * 维度根据结果（比如聚合之后的指标）排序
     */
    static List<Sort> getDimensionTargetSorts(Dimension[] dimensions) {
        List<Sort> indexSorts = new ArrayList<Sort>();
        for (Dimension dimension : dimensions) {
            Sort sort = dimension.getSort();
            if (sort != null && sort.getTargetIndex() != dimension.getIndex()) {
                indexSorts.add(sort);
            } else {
                indexSorts.add(null);
            }
        }
        return indexSorts;
    }
}
