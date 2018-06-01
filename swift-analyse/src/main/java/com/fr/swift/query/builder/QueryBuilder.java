package com.fr.swift.query.builder;

import com.fr.swift.query.Query;
import com.fr.swift.query.QueryInfo;
import com.fr.swift.query.info.DetailQueryInfo;
import com.fr.swift.query.info.GroupQueryInfo;
import com.fr.swift.query.info.GroupQueryInfoImpl;
import com.fr.swift.query.info.ResultJoinQueryInfo;
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
                return (Query<T>) buildGroupQuery((GroupQueryInfoImpl) info);
            case RESULT_JOIN:
                return (Query<T>) buildResultJoinQuery((ResultJoinQueryInfo) info);
            default:
                return (Query<T>) buildDetailQuery((DetailQueryInfo) info);
        }
    }

    private static Query<SwiftResultSet> buildResultJoinQuery(ResultJoinQueryInfo info) throws SQLException {
        return ResultJoinQueryBuilder.buildQuery(info);
    }

    private static Query<NodeResultSet> buildGroupQuery(GroupQueryInfo info) {
        return GroupQueryBuilder.buildQuery(info);
    }

    private static Query<DetailResultSet> buildDetailQuery(DetailQueryInfo info) throws SQLException{
        return DetailQueryBuilder.buildQuery(info);
    }

    protected static boolean isLocalURI(URI uri) {
        return true;
    }
}
