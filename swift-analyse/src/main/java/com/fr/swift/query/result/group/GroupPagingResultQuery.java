package com.fr.swift.query.result.group;

import com.fr.swift.query.aggregator.Aggregator;
import com.fr.swift.query.query.Query;
import com.fr.swift.result.GroupNode;
import com.fr.swift.result.NodeResultSet;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Created by pony on 2017/12/8.
 */
public class GroupPagingResultQuery extends AbstractGroupResultQuery {

    public GroupPagingResultQuery(List<Query<NodeResultSet>> queries, List<Aggregator> aggregators,
                                  List<Comparator<GroupNode>> comparators) {
        super(queries, aggregators, comparators);
    }

    @Override
    public NodeResultSet getQueryResult() throws SQLException {
        // 这边的分页行数问题已经在分块查询那边确定好了
        // 这边不用进行分页处理，直到功能适配那边合并出多少算多少
        List<NodeResultSet> groupByResultSets = new ArrayList<NodeResultSet>();
        for (Query<NodeResultSet> query : queryList) {
            groupByResultSets.add(query.getQueryResult());
        }
        return groupByResultSets.get(0);
        //@lyon node merger fixme
        //return GroupByResultSetMergingUtils.merge(groupByResultSets, aggregators, indexSorts);
    }
}
