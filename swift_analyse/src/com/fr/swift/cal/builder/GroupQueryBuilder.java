package com.fr.swift.cal.builder;

import com.fr.swift.cal.Query;
import com.fr.swift.cal.info.GroupQueryInfo;
import com.fr.swift.cal.remote.RemoteQueryImpl;
import com.fr.swift.result.NodeResultSet;
import com.fr.swift.segment.SegmentLocationProvider;
import com.fr.swift.source.SourceKey;

import java.net.URI;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Created by pony on 2017/12/14.
 */
public class GroupQueryBuilder {
    protected static Query<NodeResultSet> buildQuery(GroupQueryInfo info) throws SQLException {
        SourceKey key = info.getTable();
        Set<URI> uris = SegmentLocationProvider.getInstance().getURI(key);
//        if (info.isPagingQuery()) {
        if (false) {
            return buildQuery(uris, info, LocalGroupQueryBuilder.PAGING);
        } else {
            return buildQuery(uris, info, LocalGroupQueryBuilder.ALL);
        }
    }

    private static Query<NodeResultSet> buildQuery(Set<URI> uris, GroupQueryInfo info, LocalGroupQueryBuilder builder) {

        List<Query<NodeResultSet>> queries = new ArrayList<Query<NodeResultSet>>();
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
