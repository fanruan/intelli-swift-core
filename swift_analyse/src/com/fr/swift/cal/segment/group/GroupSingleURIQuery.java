package com.fr.swift.cal.segment.group;

import com.fr.swift.cal.segment.AbstractSegmentQuery;
import com.fr.swift.query.adapter.target.Target;
import com.fr.swift.query.filter.match.MatchFilter;
import com.fr.swift.result.RowResultCollector;

/**
 * Created by pony on 2017/12/8.
 * 所有配置下探，处理uri为单一机器的查询
 */
public class GroupSingleURIQuery extends AbstractSegmentQuery<RowResultCollector> {
    private MatchFilter filter;
    private Target[] targets;

    public GroupSingleURIQuery() {
    }

    @Override
    public RowResultCollector getQueryResult() {
        return null;
    }
}
