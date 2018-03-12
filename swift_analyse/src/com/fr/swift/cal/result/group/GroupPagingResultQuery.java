package com.fr.swift.cal.result.group;

import com.fr.swift.cal.Query;
import com.fr.swift.query.adapter.target.GroupTarget;
import com.fr.swift.query.aggregator.Aggregator;
import com.fr.swift.query.sort.Sort;
import com.fr.swift.result.GroupByResultSet;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by pony on 2017/12/8.
 */
public class GroupPagingResultQuery extends AbstractGroupResultQuery {

    private List<Sort> indexSorts;

    public GroupPagingResultQuery(List<Query<GroupByResultSet>> queries, List<Aggregator> aggregators, List<GroupTarget> targets) {
        super(queries, aggregators, targets);
    }

    @Override
    public GroupByResultSet getQueryResult() throws SQLException {
        // 这边的分页行数问题已经在分块查询那边确定好了
        // 这边不用进行分页处理，直到功能适配那边合并出多少算多少
        List<GroupByResultSet> groupByResultSets = new ArrayList<GroupByResultSet>();
        for (Query<GroupByResultSet> query : queryList) {
            groupByResultSets.add(query.getQueryResult());
        }
        return GroupByResultSetMergingUtils.merge(groupByResultSets, aggregators, indexSorts);
    }
}
