package com.fr.swift.cal.builder;

import com.fr.swift.cal.Query;
import com.fr.swift.cal.info.GroupQueryInfo;
import com.fr.swift.cal.result.group.GroupPagingResultQuery;
import com.fr.swift.cal.segment.group.GroupSingleURIQuery;
import com.fr.swift.query.adapter.target.GroupTarget;
import com.fr.swift.query.aggregator.Aggregator;
import com.fr.swift.result.GroupByResultSet;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by pony on 2017/12/18.
 * todo
 * 待实现，单机优化
 */
public class LocalGroupSingleURIQueryBuilder extends AbstractLocalGroupQueryBuilder{
    @Override
    public Query<GroupByResultSet> buildLocalQuery(GroupQueryInfo info) {
        return new GroupSingleURIQuery();
    }

    @Override
    public Query<GroupByResultSet> buildResultQuery(List<Query<GroupByResultSet>> queries, GroupQueryInfo info) {
        return new GroupPagingResultQuery(queries, new ArrayList<Aggregator>(), new ArrayList<GroupTarget>());
    }
}
