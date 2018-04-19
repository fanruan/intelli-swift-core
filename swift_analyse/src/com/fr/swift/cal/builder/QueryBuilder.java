package com.fr.swift.cal.builder;

import com.fr.swift.cal.Query;
import com.fr.swift.cal.QueryInfo;
import com.fr.swift.cal.info.DetailQueryInfo;
import com.fr.swift.cal.info.GroupQueryInfo;
import com.fr.swift.result.DetailResultSet;
import com.fr.swift.result.NodeResultSet;
import com.fr.swift.source.SwiftResultSet;

import java.net.URI;
import java.sql.SQLException;

/**
 * Created by pony on 2017/12/12.
 */
public class QueryBuilder {
    public static <T extends SwiftResultSet> Query<T> buildQuery(QueryInfo<T> info) throws SQLException{
        switch (info.getType()) {
            case GROUP:
            case CROSS_GROUP:
                return (Query<T>) buildGroupQuery((GroupQueryInfo) info);
            default:
                return (Query<T>) buildDetailQuery((DetailQueryInfo) info);
        }
    }

    private static Query<NodeResultSet> buildGroupQuery(GroupQueryInfo info) throws SQLException {
        return GroupQueryBuilder.buildQuery(info);
    }

    private static Query<DetailResultSet> buildDetailQuery(DetailQueryInfo info) throws SQLException{
        return DetailQueryBuilder.buildQuery(info);
    }

    protected static boolean isLocalURI(URI uri) {
        return true;
    }
}
