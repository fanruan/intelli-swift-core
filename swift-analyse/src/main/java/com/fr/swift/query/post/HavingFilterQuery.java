package com.fr.swift.query.post;

import com.fr.swift.query.filter.match.MatchFilter;
import com.fr.swift.query.filter.match.NodeFilter;
import com.fr.swift.result.GroupNode;
import com.fr.swift.result.NodeMergeResultSet;
import com.fr.swift.result.NodeResultSet;

import java.sql.SQLException;
import java.util.Map;

/**
 * Created by Lyon on 2018/5/31.
 */
public class HavingFilterQuery extends AbstractPostQuery<NodeResultSet> {

    private PostQuery<NodeResultSet> query;
    private Map<String, MatchFilter> filter;

    public HavingFilterQuery(PostQuery<NodeResultSet> query, Map<String, MatchFilter> filter) {
        this.query = query;
        this.filter = filter;
    }

    @Override
    public NodeResultSet getQueryResult() throws SQLException {
        NodeMergeResultSet<GroupNode> mergeResult = (NodeMergeResultSet<GroupNode>) query.getQueryResult();
        // TODO: 2018/6/3
        NodeFilter.filter(mergeResult.getNode(), null);
        return mergeResult;
    }
}
