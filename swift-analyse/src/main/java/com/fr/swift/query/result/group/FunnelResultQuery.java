package com.fr.swift.query.result.group;

import com.fr.swift.query.query.Query;
import com.fr.swift.query.result.AbstractResultQuery;
import com.fr.swift.result.FunnelResultSet;
import com.fr.swift.result.funnel.MergeFunnelQueryResultSet;
import com.fr.swift.result.qrs.QueryResultSet;

import java.util.List;

/**
 * This class created on 2018/12/13
 *
 * @author Lucifer
 * @description
 */
public class FunnelResultQuery extends AbstractResultQuery<QueryResultSet<FunnelResultSet>> {

    private int numberOfSteps;

    public FunnelResultQuery(int numberOfSteps, List<Query<QueryResultSet<FunnelResultSet>>> queries) {
        super(Integer.MAX_VALUE, queries);
        this.numberOfSteps = numberOfSteps;
        this.queries = queries;
    }

    @Override
    public QueryResultSet<FunnelResultSet> merge(List<QueryResultSet<FunnelResultSet>> resultSets) {
        return new MergeFunnelQueryResultSet(resultSets, numberOfSteps);
    }
}
