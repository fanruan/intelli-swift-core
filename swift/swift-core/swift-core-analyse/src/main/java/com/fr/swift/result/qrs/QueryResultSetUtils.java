package com.fr.swift.result.qrs;

import com.fr.swift.query.query.QueryType;
import com.fr.swift.result.SwiftResultSet;
import com.fr.swift.source.SwiftMetaData;

import java.io.Serializable;

/**
 * @author lyon
 * @date 2018/11/27
 */
public class QueryResultSetUtils {

    public static <T extends Serializable> QueryResultSetMerger<T> createMerger(QueryType type) {
        switch (type) {
            case DETAIL:
            case GROUP:
            default:
        }
        return null;
    }

    public static SwiftResultSet convert(QueryResultSet resultSet, SwiftMetaData metaData) {
        QueryResultSetConverter converter = null;
        switch (resultSet.type()) {
            case ROW:
            case NODE:
                converter = null;
            default:
        }
        return converter.convert(resultSet, metaData);
    }
}
