package com.fr.swift.cal.result.group;

import com.fr.swift.cal.Query;
import com.fr.swift.query.adapter.target.GroupTarget;
import com.fr.swift.query.aggregator.Aggregator;
import com.fr.swift.query.filter.match.MatchFilter;
import com.fr.swift.query.filter.match.NodeAggregator;
import com.fr.swift.query.filter.match.NodeFilter;
import com.fr.swift.query.sort.Sort;
import com.fr.swift.result.NodeResultSet;

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

    public GroupResultQuery(List<Query<NodeResultSet>> queries, List<Aggregator> aggregators, List<GroupTarget> targets) {
        super(queries, aggregators, targets);
    }

    public GroupResultQuery(List<Query<NodeResultSet>> queries, List<Aggregator> aggregators, List<GroupTarget> targets, List<Sort> indexSorts, List<MatchFilter> dimensionMatchFilter) {
        super(queries, aggregators, targets);
        this.indexSorts = indexSorts;
        this.dimensionMatchFilter = dimensionMatchFilter;
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
            NodeAggregator.aggregate(node.getNode(), aggregators.toArray(new Aggregator[aggregators.size()]));
        }
        return node;
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
