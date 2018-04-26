package com.fr.swift.cal.builder;

import com.fr.swift.cal.Query;
import com.fr.swift.cal.info.GroupQueryInfo;
import com.fr.swift.cal.result.ResultQuery;
import com.fr.swift.cal.result.group.GroupPagingResultQuery;
import com.fr.swift.cal.targetcal.group.GroupTargetCalQuery;
import com.fr.swift.query.adapter.target.GroupTarget;
import com.fr.swift.query.aggregator.Aggregator;
import com.fr.swift.result.NodeResultSet;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by pony on 2017/12/18.
 * todo
 * 待实现，单机优化
 */
public class LocalGroupSingleURIQueryBuilder extends AbstractLocalGroupQueryBuilder{

    @Override
    public Query<NodeResultSet> buildTargetCalQuery(ResultQuery<NodeResultSet> query, GroupQueryInfo info) {
        return new GroupTargetCalQuery(query, info);
    }

    @Override
    public ResultQuery<NodeResultSet> buildLocalQuery(GroupQueryInfo info) {
//        return new GroupSingleURIQuery();
        return null;
    }

    @Override
    public ResultQuery<NodeResultSet> buildResultQuery(List<Query<NodeResultSet>> queries, GroupQueryInfo info) {
        return new GroupPagingResultQuery(queries, new ArrayList<Aggregator>(), new ArrayList<GroupTarget>());
    }
}
