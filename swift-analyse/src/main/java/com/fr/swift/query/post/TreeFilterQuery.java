package com.fr.swift.query.post;

import com.fr.swift.query.filter.match.MatchFilter;
import com.fr.swift.result.NodeResultSet;

import java.util.Map;

/**
 * Created by Lyon on 2018/6/3.
 */
public class TreeFilterQuery extends AbstractPostQuery<NodeResultSet> {

    private PostQuery<NodeResultSet> query;
    private Map<String, MatchFilter> filter;

    public TreeFilterQuery(PostQuery<NodeResultSet> query, Map<String, MatchFilter> filter) {
        this.query = query;
        this.filter = filter;
    }

    @Override
    public NodeResultSet getQueryResult() {
        return null;
    }
}
