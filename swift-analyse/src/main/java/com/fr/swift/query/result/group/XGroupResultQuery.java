package com.fr.swift.query.result.group;

import com.fr.swift.query.aggregator.Aggregator;
import com.fr.swift.query.query.Query;
import com.fr.swift.result.NodeResultSet;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Created by Lyon on 2018/4/1.
 */
public class XGroupResultQuery extends GroupResultQuery {

    private List<Comparator<Integer>> colComparators;

    public XGroupResultQuery(List<Query<NodeResultSet>> queries, List<Aggregator> aggregators,
                             List<Comparator<Integer>> rowComparators, List<Comparator<Integer>> colComparators) {
        super(queries, aggregators, rowComparators);
        this.colComparators = colComparators;
    }

    @Override
    public NodeResultSet getQueryResult() throws SQLException {
        List<NodeResultSet> xGroupByResultSets = new ArrayList<NodeResultSet>();
        for (Query<NodeResultSet> query : queryList) {
            xGroupByResultSets.add(query.getQueryResult());
        }
        return xGroupByResultSets.get(0);
        //@lyon node merger fixme
        //return XGroupByResultSetMergingUtils.merge(xGroupByResultSets, aggregators, indexSorts, xIndexSorts);
    }
}
