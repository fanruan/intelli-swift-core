package com.fr.swift.cal.result.group;

import com.fr.swift.cal.Query;
import com.fr.swift.query.adapter.target.GroupTarget;
import com.fr.swift.query.aggregator.Aggregator;
import com.fr.swift.result.RowResultCollector;

import java.util.List;

/**
 * Created by pony on 2017/12/8.
 */
public class GroupPagingResultQuery extends AbstractGroupResultQuery {
    public GroupPagingResultQuery(List<Query<RowResultCollector>> queries, List<Aggregator> aggregators, List<GroupTarget> targets) {
        super(queries, aggregators, targets);
    }

    @Override
    public RowResultCollector getQueryResult() {
        return null;
    }
}
