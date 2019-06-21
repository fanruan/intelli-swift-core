package com.fr.swift.query.result.group;

import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.query.aggregator.Aggregator;
import com.fr.swift.query.group.by2.node.GroupPage;
import com.fr.swift.query.query.Query;
import com.fr.swift.query.result.AbstractResultQuery;
import com.fr.swift.query.sort.SortType;
import com.fr.swift.result.node.resultset.NodeQueryResultSetMerger;
import com.fr.swift.result.qrs.QueryResultSet;
import com.fr.swift.result.qrs.QueryResultSetMerger;
import com.fr.swift.source.ColumnTypeConstants;
import com.fr.swift.structure.Pair;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author pony
 * @date 2017/11/27
 */
public class GroupResultQuery extends AbstractResultQuery<QueryResultSet<GroupPage>> {
    private List<Aggregator> aggregators;

    private boolean[] isGlobalIndexed;

    private List<Pair<SortType, ColumnTypeConstants.ClassType>> comparators;

    public GroupResultQuery(int fetchSize, List<Query<QueryResultSet<GroupPage>>> queries, List<Aggregator> aggregators,
                            List<Pair<SortType, ColumnTypeConstants.ClassType>> comparators, boolean[] isGlobalIndexed) {
        super(fetchSize, queries);
        this.aggregators = aggregators;
        this.comparators = comparators;
        this.isGlobalIndexed = isGlobalIndexed;
    }

    @Override
    public QueryResultSet<GroupPage> getQueryResult() throws SQLException {
        List<QueryResultSet<GroupPage>> resultSets = new ArrayList<QueryResultSet<GroupPage>>();
        for (Query<QueryResultSet<GroupPage>> query : queryList) {
            QueryResultSet<GroupPage> resultSet = query.getQueryResult();
            if (resultSet == null) {
                SwiftLoggers.getLogger().info("failed to get result from query: ", query.toString());
            } else {
                resultSets.add(resultSet);
            }
        }
        return createMerger().merge(resultSets);
    }

    private QueryResultSetMerger<QueryResultSet<GroupPage>> createMerger() {
        return new NodeQueryResultSetMerger(fetchSize, isGlobalIndexed, aggregators, comparators);
    }
}