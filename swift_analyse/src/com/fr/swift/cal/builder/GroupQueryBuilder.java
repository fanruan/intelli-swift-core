package com.fr.swift.cal.builder;

import com.fr.swift.cal.Query;
import com.fr.swift.cal.info.GroupQueryInfo;
import com.fr.swift.cal.info.TableGroupQueryInfo;
import com.fr.swift.cal.remote.RemoteQueryImpl;
import com.fr.swift.exception.SwiftSegmentAbsentException;
import com.fr.swift.result.GroupByResultSet;
import com.fr.swift.segment.SegmentLocationProvider;
import com.fr.swift.source.SourceKey;

import java.net.URI;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by pony on 2017/12/14.
 */
public class GroupQueryBuilder {
    protected static Query<GroupByResultSet> buildQuery(GroupQueryInfo info) throws SQLException {
        TableGroupQueryInfo[] tableGroups = info.getTableGroups();
        Set<URI> uris = new HashSet<URI>();
        for (TableGroupQueryInfo tableGroup : tableGroups){
            SourceKey key = tableGroup.getTable();
            Set<URI> oneGroupURIs = SegmentLocationProvider.getInstance().getURI(key);
            if (oneGroupURIs == null || oneGroupURIs.isEmpty()) {
                throw new SwiftSegmentAbsentException("no such table");
            }
            uris.addAll(oneGroupURIs);
        }
//        if (info.isPagingQuery()) {
        if (false) {
            return buildQuery(uris, info, LocalGroupQueryBuilder.PAGING);
        } else {
            return buildQuery(uris, info, LocalGroupQueryBuilder.ALL);
        }
    }

    private static Query<GroupByResultSet> buildQuery(Set<URI> uris, GroupQueryInfo info, LocalGroupQueryBuilder builder) {

        List<Query<GroupByResultSet>> queries = new ArrayList<Query<com.fr.swift.result.GroupByResultSet>>();
        for (URI uri : uris){
            if (QueryBuilder.isLocalURI(uri)){
                queries.add(builder.buildLocalQuery(info));
            } else {
                queries.add(new RemoteQueryImpl());
            }
        }
        return builder.buildResultQuery(queries, info);
    }
}
