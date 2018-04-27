package com.fr.swift.cal.result.group;

import com.fr.swift.cal.Query;
import com.fr.swift.query.adapter.target.GroupTarget;
import com.fr.swift.query.aggregator.Aggregator;
import com.fr.swift.query.filter.match.MatchFilter;
import com.fr.swift.query.filter.match.NodeAggregator;
import com.fr.swift.query.filter.match.NodeFilter;
import com.fr.swift.query.sort.Sort;
import com.fr.swift.result.NodeResultSet;
import com.fr.swift.structure.Pair;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by pony on 2017/11/27.
 */
public class GroupResultQuery extends AbstractGroupResultQuery {

    // 这些都是对维度的排序，构建Node的时候要用到
    protected List<Sort> indexSorts;
    // 这个应该是对指标的结果过滤？维度过滤应该在分块计算中处理，不然影响分页计算量的准确性
    protected List<MatchFilter> dimensionMatchFilter;
    //对最后结果的合计
    protected List<Aggregator> resultAggregators;

    public GroupResultQuery(List<Query<NodeResultSet>> queries, List<Aggregator> aggregators, List<GroupTarget> targets) {
        super(queries, aggregators, targets);
    }

    public GroupResultQuery(List<Query<NodeResultSet>> queries, List<Aggregator> aggregators, List<GroupTarget> targets, List<Sort> indexSorts, List<MatchFilter> dimensionMatchFilter, List<Aggregator> resultAggregators) {
        super(queries, aggregators, targets);
        this.indexSorts = indexSorts;
        this.dimensionMatchFilter = dimensionMatchFilter;
        this.resultAggregators = resultAggregators;
    }

    @Override
    public NodeResultSet getQueryResult() throws SQLException {
        // 暂时不考虑对结果的过滤
        // 这边因为要构建node，需要用到cube的字典（找到全局索引对应的分组值）

        List<NodeResultSet> groupByResultSets = new ArrayList<NodeResultSet>();
        for (Query<NodeResultSet> query : queryList) {
            groupByResultSets.add(query.getQueryResult());
        }
        //@lyon node merger fixme
        //GroupByResultSet resultSet = GroupByResultSetMergingUtils.merge(groupByResultSets, aggregators, indexSorts);
        NodeResultSet node = groupByResultSets.get(0);
        if (hasDimensionFilter()) {
            NodeFilter.filter(node.getNode(), dimensionMatchFilter);
            NodeAggregator.aggregate(node.getNode(), getCombinedAggregators());
        } else if (hasDifferentResultAggregator()) {
            NodeAggregator.aggregate(node.getNode(), getDifferentAggregator());
        }
        return node;
    }

    private Pair<Aggregator, Boolean>[] getCombinedAggregators() {
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

    private Pair<Aggregator, Boolean>[] getDifferentAggregator() {
        Pair<Aggregator, Boolean>[] combinedAggregators = new Pair[aggregators.size()];
        for (int i = 0; i < aggregators.size(); i++) {
            if (resultAggregators != null && resultAggregators.get(i) != null && resultAggregators.get(i) != aggregators.get(i)) {
                combinedAggregators[i] = Pair.of(resultAggregators.get(i), true);
            }
        }
        return combinedAggregators;
    }

    private boolean hasDifferentResultAggregator() {
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

    private boolean hasDimensionFilter() {
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

}
