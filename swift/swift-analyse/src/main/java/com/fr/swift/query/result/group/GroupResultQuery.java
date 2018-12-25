package com.fr.swift.query.result.group;

import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.query.aggregator.Aggregator;
import com.fr.swift.query.query.Query;
import com.fr.swift.result.GroupNode;
import com.fr.swift.result.NodeMergeResultSet;
import com.fr.swift.result.node.resultset.INodeQueryResultSetMerger;
import com.fr.swift.result.node.resultset.NodeQueryResultSetMerger;
import com.fr.swift.result.qrs.QueryResultSet;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * @author pony
 * @date 2017/11/27
 */
public class GroupResultQuery extends AbstractGroupResultQuery {

    private boolean[] isGlobalIndexed;

    public GroupResultQuery(int fetchSize, List<Query<QueryResultSet>> queries, List<Aggregator> aggregators,
                            List<Comparator<GroupNode>> comparators, boolean[] isGlobalIndexed) {
        super(fetchSize, queries, aggregators, comparators);
        this.isGlobalIndexed = isGlobalIndexed;
    }

    @Override
    public QueryResultSet getQueryResult() throws SQLException {
        List<NodeMergeResultSet<GroupNode>> resultSets = new ArrayList<NodeMergeResultSet<GroupNode>>();
        for (Query<QueryResultSet> query : queryList) {
            NodeMergeResultSet<GroupNode> resultSet = (NodeMergeResultSet<GroupNode>) query.getQueryResult();
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