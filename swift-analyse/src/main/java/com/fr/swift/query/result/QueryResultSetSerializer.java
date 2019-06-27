package com.fr.swift.query.result;


import com.fr.swift.exception.meta.SwiftMetaDataException;
import com.fr.swift.query.post.meta.SwiftMetaDataUtils;
import com.fr.swift.query.query.QueryBean;
import com.fr.swift.query.query.QueryType;
import com.fr.swift.query.result.serialize.SerializedDetailQueryResultSet;
import com.fr.swift.query.result.serialize.SerializedGroupQueryResultSet;
import com.fr.swift.query.result.serialize.SerializedSortedDetailQueryResultSet;
import com.fr.swift.result.DetailQueryResultSet;
import com.fr.swift.result.MergeSortedDetailQueryResultSet;
import com.fr.swift.result.SwiftResultSet;
import com.fr.swift.result.node.resultset.MergeGroupQueryResultSet;
import com.fr.swift.result.qrs.QueryResultSet;
import com.fr.swift.source.SwiftMetaData;
import com.fr.swift.util.Crasher;

/**
 * @author yee
 * @date 2018-12-12
 */
public class QueryResultSetSerializer {

    public static <T> QueryResultSet<T> serialize(QueryType type, QueryResultSet<T> resultSet) {
        switch (type) {
            case DETAIL: {
                DetailQueryResultSet rs = (DetailQueryResultSet) resultSet;
                // 注意先后顺序，先getPage，然后hasNextPage
                return (QueryResultSet<T>) new SerializedDetailQueryResultSet(rs.getFetchSize(), rs.getRowCount(), rs.hasNextPage() ? rs.getPage() : null, rs.hasNextPage());
            }
            case DETAIL_SORT: {
                // 注意先后顺序，先getPage，然后hasNextPage
                MergeSortedDetailQueryResultSet rs = (MergeSortedDetailQueryResultSet) resultSet;
                return (QueryResultSet<T>) new SerializedSortedDetailQueryResultSet(rs.getFetchSize(), rs.getRowCount(), rs.getComparator(), rs.hasNextPage() ? rs.getPage() : null, rs.hasNextPage());
            }
            case GROUP: {
                // 注意先后顺序，先getPage，然后hasNextPage
                MergeGroupQueryResultSet rs = (MergeGroupQueryResultSet) resultSet;
                return (QueryResultSet<T>) new SerializedGroupQueryResultSet(rs.getFetchSize(), rs.getGlobalIndexed(), rs.getAggragators(), rs.getComparators(), rs.hasNextPage() ? rs.getPage() : null, rs.hasNextPage());
            }
            case FUNNEL:
                // funnel之前就已做序列化
            default:
                return resultSet;
        }
    }

    public static SwiftResultSet toSwiftResultSet(QueryResultSet<?> queryResultSet, QueryBean bean) {
        SwiftMetaData metaData;
        try {
            metaData = SwiftMetaDataUtils.createMetaData(bean);
        } catch (SwiftMetaDataException e) {
            return Crasher.crash(e);
        }
        return queryResultSet.convert(metaData);
    }
}
