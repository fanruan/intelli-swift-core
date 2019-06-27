package com.fr.swift.query.result;


import com.fr.swift.exception.meta.SwiftMetaDataException;
import com.fr.swift.query.group.by2.node.GroupPage;
import com.fr.swift.query.post.meta.SwiftMetaDataUtils;
import com.fr.swift.query.query.QueryBean;
import com.fr.swift.query.query.QueryType;
import com.fr.swift.query.result.serialize.DetailSerializableQRS;
import com.fr.swift.query.result.serialize.NodeSerializableQRS;
import com.fr.swift.result.DetailQueryResultSet;
import com.fr.swift.result.SwiftResultSet;
import com.fr.swift.result.qrs.QueryResultSet;
import com.fr.swift.source.SwiftMetaData;
import com.fr.swift.util.Crasher;

/**
 * @author yee
 * @date 2018-12-12
 */
public class SwiftResultSetUtils {

    public static <T> QueryResultSet<T> toSerializable(QueryType type, QueryResultSet<T> resultSet) {
        switch (type) {
            case DETAIL:
            case DETAIL_SORT: {
                DetailQueryResultSet rs = (DetailQueryResultSet) resultSet;
                return (QueryResultSet<T>) new DetailSerializableQRS(rs.getFetchSize(), rs.getRowCount(),
                        rs.<DetailQueryResultSet>getMerger(), rs.getPage(), rs.hasNextPage());
            }
            case GROUP: {
                QueryResultSet<GroupPage> groupQrs = (QueryResultSet<GroupPage>) resultSet;
                return (QueryResultSet<T>) new NodeSerializableQRS(groupQrs.getFetchSize(), groupQrs.getMerger(), groupQrs.getPage(), groupQrs.hasNextPage());
            }
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
