package com.fr.swift.result.qrs;

import com.fr.swift.source.SwiftMetaData;
import com.fr.swift.source.SwiftResultSet;

/**
 * Created by lyon on 2018/11/28.
 */
public interface QueryResultSetConverter {

    SwiftResultSet convert(QueryResultSet resultSet, SwiftMetaData metaData);
}
