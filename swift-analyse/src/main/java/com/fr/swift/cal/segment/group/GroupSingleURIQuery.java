package com.fr.swift.cal.segment.group;

import com.fr.swift.cal.segment.AbstractSegmentQuery;
import com.fr.swift.query.adapter.target.Target;
import com.fr.swift.query.filter.match.MatchFilter;
import com.fr.swift.result.NodeResultSet;

/**
 * Created by pony on 2017/12/8.
 * 所有配置下探，处理uri为单一机器的查询
 */
public class GroupSingleURIQuery extends AbstractSegmentQuery<NodeResultSet> {
    private MatchFilter filter;
    private Target[] targets;

    public GroupSingleURIQuery() {
    }

    @Override
    public NodeResultSet getQueryResult() {
        return null;
    }
}
