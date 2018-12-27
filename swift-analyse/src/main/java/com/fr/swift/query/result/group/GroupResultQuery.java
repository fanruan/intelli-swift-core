package com.fr.swift.query.result.group;

import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.query.aggregator.Aggregator;
import com.fr.swift.query.query.Query;
import com.fr.swift.result.GroupNode;
import com.fr.swift.result.NodeMergeResultSet;
import com.fr.swift.result.NodeResultSet;
import com.fr.swift.result.node.resultset.ChainedNodeMergeResultSet;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Created by pony on 2017/11/27.
 */
public class GroupResultQuery extends AbstractGroupResultQuery {

    private boolean[] isGlobalIndexed;

    public GroupResultQuery(int fetchSize, List<Query<NodeResultSet>> queries, List<Aggregator> aggregators,
                            List<Comparator<GroupNode>> comparators, boolean[] isGlobalIndexed) {
        super(fetchSize, queries, aggregators, comparators);
        this.isGlobalIndexed = isGlobalIndexed;
    }

    @Override
    public NodeResultSet getQueryResult() throws SQLException {
        List<NodeMergeResultSet<GroupNode>> resultSets = new ArrayList<NodeMergeResultSet<GroupNode>>();
        for (Query<NodeResultSet> query : queryList) {
            NodeMergeResultSet<GroupNode> resultSet = null;
            try {
                resultSet = (NodeMergeResultSet<GroupNode>) query.getQueryResult();
            } catch (Exception e) {
                SwiftLoggers.getLogger().info("segment query error: ", query.toString());
            }
            if (resultSet != null) {
                resultSets.add(resultSet);
            }
        }
        return new ChainedNodeMergeResultSet(fetchSize, isGlobalIndexed, resultSets, aggregators, comparators);
    }
}