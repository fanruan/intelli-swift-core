package com.fr.swift.cal.result.group;

import com.fr.swift.cal.Query;
import com.fr.swift.query.adapter.target.GroupTarget;
import com.fr.swift.query.aggregator.Aggregator;
import com.fr.swift.query.filter.match.MatchFilter;
import com.fr.swift.query.sort.Sort;
import com.fr.swift.result.GroupByResultSet;

import java.sql.SQLException;
import java.util.List;

/**
 * Created by Lyon on 2018/4/1.
 */
public class XGroupResultQuery extends GroupResultQuery {

    // TODO: 2018/4/1 占坑
    private List<Sort> xIndexSorts;
    private List<MatchFilter> xDimensionMatchFilter;

    public XGroupResultQuery(List<Query<GroupByResultSet>> queries, List<Aggregator> aggregators, List<GroupTarget> targets) {
        super(queries, aggregators, targets);
    }

    public XGroupResultQuery(List<Query<GroupByResultSet>> queries, List<Aggregator> aggregators, List<GroupTarget> targets, List<Sort> indexSorts, List<MatchFilter> dimensionMatchFilter) {
        super(queries, aggregators, targets, indexSorts, dimensionMatchFilter);
    }

    @Override
    public GroupByResultSet getQueryResult() throws SQLException {
        return null;
    }
}
