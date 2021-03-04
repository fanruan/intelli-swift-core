package com.fr.swift.cloud.result.qrs;

import com.fr.swift.cloud.result.SwiftResultSet;
import com.fr.swift.cloud.source.SwiftMetaData;

/**
 * @author lyon
 * @date 2018/11/28
 */
public interface QueryResultSetConverter {

    SwiftResultSet convert(QueryResultSet resultSet, SwiftMetaData metaData);
}
