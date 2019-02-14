package com.fr.swift.query.result.group;

import com.fr.swift.query.query.Query;
import com.fr.swift.query.result.ResultQuery;
import com.fr.swift.result.funnel.FunnelQueryResultSet;
import com.fr.swift.result.funnel.FunnelQueryResultSetMerger;
import com.fr.swift.result.qrs.QueryResultSet;
import com.fr.swift.result.qrs.QueryResultSetMerger;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * This class created on 2018/12/13
 *
 * @author Lucifer
 * @description
 */
public class FunnelResultQuery implements ResultQuery {

    private int numberOfSteps;
    private List<Query<FunnelQueryResultSet>> queries;

    public FunnelResultQuery(int numberOfSteps, List<Query<FunnelQueryResultSet>> queries) {
        this.numberOfSteps = numberOfSteps;
        this.queries = queries;
    }

    @Override
    public QueryResultSet getQueryResult() throws SQLException {
        List<FunnelQueryResultSet> resultList = new ArrayList<FunnelQueryResultSet>();
        for (final Query<FunnelQueryResultSet> query : queries) {
            resultList.add(query.getQueryResult());
        }
        QueryResultSetMerger merger = new FunnelQueryResultSetMerger(numberOfSteps);
        QueryResultSet result = merger.merge(resultList);
        return result;
    }
}
