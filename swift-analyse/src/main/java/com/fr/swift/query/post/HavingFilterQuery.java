package com.fr.swift.query.post;

import com.fr.swift.query.filter.match.MatchFilter;
import com.fr.swift.query.filter.match.NodeFilter;
import com.fr.swift.query.result.ResultQuery;
import com.fr.swift.result.GroupNode;
import com.fr.swift.result.NodeMergeResultSet;
import com.fr.swift.result.NodeResultSet;

import java.sql.SQLException;
import java.util.List;

/**
 * Created by Lyon on 2018/5/31.
 */
public class HavingFilterQuery extends AbstractPostQuery<NodeResultSet> {

    private ResultQuery<NodeResultSet> query;
    private List<MatchFilter> filter;

    public HavingFilterQuery(ResultQuery<NodeResultSet> query, List<MatchFilter> filter) {
        this.query = query;
        this.filter = filter;
    }

    @Override
    public NodeResultSet getQueryResult() throws SQLException {
        NodeMergeResultSet<GroupNode> mergeResult = (NodeMergeResultSet<GroupNode>) query.getQueryResult();
        NodeFilter.filter(mergeResult.getNode(), filter);
        return mergeResult;
    }
}
