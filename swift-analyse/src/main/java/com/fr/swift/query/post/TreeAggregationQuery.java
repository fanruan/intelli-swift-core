package com.fr.swift.query.post;

import com.fr.swift.query.aggregator.Aggregator;
import com.fr.swift.result.NodeResultSet;

import java.util.Map;

/**
 * Created by Lyon on 2018/6/3.
 */
public class TreeAggregationQuery extends AbstractPostQuery<NodeResultSet> {

    private PostQuery<NodeResultSet> query;
    private Map<String, Aggregator> aggregatorMap;

    public TreeAggregationQuery(PostQuery<NodeResultSet> query, Map<String, Aggregator> aggregatorMap) {
        this.query = query;
        this.aggregatorMap = aggregatorMap;
    }

    @Override
    public NodeResultSet getQueryResult() {
        return null;
    }
}
