package com.fr.swift.cal.builder;

import com.fr.swift.cal.Query;
import com.fr.swift.cal.info.DetailQueryInfo;
import com.fr.swift.cal.remote.RemoteQueryImpl;
import com.fr.swift.exception.SwiftSegmentAbsentException;
import com.fr.swift.result.DetailResultSet;
import com.fr.swift.service.SegmentLocationProvider;
import com.fr.swift.source.SourceKey;

import java.net.URI;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Created by pony on 2017/12/13.
 */
public class DetailQueryBuilder {
    protected static Query<DetailResultSet> buildQuery(DetailQueryInfo info) throws SQLException{
        if (info.hasSort()){
            return buildQuery(info, LocalDetailQueryBuilder.GROUP);
        } else {
            return buildQuery(info, LocalDetailQueryBuilder.NORMAL);
        }
    }


    private static Query<DetailResultSet> buildQuery(DetailQueryInfo info, LocalDetailQueryBuilder builder) throws SQLException{
        SourceKey table = info.getTable();
        Set<URI> uris = SegmentLocationProvider.getInstance().getSegmentLocaltionURI(table);
        if (uris == null || uris.isEmpty()){
            throw new SwiftSegmentAbsentException("no such table");
        }
        List<Query<DetailResultSet>> queries = new ArrayList<Query<DetailResultSet>>();
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
