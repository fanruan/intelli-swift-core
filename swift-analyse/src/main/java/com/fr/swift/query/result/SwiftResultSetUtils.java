package com.fr.swift.query.result;


import com.fr.swift.exception.meta.SwiftMetaDataException;
import com.fr.swift.query.post.meta.SwiftMetaDataUtils;
import com.fr.swift.query.query.QueryBean;
import com.fr.swift.query.query.QueryType;
import com.fr.swift.query.result.serialize.DetailSerializableQRS;
import com.fr.swift.query.result.serialize.NodeSerializableQRS;
import com.fr.swift.result.DetailQueryResultSet;
import com.fr.swift.result.SwiftNode;
import com.fr.swift.result.SwiftResultSet;
import com.fr.swift.result.qrs.QueryResultSet;
import com.fr.swift.source.SwiftMetaData;
import com.fr.swift.structure.Pair;
import com.fr.swift.util.Crasher;

import java.util.List;
import java.util.Map;

/**
 * @author yee
 * @date 2018-12-12
 */
public class SwiftResultSetUtils {

    public static QueryResultSet toSerializable(QueryType type, QueryResultSet resultSet) {
        switch (type) {
            case DETAIL:
            case DETAIL_SORT: {
                DetailQueryResultSet rs = (DetailQueryResultSet) resultSet;
                return new DetailSerializableQRS(rs.getFetchSize(), rs.getRowCount(),
                        rs.getMerger(), rs.getPage(), rs.hasNextPage());
            }
            case GROUP: {
                return new NodeSerializableQRS(resultSet.getFetchSize(), resultSet.getMerger(),
                        (Pair<SwiftNode, List<Map<Integer, Object>>>) resultSet.getPage(), resultSet.hasNextPage());
            }
            default:
                return resultSet;
        }
    }

    public static SwiftResultSet toSwiftResultSet(QueryResultSet queryResultSet, QueryBean bean) {
        SwiftMetaData metaData;
        try {
            metaData = SwiftMetaDataUtils.createMetaData(bean);
        } catch (SwiftMetaDataException e) {
            return Crasher.crash(e);
        }
        return queryResultSet.convert(metaData);
    }
}
