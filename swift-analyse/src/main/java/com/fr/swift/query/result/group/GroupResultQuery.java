package com.fr.swift.query.result.group;

import com.fr.swift.query.aggregator.Aggregator;
import com.fr.swift.query.query.Query;
import com.fr.swift.result.GroupNode;
import com.fr.swift.result.NodeMergeResultSet;
import com.fr.swift.result.NodeResultSet;
import com.fr.swift.result.node.ChainedNodeMergeResultSet;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Created by pony on 2017/11/27.
 */
public class GroupResultQuery extends AbstractGroupResultQuery {

    public GroupResultQuery(int fetchSize, List<Query<NodeResultSet>> queries, List<Aggregator> aggregators,
                            List<Comparator<GroupNode>> comparators) {
        super(fetchSize, queries, aggregators, comparators);
    }

    @Override
    public NodeResultSet getQueryResult() throws SQLException {
        List<NodeMergeResultSet<GroupNode>> resultSets = new ArrayList<NodeMergeResultSet<GroupNode>>();
        for (Query<NodeResultSet> query : queryList) {
            resultSets.add((NodeMergeResultSet<GroupNode>) query.getQueryResult());
        }
        return new ChainedNodeMergeResultSet(fetchSize, resultSets, aggregators, comparators);
    }
}