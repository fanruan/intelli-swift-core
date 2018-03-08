package com.fr.swift.cal.builder;

import com.fr.swift.cal.Query;
import com.fr.swift.cal.info.GroupQueryInfo;
import com.fr.swift.result.GroupByResultSet;

import java.util.List;

/**
 * Created by pony on 2017/12/15.
 */
public interface LocalGroupQueryBuilder {
    LocalGroupQueryBuilder SINGLE_URI = new LocalGroupSingleURIQueryBuilder();

    LocalGroupQueryBuilder PAGING = new LocalGroupPagingQueryBuilder();

    LocalGroupQueryBuilder ALL = new LocalGroupAllQueryBuilder();


    Query<GroupByResultSet> buildLocalQuery(GroupQueryInfo info);

    Query<GroupByResultSet> buildResultQuery(List<Query<GroupByResultSet>> queries, GroupQueryInfo info);
}
