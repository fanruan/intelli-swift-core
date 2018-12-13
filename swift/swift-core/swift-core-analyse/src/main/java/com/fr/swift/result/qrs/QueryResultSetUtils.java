package com.fr.swift.result.qrs;

import com.fr.swift.query.query.QueryType;
import com.fr.swift.result.SwiftResultSet;
import com.fr.swift.source.SwiftMetaData;

import java.io.Serializable;

/**
 * Created by lyon on 2018/11/27.
 */
public class QueryResultSetUtils {

    public static <T extends Serializable> QueryResultSetMerger<T> createMerger(QueryType type) {
        switch (type) {
            case DETAIL:
            case GROUP:
        }
        return null;
    }

    public static SwiftResultSet convert(QueryResultSet resultSet, SwiftMetaData metaData) {
        QueryResultSetConverter converter = null;
        switch (resultSet.type()) {
            case ROW:
            case NODE:
                converter = null;
        }
        return converter.convert(resultSet, metaData);
    }
}
