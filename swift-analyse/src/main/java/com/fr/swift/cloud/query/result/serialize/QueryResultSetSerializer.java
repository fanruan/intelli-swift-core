package com.fr.swift.cloud.query.result.serialize;


import com.fr.swift.cloud.exception.meta.SwiftMetaDataException;
import com.fr.swift.cloud.query.post.meta.SwiftMetaDataUtils;
import com.fr.swift.cloud.query.query.QueryBean;
import com.fr.swift.cloud.query.query.QueryType;
import com.fr.swift.cloud.result.DetailQueryResultSet;
import com.fr.swift.cloud.result.SwiftResultSet;
import com.fr.swift.cloud.result.detail.MergeSortedDetailQueryResultSet;
import com.fr.swift.cloud.result.node.resultset.MergeGroupQueryResultSet;
import com.fr.swift.cloud.result.qrs.QueryResultSet;
import com.fr.swift.cloud.source.SwiftMetaData;
import com.fr.swift.cloud.util.Crasher;

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
