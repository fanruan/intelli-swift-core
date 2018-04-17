package com.fr.swift.cal.result.group;

import com.fr.swift.cal.Query;
import com.fr.swift.query.adapter.target.GroupTarget;
import com.fr.swift.query.aggregator.Aggregator;
import com.fr.swift.query.filter.match.MatchFilter;
import com.fr.swift.query.sort.Sort;
import com.fr.swift.result.ChildMap;
import com.fr.swift.result.GroupByResultSet;
import com.fr.swift.result.node.GroupNode;
import com.fr.swift.result.node.GroupNodeFactory;

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

    public GroupResultQuery(List<Query<GroupByResultSet>> queries, List<Aggregator> aggregators, List<GroupTarget> targets) {
        super(queries, aggregators, targets);
    }

    public GroupResultQuery(List<Query<GroupByResultSet>> queries, List<Aggregator> aggregators, List<GroupTarget> targets, List<Sort> indexSorts, List<MatchFilter> dimensionMatchFilter) {
        super(queries, aggregators, targets);
        this.indexSorts = indexSorts;
        this.dimensionMatchFilter = dimensionMatchFilter;
    }

    @Override
    public GroupByResultSet getQueryResult() throws SQLException {
        // 暂时不考虑对结果的过滤
        // 这边因为要构建node，需要用到cube的字典（找到全局索引对应的分组值）

        List<GroupByResultSet> groupByResultSets = new ArrayList<GroupByResultSet>();
        for (Query<GroupByResultSet> query : queryList) {
            groupByResultSets.add(query.getQueryResult());
        }
        GroupByResultSet resultSet = GroupByResultSetMergingUtils.merge(groupByResultSets, aggregators, indexSorts);
        if (hasDimensionFilter()) {
            GroupNode groupNode = GroupNodeFactory.createNode(resultSet, aggregators.size() + targets.size());
            filter(groupNode, 0);
            return groupNode;
        }
        return resultSet;
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

    private void filter(GroupNode node, int deep) {
        if (deep < dimensionMatchFilter.size()) {
            MatchFilter filter = dimensionMatchFilter.get(deep);
            if (filter != null) {
                ChildMap<GroupNode> children = filter(node.getChildMap(), deep);
                node.clearChildMap();
                if (children == null || children.isEmpty()) {
                    clearEmptyNode(node);
                } else {
                    for (GroupNode n : children) {
                        node.addChild(n);
                    }
                }
            }
            ChildMap<GroupNode> children = node.getChildMap();
            for (GroupNode n : children) {
                filter(n, deep + 1);
            }
        }
    }


    private void clearEmptyNode(GroupNode node) {

        GroupNode parent = node.getParent();
        if (parent != null) {
            ChildMap<GroupNode> children = parent.getChildMap();
            parent.clearChildMap();
            for (GroupNode child : children) {
                if (child != node) {
                    parent.addChild(child);
                }
            }
            if (parent.getChildrenSize() == 0) {
                clearEmptyNode(parent);
            }
        }
    }

    private ChildMap<GroupNode> filter(ChildMap<GroupNode> children, final int deep) {
        MatchFilter filter = dimensionMatchFilter.get(deep);
        ChildMap<GroupNode> results = new ChildMap<GroupNode>();
        if (filter == null) {
            results = children;
        } else {
            for (GroupNode result : children) {
                if (filter.matches(result)) {
                    results.put(result.getData(), result);
                }
            }
        }
        return results;
    }

}
