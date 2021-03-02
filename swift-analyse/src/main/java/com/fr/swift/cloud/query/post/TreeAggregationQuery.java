package com.fr.swift.cloud.query.post;

import com.fr.swift.cloud.query.aggregator.Aggregator;
import com.fr.swift.cloud.query.group.by2.node.GroupPage;
import com.fr.swift.cloud.query.query.Query;
import com.fr.swift.cloud.result.NodeMergeQRS;
import com.fr.swift.cloud.result.node.GroupNodeAggregateUtils;
import com.fr.swift.cloud.result.qrs.QueryResultSet;

import java.sql.SQLException;
import java.util.List;

/**
 * Created by Lyon on 2018/6/3.
 */
public class TreeAggregationQuery implements Query<QueryResultSet<GroupPage>> {

    private Query<QueryResultSet<GroupPage>> query;
    private List<Aggregator> aggregators;

    public TreeAggregationQuery(Query<QueryResultSet<GroupPage>> query, List<Aggregator> aggregators) {
        this.query = query;
        this.aggregators = aggregators;
    }

    @Override
    public QueryResultSet<GroupPage> getQueryResult() throws SQLException {
        NodeMergeQRS mergeResult = (NodeMergeQRS) query.getQueryResult();
        GroupPage pair = mergeResult.getPage();
        GroupNodeAggregateUtils.aggregateMetric(pair.getGlobalDicts().size(), pair.getRoot(), aggregators);
        // TODO: 2018/11/27
        return mergeResult;
    }
}
