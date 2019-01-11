package com.fr.swift.query.result.group;

import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.query.aggregator.Aggregator;
import com.fr.swift.query.query.Query;
import com.fr.swift.query.sort.SortType;
import com.fr.swift.result.GroupNode;
import com.fr.swift.result.NodeMergeQRS;
import com.fr.swift.result.node.resultset.INodeQueryResultSetMerger;
import com.fr.swift.result.node.resultset.NodeQueryResultSetMerger;
import com.fr.swift.result.qrs.QueryResultSet;
import com.fr.swift.source.ColumnTypeConstants;
import com.fr.swift.structure.Pair;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author pony
 * @date 2017/11/27
 */
public class GroupResultQuery extends AbstractGroupResultQuery {

    private boolean[] isGlobalIndexed;
    private List<Pair<SortType, ColumnTypeConstants.ClassType>> comparators;

    public GroupResultQuery(int fetchSize, List<Query<QueryResultSet>> queries, List<Aggregator> aggregators,
                            List<Pair<SortType, ColumnTypeConstants.ClassType>> comparators, boolean[] isGlobalIndexed) {
        super(fetchSize, queries, aggregators);
        this.comparators = comparators;
        this.isGlobalIndexed = isGlobalIndexed;
    }

    @Override
    public QueryResultSet getQueryResult() throws SQLException {
        List<NodeMergeQRS<GroupNode>> resultSets = new ArrayList<NodeMergeQRS<GroupNode>>();
        for (Query<QueryResultSet> query : queryList) {
            NodeMergeQRS<GroupNode> resultSet = (NodeMergeQRS<GroupNode>) query.getQueryResult();
            if (resultSet == null) {
                SwiftLoggers.getLogger().info("failed to get result from query: ", query.toString());
            } else {
                resultSets.add(resultSet);
            }
        }
        return createMerger().merge(resultSets);
    }

    @Override
    protected INodeQueryResultSetMerger createMerger() {
        return new NodeQueryResultSetMerger(fetchSize, isGlobalIndexed, aggregators, comparators);
    }
}